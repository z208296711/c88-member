package com.c88.member.service.draw;

import com.c88.common.core.constant.TopicConstants;
import com.c88.common.core.enums.BalanceChangeTypeLinkEnum;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.UserUtils;
import com.c88.member.common.enums.AwardTypeEnum;
import com.c88.member.common.enums.DrawRecordStateEnum;
import com.c88.member.common.enums.DrawTemplateConditionTypeEnum;
import com.c88.member.constants.RedisKey;
import com.c88.member.enums.RechargeAwardRecordModelActionEnum;
import com.c88.member.enums.RechargeAwardRecordStateEnum;
import com.c88.member.event.RechargeAwardRecordModel;
import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.entity.MemberDrawRecord;
import com.c88.member.pojo.entity.MemberDrawTemplateCondition;
import com.c88.member.pojo.entity.MemberRechargeAwardTemplate;
import com.c88.member.pojo.vo.MemberDrawVO;
import com.c88.member.pojo.vo.MemberTodayCurrentDrawVO;
import com.c88.payment.dto.AddBalanceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.c88.common.core.constant.TopicConstants.RECHARGE_AWARD_RECORD;

@Slf4j
@Component
@RequiredArgsConstructor
public class FreeDrawTemplateConditionTypeHandler extends DrawTemplateConditionTypeHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public MemberDrawAwardItem doDrawHandler(DrawConditionVO conditionVO) {
        log.info("FreeDrawTemplateConditionTypeHandler");
        MemberDrawTemplateCondition freeDrawTemplateCondition = conditionVO.getConditionList()
                .stream()
                .filter(x -> x.getType().equals(DrawTemplateConditionTypeEnum.FREE.getValue()))
                .findFirst()
                .orElse(null);

        //若無此條件則跳過
        if (next != null && freeDrawTemplateCondition == null) {
            return next.doDrawHandler(conditionVO);
        }

        //判斷免費禮金抽中次數
        int drawTimes = conditionVO.getCurrentDrawVO().getFreeDraw();

        //超過抽中上限 換下一個條件
        if (next != null && drawTimes >= freeDrawTemplateCondition.getMaxNum()) {
            return next.doDrawHandler(conditionVO);
        }
        int remainDraw = this.getRemainingFreeDraw(conditionVO);
        if (drawTimes >= freeDrawTemplateCondition.getMaxNum() || remainDraw <= 0) {
            throw new BizException(ResultCode.OVER_DRAW_TIMES);
        }

        if (conditionVO.getMemberDrawTemplate().getFreeInterval() != 0) {
            Boolean keyExist = !redisTemplate.opsForValue().setIfAbsent(RedisKey.DRAW_ACTION + ":" + conditionVO.getMember().getId(), 1, conditionVO.getMemberDrawTemplate().getFreeInterval(), TimeUnit.SECONDS);
            if (keyExist) {
                throw new BizException(ResultCode.IN_DRAW_INTERVAL);
            }
        }

        MemberDrawVO drawVO = this.doDraw(conditionVO.getMemberDrawTemplate().getId());

        MemberDrawAwardItem memberDrawAwardItem = iMemberDrawAwardItemService.getById(drawVO.getAwardId());
        MemberDrawRecord memberDrawRecord = new MemberDrawRecord();
        memberDrawRecord.setMemberId(conditionVO.getMember().getId());
        memberDrawRecord.setTemplateId(conditionVO.getMemberDrawTemplate().getId());
        memberDrawRecord.setTemplateConditionId(freeDrawTemplateCondition.getId());
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
                    .balance(memberDrawAwardItem.getAmount())
                    .balanceChangeTypeLinkEnum(BalanceChangeTypeLinkEnum.COMMON_DRAW)
                    .type(BalanceChangeTypeLinkEnum.COMMON_DRAW.getType())
                    .betRate(memberDrawAwardItem.getBetRate())
                    .note(BalanceChangeTypeLinkEnum.COMMON_DRAW.getI18n())
                    .gmtCreate(LocalDateTime.now())
                    .bonusReviewUsername(Optional.ofNullable(UserUtils.getUsername()).orElse(""))
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
        memberTodayCurrentDrawVO.setFreeDraw(conditionVO.getCurrentDrawVO().getFreeDraw() + 1);
        this.setMemberTodayCurrentDrawVO(conditionVO.getMember().getId(), memberTodayCurrentDrawVO);
        return memberDrawAwardItem;
    }

    @Override
    public Integer getNextDrawConditionType(DrawConditionVO conditionVO) {
        MemberDrawTemplateCondition freeDrawTemplateCondition = conditionVO.getConditionList()
                .stream()
                .filter(x -> x.getType().equals(DrawTemplateConditionTypeEnum.FREE.getValue()))
                .findFirst()
                .orElse(null);

        //若無此條件則跳過
        if (next != null && freeDrawTemplateCondition == null) {
            return next.getNextDrawConditionType(conditionVO);
        }

        //判斷免費禮金抽中次數
        int drawTimes = conditionVO.getCurrentDrawVO().getFreeDraw();

        //超過抽中上限 換下一個條件
        if (next != null && drawTimes >= freeDrawTemplateCondition.getMaxNum()) {
            return next.getNextDrawConditionType(conditionVO);
        }
        return DrawTemplateConditionTypeEnum.FREE.getValue();
    }

    @Override
    public int getRemainingFreeDraw(DrawConditionVO conditionVO) {
        log.info("FreeDrawTemplateConditionTypeHandler");
        MemberDrawTemplateCondition freeDrawTemplateCondition = conditionVO.getConditionList()
                .stream()
                .filter(x -> x.getType().equals(DrawTemplateConditionTypeEnum.FREE.getValue()))
                .findFirst()
                .orElse(null);

        if (!conditionVO.isPrecondition()) {
            return 0;
        }
        return freeDrawTemplateCondition.getMaxNum() - conditionVO.getCurrentDrawVO().getFreeDraw();
    }
}
