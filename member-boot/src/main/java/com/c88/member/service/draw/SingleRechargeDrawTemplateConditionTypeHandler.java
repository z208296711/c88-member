package com.c88.member.service.draw;

import com.c88.common.core.constant.TopicConstants;
import com.c88.common.core.enums.BalanceChangeTypeLinkEnum;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.UserUtils;
import com.c88.member.common.enums.AwardTypeEnum;
import com.c88.member.common.enums.DrawRecordStateEnum;
import com.c88.member.common.enums.DrawTemplateConditionTypeEnum;
import com.c88.member.enums.RechargeAwardRecordModelActionEnum;
import com.c88.payment.dto.AddBalanceDTO;
import com.c88.member.enums.RechargeAwardRecordStateEnum;
import com.c88.member.event.RechargeAwardRecordModel;
import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.entity.MemberDrawRecord;
import com.c88.member.pojo.entity.MemberDrawTemplateCondition;
import com.c88.member.pojo.entity.MemberRechargeAwardTemplate;
import com.c88.member.pojo.vo.MemberDrawVO;
import com.c88.member.pojo.vo.MemberTodayCurrentDrawVO;
import com.c88.payment.client.MemberRechargeClient;
import com.c88.payment.vo.MemberRechargeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.c88.common.core.constant.TopicConstants.RECHARGE_AWARD_RECORD;

@Slf4j
@Component
@RequiredArgsConstructor
public class SingleRechargeDrawTemplateConditionTypeHandler extends DrawTemplateConditionTypeHandler {

    private final MemberRechargeClient memberRechargeClient;

