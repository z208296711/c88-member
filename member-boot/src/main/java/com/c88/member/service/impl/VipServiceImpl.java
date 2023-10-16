package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.common.core.constant.RedisConstants;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.core.vo.BetRecordVo;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.UserUtils;
import com.c88.feign.RiskFeignClient;
import com.c88.game.adapter.api.GameFeignClient;
import com.c88.member.common.enums.LevelUpModeEnum;
import com.c88.member.common.enums.LevelUpReviewEnum;
import com.c88.member.common.enums.LevelUpTypeEnum;
import com.c88.member.common.enums.VipLevelRecordStateEnum;
import com.c88.member.pojo.entity.MemberVip;
import com.c88.member.pojo.entity.MemberVipConfig;
import com.c88.member.pojo.entity.MemberVipLevelRecord;
import com.c88.member.pojo.form.UpdateMemberVipForm;
import com.c88.member.service.*;
import com.c88.payment.client.MemberRechargeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VipServiceImpl implements IVipService {

    private final IMemberVipConfigService iMemberVipConfigService;

    private final IMemberVipLevelRecordService iMemberVipLevelRecordService;

    private final IMemberVipService iMemberVipService;

    private final IMemberService iMemberService;

    private final MemberRechargeClient memberRechargeClient;

    private final RiskFeignClient riskFeignClient;

    private final GameFeignClient gameFeignClient;

    private final RedissonClient redissonClient;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Integer VIP_0 = 1;

    private static final int PAGE_SIZE = 1000;

    /**
     * 升降級紀錄-手動降級
     */
    @Override
    @Transactional
    public Boolean levelDown(List<Long> memberVipRecordIdList) {
        List<MemberVipLevelRecord> recordList = iMemberVipLevelRecordService.lambdaQuery()
                .in(MemberVipLevelRecord::getId, memberVipRecordIdList)
                .list();

        Map<Long, MemberVip> memberVipMap = iMemberVipService.lambdaQuery()
                .in(MemberVip::getMemberId, recordList.stream().map(MemberVipLevelRecord::getMemberId).collect(Collectors.toList()))
                .list()
                .stream()
                .collect(Collectors.toMap(MemberVip::getMemberId, Function.identity()));

        recordList.forEach(memberVipLevelRecord -> {
            MemberVip memberVip = memberVipMap.getOrDefault(memberVipLevelRecord.getMemberId(), null);
            if (memberVip == null) {
                return;
            }
            memberVip.setCurrentVipId(memberVipLevelRecord.getTargetVipId());
            memberVip.setPreviousVipId(memberVipLevelRecord.getOriginVipId());
            memberVip.setLevelDownTime(LocalDateTime.now());
            iMemberVipService.updateById(memberVip);
        });

        return iMemberVipLevelRecordService.lambdaUpdate()
                .in(MemberVipLevelRecord::getId, memberVipRecordIdList)
                .set(MemberVipLevelRecord::getState, VipLevelRecordStateEnum.LEVEL_DOWN.getValue())
                .set(MemberVipLevelRecord::getLevelChangeTime, LocalDateTime.now())
                .set(MemberVipLevelRecord::getApproveTime, LocalDateTime.now())
                .set(StringUtils.isNotBlank(UserUtils.getUsername()), MemberVipLevelRecord::getApproveBy, UserUtils.getUsername())
                .update();
    }

    /**
     * 升降級紀錄-手動保級
     */
    @Override
    @Transactional
    public Boolean levelKeep(List<Long> memberVipRecordIdList) {
        return iMemberVipLevelRecordService.lambdaUpdate()
                .in(MemberVipLevelRecord::getId, memberVipRecordIdList)
                .set(MemberVipLevelRecord::getState, VipLevelRecordStateEnum.KEEP_LEVEL.getValue())
                .set(MemberVipLevelRecord::getApproveTime, LocalDateTime.now())
                .set(MemberVipLevelRecord::getApproveBy, UserUtils.getUsername())
                .update();
    }

    /**
     * 手動升降級
     */
    @Override
    @Transactional
    public boolean doManualVipLevelUpDownAction(UpdateMemberVipForm form) {
        MemberVip memberVip = iMemberService.getMemberVip(form.getMemberId());
        //倒序由大到小
        List<MemberVipConfig> memberVipConfigList = iMemberVipConfigService.findAllVipConfig();
        Map<Integer, MemberVipConfig> memberVipConfigMap = memberVipConfigList.stream().collect(Collectors.toMap(MemberVipConfig::getId, Function.identity()));

        MemberVipConfig currentVipConfig = memberVipConfigMap.get(memberVip.getCurrentVipId());
        MemberVipConfig targetVipConfig = memberVipConfigMap.get(form.getVipId());

        Result<BigDecimal> periodRechargeResult = memberRechargeClient.memberTotalRechargeFromTo(form.getMemberId(),
                LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())), LocalTime.MIN),
                LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth())), LocalTime.MAX));

        if (!Result.isSuccess(periodRechargeResult)) {
            log.warn("can not get periodRechargeResult from Payment");
            throw new BizException(ResultCode.INTERNAL_SERVICE_CALLEE_ERROR);
        }

        Result<BetRecordVo> betRecordVoResult = riskFeignClient.betRecord(memberVip.getUsername(), false);
        if (!Result.isSuccess(betRecordVoResult)) {
            log.warn("can not get BetRecordVo from riskService");
            throw new BizException(ResultCode.INTERNAL_SERVICE_CALLEE_ERROR);
        }

        Integer count = iMemberVipLevelRecordService.lambdaQuery()
                .select(MemberVipLevelRecord::getId)
                .eq(MemberVipLevelRecord::getMemberId, form.getMemberId())
                .eq(MemberVipLevelRecord::getState, VipLevelRecordStateEnum.PRE_APPROVE.getValue())
                .list()
                .size();

        if (count > 0) {
            log.warn("can not get BetRecordVo from riskService");
            throw new BizException(ResultCode.HAVE_APPROVE_RECORD);
        }

        BigDecimal recharge = periodRechargeResult.getData();
        BigDecimal exp = memberVip.getCurrentVipValidBet();//betRecordVoResult.getData() == null ? BigDecimal.ZERO : betRecordVoResult.getData().getValidBetAmount();

        Integer state = currentVipConfig.getLevelUpRecharge().compareTo(targetVipConfig.getLevelUpRecharge()) > 0 ? VipLevelRecordStateEnum.LEVEL_DOWN.getValue() : VipLevelRecordStateEnum.LEVEL_UP.getValue();
        Integer levelUpMode = (targetVipConfig.getLevelUpMode().equals(LevelUpModeEnum.MANUAL) || currentVipConfig.getLevelUpMode().equals(LevelUpModeEnum.MANUAL)) ? 2 : 1;
        if (targetVipConfig.getLevelUpMode().equals(LevelUpModeEnum.MANUAL)) {
            state = VipLevelRecordStateEnum.LEVEL_UP.getValue();
        } else if (currentVipConfig.getLevelUpMode().equals(LevelUpModeEnum.MANUAL) &&
                targetVipConfig.getLevelUpMode().equals(LevelUpModeEnum.AUTO)) {
            state = VipLevelRecordStateEnum.LEVEL_DOWN.getValue();

        }
        //新增紀錄
        iMemberVipLevelRecordService.save(MemberVipLevelRecord.builder()
                .username(memberVip.getUsername())
                .memberId(memberVip.getMemberId())
                .type(LevelUpTypeEnum.MANUAL.getValue())
                .originVipId(currentVipConfig.getId())
                .originLevel(currentVipConfig.getName())
                .originRecharge(currentVipConfig.getLevelUpRecharge())
                .originExp(currentVipConfig.getLevelUpExp())
                .keepRecharge(BigDecimal.valueOf(currentVipConfig.getKeepRecharge()))
                .keepExp(currentVipConfig.getKeepExp())
                .targetVipId(targetVipConfig.getId())
                .targetLevel(targetVipConfig.getName())
                .targetRecharge(targetVipConfig.getLevelUpRecharge())
                .targetExp(targetVipConfig.getLevelUpExp())
                .state(state)
                .levelUpMode(levelUpMode)
                .levelChangeTime(LocalDateTime.now())
                .approveBy(UserUtils.getUsername())
                .exp(exp)
                .recharge(recharge)
                .build());

        memberVip.setCurrentVipId(targetVipConfig.getId());
        memberVip.setCurrentVipName(targetVipConfig.getName());
        memberVip.setPreviousVipId(currentVipConfig.getId());
        memberVip.setPreviousVipName(currentVipConfig.getName());

        if (state.equals(VipLevelRecordStateEnum.LEVEL_UP.getValue())) {
            memberVip.setLevelUpTime(LocalDateTime.now());
            redisTemplate.opsForValue().set(RedisUtils.buildKey(RedisConstants.VIP_LEVEL_UP_NOTIFICATION, memberVip.getMemberId()), 1);
        } else {
            memberVip.setCurrentVipValidBet(targetVipConfig.getKeepExp());
            memberVip.setLevelDownTime(LocalDateTime.now());
            redisTemplate.delete(RedisUtils.buildKey(RedisConstants.VIP_LEVEL_UP_NOTIFICATION, memberVip.getMemberId()));
        }
        return iMemberVipService.updateById(memberVip);
    }

    /**
     * 登入自動升級判斷
     */
    @Override
    @Transactional
    public void doVipLevelUpAction(Long memberId) {

        String key = RedisUtils.buildKey(RedisConstants.VIP_LEVEL_DOWN_SCHEDULE_LOCK);
        RLock lock = redissonClient.getLock(key);
        if (lock.isLocked()) {
            log.info("doVipLevelUpAction VIP_LEVEL_DOWN_SCHEDULE_LOCK");
            return;
        }

        MemberVip memberVip = iMemberService.getMemberVip(memberId);
        if (memberVip.getLevelUpTime() != null) {
            ZonedDateTime ldtZoned = memberVip.getLevelUpTime().atZone(ZoneId.systemDefault());
            ZonedDateTime utc7Zoned = ldtZoned.withZoneSameInstant(ZoneId.of("+7"));
            if (utc7Zoned.toLocalDate().equals(LocalDate.now(ZoneId.of("+7")))) {
                log.info("doVipLevelUpAction level up in the same day, so do not level up");
                return;
            }
        }

        MemberVipConfig currentVipConfig = iMemberVipConfigService.getById(memberVip.getCurrentVipId());
        if (currentVipConfig.getLevelUpMode().equals(LevelUpModeEnum.MANUAL)) {
            log.info("doVipLevelUpAction do not use:{}", currentVipConfig.getLevelUpMode().getLabel());
            return;
        }

        Result<BigDecimal> periodRechargeResult = memberRechargeClient.memberTotalRechargeFromTo(memberId,
                LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())), LocalTime.MIN),
                LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth())), LocalTime.MAX));

        if (!Result.isSuccess(periodRechargeResult)) {
            log.warn("can not get periodRechargeResult from Payment");
            //金額不符
            return;
        }

