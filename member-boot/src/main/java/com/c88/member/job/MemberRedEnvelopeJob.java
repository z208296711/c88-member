package com.c88.member.job;

import com.c88.common.redis.utils.RedisUtils;
import com.c88.member.constants.RedisKey;
import com.c88.member.pojo.entity.MemberRedEnvelopeTemplateCondition;
import com.c88.member.service.IMemberRedEnvelopeService;
import com.c88.member.service.IMemberRedEnvelopeTemplateConditionService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.c88.member.constants.RedisKey.MEMBER_TEMPLATE_RECEIVE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberRedEnvelopeJob {

    private final RedisTemplate<String, Object> redisTemplate;

    private final IMemberRedEnvelopeTemplateConditionService iMemberRedEnvelopeTemplateConditionService;

    private final IMemberRedEnvelopeService iMemberRedEnvelopeService;

    /**
     * 將紅包領取狀態前進下一階
     */
    @XxlJob("resetRedEnvelopeReceiveState")
    public void resetRedEnvelopeReceiveState() {
        log.info("=====do-resetRedEnvelopeReceiveState=====");
        Set<String> keys = redisTemplate.keys(MEMBER_TEMPLATE_RECEIVE + ":*");

        if (CollectionUtils.isEmpty(keys)) {
            return;
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
    }


}
