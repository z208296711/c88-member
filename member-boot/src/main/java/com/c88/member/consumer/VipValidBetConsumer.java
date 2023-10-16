package com.c88.member.consumer;

import com.alibaba.fastjson.JSON;
import com.c88.common.core.constant.TopicConstants;
import com.c88.game.adapter.enums.BetOrderEventTypeEnum;
import com.c88.game.adapter.event.BetRecord;
import com.c88.member.pojo.entity.MemberVip;
import com.c88.member.service.IMemberService;
import com.c88.member.service.IMemberVipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.c88.common.core.constant.TopicConstants.VIP_VALID_BET;

@Slf4j
@Component
@RequiredArgsConstructor
public class VipValidBetConsumer {

    private final IMemberService iMemberService;

    private final IMemberVipService iMemberVipService;

    @KafkaListener(id = VIP_VALID_BET, topics = TopicConstants.VALID_BET)
    @Transactional
    public void listenValidBet(BetRecord betRecord, Acknowledgment acknowledgement) {
        log.info("VIP_VALID_BET Consumer: {}", JSON.toJSONString(betRecord));

        MemberVip memberVip = iMemberService.getMemberVip(betRecord.getMemberId());
        if (Objects.equals(betRecord.getEventType(), BetOrderEventTypeEnum.BET_SETTLED.getValue())) {
            //注單成立扣除提款流水
            memberVip.setCurrentVipValidBet(memberVip.getCurrentVipValidBet().add(betRecord.getValidBetAmount()));
        } else if (Objects.equals(betRecord.getEventType(), BetOrderEventTypeEnum.BET_CANCELED.getValue())) {
            //注單被取消必須加回來

            memberVip.setCurrentVipValidBet(memberVip.getCurrentVipValidBet().subtract(betRecord.getValidBetAmount()));
        }
        iMemberVipService.updateById(memberVip);
        acknowledgement.acknowledge();
    }
}
