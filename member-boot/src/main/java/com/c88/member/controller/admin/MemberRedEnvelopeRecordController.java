package com.c88.member.controller.admin;

import com.c88.common.core.enums.BalanceChangeTypeLinkEnum;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.MemberUtils;
import com.c88.member.common.enums.RedEnvelopeRecordStateEnum;
import com.c88.member.common.enums.RedEnvelopeTemplateTypeEnum;
import com.c88.member.constants.RedisKey;
import com.c88.member.pojo.entity.MemberRedEnvelopeRecord;
import com.c88.member.pojo.entity.MemberRedEnvelopeTemplate;
import com.c88.member.pojo.entity.MemberRedEnvelopeTemplateCondition;
import com.c88.member.pojo.form.ApproveRedEnvelopeRecordForm;
import com.c88.member.pojo.form.FindRedEnvelopeRecordForm;
import com.c88.member.pojo.form.RejectRedEnvelopeRecordForm;
import com.c88.member.pojo.vo.MemberRedEnvelopeRecordVO;
import com.c88.member.service.IMemberRedEnvelopeRecordService;
import com.c88.member.service.IMemberRedEnvelopeService;
import com.c88.member.service.IMemberRedEnvelopeTemplateConditionService;
import com.c88.member.service.IMemberRedEnvelopeTemplateService;
import com.c88.payment.dto.AddBalanceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import static com.c88.common.core.constant.TopicConstants.BALANCE_CHANGE;
import static com.c88.common.core.enums.BalanceChangeTypeLinkEnum.CHINESE_CABBAGE_RED_ENVELOPE;
import static com.c88.common.core.enums.BalanceChangeTypeLinkEnum.RED_ENVELOPE_CODE;

@Tag(name = "『後台』白菜紅包領取紀錄")
@RestController
@RequestMapping("/api/v1/red/envelope/record")
@RequiredArgsConstructor
public class MemberRedEnvelopeRecordController {

    private final IMemberRedEnvelopeRecordService iMemberRedEnvelopeRecordService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final IMemberRedEnvelopeService iMemberRedEnvelopeService;

    private final IMemberRedEnvelopeTemplateConditionService iMemberRedEnvelopeTemplateConditionService;

