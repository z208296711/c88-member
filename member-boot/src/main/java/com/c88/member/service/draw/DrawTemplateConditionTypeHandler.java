package com.c88.member.service.draw;


import com.c88.common.core.constant.RedisConstants;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.member.common.enums.DrawTypeEnum;
import com.c88.member.constants.RedisKey;
import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.vo.DrawStatisticVO;
import com.c88.member.pojo.vo.MemberDrawVO;
import com.c88.member.pojo.vo.MemberTodayCurrentDrawVO;
import com.c88.member.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public abstract class DrawTemplateConditionTypeHandler<T> {

    protected IMemberService iMemberService;

    protected IMemberCoinService iMemberCoinService;

    protected IMemberDrawPoolService iMemberDrawPoolService;

    protected IMemberDrawRecordService iMemberDrawRecordService;

    protected IMemberDrawAwardItemService iMemberDrawAwardItemService;

    protected IMemberRechargeAwardTemplateService iMemberRechargeAwardTemplateService;

    protected KafkaTemplate kafkaTemplate;

    private ObjectMapper objectMapper;

    private RedisTemplate<String, Object> redisTemplate;

    protected RedissonClient redisson;

    protected static final int REVIEW = 1;


    @Autowired
    public final void setMemberRechargeAwardTemplateService(IMemberRechargeAwardTemplateService iMemberRechargeAwardTemplateService) {
        this.iMemberRechargeAwardTemplateService = iMemberRechargeAwardTemplateService;
    }

    @Autowired
    public final void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    public final void setMemberCoinService(IMemberCoinService iMemberCoinService) {
        this.iMemberCoinService = iMemberCoinService;
    }


    @Autowired
    public final void setMemberService(IMemberService iMemberService) {
        this.iMemberService = iMemberService;
    }

    @Autowired
    public final void setRedissonClient(RedissonClient redisson) {
        this.redisson = redisson;
    }

    @Autowired
    public final void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public final void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Autowired
    public final void setMemberDrawAwardItemService(IMemberDrawAwardItemService iMemberDrawAwardItemService) {
        this.iMemberDrawAwardItemService = iMemberDrawAwardItemService;
    }

    @Autowired
    public final void setMemberDrawRecordService(IMemberDrawRecordService iMemberDrawRecordService) {
        this.iMemberDrawRecordService = iMemberDrawRecordService;
    }

    @Autowired
    public final void setMemberDrawPoolService(IMemberDrawPoolService iMemberDrawPoolService) {
        this.iMemberDrawPoolService = iMemberDrawPoolService;
    }

    /**
     * 责任链，下一个链接节点
     */
    protected DrawTemplateConditionTypeHandler<T> next = null;

    public abstract MemberDrawAwardItem doDrawHandler(DrawConditionVO conditionVO);

    public abstract Integer getNextDrawConditionType(DrawConditionVO conditionVO);

    public abstract int getRemainingFreeDraw(DrawConditionVO conditionVO);

    public void next(DrawTemplateConditionTypeHandler handler) {
        this.next = handler;
    }

    protected void setMemberTodayCurrentDrawVO(Long memberId, MemberTodayCurrentDrawVO memberTodayCurrentDrawVO) {
        Map<Object, Object> info = objectMapper.convertValue(memberTodayCurrentDrawVO, Map.class);
        String key = RedisKey.MEMBER_TODAY_DRAW + ":" + memberId;
        redisTemplate.opsForHash().putAll(key, info);
    }

    protected MemberDrawVO doDraw(long templateId) {
        String key = RedisUtils.buildKey(RedisConstants.DRAW_LOCK);
        RLock lock = redisson.getLock(key);
        //未中獎獎項
        MemberDrawAwardItem defalutAwardItem = iMemberDrawAwardItemService.getById(1);
        MemberDrawVO defaultDrawVO = MemberDrawVO.builder()
                .templateId(templateId)
                .drawType(DrawTypeEnum.COMMON_DRAW.getValue())
                .awardId(defalutAwardItem.getId())
                .prizeCount(0)
                .prizeToday(0)
                .percent(0)
                .itemName(defalutAwardItem.getName())
                .awardType(defalutAwardItem.getAwardType())
//                    .sort(0)
                .dailyNumber(defalutAwardItem.getDailyNumber())
                .totalNumber(defalutAwardItem.getTotalNumber()).build();
        try {
            lock.tryLock(10, 5, TimeUnit.SECONDS);
            List<MemberDrawVO> pools = iMemberDrawPoolService.getDrawPoolsByTemplateId(templateId).stream().filter(m -> m.getEnable() == 1).collect(Collectors.toList());
            Map<Long, Integer> allStatistics = Optional.ofNullable(iMemberDrawRecordService.getDrawResultByTemplateStatistics(templateId)).orElse(new ArrayList<DrawStatisticVO>())
                    .stream().collect(Collectors.toMap(DrawStatisticVO::getAwardId, DrawStatisticVO::getTotal));
            Map<Long, Integer> todayStatistics = Optional.ofNullable(iMemberDrawRecordService.getDrawResultTodayByTemplateStatistics(templateId))
                    .orElse(new ArrayList<DrawStatisticVO>())
                    .stream().collect(Collectors.toMap(DrawStatisticVO::getAwardId, DrawStatisticVO::getTotal));

            Map<Integer, MemberDrawVO> map = new HashMap<>();
            for (int i = 0; i < pools.size(); i++) {
                map.put(i, pools.get(i));
            }
            List<Double> originalRates = new ArrayList<>(pools.size());
            for (MemberDrawVO pool : pools) {
                //如果獎項不啟用，機率改為0
                if (pool.getEnable() == 0 || pool.getItemEnable() == 0 ||
                        //如果設為無限制，則檢查獎品本身數量限制
                        (pool.getPrizeToday() == 0 && (pool.getDailyNumber() != -1 && Optional.ofNullable(todayStatistics.get(pool.getAwardId())).orElse(0) >= pool.getDailyNumber())) ||
                        //如果今天次數達到，機率改為0
                        (pool.getPrizeToday() != 0 && (pool.getPrizeToday() - (Optional.ofNullable(todayStatistics.get(pool.getAwardId())).orElse(0)) <= 0)) ||
                        //如果設為無限制，則檢查獎品本身數量限制
                        (pool.getPrizeCount() == 0 && (pool.getTotalNumber() != -1 && Optional.ofNullable(allStatistics.get(pool.getAwardId())).orElse(0) >= pool.getTotalNumber())) ||
                        //如果累積次數達到，機率改為0
                        (pool.getPrizeCount() != 0 && (pool.getPrizeCount() - (Optional.ofNullable(allStatistics.get(pool.getAwardId())).orElse(0)) <= 0))
                ) {
                    pool.setPercent(0);
                }
                originalRates.add(pool.getPercent() * 0.01);
            }

            int originalIndex = LotteryUtil.lottery(originalRates);
            return Optional.ofNullable(map.get(originalIndex)).orElse(defaultDrawVO);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return defaultDrawVO;
        } finally {
            lock.unlock();
        }
    }


    public static class Builder<T> {
        private DrawTemplateConditionTypeHandler<T> head;
        private DrawTemplateConditionTypeHandler<T> tail;

        public Builder<T> addHandler(DrawTemplateConditionTypeHandler handler) {
            if (this.head == null) {
                this.head = handler;
                this.tail = handler;
            } else {
                this.tail.next(handler);
                this.tail = handler;
            }
            return this;
        }

        public DrawTemplateConditionTypeHandler build() {
            return this.head;
        }

        public void clean() {
            boolean b1 = this.head == null;
            boolean b2 = this.tail == null;
        }
    }

}
