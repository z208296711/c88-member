package com.c88.member.service.impl;

import com.c88.common.core.enums.EnableEnum;
import com.c88.common.core.enums.ReviewEnum;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.common.web.exception.BizException;
import com.c88.game.adapter.api.GameFeignClient;
import com.c88.member.common.enums.*;
import com.c88.member.constants.RedisKey;
import com.c88.member.pojo.entity.*;
import com.c88.member.pojo.form.ReceiveChineseCabbageForm;
import com.c88.member.pojo.vo.H5ChineseCabbageStateVO;
import com.c88.member.service.*;
import com.c88.payment.client.MemberBankClient;
import com.c88.payment.client.MemberRechargeClient;
import com.c88.payment.dto.AddBalanceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.c88.common.core.constant.RedisConstants.CHINESE_CABBAGE_LOCK;
import static com.c88.common.core.constant.RedisConstants.RED_ENVELOPE_CODE_LOCK;
import static com.c88.common.core.constant.TopicConstants.BALANCE_CHANGE;
import static com.c88.common.core.enums.BalanceChangeTypeLinkEnum.CHINESE_CABBAGE_RED_ENVELOPE;
import static com.c88.common.core.enums.BalanceChangeTypeLinkEnum.RED_ENVELOPE_CODE;
import static com.c88.common.core.result.ResultCode.*;
import static com.c88.member.constants.RedisKey.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRedEnvelopeServiceImpl implements IMemberRedEnvelopeService {

    private final IMemberRedEnvelopeCodeService iMemberRedEnvelopeCodeService;

    private final IMemberRedEnvelopeRecordService iMemberRedEnvelopeRecordService;

    private final IMemberRedEnvelopeTemplateService iMemberRedEnvelopeTemplateService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final MemberRechargeClient memberRechargeClient;

    private final MemberBankClient memberBankClient;

    private final GameFeignClient gameFeignClient;

    private final IMemberService iMemberService;

    private final RedissonClient redissonClient;

    private final IMemberTagService iMemberTagService;

    private final IMemberRedEnvelopeTemplateConditionService iMemberRedEnvelopeTemplateConditionService;

    private final IMemberAssociationRateService iMemberAssociationRateService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public List<H5ChineseCabbageStateVO> findChineseCabbageState(Long memberId, String username) {
        // 取的會員
        Member member = Optional.ofNullable(iMemberService.getById(memberId))
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

        // 取得會員VIP
        MemberVip memberVip = iMemberService.getMemberVip(memberId);

        // 取得會員標籤
        Set<Integer> tags = iMemberTagService.lambdaQuery()
                .select(MemberTag::getTagId)
                .eq(MemberTag::getMemberId, memberId)
                .list()
                .stream()
                .map(MemberTag::getTagId)
                .collect(Collectors.toSet());

        // 檢查有無提款方式
        Result<Boolean> memberBankExistResult = memberBankClient.checkMemberBankExist();

        // 取得模組
        LocalDate nowDate = LocalDate.now(ZoneId.of("+7"));
        LocalDateTime nowDateTime = LocalDateTime.now();
        List<MemberRedEnvelopeTemplate> templates = iMemberRedEnvelopeTemplateService.lambdaQuery()
                .eq(MemberRedEnvelopeTemplate::getType, RedEnvelopeTemplateTypeEnum.CHINESE_CABBAGE.getCode())
                .eq(MemberRedEnvelopeTemplate::getEnable, EnableEnum.START.getCode())
                .le(MemberRedEnvelopeTemplate::getStartTime, nowDateTime)
                .ge(MemberRedEnvelopeTemplate::getEndTime, nowDateTime)
                .list();

        templates = templates.stream()
                .filter(template -> Objects.isNull(template.getRegisterStartTime()) || member.getGmtCreate().isAfter(template.getRegisterStartTime()))
                .filter(template -> Objects.isNull(template.getRegisterEndTime()) || member.getGmtCreate().isBefore(template.getRegisterEndTime()))
                .filter(template -> template.getVipIds().contains(memberVip.getCurrentVipId()))
                .filter(template -> CollectionUtils.isEmpty(template.getTagIds()) || template.getTagIds().stream().anyMatch(tags::contains))
                .filter(template -> Objects.equals(template.getValidatedMobile(), ValidEnum.UN_VALID.getValue()) || (Objects.equals(template.getValidatedMobile(), ValidEnum.NEED_VALID.getValue()) && StringUtils.isNotBlank(member.getMobile())))
                .filter(template -> Objects.equals(template.getValidatedLink(), ValidEnum.UN_VALID.getValue()) || (Objects.equals(template.getValidatedLink(), ValidEnum.NEED_VALID.getValue()) && Boolean.FALSE.equals(iMemberAssociationRateService.checkUsernameValue(username))))
                .filter(template -> Objects.equals(template.getValidatedWithdraw(), ValidEnum.UN_VALID.getValue()) || (Objects.equals(template.getValidatedWithdraw(), ValidEnum.NEED_VALID.getValue()) && Boolean.TRUE.equals(memberBankExistResult.getData())))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(templates)) {
            return Collections.emptyList();
        }

        // 模組ID
        Set<Long> templateIds = templates.stream()
                .map(MemberRedEnvelopeTemplate::getId).collect(Collectors.toSet());

        // 模組條件
        List<MemberRedEnvelopeTemplateCondition> templateConditions = iMemberRedEnvelopeTemplateConditionService.lambdaQuery()
                .in(MemberRedEnvelopeTemplateCondition::getTemplateId, templateIds)
                .list();

        // 會員領取紀錄
        List<MemberRedEnvelopeRecord> redEnvelopeRecords = iMemberRedEnvelopeRecordService.lambdaQuery()
                .eq(MemberRedEnvelopeRecord::getMemberId, memberId)
                .in(MemberRedEnvelopeRecord::getTemplateId, templateIds)
                .list();

        // 昨日充值金額
        BigDecimal yesterdayRechargeAmount = getYesterdayRechargeAmount(memberId);

        // 今日充值金額
        BigDecimal dayRechargeAmount = getDayRechargeAmount(memberId);

        return templates.stream()
                .map(template -> {
                            // 取得層數
                            Integer level = getLevel(memberId, template.getId());

                            // 取得輪數
                            Integer cycle = getCycle(memberId, template.getId());

                            // 模組條件
                            MemberRedEnvelopeTemplateCondition condition = templateConditions.stream()
                                    .filter(filter -> Objects.equals(filter.getTemplateId(), template.getId()) && Objects.equals(filter.getLevel(), level))
                                    .findFirst()
                                    .orElseThrow();

                            // 領取紀錄
                            Optional<MemberRedEnvelopeRecord> recordOpt = redEnvelopeRecords.stream()
                                    .filter(filter -> Objects.equals(filter.getTemplateId(), template.getId()) && Objects.equals(filter.getLevel(), level) && Objects.equals(filter.getCycle(), cycle))
                                    .findFirst();

                            // 取得用戶在活動期間當前充值金額
                            BigDecimal currentRechargeAmount = BigDecimal.ZERO;
                            Result<BigDecimal> currentRechargeAmountResult = memberRechargeClient.memberTotalRechargeFromTo(
                                    memberId,
                                    template.getStartTime(),
                                    template.getEndTime()
                            );
                            if (Result.isSuccess(currentRechargeAmountResult)) {
                                // 取得金額並減少上一輪的充值金額
                                currentRechargeAmount = currentRechargeAmountResult.getData().subtract(condition.getRechargeAmountTotal().multiply(new BigDecimal(cycle - 1)));
                            }

                            // 取得用戶在活動期間的有效投注金額,並減少上一輪的有效投注金額
                            BigDecimal currentValidBetAmount = getMemberValidBetAmount(memberId, template);
                            currentValidBetAmount = currentValidBetAmount.subtract(condition.getValidBetAmountTotal().multiply(new BigDecimal(cycle - 1)));

                            H5ChineseCabbageStateVO vo = H5ChineseCabbageStateVO.builder()
                                    .id(template.getId())
                                    .name(template.getName())
                                    .amount(condition.getAmount())
                                    .exp(condition.getAmount().multiply(template.getBetRate()))
                                    .isReview(template.getReview())
                                    .currentRechargeAmount(currentRechargeAmount)
                                    .targetRechargeAmount(condition.getRechargeAmount())
                                    .currentValidBetAmount(currentValidBetAmount)
                                    .targetValidBetAmount(condition.getValidBetAmount())
                                    .state(RedEnvelopeChineseCabbageReceiveStateEnum.RECEIVE.getCode())
                                    .build();

                            if (recordOpt.isPresent()) {
                                MemberRedEnvelopeRecord record = recordOpt.get();
                                if (Objects.equals(record.getState(), RedEnvelopeRecordStateEnum.APPROVE.getCode())) {
                                    vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.RECEIVED.getCode());
                                } else if (Objects.equals(record.getState(), RedEnvelopeRecordStateEnum.PENDING_REVIEW.getCode())) {
                                    vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.PENDING_REVIEW.getCode());
                                } else if (Objects.equals(record.getState(), RedEnvelopeRecordStateEnum.REJECT.getCode())) {
                                    vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.REJECT.getCode());
                                }
                            } else {
                                // 只有階級1才需驗證
                                if (level == 1) {
                                    // 紅包數量
                                    Integer redEnvelopNumber = getRedEnvelopNumber(template.getId());

                                    // 今日紅包數量
                                    Integer dailyRedEnvelopeNumber = getDailyRedEnvelopeNumber(nowDate, template.getId());

                                    // 會員紅包累積數量
                                    Integer memberRedEnvelopNumber = getMemberRedEnvelopRecordNumber(template.getId(), memberId);

                                    // 會員紅包單日數量
                                    Integer memberRedEnvelopDailyNumber = getMemberDailyRedEnvelopNumber(nowDate, template.getId(), memberId);

                                    if (yesterdayRechargeAmount.compareTo(template.getYesterdayRechargeAmount()) < 0) {
                                        // 昨日充值要求
                                        vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.YESTERDAY_RECHARGE_AMOUNT_INSUFFICIENT.getCode());
                                    } else if (dayRechargeAmount.compareTo(template.getDayRechargeAmount()) < 0) {
                                        // 當日充值要求
                                        vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.DAY_RECHARGE_AMOUNT_INSUFFICIENT.getCode());
                                    } else if (Boolean.FALSE.equals(checkTotalRechargeAmount(memberId, template))) {
                                        // 檢查累積充值金額
                                        vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.TOTAL_RECHARGE_AMOUNT_INSUFFICIENT.getCode());
                                    } else if (template.getMaxTotal() != 0 && memberRedEnvelopNumber >= template.getMaxTotal()) {
                                        // 會員紅包領取上限
                                        vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.MEMBER_RED_ENVELOPE_LIMIT.getCode());
                                    } else if (template.getDailyMaxTotal() != 0 && memberRedEnvelopDailyNumber >= template.getDailyMaxTotal()) {
                                        // 會員單日紅包領取上限
                                        vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.MEMBER_DAILY_RED_ENVELOPE_LIMIT.getCode());
                                    } else if (template.getRedEnvelopeTotal() != 0 && redEnvelopNumber >= template.getRedEnvelopeTotal()) {
                                        // 紅包領取上限
                                        vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.RED_ENVELOPE_LIMIT.getCode());
                                    } else if (template.getDailyRedEnvelopeTotal() != 0 && dailyRedEnvelopeNumber >= template.getDailyRedEnvelopeTotal()) {
                                        // 紅包單日領取上限
                                        vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.DAILY_RED_ENVELOPE_LIMIT.getCode());
                                    }
                                }

                                // 檢查領取條件
                                if (currentRechargeAmount.compareTo(condition.getRechargeAmount()) < 0 ||
                                        currentValidBetAmount.compareTo(condition.getValidBetAmount()) < 0
                                ) {
                                    vo.setState(RedEnvelopeChineseCabbageReceiveStateEnum.NO_POSITION.getCode());
                                }

                            }

                            return vo;
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean receiveChineseCabbage(Long memberId, String username, ReceiveChineseCabbageForm form) {
        RLock lock = redissonClient.getLock(CHINESE_CABBAGE_LOCK);
        try {
            lock.lock(10, TimeUnit.SECONDS);
            Integer currentLevel = getLevel(memberId, form.getId());
            Integer currentCycle = getCycle(memberId, form.getId());

            // 取得領取條件
            MemberRedEnvelopeTemplateCondition memberRedEnvelopeTemplateCondition = iMemberRedEnvelopeTemplateConditionService.lambdaQuery()
                    .eq(MemberRedEnvelopeTemplateCondition::getTemplateId, form.getId())
                    .eq(MemberRedEnvelopeTemplateCondition::getLevel, currentLevel)
                    .oneOpt()
                    .orElseThrow(() -> new BizException(ResultCode.PARAM_ERROR));

            // 找白菜紅包模組
            MemberRedEnvelopeTemplate template = iMemberRedEnvelopeTemplateService.findById(memberRedEnvelopeTemplateCondition.getTemplateId());

            LocalDate nowDate = LocalDate.now(ZoneId.of("+7"));

            // 第一階才需檢查
            if (currentLevel == 1) {
                // 檢查紅包數量
                if (template.getRedEnvelopeTotal() != 0) {
                    Integer redEnvelopeNumber = getRedEnvelopNumber(template.getId());
                    if (redEnvelopeNumber >= template.getRedEnvelopeTotal()) {
                        throw new BizException(EXCEED_TOTAL_LIMIT);
                    }
                }

                // 檢查每日紅包數量
                if (template.getDailyRedEnvelopeTotal() != 0) {
                    Integer redEnvelopeDailyNumber = getDailyRedEnvelopeNumber(nowDate, template.getId());
                    if (redEnvelopeDailyNumber >= template.getDailyRedEnvelopeTotal()) {
                        throw new BizException(EXCEED_DAILY_TOTAL_LIMIT);
                    }
                }

                // 檢查每人每日領取上限
                if (template.getDailyMaxTotal() != 0) {
                    Integer memberRedEnvelopDailyNumber = getMemberDailyRedEnvelopNumber(nowDate, template.getId(), memberId);
                    if (memberRedEnvelopDailyNumber >= template.getDailyMaxTotal() || currentCycle > template.getDailyMaxTotal()) {
                        throw new BizException(EXCEED_DAILY_TOTAL_LIMIT);
                    }
                }

                // 檢查每人累計領取上限
                if (template.getMaxTotal() != 0) {
                    Integer memberRedEnvelopNumber = getMemberRedEnvelopRecordNumber(template.getId(), memberId);
                    if (memberRedEnvelopNumber >= template.getMaxTotal() || currentCycle > template.getMaxTotal()) {
                        throw new BizException(EXCEED_TOTAL_LIMIT);
                    }
                }

                // 檢查累積充值要求
                if (Boolean.FALSE.equals(checkTotalRechargeAmount(memberId, template))) {
                    throw new BizException(TOTAL_RECHARGE_AMOUNT_INSUFFICIENT);
                }

                // 檢查註冊時間，不輸入時不檢查
                Member member = Optional.ofNullable(iMemberService.getById(memberId)).orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));
                if (Boolean.FALSE.equals(checkRegisterTime(template, member.getGmtCreate()))) {
                    throw new BizException(ResultCode.REGISTER_TIME_OUT);
                }

                // 檢查充值要求
                Result<BigDecimal> rechargeAmountResult = memberRechargeClient.memberTotalRechargeFromTo(
                        memberId,
                        template.getStartTime(),
                        template.getEndTime()
                );

                // 找會員vip，檢查會員VIP等級有無符合
                MemberVip memberVip = iMemberService.getMemberVip(memberId);
                if (!template.getVipIds().contains(memberVip.getCurrentVipId())) {
                    throw new BizException(ResultCode.VIP_VALID_FAIL);
                }

                // 找會員標籤
                List<Integer> tagIds = template.getTagIds();
                if (CollectionUtils.isNotEmpty(tagIds)) {
                    Set<Integer> memberTagIds = iMemberService.getMemberTagIds(memberId);
                    if (memberTagIds.parallelStream().noneMatch(tagIds::contains)) {
                        throw new BizException(ResultCode.TAG_VALID_FAIL);
                    }
                }

                // 需扣除上一輪的總充值要求
                BigDecimal rechargeAmount = memberRedEnvelopeTemplateCondition.getRechargeAmount();
                if (rechargeAmount.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal rechargeAmountTotal = memberRedEnvelopeTemplateCondition.getRechargeAmountTotal();
                    rechargeAmountTotal = rechargeAmountTotal.multiply(new BigDecimal(currentCycle - 1));
                    if (!Result.isSuccess(rechargeAmountResult) || rechargeAmountResult.getData().compareTo(memberRedEnvelopeTemplateCondition.getAmount().add(rechargeAmountTotal)) < 0) {
                        throw new BizException(NOT_ACHIEVE_RECHARGE_AMOUNT);
                    }
                }

                // 取得用戶在活動期間的有效投注金額
                BigDecimal currentValidBetAmount = getMemberValidBetAmount(memberId, template);

                // 需扣除上一輪的總投注要求
                BigDecimal validBetAmountTotal = memberRedEnvelopeTemplateCondition.getValidBetAmountTotal().multiply(new BigDecimal(currentCycle - 1));
                if (currentValidBetAmount.compareTo(memberRedEnvelopeTemplateCondition.getValidBetAmount().add(validBetAmountTotal)) < 0) {
                    throw new BizException(NOT_ACHIEVE_VALID_BET_AMOUNT);
                }
            }

            // 檢查有無領取紀錄
            Optional<MemberRedEnvelopeRecord> memberRedEnvelopeRecordOpt = iMemberRedEnvelopeRecordService.lambdaQuery()
                    .eq(MemberRedEnvelopeRecord::getMemberId, memberId)
                    .eq(MemberRedEnvelopeRecord::getTemplateId, template.getId())
                    .eq(MemberRedEnvelopeRecord::getLevel, currentLevel)
                    .eq(MemberRedEnvelopeRecord::getCycle, currentCycle)
                    .oneOpt();
            if (memberRedEnvelopeRecordOpt.isPresent()) {
                throw new BizException(PARAM_ERROR);
            }

            // 檢查昨日充值量
            if (template.getYesterdayRechargeAmount().compareTo(BigDecimal.ZERO) != 0 &&
                    template.getYesterdayRechargeAmount().compareTo(getYesterdayRechargeAmount(memberId)) > 0) {
                throw new BizException(YESTERDAY_RECHARGE_AMOUNT_INSUFFICIENT);
            }

            // 檢查今日充值量
            if (template.getDayRechargeAmount().compareTo(BigDecimal.ZERO) != 0 &&
                    template.getDayRechargeAmount().compareTo(getDayRechargeAmount(memberId)) > 0) {
                throw new BizException(DAY_RECHARGE_AMOUNT_INSUFFICIENT);
            }

            boolean save = iMemberRedEnvelopeRecordService.save(
                    MemberRedEnvelopeRecord.builder()
                            .memberId(memberId)
                            .username(username)
                            .templateId(template.getId())
                            .type(RedEnvelopeTemplateTypeEnum.CHINESE_CABBAGE.getCode())
                            .name(template.getName())
                            .amount(memberRedEnvelopeTemplateCondition.getAmount())
                            .betRate(template.getBetRate())
                            .level(currentLevel)
                            .cycle(currentCycle)
                            .state(Objects.equals(template.getReview(), ReviewEnum.NOT_REVIEW.getCode()) ? RedEnvelopeRecordStateEnum.APPROVE.getCode() : RedEnvelopeRecordStateEnum.PENDING_REVIEW.getCode())
                            .build()
            );

            // 成功領取紀錄加一層
            if (save) {
                // 紅包累積數量
                Integer redEnvelopNumber = getRedEnvelopNumber(template.getId());

                // 紅包單日數量
                Integer redEnvelopDailyNumber = getDailyRedEnvelopeNumber(nowDate, template.getId());

                // 會員紅包累積數量
                Integer memberRedEnvelopNumber = getMemberRedEnvelopRecordNumber(template.getId(), memberId);

                // 會員紅包單日數量
                Integer memberRedEnvelopDailyNumber = getMemberDailyRedEnvelopNumber(nowDate, template.getId(), memberId);

                if (currentLevel == 1) {
                    redisTemplate.opsForValue().increment(getRedEnvelopeNumberKey(template.getId()));
                    redisTemplate.opsForValue().increment(getDailyRedEnvelopeNumberKey(nowDate, template.getId()));
                    redisTemplate.opsForValue().increment(getMemberRedEnvelopNumberKey(template.getId(), memberId));
                    redisTemplate.opsForValue().increment(getMemberDailyRedEnvelopNumberKey(nowDate, template.getId(), memberId));
                }

                // 0=無限
                if (currentLevel >= memberRedEnvelopeTemplateCondition.getMaxLevel()) {
                    if ((template.getDailyRedEnvelopeTotal() == 0 || template.getDailyRedEnvelopeTotal() > redEnvelopNumber) &&
                            (template.getRedEnvelopeTotal() == 0 || template.getRedEnvelopeTotal() > redEnvelopDailyNumber) &&
                            (template.getMaxTotal() == 0 || template.getMaxTotal() > memberRedEnvelopNumber) &&
                            (template.getDailyMaxTotal() == 0 || template.getDailyMaxTotal() > memberRedEnvelopDailyNumber)) {

                        if (Objects.equals(template.getReview(), ReviewEnum.NOT_REVIEW.getCode())) {
                            String cycleKey = getCycleKey(memberId, template.getId());
                            redisTemplate.opsForValue().set(cycleKey, ++currentCycle);
                            redisTemplate.opsForValue().set(getLevelKey(memberId, template.getId()), 1);
                        }
                    } else {
                        // 存儲每日階層重置

                        redisTemplate.opsForList().leftPush(RedisUtils.buildKey(MEMBER_TEMPLATE_RECEIVE, memberId), memberRedEnvelopeTemplateCondition.getId());
                    }

                } else {
                    redisTemplate.opsForValue().set(getLevelKey(memberId, template.getId()), ++currentLevel);
                }

                // 判斷不審核時直接加值
                if (Objects.equals(template.getReview(), ReviewEnum.NOT_REVIEW.getCode())) {
                    kafkaTemplate.send(BALANCE_CHANGE,
                            AddBalanceDTO.builder()
                                    .memberId(memberId)
                                    .balance(memberRedEnvelopeTemplateCondition.getAmount())
                                    .balanceChangeTypeLinkEnum(CHINESE_CABBAGE_RED_ENVELOPE)
                                    .type(CHINESE_CABBAGE_RED_ENVELOPE.getType())
                                    .betRate(template.getBetRate())
                                    .note(CHINESE_CABBAGE_RED_ENVELOPE.getI18n())
                                    .gmtCreate(LocalDateTime.now())
                                    .build()
                    );

                }
            }

            return save;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public Boolean receiveCode(Long memberId, String username, String code) {
        RLock lock = redissonClient.getLock(RED_ENVELOPE_CODE_LOCK);
        try {
            lock.lock(10, TimeUnit.SECONDS);
            // 檢查有無輸入紀錄
            Optional<MemberRedEnvelopeRecord> memberRedEnvelopeRecordOpt = iMemberRedEnvelopeRecordService.lambdaQuery()
                    .eq(MemberRedEnvelopeRecord::getCode, code)
                    .oneOpt();
            if (memberRedEnvelopeRecordOpt.isPresent()) {
                throw new BizException(ResultCode.USED_RED_ENVELOPE);
            }

            // 找紅包代碼
            MemberRedEnvelopeCode memberRedEnvelopeCode = iMemberRedEnvelopeCodeService.lambdaQuery()
                    .eq(MemberRedEnvelopeCode::getCode, code)
                    .eq(MemberRedEnvelopeCode::getState, RedEnvelopeCodeStateEnum.UNUSED.getCode())
                    .oneOpt()
                    .orElseThrow(() -> new BizException(ResultCode.NOT_RED_ENVELOPE));

            // 找白菜紅包模組
            MemberRedEnvelopeTemplate memberRedEnvelopeTemplate = iMemberRedEnvelopeTemplateService.findById(memberRedEnvelopeCode.getTemplateId());

            // 檢查模組啟用狀態
            if (Objects.equals(memberRedEnvelopeTemplate.getEnable(), EnableEnum.STOP.getCode())) {
                throw new BizException(ResultCode.NOT_USE_RED_ENVELOPE);
            }

            // 找會員
            Member member = Optional.ofNullable(iMemberService.getById(memberId)).orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

            // 找會員vip，檢查會員VIP等級有無符合
            MemberVip memberVip = iMemberService.getMemberVip(memberId);
            if (!memberRedEnvelopeTemplate.getVipIds().contains(memberVip.getCurrentVipId())) {
                throw new BizException(ResultCode.VIP_VALID_FAIL);
            }

            // 找會員標籤
            List<Integer> tagIds = memberRedEnvelopeTemplate.getTagIds();
            if (CollectionUtils.isNotEmpty(tagIds)) {
                Set<Integer> memberTagIds = iMemberService.getMemberTagIds(memberId);
                if (memberTagIds.parallelStream().noneMatch(tagIds::contains)) {
                    throw new BizException(ResultCode.TAG_VALID_FAIL);
                }
            }

            LocalDate nowDate = LocalDate.now(ZoneId.of("+7"));
            LocalDateTime nowDateTime = LocalDateTime.now();

            // 檢查活動時間
            LocalDateTime startTime = memberRedEnvelopeTemplate.getStartTime();
            LocalDateTime endTime = memberRedEnvelopeTemplate.getEndTime();
            if (!(startTime.isBefore(nowDateTime) && endTime.isAfter(nowDateTime))) {
                throw new BizException(ResultCode.ACTIVITY_TIME_OUT);
            }

            // 檢查註冊時間，不輸入時不檢查
            if (Boolean.FALSE.equals(checkRegisterTime(memberRedEnvelopeTemplate, member.getGmtCreate()))) {
                throw new BizException(ResultCode.REGISTER_TIME_OUT);
            }

            // 檢查昨日充值要求
            if (memberRedEnvelopeTemplate.getYesterdayRechargeAmount().compareTo(BigDecimal.ZERO) != 0 &&
                    getYesterdayRechargeAmount(memberId).compareTo(memberRedEnvelopeTemplate.getYesterdayRechargeAmount()) < 0) {
                throw new BizException(YESTERDAY_RECHARGE_AMOUNT_INSUFFICIENT);
            }

            // 檢查當日充值要求
            if (memberRedEnvelopeTemplate.getDayRechargeAmount().compareTo(BigDecimal.ZERO) != 0 &&
                    getDayRechargeAmount(memberId).compareTo(memberRedEnvelopeTemplate.getDayRechargeAmount()) < 0) {
                throw new BizException(DAY_RECHARGE_AMOUNT_INSUFFICIENT);
            }

            // 檢查累積充值
            if (Boolean.FALSE.equals(checkTotalRechargeAmount(memberId, memberRedEnvelopeTemplate))) {
                throw new BizException(ResultCode.TOTAL_RECHARGE_AMOUNT_INSUFFICIENT);
            }

            // 驗證手機要求
            if (Objects.equals(memberRedEnvelopeTemplate.getValidatedMobile(), 1) && StringUtils.isBlank(member.getMobile())) {
                throw new BizException(ResultCode.NEED_PHONE);
            }

            // 拒絕關聯帳戶
            if (Objects.equals(memberRedEnvelopeTemplate.getValidatedLink(), 1) && Boolean.TRUE.equals(iMemberAssociationRateService.checkUsernameValue(username))) {
                throw new BizException(ResultCode.LINK_PROBLEM_USERNAME);
            }

            // 是否需要綁定提款方式
            if (Objects.equals(memberRedEnvelopeTemplate.getValidatedWithdraw(), 1)) {
                Result<Boolean> booleanResult = memberBankClient.checkMemberBankExist();
                if (!Result.isSuccess(booleanResult) || Boolean.FALSE.equals(booleanResult.getData())) {
                    throw new BizException(ResultCode.NEED_WITHDRAW_CARD);
                }
            }

            // 檢查每日領取上限
            String memberDailyMaxTotalKey = RedisUtils.buildKey(MEMBER_DAILY_MAX_TOTAL, memberRedEnvelopeTemplate.getId(), nowDate, memberId);
            Integer dailyMaxTotal = (Integer) redisTemplate.opsForValue().get(memberDailyMaxTotalKey);
            dailyMaxTotal = dailyMaxTotal == null ? 0 : dailyMaxTotal;
            if (memberRedEnvelopeTemplate.getDailyMaxTotal() != 0 && dailyMaxTotal >= memberRedEnvelopeTemplate.getDailyMaxTotal()) {
                throw new BizException(ResultCode.EXCEED_DAILY_TOTAL_LIMIT);
            }

            // 檢查累積領取上限
            String memberMaxTotalKey = RedisUtils.buildKey(MEMBER_MAX_TOTAL, memberRedEnvelopeTemplate.getId(), memberId);
            Integer maxTotal = (Integer) redisTemplate.opsForValue().get(memberMaxTotalKey);
            maxTotal = maxTotal == null ? 0 : maxTotal;
            if (memberRedEnvelopeTemplate.getMaxTotal() != 0 && maxTotal >= memberRedEnvelopeTemplate.getMaxTotal()) {
                throw new BizException(EXCEED_TOTAL_LIMIT);
            }

            BigDecimal amount = memberRedEnvelopeTemplate.getAmount();

            // 寫入領取紀錄
            boolean saveRecord = iMemberRedEnvelopeRecordService.save(
                    MemberRedEnvelopeRecord.builder()
                            .memberId(memberId)
                            .username(username)
                            .templateId(memberRedEnvelopeTemplate.getId())
                            .type(RedEnvelopeTemplateTypeEnum.RED_PACKETS.getCode())
                            .name(memberRedEnvelopeTemplate.getName())
                            .amount(amount)
                            .betRate(memberRedEnvelopeTemplate.getBetRate())
                            .code(code)
                            .state(Objects.equals(memberRedEnvelopeTemplate.getReview(), ReviewEnum.NOT_REVIEW.getCode()) ? RedEnvelopeRecordStateEnum.APPROVE.getCode() : RedEnvelopeRecordStateEnum.PENDING_REVIEW.getCode())
                            .build()
            );

            iMemberRedEnvelopeCodeService.lambdaUpdate()
                    .eq(MemberRedEnvelopeCode::getId, memberRedEnvelopeCode.getId())
                    .set(MemberRedEnvelopeCode::getMemberId, memberId)
                    .set(MemberRedEnvelopeCode::getUsername, username)
                    .set(MemberRedEnvelopeCode::getTemplateId, memberRedEnvelopeTemplate.getId())
                    .set(MemberRedEnvelopeCode::getState, RedEnvelopeCodeStateEnum.USED.getCode())
                    .update();

            // 判斷不審核時直接加值
            if (saveRecord && Objects.equals(memberRedEnvelopeTemplate.getReview(), ReviewEnum.NOT_REVIEW.getCode())) {
                kafkaTemplate.send(BALANCE_CHANGE,
                        AddBalanceDTO.builder()
                                .memberId(memberId)
                                .balance(amount)
                                .balanceChangeTypeLinkEnum(RED_ENVELOPE_CODE)
                                .type(RED_ENVELOPE_CODE.getType())
                                .betRate(memberRedEnvelopeTemplate.getBetRate())
                                .note(RED_ENVELOPE_CODE.getI18n())
                                .gmtCreate(LocalDateTime.now())
                                .build()
                );
            }

            // 增加每人每日領取上限
            redisTemplate.opsForValue().increment(memberDailyMaxTotalKey);

            // 增加每人累積領取上限
            redisTemplate.opsForValue().increment(memberMaxTotalKey);

            return Objects.equals(memberRedEnvelopeTemplate.getReview(), ReviewEnum.NEED_REVIEW.getCode()) && saveRecord ? null : saveRecord;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 檢查註冊時間
     *
     * @param template     模組
     * @param registerTime 註冊時間
     */
    private Boolean checkRegisterTime(MemberRedEnvelopeTemplate template, LocalDateTime registerTime) {
        LocalDateTime registerStartTime = Objects.isNull(template.getRegisterStartTime()) ? LocalDateTime.MIN : template.getRegisterStartTime();
        LocalDateTime registerEndTime = Objects.isNull(template.getRegisterEndTime()) ? LocalDateTime.MAX : template.getRegisterEndTime();
        if (!(registerStartTime.isBefore(registerTime) && registerEndTime.isAfter(registerTime))) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * 檢查累積充值
     *
     * @param memberId                  會員ID
     * @param memberRedEnvelopeTemplate 白菜紅包模組
     * @return ture達標 false未達標
     */
    private Boolean checkTotalRechargeAmount(Long memberId, MemberRedEnvelopeTemplate memberRedEnvelopeTemplate) {
        BigDecimal totalRechargeAmount = memberRedEnvelopeTemplate.getTotalRechargeAmount();
        if (totalRechargeAmount.compareTo(BigDecimal.ZERO) != 0) {
            LocalDateTime totalRechargeStartTime = memberRedEnvelopeTemplate.getTotalRechargeStartTime();
            LocalDateTime totalRechargeEndTime = memberRedEnvelopeTemplate.getTotalRechargeEndTime();
            Result<BigDecimal> totalRechargeResult = memberRechargeClient.memberTotalRechargeFromTo(memberId,
                    totalRechargeStartTime,
                    totalRechargeEndTime
            );
            if (!Result.isSuccess(totalRechargeResult) || totalRechargeResult.getData().compareTo(totalRechargeAmount) < 0) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    /**
     * 取得輪數
     *
     * @param memberId   會員ID
     * @param templateId 白菜紅包模組ID
     * @return
     */
    public Integer getCycle(Long memberId, Long templateId) {
        Integer cycle = (Integer) redisTemplate.opsForValue().get(getCycleKey(memberId, templateId));
        return Objects.isNull(cycle) ? 1 : cycle;
    }

    /**
     * 取得階層
     *
     * @param memberId   會員ID
     * @param templateId 白菜紅包模組ID
     * @return
     */
    @Override
    public Integer getLevel(Long memberId, Long templateId) {
        Integer level = (Integer) redisTemplate.opsForValue().get(getLevelKey(memberId, templateId));
        return Objects.isNull(level) ? 1 : level;
    }

    /**
     * 取得會員流水
     *
     * @param memberId 會員ID
     * @param template 模組
     * @return
     */
    private BigDecimal getMemberValidBetAmount(Long memberId, MemberRedEnvelopeTemplate template) {
        if (CollectionUtils.isEmpty(template.getAssignPlatformGame())) {
            BigDecimal validBetAmount = BigDecimal.ZERO;
            Result<BigDecimal> currentValidBetAmountResult = gameFeignClient.getMemberValidBet(
                    memberId,
                    null,
                    null,
                    null,
                    template.getStartTime(),
                    template.getEndTime()
            );
            if (Result.isSuccess(currentValidBetAmountResult)) {
                validBetAmount = currentValidBetAmountResult.getData();
            }
            return validBetAmount;
        }

        return template.getAssignPlatformGame()
                .stream()
                .reduce(BigDecimal.ZERO, (sumPlatformAmount, gameVO) ->
                                gameVO.getPlatformCodes()
                                        .stream()
                                        .reduce(BigDecimal.ZERO, (sumGameCategoryAmount, platformCode) -> {
                                                    BigDecimal validBetAmount = BigDecimal.ZERO;
                                                    Result<BigDecimal> currentValidBetAmountResult = gameFeignClient.getMemberValidBet(
                                                            memberId,
                                                            platformCode,
                                                            null,
                                                            gameVO.getGameCategoryCode(),
                                                            template.getStartTime(),
                                                            template.getEndTime()
                                                    );
                                                    if (Result.isSuccess(currentValidBetAmountResult)) {
                                                        validBetAmount = currentValidBetAmountResult.getData();
                                                    }
                                                    return validBetAmount;
                                                }, BigDecimal::add
                                        ),
                        BigDecimal::add
                );
    }

    /**
     * 此模組每日紅包使用數量
     *
     * @param nowDate    目前日期
     * @param templateId 模組ID
     * @return 已使用數量
     */
    public Integer getDailyRedEnvelopeNumber(LocalDate nowDate, Long templateId) {
        String redEnvelopeDailyNumberKey = getDailyRedEnvelopeNumberKey(nowDate, templateId);
        Integer redEnvelopeDailyNumber = (Integer) redisTemplate.opsForValue().get(redEnvelopeDailyNumberKey);
        return Objects.isNull(redEnvelopeDailyNumber) ? 0 : redEnvelopeDailyNumber;
    }

    /**
     * 模組每日紅包使用數量KEY
     *
     * @param nowDate    目前日期
     * @param templateId 模組ID
     * @return
     */
    public String getDailyRedEnvelopeNumberKey(LocalDate nowDate, Long templateId) {
        return RedisUtils.buildKey(RED_ENVELOPE_DAILY_NUMBER, nowDate, templateId);
    }

    /**
     * 此模組紅包已使用的數量
     *
     * @param templateId 模組ID
     * @return 已使用數量
     */
    public Integer getRedEnvelopNumber(Long templateId) {
        String redEnvelopeNumberKey = getRedEnvelopeNumberKey(templateId);
        Integer redEnvelopeNumber = (Integer) redisTemplate.opsForValue().get(redEnvelopeNumberKey);
        return Objects.isNull(redEnvelopeNumber) ? 0 : redEnvelopeNumber;
    }

    /**
     * 模組紅包已使用的數量Key
     *
     * @param templateId 模組ID
     * @return
     */
    public String getRedEnvelopeNumberKey(Long templateId) {
        return RedisUtils.buildKey(RED_ENVELOPE_NUMBER, templateId);
    }

    /**
     * 會員目前在此模組單日使用紅包數量
     *
     * @param nowDate    目前日期
     * @param templateId 模組ID
     * @param memberId   會員ID
     * @return 已使用數量
     */
    public Integer getMemberDailyRedEnvelopNumber(LocalDate nowDate, Long templateId, Long memberId) {
        String memberRedEnvelopDailyNumberKey = getMemberDailyRedEnvelopNumberKey(nowDate, templateId, memberId);
        Integer memberRedEnvelopDailyNumber = (Integer) redisTemplate.opsForValue().get(memberRedEnvelopDailyNumberKey);
        return Objects.isNull(memberRedEnvelopDailyNumber) ? 0 : memberRedEnvelopDailyNumber;
    }

    /**
     * 會員目前在此模組單日使用紅包數量Key
     *
     * @param nowDate    目前日期
     * @param templateId 模組ID
     * @param memberId   會員ID
     * @return
     */
    public String getMemberDailyRedEnvelopNumberKey(LocalDate nowDate, Long templateId, Long memberId) {
        return RedisUtils.buildKey(MEMBER_RED_ENVELOPE_DAILY_NUMBER, nowDate, templateId, memberId);
    }

    /**
     * 會員目前在此模組使用的紅包數量
     *
     * @param templateId 模組ID
     * @param memberId   會員ID
     * @return 已使用數量
     */
    public Integer getMemberRedEnvelopRecordNumber(Long templateId, Long memberId) {
        String memberRedEnvelopNumberKey = getMemberRedEnvelopNumberKey(templateId, memberId);
        Integer memberRedEnvelopNumber = (Integer) redisTemplate.opsForValue().get(memberRedEnvelopNumberKey);
        return Objects.isNull(memberRedEnvelopNumber) ? 0 : memberRedEnvelopNumber;
    }

    /**
     * 會員目前在此模組使用的紅包數量Key
     *
     * @param templateId 模組ID
     * @param memberId   會員ID
     * @return
     */
    public String getMemberRedEnvelopNumberKey(Long templateId, Long memberId) {
        return RedisUtils.buildKey(MEMBER_RED_ENVELOPE_NUMBER, templateId, memberId);
    }

    /**
     * 取得會員目前模組階層Key
     *
     * @param memberId   會員ID
     * @param templateId 模組ID
     * @return
     */
    public String getLevelKey(Long memberId, Long templateId) {
        return RedisUtils.buildKey(RedisKey.MEMBER_RED_ENVELOPE_TEMPLATE_LEVEL, templateId, memberId);
    }

    /**
     * 取得會員目前模組圈數Key
     *
     * @param memberId   會員ID
     * @param templateId 模組ID
     * @return
     */
    public String getCycleKey(Long memberId, Long templateId) {
        return RedisUtils.buildKey(RedisKey.MEMBER_RED_ENVELOPE_TEMPLATE_CYCLE, templateId, memberId);
    }

    /**
     * 取得昨日充值金額
     *
     * @param memberId 會員ID
     * @return
     */
    private BigDecimal getYesterdayRechargeAmount(Long memberId) {
        LocalDate nowDate = LocalDate.now(ZoneId.of("+7"));
        BigDecimal yesterdayRechargeAmount = BigDecimal.ZERO;
        Result<BigDecimal> yesterdayRechargeAmountResult = memberRechargeClient.memberTotalRechargeFromTo(memberId,
                LocalDateTime.of(nowDate.minusDays(1), LocalTime.MIN),
                LocalDateTime.of(nowDate.minusDays(1), LocalTime.MAX)
        );
        if (Result.isSuccess(yesterdayRechargeAmountResult)) {
            yesterdayRechargeAmount = yesterdayRechargeAmountResult.getData();
        }
        return yesterdayRechargeAmount;
    }

    /**
     * 取得今日充值金額
     *
     * @param memberId 會員ID
     * @return
     */
    private BigDecimal getDayRechargeAmount(Long memberId) {
        LocalDate nowDate = LocalDate.now(ZoneId.of("+7"));
        BigDecimal dayRechargeAmount = BigDecimal.ZERO;
        Result<BigDecimal> dayRechargeAmountResult = memberRechargeClient.memberTotalRechargeFromTo(memberId,
                LocalDateTime.of(nowDate, LocalTime.MIN),
                LocalDateTime.of(nowDate, LocalTime.MAX)
        );
        if (Result.isSuccess(dayRechargeAmountResult)) {
            dayRechargeAmount = dayRechargeAmountResult.getData();
        }
        return dayRechargeAmount;
    }

}
