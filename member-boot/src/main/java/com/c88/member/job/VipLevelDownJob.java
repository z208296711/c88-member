package com.c88.member.job;

import com.c88.common.core.constant.RedisConstants;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.member.service.IVipService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class VipLevelDownJob {

    public final IVipService iVipService;

    private final RedissonClient redissonClient;

    @XxlJob("doVipLevelDown")
    public void doVipLevelDown() {
        String key = RedisUtils.buildKey(RedisConstants.VIP_LEVEL_DOWN_SCHEDULE_LOCK);
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock();
            XxlJobHelper.log("XXL-JOB, doVipLevelDown Start");
            log.info("VipLevelDownJob start: {}", LocalDateTime.now().toLocalTime());
            iVipService.doLevelDownAction();
            log.info("VipLevelDownJob end: {}", LocalDateTime.now().toLocalTime());
            XxlJobHelper.log("XXL-JOB, doVipLevelDown END");
        } finally {
            lock.unlock();
        }
    }
}