    @Override
    public MemberDrawAwardItem doDrawHandler(DrawConditionVO conditionVO) {
        log.info("SingleRechargeDrawTemplateConditionTypeHandler");

        MemberDrawTemplateCondition singleRechargeCondition = conditionVO.getConditionList().stream()
                .filter(x -> x.getType().equals(DrawTemplateConditionTypeEnum.SINGLE_RECHARGE.getValue()))
                .findFirst()
                .orElse(null);

        if (next != null && singleRechargeCondition == null) {
            return next.doDrawHandler(conditionVO);
        }

        Result<List<MemberRechargeDTO>> rechargeResult = memberRechargeClient.findMemberSingleRecharge(conditionVO.getMember().getId(),
                conditionVO.getResetTime(),
                LocalDateTime.now(),
                new BigDecimal(singleRechargeCondition.getConditionNum()));
        if (!Result.isSuccess(rechargeResult)) {
            throw new BizException(ResultCode.INTERNAL_SERVICE_CALLEE_ERROR);
        }

        //判斷單筆充值禮金抽中次數
        int drawTimes = conditionVO.getCurrentDrawVO().getSingleRechargeDraw();
        int remainDraw = this.getRemainingFreeDraw(conditionVO);
        //超過抽中上限 換下一個條件
        if (next != null && drawTimes >= singleRechargeCondition.getMaxNum() ||
                next != null && remainDraw == 0) {
            return next.doDrawHandler(conditionVO);
        }
        if (drawTimes >= singleRechargeCondition.getMaxNum() || remainDraw <= 0) {
            throw new BizException(ResultCode.OVER_DRAW_TIMES);
        }

        MemberDrawVO drawVO = this.doDraw(conditionVO.getMemberDrawTemplate().getId());

        MemberDrawAwardItem memberDrawAwardItem = iMemberDrawAwardItemService.getById(drawVO.getAwardId());
        MemberDrawRecord memberDrawRecord = new MemberDrawRecord();
        memberDrawRecord.setMemberId(conditionVO.getMember().getId());
        memberDrawRecord.setTemplateId(conditionVO.getMemberDrawTemplate().getId());
        memberDrawRecord.setTemplateConditionId(singleRechargeCondition.getId());
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

        iMemberDrawRecordService.save(memberDrawRecord);

        if (memberDrawAwardItem.getReview().equals(REVIEW)) {
            memberDrawRecord.setState(DrawRecordStateEnum.PRE_APPLY.getValue());
        } else {
            memberDrawRecord.setState(DrawRecordStateEnum.SEND.getValue());
        }

        if (AwardTypeEnum.fromIntValue(memberDrawRecord.getAwardType()).equals(AwardTypeEnum.BONUS) &&
                DrawRecordStateEnum.fromIntValue(memberDrawRecord.getState()).equals(DrawRecordStateEnum.SEND)) {
            kafkaTemplate.send(TopicConstants.BALANCE_CHANGE,AddBalanceDTO.builder()
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

        memberDrawAwardItem.setMemberDrawRecord(memberDrawRecord);
        MemberTodayCurrentDrawVO memberTodayCurrentDrawVO = conditionVO.getCurrentDrawVO();
        memberTodayCurrentDrawVO.setSingleRechargeDraw(conditionVO.getCurrentDrawVO().getSingleRechargeDraw() + 1);
        this.setMemberTodayCurrentDrawVO(conditionVO.getMember().getId(), memberTodayCurrentDrawVO);
        return memberDrawAwardItem;
    }

    @Override
    public Integer getNextDrawConditionType(DrawConditionVO conditionVO) {

        MemberDrawTemplateCondition singleRechargeCondition = conditionVO.getConditionList().stream()
                .filter(x -> x.getType().equals(DrawTemplateConditionTypeEnum.SINGLE_RECHARGE.getValue()))
                .findFirst()
                .orElse(null);

        if (next != null && singleRechargeCondition == null) {
            return next.getNextDrawConditionType(conditionVO);
        }

        Result<List<MemberRechargeDTO>> rechargeResult = memberRechargeClient.findMemberSingleRecharge(conditionVO.getMember().getId(),
                conditionVO.getResetTime(),
                LocalDateTime.now(),
                new BigDecimal(singleRechargeCondition.getConditionNum()));
        if (!Result.isSuccess(rechargeResult)) {
            throw new BizException(ResultCode.INTERNAL_SERVICE_CALLEE_ERROR);
        }

        //判斷單筆充值禮金抽中次數
        int drawTimes = conditionVO.getCurrentDrawVO().getSingleRechargeDraw();
        int remainDraw = this.getRemainingFreeDraw(conditionVO);
        //超過抽中上限 換下一個條件
        if (next != null && drawTimes >= singleRechargeCondition.getMaxNum() ||
                next != null && remainDraw == 0) {
            return next.getNextDrawConditionType(conditionVO);
        }
        return DrawTemplateConditionTypeEnum.SINGLE_RECHARGE.getValue();
    }

    @Override
    public int getRemainingFreeDraw(DrawConditionVO conditionVO) {
        MemberDrawTemplateCondition singleRechargeCondition = conditionVO.getConditionList().stream()
                .filter(x -> x.getType().equals(DrawTemplateConditionTypeEnum.SINGLE_RECHARGE.getValue()))
                .findFirst()
                .orElse(null);
        if (!conditionVO.isPrecondition()) {
            return 0;
        }
        Result<List<MemberRechargeDTO>> rechargeResult = memberRechargeClient.findMemberSingleRecharge(conditionVO.getMember().getId(),
                conditionVO.getResetTime(),
                LocalDateTime.now(),
                new BigDecimal(singleRechargeCondition.getConditionNum()));
        if (!Result.isSuccess(rechargeResult)) {
            throw new BizException(ResultCode.INTERNAL_SERVICE_CALLEE_ERROR);
        }
        int canDraw = rechargeResult.getData().size() > singleRechargeCondition.getMaxNum() ? singleRechargeCondition.getMaxNum() : rechargeResult.getData().size();
        return canDraw - conditionVO.getCurrentDrawVO().getSingleRechargeDraw() > 0 ? canDraw - conditionVO.getCurrentDrawVO().getSingleRechargeDraw() : 0;
    }
}
