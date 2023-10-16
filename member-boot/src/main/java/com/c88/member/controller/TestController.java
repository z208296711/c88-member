package com.c88.member.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.c88.common.core.result.Result;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.member.constants.RedisKey;
import com.c88.member.event.RechargeAwardRecordModel;
import com.c88.member.pojo.entity.*;
import com.c88.member.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.c88.common.core.constant.TopicConstants.RECHARGE_AWARD_RECORD;
import static com.c88.member.constants.RedisKey.MEMBER_TEMPLATE_RECEIVE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Profile("!prod & !pre")
@Tag(name = "測試各項功能")
public class TestController {

    private final IMemberBirthdayGiftRecordService iMemberBirthdayGiftRecordService;

    private final IMemberLevelUpGiftRecordService iMemberLevelUpGiftRecordService;

    private final IMemberFreeBetGiftRecordService iMemberFreeBetGiftRecordService;

    private final IMemberRedEnvelopeTemplateConditionService iMemberRedEnvelopeTemplateConditionService;

    private final IMemberRedEnvelopeService iMemberRedEnvelopeService;

    private final IVipService iVipService;

    private final IMemberVipService iMemberVipService;

    private final ApplicationEventPublisher applicationContext;

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Operation(summary = "XXX")
    @GetMapping
    public Result<Boolean> ddd() {
        // RechargeAwardEventModel rechargeAwardEventModel = new RechargeAwardEventModel();
        // RechargeAwardEventModel build = RechargeAwardEventModel.builder().platformId(22222).build();
        // rechargeAwardEventModel.setIp("111");
        // applicationContext.publishEvent(rechargeAwardEventModel);
        // applicationContext.publishEvent(build);

        kafkaTemplate.send(RECHARGE_AWARD_RECORD, RechargeAwardRecordModel.builder().id(111L).build());

        return Result.success();
    }


    @Operation(summary = "修改會員最後晉級時間")
    @PutMapping("/levelup/{username}")
    public Result<Boolean> modifyLastLevelUpTime(@PathVariable("username") String username, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime lastLevelUpTime) {
        return Result.success(iMemberVipService.lambdaUpdate().eq(MemberVip::getUsername, username).set(MemberVip::getLevelUpTime, lastLevelUpTime).update());
    }

    @Operation(summary = "修改vip 會員 累計流水")
    @PutMapping("/vip/bet/{username}")
    public Result<Boolean> modifyLastLevelUpTime(@PathVariable("username") String username,
                                                 Integer amount) {
        return Result.success(iMemberVipService.lambdaUpdate()
                .eq(MemberVip::getUsername, username)
                .set(MemberVip::getCurrentVipValidBet, amount)
                .update());
    }

    @Operation(summary = "晉級")
    @DeleteMapping("/levelup/{memberId}")
    public Result<Boolean> levelup(@PathVariable("memberId") Long memberId) {
        iVipService.doVipLevelUpAction(memberId);
        return Result.success();
    }

    @Operation(summary = "重置生日禮金")
    @DeleteMapping("/reset/gift/birthday/{username}")
    public Result<Boolean> resetBirthdayGift(@PathVariable("username") String username) {
        return Result.success(iMemberBirthdayGiftRecordService.remove(
                        Wrappers.<MemberBirthdayGiftRecord>lambdaQuery().eq(MemberBirthdayGiftRecord::getUsername, username)
                )
        );
    }

    @Operation(summary = "重置晉級禮金")
    @DeleteMapping("/reset/gift/level/up/{username}")
    public Result<Boolean> resetLevelUpGift(@PathVariable("username") String username) {
        return Result.success(iMemberLevelUpGiftRecordService.remove(
                        Wrappers.<MemberLevelUpGiftRecord>lambdaQuery().eq(MemberLevelUpGiftRecord::getUsername, username)
                )
        );
    }

    @Operation(summary = "重置免費籌碼禮金")
    @DeleteMapping("/reset/gift/fee/bet/{username}")
    public Result<Boolean> resetFeeBetGift(@PathVariable("username") String username) {
        return Result.success(iMemberFreeBetGiftRecordService.remove(
                        Wrappers.<MemberFreeBetGiftRecord>lambdaQuery().eq(MemberFreeBetGiftRecord::getUsername, username)
                )
        );
    }

    @Operation(summary = "重置紅包領取上限")
    @DeleteMapping("/reset/red/envelope/{templateId}")
    public Result<Boolean> resetRedEnvelope(@PathVariable("templateId") String templateId) {
        List<String> allKey = List.of("memberDailyMaxTotal:*",
                "memberMaxTotal:" + templateId + ":*",
                "memberRedEnvelopeTemplateLevel:" + templateId + ":*",
                "memberRedEnvelopeTemplateCycle:" + templateId + ":*",
                "redEnvelopeNumber:" + templateId,
                "redEnvelopeDailyNumber:*:" + templateId,
                "memberRedEnvelopeNumber:" + templateId + ":*",
                "memberRedEnvelopeDailyNumber:*:" + templateId);

        allKey.forEach(x ->
                redisTemplate.delete(redisTemplate.keys(x))
        );

        return Result.success(Boolean.TRUE);
    }

    @Operation(summary = "將紅包領取狀態前進下一階")
    @PutMapping("/red/envelope/receive/state/next")
    public Result<Boolean> resetRedEnvelopeReceiveState() {
        Set<String> keys = redisTemplate.keys(MEMBER_TEMPLATE_RECEIVE + ":*");

        if (CollectionUtils.isEmpty(keys)) {
            return Result.success(Boolean.TRUE);
        }

        List<MemberRedEnvelopeTemplateCondition> conditions = iMemberRedEnvelopeTemplateConditionService.list();

        keys.forEach(x -> {
                    while (true) {
                        Object conditionIdObj = redisTemplate.opsForList().leftPop(x);
                        if (Objects.isNull(conditionIdObj)) {
                            break;
                        }

                        Long conditionId = Long.parseLong(conditionIdObj.toString());

                        Long memberId = Long.parseLong(x.split(":")[1]);

                        MemberRedEnvelopeTemplateCondition condition = conditions.stream()
                                .filter(filter -> Objects.equals(filter.getId(), conditionId))
                                .findFirst()
                                .orElse(null);

                        String cycleKey = RedisUtils.buildKey(RedisKey.MEMBER_RED_ENVELOPE_TEMPLATE_CYCLE, condition.getTemplateId(), memberId);
                        String levelKey = RedisUtils.buildKey(RedisKey.MEMBER_RED_ENVELOPE_TEMPLATE_LEVEL, condition.getTemplateId(), memberId);
                        Integer currentCycle = iMemberRedEnvelopeService.getCycle(memberId, condition.getTemplateId());
                        redisTemplate.opsForValue().set(cycleKey, ++currentCycle);
                        redisTemplate.opsForValue().set(levelKey, 1);

                    }

                }
        );

        return Result.success(Boolean.TRUE);
    }

}
