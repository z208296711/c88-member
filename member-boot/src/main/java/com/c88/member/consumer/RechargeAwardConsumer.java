package com.c88.member.consumer;

import com.c88.common.core.constant.TopicConstants;
import com.c88.member.enums.RechargeAwardRecordModelActionEnum;
import com.c88.member.enums.RechargeAwardRecordStateEnum;
import com.c88.member.event.RechargeAwardRecordModel;
import com.c88.member.mapstruct.MemberRechargeAwardRecordConverter;
import com.c88.member.pojo.entity.MemberRechargeAwardRecord;
import com.c88.member.service.IMemberRechargeAwardRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.c88.common.core.constant.TopicConstants.RECHARGE_AWARD_RECORD;

@Slf4j
@Component
@RequiredArgsConstructor
public class RechargeAwardConsumer {

    private final IMemberRechargeAwardRecordService iMemberRechargeAwardRecordService;

    private final MemberRechargeAwardRecordConverter memberRechargeAwardRecordConverter;

    @KafkaListener(id = RECHARGE_AWARD_RECORD,topics = TopicConstants.RECHARGE_AWARD_RECORD)
    @Transactional
    public void rechargeAwardRecordConsumer(RechargeAwardRecordModel rechargeAwardRecordModel, Acknowledgment acknowledgement) {
        log.info("listenRechargeAwardRecord: {}", rechargeAwardRecordModel);

        switch (RechargeAwardRecordModelActionEnum.getEnum(rechargeAwardRecordModel.getAction())) {
            case MODIFY:
                Optional<MemberRechargeAwardRecord> memberRechargeAwardRecordOpt = iMemberRechargeAwardRecordService.lambdaQuery()
                        .eq(MemberRechargeAwardRecord::getState, RechargeAwardRecordStateEnum.UNUSED.getCode())
                        .eq(MemberRechargeAwardRecord::getMemberId, rechargeAwardRecordModel.getMemberId())
                        .eq(MemberRechargeAwardRecord::getTemplateId, rechargeAwardRecordModel.getTemplateId())
                        .last("limit 1")
                        .oneOpt();
                if (memberRechargeAwardRecordOpt.isPresent()) {
                    MemberRechargeAwardRecord memberRechargeAwardRecord = memberRechargeAwardRecordOpt.get();
                    iMemberRechargeAwardRecordService.lambdaUpdate()
                            .eq(MemberRechargeAwardRecord::getId, memberRechargeAwardRecord.getId())
                            .set(MemberRechargeAwardRecord::getRechargeAmount, rechargeAwardRecordModel.getRechargeAmount())
                            .set(MemberRechargeAwardRecord::getUseTime, rechargeAwardRecordModel.getUseTime())
                            .set(MemberRechargeAwardRecord::getState, RechargeAwardRecordStateEnum.USED.getCode())
                            .update();
                }
                break;
            case ADD:
            default:
                iMemberRechargeAwardRecordService.save(memberRechargeAwardRecordConverter.toEntity(rechargeAwardRecordModel));
        }
        acknowledgement.acknowledge();
    }
}