//        // 取有效流水
//        Result<BigDecimal> validBetResult = gameFeignClient.getMemberValidBet(memberVip.getMemberId(),
//                null,
//                memberVip.getLevelDownTime(),
//                null);
//        if (!Result.isSuccess(validBetResult)) {
//            log.warn("can not get BetRecordVo from riskService");
//            return;
//        }

        //倒序由小到大
        List<MemberVipConfig> memberVipConfigList = iMemberVipConfigService.findAutoLevelUpVipConfigs().stream().sorted(Comparator.comparing(MemberVipConfig::getLevelUpRecharge)).collect(Collectors.toList());

        Map<Integer, MemberVipConfig> memberVipConfigMap = memberVipConfigList.stream().collect(Collectors.toMap(MemberVipConfig::getId, Function.identity()));

        MemberVipConfig memberVipConfig = memberVipConfigMap.get(memberVip.getCurrentVipId());

        BigDecimal recharge = periodRechargeResult.getData();

        BigDecimal exp = memberVip.getCurrentVipValidBet();

        BigDecimal finalExp = exp;
        memberVipConfigList
                .stream()
                .filter(config -> config.getLevelUpRecharge().compareTo(memberVipConfig.getLevelUpRecharge()) > 0)
                .findFirst()
                .ifPresent(levelVipConfig -> {

                    if (finalExp.compareTo(levelVipConfig.getLevelUpExp()) < 0 ||
                            recharge.compareTo(levelVipConfig.getLevelUpRecharge()) < 0) {
                        log.info("doVipLevelUpAction memberId: {} exp:{}, recharge:{}, 條件不符無法升級", memberId, finalExp, recharge);
                        return;
                    }

                    int previousId = memberVip.getCurrentVipId();
                    String previousName = memberVip.getCurrentVipName();

                    iMemberVipService.lambdaUpdate()
                            .eq(MemberVip::getMemberId, memberId)
                            .set(MemberVip::getPreviousVipId, previousId)
                            .set(MemberVip::getPreviousVipName, previousName)
                            .set(MemberVip::getCurrentVipId, levelVipConfig.getId())
                            .set(MemberVip::getCurrentVipName, levelVipConfig.getName())
                            .set(MemberVip::getLevelUpTime, LocalDateTime.now())
                            .update();

                    //若有以待審核紀錄,則直接變保級
                    iMemberVipLevelRecordService.lambdaQuery()
                            .eq(MemberVipLevelRecord::getMemberId, memberId)
                            .eq(MemberVipLevelRecord::getState, VipLevelRecordStateEnum.PRE_APPROVE.getValue())
                            .oneOpt().ifPresent(memberVipLevelRecord -> {
                                memberVipLevelRecord.setLevelChangeTime(LocalDateTime.now());
                                memberVipLevelRecord.setState(VipLevelRecordStateEnum.KEEP_LEVEL.getValue());
                                memberVipLevelRecord.setApproveBy("System");
                                memberVipLevelRecord.setApproveTime(LocalDateTime.now());
                                iMemberVipLevelRecordService.updateById(memberVipLevelRecord);
                            });

                    //新增紀錄
                    iMemberVipLevelRecordService.save(MemberVipLevelRecord.builder()
                            .username(memberVip.getUsername())
                            .memberId(memberVip.getMemberId())
                            .type(LevelUpTypeEnum.LEVEL_UP.getValue())
                            .originLevel(memberVipConfig.getName())
                            .originRecharge(memberVipConfig.getLevelUpRecharge())
                            .originExp(memberVipConfig.getLevelUpExp())
                            .keepRecharge(BigDecimal.valueOf(memberVipConfig.getKeepRecharge()))
                            .keepExp(memberVipConfig.getKeepExp())
                            .targetLevel(levelVipConfig.getName())
                            .targetRecharge(levelVipConfig.getLevelUpRecharge())
                            .targetExp(levelVipConfig.getLevelUpExp())
                            .state(VipLevelRecordStateEnum.LEVEL_UP.getValue())
                            .levelChangeTime(LocalDateTime.now())
                            .levelUpMode(LevelUpModeEnum.MANUAL.getValue())
                            .exp(finalExp)
                            .recharge(recharge)
                            .build());

                    redisTemplate.opsForList().rightPush(RedisUtils.buildKey(RedisConstants.VIP_LEVEL_UP_REWARD_NOTIFICATION, memberId), levelVipConfig.getName());
                    redisTemplate.opsForValue().set(RedisUtils.buildKey(RedisConstants.VIP_LEVEL_UP_NOTIFICATION, memberId), 1);
                });
    }

    /**
     * 降級排成程作業
     */
    @Override
    @Transactional
    public void doLevelDownAction() {

        //取出所有自動vip等級
        List<MemberVipConfig> vipConfigList = iMemberVipConfigService.findAutoLevelUpVipConfigs().stream().sorted(Comparator.comparing(MemberVipConfig::getLevelUpRecharge).reversed()).collect(Collectors.toList());

        Map<Integer, MemberVipConfig> vipConfigMap = vipConfigList.stream().collect(Collectors.toMap(MemberVipConfig::getId, Function.identity()));

        //取出兩個月前沒升級的人
        LocalDateTime memberLevelUpTime = LocalDateTime.of(LocalDate.now(ZoneId.of("+7"))
                .minusMonths(2)
                .with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);

        IPage<MemberVip> memberPage = iMemberVipService.lambdaQuery()
                .in(MemberVip::getCurrentVipId, vipConfigList
                        .stream()
                        .filter(x -> !Objects.equals(x.getVip0(), VIP_0))
                        .map(MemberVipConfig::getId)
                        .collect(Collectors.toList()))
                .le(MemberVip::getLevelUpTime, memberLevelUpTime)
                .page(new Page<>(1, PAGE_SIZE));

        List<Long> preApprovalMemberIdList = iMemberVipLevelRecordService.lambdaQuery()
                .select(MemberVipLevelRecord::getMemberId)
                .eq(MemberVipLevelRecord::getState, VipLevelRecordStateEnum.PRE_APPROVE.getValue())
                .list()
                .stream()
                .map(MemberVipLevelRecord::getMemberId)
                .collect(Collectors.toList());

        while (CollectionUtils.isNotEmpty(memberPage.getRecords())) {
            List<MemberVip> memberPageRecords = memberPage.getRecords();

            List<MemberVipLevelRecord> memberVipLevelRecordList = this.convertMemberVipLevelRecords(vipConfigList,
                    vipConfigMap,
                    memberPageRecords,
                    preApprovalMemberIdList);
            iMemberVipLevelRecordService.saveBatch(memberVipLevelRecordList);

            Map<Long, MemberVipLevelRecord> memberVipLevelRecordMap = memberVipLevelRecordList
                    .stream()
                    .filter(memberPageRecord -> memberPageRecord.getState().equals(VipLevelRecordStateEnum.LEVEL_DOWN.getValue()))
                    .collect(Collectors.toMap(MemberVipLevelRecord::getMemberId, Function.identity()));

            this.vipLevelDown(memberPageRecords, memberVipLevelRecordMap, vipConfigMap);

            memberPage = iMemberVipService.lambdaQuery()
                    .in(MemberVip::getCurrentVipId, vipConfigList
                            .stream()
                            .filter(x -> !Objects.equals(x.getVip0(), VIP_0))
                            .map(MemberVipConfig::getId)
                            .collect(Collectors.toList()))
                    .le(MemberVip::getLevelUpTime, memberLevelUpTime)
                    .page(new Page<>(memberPage.getPages() + 1, PAGE_SIZE));
        }
    }

    private List<MemberVipLevelRecord> convertMemberVipLevelRecords(List<MemberVipConfig> vipConfigList,
                                                                    Map<Integer, MemberVipConfig> vipConfigMap,
                                                                    List<MemberVip> memberPageRecords,
                                                                    List<Long> preApprovalMemberIdList) {
        return memberPageRecords.stream()
                .map(memberVip -> {
                    MemberVipConfig currentVipConfig = vipConfigMap.get(memberVip.getCurrentVipId());
                    BigDecimal keepRecharge = BigDecimal.valueOf(currentVipConfig.getKeepRecharge());
                    BigDecimal keepExp = currentVipConfig.getKeepExp();

                    // 取有效流水
                    Result<BigDecimal> validBetResult = gameFeignClient.getMemberValidBet(memberVip.getMemberId(),
                            null,
                            null,
                            null,
                            LocalDateTime.of(LocalDate.from(LocalDateTime.now().minusMonths(2).with(TemporalAdjusters.firstDayOfMonth())), LocalTime.MIN),
                            LocalDateTime.of(LocalDate.from(LocalDateTime.now().minusMonths(2).with(TemporalAdjusters.lastDayOfMonth())), LocalTime.MAX));
                    BigDecimal memberExp = BigDecimal.ZERO;
                    if (Result.isSuccess(validBetResult)) {
                        memberExp = validBetResult.getData();
                    }

                    // 取累計充值金額
                    Result<BigDecimal> totalRechargeResult = memberRechargeClient.memberTotalRecharge(memberVip.getMemberId());
                    BigDecimal memberRecharge = BigDecimal.ZERO;
                    if (Result.isSuccess(totalRechargeResult)) {
                        memberRecharge = totalRechargeResult.getData();
                    }

                    if (memberExp.compareTo(keepExp) > 0 && memberRecharge.compareTo(keepRecharge) > 0) {
                        return null;
                    }
                    MemberVipConfig newVipConfig = this.getLevelDownVipConfig(vipConfigList, currentVipConfig);
                    //降級
                    MemberVipLevelRecord memberVipLevelRecord = MemberVipLevelRecord.builder()
                            .username(memberVip.getUsername())
                            .memberId(memberVip.getMemberId())
                            .type(LevelUpTypeEnum.LEVEL_DOWN.getValue())
                            .originVipId(currentVipConfig.getId())
                            .originLevel(currentVipConfig.getName())
                            .originRecharge(currentVipConfig.getLevelUpRecharge())
                            .originExp(currentVipConfig.getLevelUpExp())
                            .keepRecharge(keepRecharge)
                            .keepExp(keepExp)
                            .targetVipId(newVipConfig.getId())
                            .targetLevel(newVipConfig.getName())
                            .targetRecharge(newVipConfig.getLevelUpRecharge())
                            .targetExp(newVipConfig.getLevelUpExp())
                            .state(VipLevelRecordStateEnum.LEVEL_DOWN.getValue())
                            .levelChangeTime(LocalDateTime.now())
                            .exp(memberExp)
                            .recharge(memberRecharge)
                            .build();
                    if (currentVipConfig.getLevelUpReview().equals(LevelUpReviewEnum.NEED_REVIEW)) {
                        memberVipLevelRecord.setState(VipLevelRecordStateEnum.PRE_APPROVE.getValue());
                        memberVipLevelRecord.setLevelChangeTime(null);
                    }
                    return memberVipLevelRecord;
                })
                .filter(Objects::nonNull)
                .filter(x -> !preApprovalMemberIdList.contains(x.getMemberId()))
                .collect(Collectors.toList());
    }

    private void vipLevelDown(List<MemberVip> memberPageRecords,
                              Map<Long, MemberVipLevelRecord> memberVipLevelRecordMap,
                              Map<Integer, MemberVipConfig> vipConfigMap) {
        List<MemberVip> memberVipList = memberPageRecords
                .stream()
                .filter(memberVipInfoVO -> memberVipLevelRecordMap.containsKey(memberVipInfoVO.getMemberId()))
                .map(memberVip -> {
                    MemberVipLevelRecord memberVipLevelRecord = memberVipLevelRecordMap.get(memberVip.getMemberId());
                    memberVip.setCurrentVipId(memberVipLevelRecord.getTargetVipId());
                    memberVip.setCurrentVipName(memberVipLevelRecord.getTargetLevel());
                    memberVip.setPreviousVipId(memberVipLevelRecord.getOriginVipId());
                    memberVip.setPreviousVipName(memberVipLevelRecord.getOriginLevel());
                    memberVip.setCurrentVipValidBet(vipConfigMap.get(memberVipLevelRecord.getTargetVipId()).getKeepExp());
                    memberVip.setLevelDownTime(LocalDateTime.now());
                    return memberVip;
                }).collect(Collectors.toList());
        iMemberVipService.updateBatchById(memberVipList);
    }

    //取得降級VipConfig
    @Override
    public MemberVipConfig getLevelDownVipConfig(List<MemberVipConfig> vipConfigList, MemberVipConfig currentVipConfig) {
        int index = vipConfigList.indexOf(currentVipConfig);
        return vipConfigList.get(index == vipConfigList.size() - 1 ? vipConfigList.size() - 1 : index + 1);
    }

    @Override
    public List<MemberVipConfig> getAllLevelDownVipConfig(MemberVipConfig currentMemberVipConfig) {
        List<MemberVipConfig> memberVipConfigs = iMemberVipConfigService.lambdaQuery()
                .orderByAsc(MemberVipConfig::getLevelUpRecharge)
                .list();

        return memberVipConfigs.stream()
                .limit(memberVipConfigs.indexOf(currentMemberVipConfig) + 1)
                .collect(Collectors.toList());
    }

}