    private final IMemberRedEnvelopeTemplateService iMemberRedEnvelopeTemplateService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "找白菜紅包領取紀錄")
    @GetMapping
    public PageResult<MemberRedEnvelopeRecordVO> findRedEnvelopeRecord(@ParameterObject FindRedEnvelopeRecordForm form) {
        return PageResult.success(iMemberRedEnvelopeRecordService.findRedEnvelopeRecord(form));
    }

    @Transactional
    @Operation(summary = "白菜紅包領取紀錄通過")
    @PutMapping("/approve")
    public Result<Boolean> approveRedEnvelopeRecord(@RequestBody ApproveRedEnvelopeRecordForm form) {
        List<MemberRedEnvelopeRecord> memberRedEnvelopeRecords = iMemberRedEnvelopeRecordService.lambdaQuery()
                .in(MemberRedEnvelopeRecord::getId, form.getIds())
                .eq(MemberRedEnvelopeRecord::getState, RedEnvelopeRecordStateEnum.PENDING_REVIEW.getCode())
                .list();

        boolean update = iMemberRedEnvelopeRecordService.lambdaUpdate().in(MemberRedEnvelopeRecord::getId, form.getIds())
                .set(MemberRedEnvelopeRecord::getState, RedEnvelopeRecordStateEnum.APPROVE.getCode())
                .set(MemberRedEnvelopeRecord::getReviewTime, LocalDateTime.now())
                .update();

        if (!update) {
            throw new BizException(ResultCode.SYSTEM_RESOURCE_ERROR);
        }

        form.getIds()
                .forEach(id -> {
                            MemberRedEnvelopeRecord redEnvelopeRecord = memberRedEnvelopeRecords.stream()
                                    .filter(filter -> Objects.equals(filter.getId(), id))
                                    .findFirst()
                                    .orElse(MemberRedEnvelopeRecord.builder().build());

                            BalanceChangeTypeLinkEnum linkEnum =
                                    Objects.equals(redEnvelopeRecord.getType(), RedEnvelopeTemplateTypeEnum.CHINESE_CABBAGE.getCode()) ?
                                            CHINESE_CABBAGE_RED_ENVELOPE :
                                            RED_ENVELOPE_CODE;

                            kafkaTemplate.send(BALANCE_CHANGE,
                                    AddBalanceDTO.builder()
                                            .memberId(redEnvelopeRecord.getMemberId())
                                            .balance(redEnvelopeRecord.getAmount())
                                            .balanceChangeTypeLinkEnum(linkEnum)
                                            .type(linkEnum.getType())
                                            .betRate(redEnvelopeRecord.getBetRate())
                                            .note(linkEnum.getI18n())
                                            .bonusReviewUsername(MemberUtils.getUsername())
                                            .gmtCreate(LocalDateTime.now())
                                            .build()
                            );

                            LocalDate nowDate = LocalDate.now(ZoneId.of("+7"));
                            Integer level = iMemberRedEnvelopeService.getLevel(redEnvelopeRecord.getMemberId(), redEnvelopeRecord.getTemplateId());
                            Integer cycle = iMemberRedEnvelopeService.getCycle(redEnvelopeRecord.getMemberId(), redEnvelopeRecord.getTemplateId());
                            String levelKey = RedisUtils.buildKey(RedisKey.MEMBER_RED_ENVELOPE_TEMPLATE_LEVEL, redEnvelopeRecord.getTemplateId(), redEnvelopeRecord.getMemberId());
                            String cycleKey = RedisUtils.buildKey(RedisKey.MEMBER_RED_ENVELOPE_TEMPLATE_CYCLE, redEnvelopeRecord.getTemplateId(), redEnvelopeRecord.getMemberId());
                            List<MemberRedEnvelopeTemplateCondition> byTemplateId = iMemberRedEnvelopeTemplateConditionService.getByTemplateId(redEnvelopeRecord.getTemplateId());
                            MemberRedEnvelopeTemplate template = iMemberRedEnvelopeTemplateService.getById(redEnvelopeRecord.getTemplateId());

                            // 紅包累積數量
                            Integer redEnvelopNumber = iMemberRedEnvelopeService.getRedEnvelopNumber(redEnvelopeRecord.getTemplateId());

                            // 紅包單日數量
                            Integer redEnvelopDailyNumber = iMemberRedEnvelopeService.getDailyRedEnvelopeNumber(nowDate, redEnvelopeRecord.getTemplateId());

                            // 會員紅包累積數量
                            Integer memberRedEnvelopNumber = iMemberRedEnvelopeService.getMemberRedEnvelopRecordNumber(redEnvelopeRecord.getTemplateId(), redEnvelopeRecord.getMemberId());

                            // 會員紅包單日數量
                            Integer memberRedEnvelopDailyNumber = iMemberRedEnvelopeService.getMemberDailyRedEnvelopNumber(nowDate, redEnvelopeRecord.getTemplateId(), redEnvelopeRecord.getMemberId());

                            if (level >= byTemplateId.size()) {
                                if (template.getDailyRedEnvelopeTotal() > redEnvelopNumber ||
                                        template.getRedEnvelopeTotal() > redEnvelopDailyNumber ||
                                        template.getMaxTotal() > memberRedEnvelopNumber ||
                                        template.getDailyMaxTotal() > memberRedEnvelopDailyNumber) {
                                } else {
                                    redisTemplate.opsForValue().set(cycleKey, ++cycle);
                                    redisTemplate.opsForValue().set(levelKey, 1);
                                }
                            } else {
                                redisTemplate.opsForValue().set(levelKey, ++level);

                            }

                        }
                );

        //todo 發送站內信

        return Result.success(Boolean.TRUE);
    }

    @Operation(summary = "白菜紅包領取紀錄拒絕")
    @PutMapping("/reject")
    public Result<Boolean> rejectRedEnvelopeRecord(@RequestBody RejectRedEnvelopeRecordForm form) {
        boolean update = iMemberRedEnvelopeRecordService.lambdaUpdate()
                .in(MemberRedEnvelopeRecord::getId, form.getIds())
                .eq(MemberRedEnvelopeRecord::getState, RedEnvelopeRecordStateEnum.PENDING_REVIEW.getCode())
                .set(MemberRedEnvelopeRecord::getState, RedEnvelopeRecordStateEnum.REJECT.getCode())
                .set(MemberRedEnvelopeRecord::getReviewTime, LocalDateTime.now())
                .update();

        //todo 發送站內信

        return Result.success(update);
    }


}
