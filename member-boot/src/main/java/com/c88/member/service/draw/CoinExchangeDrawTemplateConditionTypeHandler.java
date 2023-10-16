package com.c88.member.service.draw;

import com.c88.common.core.constant.TopicConstants;
import com.c88.common.core.enums.BalanceChangeTypeLinkEnum;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.common.enums.AwardTypeEnum;
import com.c88.member.common.enums.DrawRecordStateEnum;
import com.c88.member.common.enums.DrawTemplateConditionTypeEnum;
import com.c88.member.enums.RechargeAwardRecordModelActionEnum;
import com.c88.member.enums.RechargeAwardRecordStateEnum;
import com.c88.member.event.RechargeAwardRecordModel;
import com.c88.member.pojo.entity.*;
import com.c88.member.pojo.vo.MemberDrawVO;
import com.c88.member.pojo.vo.MemberTodayCurrentDrawVO;
import com.c88.member.service.IMemberCoinService;
import com.c88.payment.dto.AddBalanceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.c88.common.core.constant.TopicConstants.RECHARGE_AWARD_RECORD;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoinExchangeDrawTemplateConditionTypeHandler extends DrawTemplateConditionTypeHandler {

    private final IMemberCoinService iMemberCoinService;

    @Override
    public MemberDrawAwardItem doDrawHandler(DrawConditionVO conditionVO) {

        MemberDrawTemplateCondition coinExchangeCondition = conditionVO.getConditionList().stream()
                .filter(x -> x.getType().equals(DrawTemplateConditionTypeEnum.COIN_EXCHANGE.getValue()))
                .findFirst()
                .orElse(null);

        if (next != null && coinExchangeCondition == null) {
            return next.doDrawHandler(conditionVO);
        }
        //判斷金幣兌換次數
        int drawTimes = conditionVO.getCurrentDrawVO().getCoinExchangeDraw();

        //超過抽中上限 換下一個條件
        if (next != null && drawTimes >= coinExchangeCondition.getMaxNum()) {
            return next.doDrawHandler(conditionVO);
        }

        if (drawTimes >= coinExchangeCondition.getMaxNum()) {
            throw new BizException(ResultCode.OVER_DRAW_TIMES);
        }

        MemberCoin memberCoin = iMemberCoinService.lambdaQuery()
                .eq(MemberCoin::getMemberId, conditionVO.getMember().getId())
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

        if (memberCoin.getCoin() < coinExchangeCondition.getConditionNum()) {
            throw new BizException(ResultCode.COIN_IS_NOT_ENOUGH);
        }

        memberCoin.setCoin(memberCoin.getCoin() - coinExchangeCondition.getConditionNum());
        iMemberCoinService.updateById(memberCoin);

        MemberDrawVO drawVO = this.doDraw(conditionVO.getMemberDrawTemplate().getId());

        MemberDrawAwardItem memberDrawAwardItem = iMemberDrawAwardItemService.getById(drawVO.getAwardId());
        MemberDrawRecord memberDrawRecord = new MemberDrawRecord();
        memberDrawRecord.setMemberId(conditionVO.getMember().getId());
        memberDrawRecord.setTemplateId(conditionVO.getMemberDrawTemplate().getId());
        memberDrawRecord.setTemplateConditionId(coinExchangeCondition.getId());
        memberDrawRecord.setUsername(conditionVO.getMember().getUserName());
        memberDrawRecord.setDrawType(drawVO.getDrawType());
        memberDrawRecord.setAwardId(drawVO.getAwardId());
        memberDrawRecord.setAwardName(memberDrawAwardItem.getName());
        memberDrawRecord.setAwardType(memberDrawAwardItem.getAwardType());
        memberDrawRecord.setAmount(memberDrawAwardItem.getAmount());
        memberDrawRecord.setBetRate(memberDrawAwardItem.getBetRate());
        memberDrawRecord.setState(memberDrawAwardItem.getReview().equals(REVIEW) ? DrawRecordStateEnum.PRE_APPROVE.getValue() : DrawRecordStateEnum.SEND.getValue());
        if (AwardTypeEnum.fromIntValue(memberDrawAwardItem.getAwardType()).equals(AwardTypeEnum.ENTITY)) {
            memberDrawRecord.setState(DrawRecordStateEnum.PRE_APPLY.getValue());
        }
        if (AwardTypeEnum.fromIntValue(memberDrawRecord.getAwardType()).equals(AwardTypeEnum.BONUS) &&
                DrawRecordStateEnum.fromIntValue(memberDrawRecord.getState()).equals(DrawRecordStateEnum.SEND)) {
            kafkaTemplate.send(TopicConstants.BALANCE_CHANGE, AddBalanceDTO.builder()
                    .memberId(conditionVO.getMember().getId())
                    .balanceChangeTypeLinkEnum(BalanceChangeTypeLinkEnum.COMMON_DRAW)
                    .balance(memberDrawAwardItem.getAmount())
                    .type(BalanceChangeTypeLinkEnum.COMMON_DRAW.getType())
                    .betRate(memberDrawAwardItem.getBetRate())
                    .note(BalanceChangeTypeLinkEnum.COMMON_DRAW.getI18n())
                    .build());

        } else if (AwardTypeEnum.fromIntValue(memberDrawRecord.getAwardType()).equals(AwardTypeEnum.COIN) &&
                DrawRecordStateEnum.fromIntValue(memberDrawRecord.getState()).equals(DrawRecordStateEnum.SEND)) {
            iMemberCoinService.addCoin(conditionVO.getMember().getId(), memberDrawRecord.getAmount().intValue());
        } else if (AwardTypeEnum.fromIntValue(memberDrawRecord.getAwardType()).equals(AwardTypeEnum.RECHARGE_PROMOTION) &&
                DrawRecordStateEnum.fromIntValue(memberDrawRecord.getState()).equals(DrawRecordStateEnum.SEND)) {

            MemberRechargeAwardTemplate memberRechargeAwardTemplate = iMemberRechargeAwardTemplateService.getById(memberDrawAwardItem.getPromotionId());
            kafkaTemplate.send(RECHARGE_AWARD_RECORD,
                    RechargeAwardRecordModel.builder()
                            .action(RechargeAwardRecordModelActionEnum.ADD.getCode())
                            .memberId(memberDrawRecord.getMemberId())
                            .username(memberDrawRecord.getUsername())
                            .templateId(memberRechargeAwardTemplate.getId())
                            .name(memberRechargeAwardTemplate.getName())
                            .type(memberRechargeAwardTemplate.getType())
                            .mode(memberRechargeAwardTemplate.getMode())
                            .rate(memberRechargeAwardTemplate.getRate())
                            .betRate(memberRechargeAwardTemplate.getBetRate())
                            .amount(memberRechargeAwardTemplate.getAmount())
                            .source("deposit_delivery_offer_personal.column_03_01")
                            .state(RechargeAwardRecordStateEnum.UNUSED.getCode())
                            .build()
            );
        }

        iMemberDrawRecordService.save(memberDrawRecord);
        memberDrawAwardItem.setMemberDrawRecord(memberDrawRecord);
        MemberTodayCurrentDrawVO memberTodayCurrentDrawVO = conditionVO.getCurrentDrawVO();
        memberTodayCurrentDrawVO.setCoinExchangeDraw(conditionVO.getCurrentDrawVO().getCoinExchangeDraw() + 1);
        this.setMemberTodayCurrentDrawVO(conditionVO.getMember().getId(), memberTodayCurrentDrawVO);
        return memberDrawAwardItem;
    }

    @Override
    public Integer getNextDrawConditionType(DrawConditionVO conditionVO) {
        MemberDrawTemplateCondition coinExchangeCondition = conditionVO.getConditionList().stream()
                .filter(x -> x.getType().equals(DrawTemplateConditionTypeEnum.COIN_EXCHANGE.getValue()))
                .findFirst()
                .orElse(null);

        if (next != null && coinExchangeCondition == null) {
            return next.getNextDrawConditionType(conditionVO);
        }
        //判斷金幣兌換次數
        int drawTimes = conditionVO.getCurrentDrawVO().getCoinExchangeDraw();

        //超過抽中上限 換下一個條件
        if (next != null && drawTimes >= coinExchangeCondition.getMaxNum()) {
            return next.getNextDrawConditionType(conditionVO);
        }
        return DrawTemplateConditionTypeEnum.COIN_EXCHANGE.getValue();
    }

    @Override
    public int getRemainingFreeDraw(DrawConditionVO conditionVO) {
        return 0;
    }
}
