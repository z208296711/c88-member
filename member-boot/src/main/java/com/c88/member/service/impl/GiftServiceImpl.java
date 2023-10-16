package com.c88.member.service.impl;

import com.c88.common.core.base.BaseEntity;
import com.c88.common.core.constant.RedisConstants;
import com.c88.common.core.enums.BalanceChangeTypeLinkEnum;
import com.c88.common.core.enums.EnableEnum;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.core.util.DateUtil;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.common.web.exception.BizException;
import com.c88.member.common.enums.BirthdayGiftStateEnum;
import com.c88.member.common.enums.FreeBetGiftStateEnum;
import com.c88.member.common.enums.FreeBetGiftTypeEnum;
import com.c88.member.common.enums.LevelUpGiftStateEnum;
import com.c88.member.common.enums.LevelUpModeEnum;
import com.c88.member.common.enums.RedEnvelopeChineseCabbageReceiveStateEnum;
import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.entity.MemberBirthdayGiftRecord;
import com.c88.member.pojo.entity.MemberBirthdayGiftSetting;
import com.c88.member.pojo.entity.MemberFreeBetGiftRecord;
import com.c88.member.pojo.entity.MemberFreeBetGiftSetting;
import com.c88.member.pojo.entity.MemberLevelUpGiftRecord;
import com.c88.member.pojo.entity.MemberLevelUpGiftSetting;
import com.c88.member.pojo.entity.MemberVip;
import com.c88.member.pojo.entity.MemberVipConfig;
import com.c88.member.pojo.entity.MemberVipLevelRecord;
import com.c88.member.pojo.vo.H5ChineseCabbageStateVO;
import com.c88.member.pojo.vo.H5FreeBetGiftVO;
import com.c88.member.pojo.vo.H5GiftNotificationVO;
import com.c88.member.pojo.vo.H5LevelUpGiftVO;
import com.c88.member.pojo.vo.MemberBirthdayGiftVO;
import com.c88.member.service.IGiftService;
import com.c88.member.service.IMemberBirthdayGiftRecordService;
import com.c88.member.service.IMemberBirthdayGiftSettingService;
import com.c88.member.service.IMemberFreeBetGiftRecordService;
import com.c88.member.service.IMemberFreeBetGiftSettingService;
import com.c88.member.service.IMemberLevelUpGiftRecordService;
import com.c88.member.service.IMemberLevelUpGiftSettingService;
import com.c88.member.service.IMemberRedEnvelopeService;
import com.c88.member.service.IMemberService;
import com.c88.member.service.IMemberVipConfigService;
import com.c88.member.service.IMemberVipLevelRecordService;
import com.c88.member.service.IMemberVipService;
import com.c88.payment.client.MemberRechargeClient;
import com.c88.payment.dto.AddBalanceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.c88.common.core.constant.TopicConstants.BALANCE_CHANGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftServiceImpl implements IGiftService {

    private final IMemberService iMemberService;

    private final IMemberVipService iMemberVipService;

    private final IMemberVipConfigService iMemberVipConfigService;

    private final IMemberBirthdayGiftRecordService iMemberBirthdayGiftRecordService;

    private final IMemberBirthdayGiftSettingService iMemberBirthdayGiftSettingService;

    private final IMemberFreeBetGiftRecordService iMemberFreeBetGiftRecordService;

    private final IMemberFreeBetGiftSettingService iMemberFreeBetGiftSettingService;

    private final RedissonClient redissonClient;

    private final IMemberLevelUpGiftRecordService iMemberLevelUpGiftRecordService;

    private final IMemberLevelUpGiftSettingService iMemberLevelUpGiftSettingService;

    private final MemberRechargeClient memberRechargeClient;

    private final IMemberVipLevelRecordService iMemberVipLevelRecordService;

    private final IMemberRedEnvelopeService iMemberRedEnvelopeService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MemberBirthdayGiftVO findMemberBirthdayGift(Long memberId) {
        // 取得會員VIP
        MemberVip memberVip = iMemberVipService.lambdaQuery()
                .eq(MemberVip::getMemberId, memberId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

        // 取得會員VIP設定
        MemberVipConfig memberVipConfig = iMemberVipConfigService.lambdaQuery()
                .eq(MemberVipConfig::getId, memberVip.getCurrentVipId())
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.PARAM_ERROR));

        // 取得生日禮金設定
        MemberBirthdayGiftSetting memberBirthdayGiftSetting = iMemberBirthdayGiftSettingService.lambdaQuery()
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

        // 取得生日禮金領取紀錄
        Member member = iMemberService.getById(memberId);

        MemberBirthdayGiftVO giftVO = MemberBirthdayGiftVO.builder()
                .amount(BigDecimal.valueOf(memberVipConfig.getBirthdayGift()))
                .days(memberBirthdayGiftSetting.getDays())
                .exp(memberBirthdayGiftSetting.getBetRate().multiply(BigDecimal.valueOf(memberVipConfig.getBirthdayGift())).intValue())
                .state(BirthdayGiftStateEnum.NO_POSITION.getValue())
                .build();

        if (Objects.nonNull(member.getBirthday())) {
            LocalDate now = LocalDate.now();

            LocalDate isBirthday = LocalDate.of(now.getYear(), member.getBirthday().getMonth(), member.getBirthday().getDayOfMonth());
            LocalDate startDate = now.minusDays(memberBirthdayGiftSetting.getDays());
            LocalDate endDate = now.plusDays(memberBirthdayGiftSetting.getDays());

            // 領取年份
            String year = now.minusDays(memberBirthdayGiftSetting.getDays()).format(DateTimeFormatter.ofPattern("yyyy"));
            Optional<MemberBirthdayGiftRecord> memberBirthdayGiftRecordOpt = iMemberBirthdayGiftRecordService.lambdaQuery()
                    .eq(MemberBirthdayGiftRecord::getMemberId, memberId)
                    .eq(MemberBirthdayGiftRecord::getReceiveYear, year)
                    .oneOpt();

            // 是否在效期內
            boolean isDay = startDate.compareTo(isBirthday) <= 0 && endDate.compareTo(isBirthday) >= 0;

            // 判斷今年已領取且效期內顯示領取否則判斷只有在效期內則未領取
            if (memberBirthdayGiftRecordOpt.isPresent() && isDay) {
                MemberBirthdayGiftRecord memberBirthdayGiftRecord = memberBirthdayGiftRecordOpt.get();
                giftVO.setState(BirthdayGiftStateEnum.RECEIVED.getValue());
                giftVO.setAmount(memberBirthdayGiftRecord.getAmount());
                giftVO.setExp(memberBirthdayGiftRecord.getExp());
                giftVO.setDays(memberBirthdayGiftRecord.getDay());
            } else if (isDay) {
                giftVO.setState(BirthdayGiftStateEnum.UN_RECEIVE.getValue());
            }

        }
        return giftVO;
    }

    @Transactional
    public Boolean receiveMemberBirthdayGift(Long memberId) {
        String key = RedisUtils.buildKey(RedisConstants.RECEIVE_BIRTHDAY_GIFT, memberId);
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock(10, TimeUnit.SECONDS);
            Member member = iMemberService.getById(memberId);

            // 尚未設定生日，是否前往設置領取獎金
            LocalDate birthday = member.getBirthday();
            if (Objects.isNull(member.getBirthday())) {
                throw new BizException("bonus_birthday.content019");
            }

            // 取得會員VIP
            MemberVip memberVip = iMemberVipService.lambdaQuery()
                    .eq(MemberVip::getMemberId, memberId)
                    .oneOpt()
                    .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

            // 取得會員VIP設定
            MemberVipConfig memberVipConfig = iMemberVipConfigService.lambdaQuery()
                    .eq(MemberVipConfig::getId, memberVip.getCurrentVipId())
                    .oneOpt()
                    .orElseThrow(() -> new BizException(ResultCode.PARAM_ERROR));

            // 已設定生日，但金額為0
            if (memberVipConfig.getBirthdayGift() == 0) {
                throw new BizException("bonus_birthday.content021");
            }

            // 取得生日禮金設定
            MemberBirthdayGiftSetting memberBirthdayGiftSetting = iMemberBirthdayGiftSettingService.lambdaQuery()
                    .oneOpt()
                    .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

            // 判斷今年有無領取過生日禮金
            LocalDate now = LocalDate.now(ZoneId.of("+7"));

            // 領取年份
            String year = now.minusDays(memberBirthdayGiftSetting.getDays()).format(DateTimeFormatter.ofPattern("yyyy"));

            LocalDate isBirthday = LocalDate.of(now.getYear(), birthday.getMonth(), birthday.getDayOfMonth());
            Optional<MemberBirthdayGiftRecord> isReceiveOpt = iMemberBirthdayGiftRecordService.lambdaQuery()
                    .eq(MemberBirthdayGiftRecord::getMemberId, memberId)
                    .and(wrapper ->
                            wrapper.between(BaseEntity::getGmtCreate, isBirthday.minusDays(memberBirthdayGiftSetting.getDays()), isBirthday.plusDays(memberBirthdayGiftSetting.getDays()))
                                    .or()
                                    .eq(MemberBirthdayGiftRecord::getReceiveYear, year)
                    )
                    .oneOpt();

            // 已領取
            if (isReceiveOpt.isPresent()) {
                throw new BizException("bonus_birthday.btn010");
            }

            // 生日禮金
            BigDecimal birthdayGiftAmount = BigDecimal.valueOf(memberVipConfig.getBirthdayGift());

            MemberBirthdayGiftRecord memberBirthdayGiftRecord = MemberBirthdayGiftRecord.builder()
                    .memberId(memberId)
                    .username(member.getUserName())
                    .birthday(member.getBirthday())
                    .receiveYear(year)
                    .vipName(memberVipConfig.getName())
                    .amount(birthdayGiftAmount)
                    .betRate(memberBirthdayGiftSetting.getBetRate().intValue())
                    .exp(birthdayGiftAmount.multiply(memberBirthdayGiftSetting.getBetRate()).intValue())
                    .day(memberBirthdayGiftSetting.getDays())
                    .build();

            iMemberBirthdayGiftRecordService.save(memberBirthdayGiftRecord);

            kafkaTemplate.send(BALANCE_CHANGE, AddBalanceDTO.builder()
                    .memberId(memberId)
                    .balance(birthdayGiftAmount)
                    .balanceChangeTypeLinkEnum(BalanceChangeTypeLinkEnum.BIRTHDAY_GIFT)
                    .type(BalanceChangeTypeLinkEnum.BIRTHDAY_GIFT.getType())
                    .betRate(memberBirthdayGiftSetting.getBetRate())
                    .note(BalanceChangeTypeLinkEnum.BIRTHDAY_GIFT.getI18n())
                    .gmtCreate(LocalDateTime.now())
                    .build());

        } finally {
            lock.unlock();
        }

        return Boolean.TRUE;
    }

    public List<H5FreeBetGiftVO> getFreeBetGiftList(Long memberId) {

        MemberVip memberVip = iMemberService.getMemberVip(memberId);

        List<MemberFreeBetGiftSetting> memberFreeBetGiftSettings = iMemberFreeBetGiftSettingService.findAll();

        List<MemberVipConfig> memberVipConfigList = iMemberVipConfigService.findAllVipConfig();

        MemberVipConfig currentMemberVipConfig = memberVipConfigList
                .stream()
                .filter(x -> x.getId().equals(memberVip.getCurrentVipId()))
                .findFirst()
                .orElse(MemberVipConfig.builder().name("").build());

        // 取的免費籌碼記錄
        LocalDate now = LocalDate.now();
        List<MemberFreeBetGiftRecord> memberFreeBetGiftRecords = iMemberFreeBetGiftRecordService.lambdaQuery()
                .or(wrapper -> wrapper
                        .eq(MemberFreeBetGiftRecord::getMemberId, memberId)
                        .eq(MemberFreeBetGiftRecord::getType, FreeBetGiftTypeEnum.WEEKLY.getValue())
                        .between(BaseEntity::getGmtCreate, now.minusDays(now.getDayOfWeek().getValue()), now.plusDays(7 - now.getDayOfWeek().getValue()))
                )
                .or(wrapper -> wrapper
                        .eq(MemberFreeBetGiftRecord::getMemberId, memberId)
                        .eq(MemberFreeBetGiftRecord::getType, FreeBetGiftTypeEnum.MONTHLY.getValue())
                        .between(BaseEntity::getGmtCreate, LocalDate.of(now.getYear(), now.getMonth(), 1), LocalDate.of(now.getYear(), now.getMonth(), now.lengthOfMonth()))
                )
                .list();

        // 取的上週的充值金額
        LocalDate lastWeekDate = now.minusWeeks(1);
        BigDecimal weekAmount = BigDecimal.ZERO;
        Result<BigDecimal> weekAmountResult = memberRechargeClient.memberTotalRechargeFromTo(
                memberId,
                LocalDateTime.of(lastWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), LocalTime.MIN),
                LocalDateTime.of(lastWeekDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)), LocalTime.MAX)
        );
        if (Result.isSuccess(weekAmountResult)) {
            weekAmount = weekAmountResult.getData();
        }

        // 取得當月的充值金額
        BigDecimal monthAmount = BigDecimal.ZERO;
        Result<BigDecimal> monthAmountResult = memberRechargeClient.memberTotalRechargeFromTo(
                memberId,
                LocalDateTime.of(LocalDate.of(now.getYear(), now.getMonth(), 1), LocalTime.MIN),
                LocalDateTime.of(LocalDate.of(now.getYear(), now.getMonth(), now.lengthOfMonth()), LocalTime.MAX)
        );
        if (Result.isSuccess(monthAmountResult)) {
            monthAmount = monthAmountResult.getData();
        }

        BigDecimal finalWeekAmount = weekAmount;
        BigDecimal finalMonthAmount = monthAmount;
        List<H5FreeBetGiftVO> freeBetGiftVOS = memberFreeBetGiftSettings
                .stream()
                .filter(setting -> Objects.equals(setting.getEnable(), EnableEnum.START.getCode()))
                .map(setting -> {
                            FreeBetGiftTypeEnum freeBetGiftTypeEnum = FreeBetGiftTypeEnum.fromIntValue(setting.getType());
                            H5FreeBetGiftVO vo = new H5FreeBetGiftVO();
                            vo.setType(setting.getType());
                            vo.setCurrentVipName(currentMemberVipConfig.getName());

                            vo.setAmount(Objects.equals(setting.getType(), FreeBetGiftTypeEnum.WEEKLY.getValue()) ? currentMemberVipConfig.getWeeklyFreeBet() : currentMemberVipConfig.getMonthlyFreeBet());
                            vo.setRechargeCondition(setting.getAmount());
                            vo.setState(FreeBetGiftStateEnum.UN_RECEIVE.getValue());
                            if (memberFreeBetGiftRecords.stream().anyMatch(memberFreeBetGiftRecord -> Objects.equals(memberFreeBetGiftRecord.getType(), setting.getType()))) {
                                vo.setState(FreeBetGiftStateEnum.RECEIVED.getValue());
                            }

                            BigDecimal exp;
                            if (freeBetGiftTypeEnum.equals(FreeBetGiftTypeEnum.WEEKLY)) {
                                ZonedDateTime ldtZoned = LocalDateTime.now().atZone(ZoneId.systemDefault());
                                ZonedDateTime utc7Zoned = ldtZoned.withZoneSameInstant(ZoneId.of("+7"));
                                exp = setting.getBetRate().multiply(currentMemberVipConfig.getWeeklyFreeBet());
                                if (setting.getValue() > utc7Zoned.getDayOfWeek().getValue()) {
                                    vo.setState(FreeBetGiftStateEnum.NO_POSITION.getValue());
                                } else {
                                    // 判斷上週充值條件
                                    if (Objects.equals(vo.getState(), FreeBetGiftStateEnum.UN_RECEIVE.getValue()) &&
                                            finalWeekAmount.compareTo(setting.getAmount()) < 0) {
                                        vo.setState(FreeBetGiftStateEnum.NO_POSITION.getValue());
                                    }
                                }
                                vo.setExp(exp.stripTrailingZeros().toPlainString());
                            } else {
                                ZonedDateTime ldtZoned = LocalDateTime.now().atZone(ZoneId.systemDefault());
                                ZonedDateTime utc7Zoned = ldtZoned.withZoneSameInstant(ZoneId.of("+7"));
                                exp = setting.getBetRate().multiply(currentMemberVipConfig.getMonthlyFreeBet());
                                if (setting.getValue() > utc7Zoned.getDayOfMonth()) {
                                    vo.setState(FreeBetGiftStateEnum.NO_POSITION.getValue());
                                } else {
                                    // 判斷上月充值條件
                                    if (Objects.equals(vo.getState(), FreeBetGiftStateEnum.UN_RECEIVE.getValue()) &&
                                            finalMonthAmount.compareTo(setting.getAmount()) < 0) {
                                        vo.setState(FreeBetGiftStateEnum.NO_POSITION.getValue());
                                    }
                                }
                                vo.setExp(exp.stripTrailingZeros().toPlainString());
                            }
                            return vo;
                        }
                )
                .filter(filter -> filter.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        // 判斷已領取過的免費籌碼以原金額取代
        freeBetGiftVOS.forEach(gift -> {
                    if (Objects.equals(gift.getState(), FreeBetGiftStateEnum.RECEIVED.getValue())) {
                        MemberFreeBetGiftRecord memberFreeBetGiftRecord = memberFreeBetGiftRecords.stream()
                                .filter(filter -> Objects.equals(filter.getType(), gift.getType()))
                                .findFirst()
                                .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

                        gift.setAmount(memberFreeBetGiftRecord.getAmount());
                        gift.setExp(memberFreeBetGiftRecord.getExp().toString());
                        gift.setRechargeCondition(new BigDecimal(memberFreeBetGiftRecord.getRechargeCondition()));
                    }
                }
        );

        return freeBetGiftVOS;
    }

    @Transactional
    public Boolean receiveFreeBetGift(Long memberId, Integer type) {
        String key = RedisUtils.buildKey(RedisConstants.RECEIVE_FREE_BET_GIFT, memberId + ":" + type);
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock(10, TimeUnit.SECONDS);

            MemberFreeBetGiftSetting memberFreeBetGiftSetting = iMemberFreeBetGiftSettingService
                    .lambdaQuery()
                    .eq(MemberFreeBetGiftSetting::getType, FreeBetGiftTypeEnum.fromIntValue(type).getValue())
                    .one();

            List<MemberFreeBetGiftRecord> memberFreeBetGiftRecordList = iMemberFreeBetGiftRecordService.lambdaQuery()
                    .eq(MemberFreeBetGiftRecord::getMemberId, memberId)
                    .eq(MemberFreeBetGiftRecord::getType, type)
                    .list();

            FreeBetGiftTypeEnum freeBetGiftTypeEnum = FreeBetGiftTypeEnum.fromIntValue(type);
            ZonedDateTime ldtZoned = LocalDateTime.now().atZone(ZoneId.systemDefault());
            ZonedDateTime utc7Zoned = ldtZoned.withZoneSameInstant(ZoneId.of("+7"));
            if (freeBetGiftTypeEnum.equals(FreeBetGiftTypeEnum.WEEKLY)) {
                //週
                if (memberFreeBetGiftSetting.getValue() > utc7Zoned.getDayOfWeek().getValue()) {
                    throw new BizException(ResultCode.NO_POSITION_RECEIVE_WEEKLY_FREE_BET_GIFT);
                }
                Boolean received = this.checkWeeklyGiftReceived(memberFreeBetGiftRecordList, utc7Zoned);
                if (received) {
                    throw new BizException(ResultCode.RECEIVED_WEEKLY_FREE_BET_GIFT);
                }
            } else {
                //月
                if (memberFreeBetGiftSetting.getValue() > utc7Zoned.getDayOfMonth()) {
                    throw new BizException(ResultCode.NO_POSITION_RECEIVE_MONTHLY_FREE_BET_GIFT);
                }
                Boolean received = this.checkMonthlyGiftReceived(memberFreeBetGiftRecordList, utc7Zoned);
                if (received) {
                    throw new BizException(ResultCode.RECEIVED_MONTHLY_FREE_BET_GIFT);
                }
            }

            List<MemberVipConfig> memberVipConfigList = iMemberVipConfigService.findAllVipConfig();

            MemberVip memberVip = iMemberService.getMemberVip(memberId);

            MemberVipConfig currentMemberVipConfig = memberVipConfigList
                    .stream()
                    .filter(config -> config.getId().equals(memberVip.getCurrentVipId()))
                    .findFirst()
                    .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

            BigDecimal freeBetAmount = FreeBetGiftTypeEnum.fromIntValue(type) == FreeBetGiftTypeEnum.WEEKLY ? currentMemberVipConfig.getWeeklyFreeBet() : currentMemberVipConfig.getMonthlyFreeBet();

            iMemberFreeBetGiftRecordService.save(MemberFreeBetGiftRecord.builder()
                    .type(type)
                    .memberId(memberVip.getMemberId())
                    .username(memberVip.getUsername())
                    .vipId(currentMemberVipConfig.getId())
                    .vipName(currentMemberVipConfig.getName())
                    .amount(freeBetAmount)
                    .betRate(memberFreeBetGiftSetting.getBetRate().intValue())
                    .exp(freeBetAmount.multiply(memberFreeBetGiftSetting.getBetRate()).intValue())
                    .rechargeCondition(memberFreeBetGiftSetting.getAmount().intValue())
                    .build());

            BigDecimal amount = type.equals(FreeBetGiftTypeEnum.WEEKLY.getValue()) ? currentMemberVipConfig.getWeeklyFreeBet() : currentMemberVipConfig.getMonthlyFreeBet();

            kafkaTemplate.send(BALANCE_CHANGE,
                    AddBalanceDTO.builder()
                            .memberId(memberId)
                            .balance(amount)
                            .balanceChangeTypeLinkEnum(type.equals(FreeBetGiftTypeEnum.WEEKLY.getValue()) ? BalanceChangeTypeLinkEnum.FREE_BET_WEEKLY_GIFT : BalanceChangeTypeLinkEnum.FREE_BET_MONTHLY_GIFT)
                            .type(type.equals(FreeBetGiftTypeEnum.WEEKLY.getValue()) ? BalanceChangeTypeLinkEnum.FREE_BET_WEEKLY_GIFT.getType() : BalanceChangeTypeLinkEnum.FREE_BET_MONTHLY_GIFT.getType())
                            .betRate(memberFreeBetGiftSetting.getBetRate())
                            .note(type.equals(FreeBetGiftTypeEnum.WEEKLY.getValue()) ? BalanceChangeTypeLinkEnum.FREE_BET_WEEKLY_GIFT.getI18n() : BalanceChangeTypeLinkEnum.FREE_BET_MONTHLY_GIFT.getI18n())
                            .gmtCreate(LocalDateTime.now())
                            .build());

        } finally {
            lock.unlock();
        }
        return true;
    }

    private Boolean checkWeeklyGiftReceived(List<MemberFreeBetGiftRecord> memberFreeBetGiftRecordList, ZonedDateTime utc7Zoned) {
        return memberFreeBetGiftRecordList.stream()
                .filter(record -> record.getType().equals(FreeBetGiftTypeEnum.WEEKLY.getValue()))
                .anyMatch(record -> {
                    ZonedDateTime recordLdtZoned = record.getGmtCreate().atZone(ZoneId.systemDefault());
                    ZonedDateTime recordUtc7Zoned = recordLdtZoned.withZoneSameInstant(ZoneId.of("+7"));
                    return DateUtil.checkWeekBetween(recordUtc7Zoned.toLocalDateTime(), utc7Zoned.toLocalDateTime());
                });

    }

    private Boolean checkMonthlyGiftReceived(List<MemberFreeBetGiftRecord> memberFreeBetGiftRecordList, ZonedDateTime utc7Zoned) {
        return memberFreeBetGiftRecordList.stream()
                .filter(record -> record.getType().equals(FreeBetGiftTypeEnum.MONTHLY.getValue()))
                .anyMatch(record -> {
                    ZonedDateTime recordLdtZoned = record.getGmtCreate().atZone(ZoneId.systemDefault());
                    ZonedDateTime recordUtc7Zoned = recordLdtZoned.withZoneSameInstant(ZoneId.of("+7"));
                    return recordUtc7Zoned.getMonth().equals(utc7Zoned.getMonth());
                });

    }

    public List<H5LevelUpGiftVO> getLevelUpGiftList(Long memberId) {

        MemberLevelUpGiftSetting memberLevelUpGiftSetting = iMemberLevelUpGiftSettingService.findLevelUpGiftSetting();

        MemberVip memberVip = iMemberService.getMemberVip(memberId);

        Map<Integer, MemberLevelUpGiftRecord> memberLevelUpGiftRecordMap = iMemberLevelUpGiftRecordService.lambdaQuery()
                .eq(MemberLevelUpGiftRecord::getMemberId, memberId)
                .list()
                .stream()
                .collect(Collectors.toMap(MemberLevelUpGiftRecord::getVipId, Function.identity()));

        List<MemberVipConfig> memberVipConfigList = iMemberVipConfigService.findAllVipConfig();

        List<Integer> memberVipLevelRecordList = iMemberVipLevelRecordService.lambdaQuery()
                .eq(MemberVipLevelRecord::getMemberId, memberId)
                .list()
                .stream()
                .map(MemberVipLevelRecord::getTargetVipId)
                .collect(Collectors.toList());

        //找出所有手動
        List<MemberVipConfig> manualVipConfigList = memberVipConfigList
                .stream()
                .filter(x -> memberVipLevelRecordList.contains(x.getId()) && x.getLevelUpMode().equals(LevelUpModeEnum.MANUAL))
                .collect(Collectors.toList());

        MemberVipConfig maxVipConfig = memberVipConfigList
                .stream()
                .filter(x -> memberVipLevelRecordList.contains(x.getId()) && x.getLevelUpMode().equals(LevelUpModeEnum.AUTO))
                .sorted(Comparator.comparing(MemberVipConfig::getLevelUpRecharge).reversed())
                .findFirst()
                .orElse(memberVipConfigList
                        .stream()
                        .filter(memberVipConfig -> 1 == memberVipConfig.getVip0())
                        .findFirst()
                        .get());

        Map<Integer, MemberVipConfig> memberVipConfigMap = memberVipConfigList.stream().collect(Collectors.toMap(MemberVipConfig::getId, Function.identity()));

        MemberVipConfig currentMemberVipConfig = memberVipConfigMap.get(memberVip.getCurrentVipId());

        List<H5LevelUpGiftVO> h5LevelUpGiftVOS = memberVipConfigList
                .stream()
                .filter(config -> config.getVip0() != 1)
                .map(config -> {

                            H5LevelUpGiftVO vo = H5LevelUpGiftVO.builder()
                                    .vipId(config.getId())
                                    .vipName(config.getName())
                                    .currentVipName(memberVipConfigMap.get(memberVip.getCurrentVipId()).getName())
                                    .state(LevelUpGiftStateEnum.UN_RECEIVE.getValue())
                                    .levelUpGift(config.getLevelUpGift())
                                    .build();

                            if (config.getLevelUpMode().equals(LevelUpModeEnum.MANUAL)) {
                                MemberVipConfig first = manualVipConfigList
                                        .stream()
                                        .filter(filter -> Objects.equals(filter.getId(), config.getId()))
                                        .findFirst()
                                        .orElse(null);
                                if (first != null) {
                                    vo.setState(LevelUpGiftStateEnum.UN_RECEIVE.getValue());
                                } else {
                                    vo.setState(LevelUpGiftStateEnum.NO_POSITION.getValue());
                                }
                            } else {
                                if (maxVipConfig.getLevelUpRecharge().compareTo(config.getLevelUpRecharge()) >= 0) {
                                    vo.setState(LevelUpGiftStateEnum.UN_RECEIVE.getValue());
                                } else if ((currentMemberVipConfig.getLevelUpRecharge().compareTo(config.getLevelUpRecharge()) < 0 &&
                                        currentMemberVipConfig.getLevelUpMode().equals(LevelUpModeEnum.AUTO))) {
                                    vo.setState(LevelUpGiftStateEnum.NO_POSITION.getValue());
                                } else if (currentMemberVipConfig.getLevelUpMode().equals(LevelUpModeEnum.MANUAL) &&
                                        maxVipConfig.getLevelUpRecharge().compareTo(config.getLevelUpRecharge()) < 0) {
                                    vo.setState(LevelUpGiftStateEnum.NO_POSITION.getValue());
                                }

                            }

                            if (memberLevelUpGiftRecordMap.containsKey(config.getId())) {
                                vo.setState(LevelUpGiftStateEnum.RECEIVED.getValue());
                            }
                            if (config.getLevelUpMode().equals(LevelUpModeEnum.MANUAL)) {
                                vo.setExp(config.getLevelUpNote());
                            } else {
                                vo.setExp(BigDecimal.valueOf(config.getLevelUpGift()).multiply(memberLevelUpGiftSetting.getBetRate()).stripTrailingZeros().toPlainString());
                            }

                            return vo;
                        }
                )
                .collect(Collectors.toList());

        // 判斷是已領取過的晉升等級則取代金額
        h5LevelUpGiftVOS.forEach(gift -> {
                    if (Objects.equals(gift.getState(), LevelUpGiftStateEnum.RECEIVED.getValue())) {
                        MemberLevelUpGiftRecord memberLevelUpGiftRecord = memberLevelUpGiftRecordMap.get(gift.getVipId());
                        gift.setLevelUpGift(memberLevelUpGiftRecord.getAmount().intValue());
                        gift.setExp(memberLevelUpGiftRecord.getExp().toString());
                    }
                }
        );

        return h5LevelUpGiftVOS;
    }

    @Transactional
    public Boolean receiveLevelUpGift(Long memberId, Integer vipId) {
        String key = RedisUtils.buildKey(RedisConstants.RECEIVE_LEVEL_UP_GIFT, memberId + ":" + vipId);
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock(10, TimeUnit.SECONDS);

            MemberLevelUpGiftSetting memberLevelUpGiftSetting = iMemberLevelUpGiftSettingService.findLevelUpGiftSetting();

            List<MemberVipConfig> memberVipConfigList = iMemberVipConfigService.findAllVipConfig();

            MemberVip memberVip = iMemberService.getMemberVip(memberId);

            MemberVipConfig targetVipConfig = memberVipConfigList
                    .stream()
                    .filter(config -> config.getId().equals(vipId))
                    .findFirst()
                    .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

            List<H5LevelUpGiftVO> levelUpGiftList = this.getLevelUpGiftList(memberId);

            Boolean checkReceive = levelUpGiftList.stream()
                    .anyMatch(x -> x.getVipId().equals(vipId) && x.getState().equals(LevelUpGiftStateEnum.UN_RECEIVE.getValue()));

            if (Boolean.FALSE.equals(checkReceive)) {
                throw new BizException(ResultCode.NO_POSITION_RECEIVE_LEVEL_UP_GIFT);
            }

            iMemberLevelUpGiftRecordService.lambdaQuery()
                    .eq(MemberLevelUpGiftRecord::getMemberId, memberId)
                    .eq(MemberLevelUpGiftRecord::getVipId, vipId)
                    .oneOpt()
                    .ifPresent(x -> {
                                throw new BizException(ResultCode.RECEIVED_LEVEL_UP_GIFT);
                            }
                    );

            iMemberLevelUpGiftRecordService.save(
                    MemberLevelUpGiftRecord.builder()
                            .memberId(memberVip.getMemberId())
                            .username(memberVip.getUsername())
                            .vipId(targetVipConfig.getId())
                            .vipName(targetVipConfig.getName())
                            .amount(BigDecimal.valueOf(targetVipConfig.getLevelUpGift()))
                            .betRate(memberLevelUpGiftSetting.getBetRate().intValue())
                            .exp(BigDecimal.valueOf(targetVipConfig.getLevelUpGift()).multiply(memberLevelUpGiftSetting.getBetRate()).intValue())
                            .build()
            );

            kafkaTemplate.send(BALANCE_CHANGE,
                    AddBalanceDTO.builder()
                            .memberId(memberId)
                            .balance(BigDecimal.valueOf(targetVipConfig.getLevelUpGift()))
                            .balanceChangeTypeLinkEnum(BalanceChangeTypeLinkEnum.LEVEL_UP_GIFT)
                            .type(BalanceChangeTypeLinkEnum.LEVEL_UP_GIFT.getType())
                            .betRate(memberLevelUpGiftSetting.getBetRate())
                            .note(BalanceChangeTypeLinkEnum.LEVEL_UP_GIFT.getI18n())
                            .gmtCreate(LocalDateTime.now())
                            .build());

            String redisKey = RedisUtils.buildKey(RedisConstants.VIP_LEVEL_UP_REWARD_NOTIFICATION, memberId);
            redisTemplate.opsForList().leftPop(redisKey);

        } finally {
            lock.unlock();
        }
        return true;
    }

    public List<H5GiftNotificationVO> findGiftNotification(Long memberId, String username) {
        LocalDate now = LocalDate.now();

        List<H5GiftNotificationVO> voList = new ArrayList<>();
        H5GiftNotificationVO birthday = getBirthdayReceiveCondition(memberId, now);
        voList.add(birthday);

        //免費籌碼有可能關閉
        MemberVip memberVip = iMemberVipService.lambdaQuery().eq(MemberVip::getMemberId, memberId).one();
        MemberVipConfig memberVipConfig = iMemberVipConfigService.getById(memberVip.getCurrentVipId());
        List<MemberFreeBetGiftSetting> memberFreeBetGiftSettings = iMemberFreeBetGiftSettingService.list();
        BigDecimal enableCount = memberFreeBetGiftSettings.stream().map(x -> BigDecimal.valueOf(x.getEnable())).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (memberVipConfig.getWeeklyFreeBet().add(memberVipConfig.getMonthlyFreeBet()).compareTo(BigDecimal.ZERO) != 0 && enableCount.compareTo(BigDecimal.ZERO) != 0) {
            List<H5FreeBetGiftVO> freeBetGiftList = getFreeBetGiftList(memberId);
            H5GiftNotificationVO freeBet = H5GiftNotificationVO.builder().gift("freeBet").notification(0).build();
            if (freeBetGiftList.stream().anyMatch(x -> Objects.equals(x.getState(), FreeBetGiftStateEnum.UN_RECEIVE.getValue()))) {
                freeBet.setNotification(1);
            }
            voList.add(freeBet);
        }

        H5GiftNotificationVO levelUp = getLevelUpReceiveCondition(memberId);
        voList.add(levelUp);

        // 白菜紅包通知
        List<H5ChineseCabbageStateVO> chineseCabbageState = iMemberRedEnvelopeService.findChineseCabbageState(memberId, username);
        H5GiftNotificationVO chineseCabbageStateNotificationVO = new H5GiftNotificationVO();
        if (CollectionUtils.isNotEmpty(chineseCabbageState)) {
            chineseCabbageStateNotificationVO.setGift("chineseCabbage");
            chineseCabbageStateNotificationVO.setNotification(0);
            if (chineseCabbageState.stream().anyMatch(state -> Objects.equals(state.getState(), RedEnvelopeChineseCabbageReceiveStateEnum.RECEIVE.getCode()))) {
                chineseCabbageStateNotificationVO.setNotification(1);
            }
            voList.add(chineseCabbageStateNotificationVO);
        }

        return voList;
    }

    /**
     * 判斷晉級獎勵禮金領取條件
     *
     * @param memberId 會員ID
     * @return
     */
    private H5GiftNotificationVO getLevelUpReceiveCondition(Long memberId) {
        List<H5LevelUpGiftVO> levelUpGiftList = this.getLevelUpGiftList(memberId);
        int notification = 0;
        if (levelUpGiftList.stream()
                .anyMatch(levelUpGift -> Objects.equals(levelUpGift.getState(), LevelUpGiftStateEnum.UN_RECEIVE.getValue()))) {
            notification = 1;
        }

        H5GiftNotificationVO h5GiftNotificationVO = new H5GiftNotificationVO();
        h5GiftNotificationVO.setGift("levelUp");
        h5GiftNotificationVO.setNotification(notification);
        return h5GiftNotificationVO;
    }

    /**
     * 判斷免費籌碼禮金領取條件
     *
     * @param memberId 會員ID
     * @param now      現在時間
     * @return
     */
    private H5GiftNotificationVO getFreeBetReceiveCondition(Long memberId, LocalDate now) {
        H5GiftNotificationVO h5GiftNotificationVO = new H5GiftNotificationVO();
        h5GiftNotificationVO.setGift("freeBet");
        h5GiftNotificationVO.setNotification(0);

        List<MemberFreeBetGiftSetting> memberFreeBetGiftSettings = iMemberFreeBetGiftSettingService.lambdaQuery()
                .eq(MemberFreeBetGiftSetting::getEnable, EnableEnum.START.getCode())
                .list();
        if (memberFreeBetGiftSettings.isEmpty()) {
            return null;
        }

        List<H5FreeBetGiftVO> freeBetGiftList = this.getFreeBetGiftList(memberId);

        if (freeBetGiftList.stream().anyMatch(freeBetGift -> Objects.equals(freeBetGift.getState(), FreeBetGiftStateEnum.UN_RECEIVE.getValue()))) {
            h5GiftNotificationVO.setNotification(1);
        }

        return h5GiftNotificationVO;

        //
        // // 取的免費籌碼記錄
        // List<MemberFreeBetGiftRecord> memberFreeBetGiftRecords = iMemberFreeBetGiftRecordService.lambdaQuery()
        //         .or(wrapper -> wrapper
        //                 .eq(MemberFreeBetGiftRecord::getMemberId, memberId)
        //                 .eq(MemberFreeBetGiftRecord::getType, FreeBetGiftTypeEnum.WEEKLY.getValue())
        //                 .between(BaseEntity::getGmtCreate, now.minusDays(now.getDayOfWeek().getValue()), now.plusDays(7 - now.getDayOfWeek().getValue()))
        //         )
        //         .or(wrapper -> wrapper
        //                 .eq(MemberFreeBetGiftRecord::getMemberId, memberId)
        //                 .eq(MemberFreeBetGiftRecord::getType, FreeBetGiftTypeEnum.MONTHLY.getValue())
        //                 .between(BaseEntity::getGmtCreate, LocalDate.of(now.getYear(), now.getMonth(), 1), LocalDate.of(now.getYear(), now.getMonth(), now.lengthOfMonth()))
        //         )
        //         .list();
        //
        // // 判斷免費籌碼禮金
        // Boolean freeBetCondition = Boolean.TRUE;
        //
        // // 判斷周禮金與月禮金都沒開啟時表示無法領取
        // if (memberFreeBetGiftSettings.isEmpty()) {
        //     freeBetCondition = Boolean.FALSE;
        // }
        //
        // // 判斷周禮金與月禮金同時開啟時有兩筆紀錄表示已領取
        // if (memberFreeBetGiftSettings.size() == 2 && memberFreeBetGiftRecords.size() == 2) {
        //     freeBetCondition = Boolean.FALSE;
        // }
        //
        // // 取的上週的充值金額
        // LocalDate lastWeekDate = now.minusWeeks(1);
        // BigDecimal weekAmount = BigDecimal.ZERO;
        // Result<BigDecimal> weekAmountResult = memberRechargeClient.memberTotalRechargeFromTo(
        //         memberId,
        //         LocalDateTime.of(lastWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), LocalTime.MIN),
        //         LocalDateTime.of(lastWeekDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)), LocalTime.MAX)
        // );
        // if (Result.isSuccess(weekAmountResult)) {
        //     weekAmount = weekAmountResult.getData();
        // }
        //
        // // 取得上個月的充值金額
        // LocalDate lastMonthDate = now.minusMonths(1);
        // BigDecimal monthAmount = BigDecimal.ZERO;
        // Result<BigDecimal> monthAmountResult = memberRechargeClient.memberTotalRechargeFromTo(
        //         memberId,
        //         LocalDateTime.of(LocalDate.of(lastMonthDate.getYear(), lastMonthDate.getMonth(), 1), LocalTime.MIN),
        //         LocalDateTime.of(LocalDate.of(lastMonthDate.getYear(), lastMonthDate.getMonth(), lastMonthDate.lengthOfMonth()), LocalTime.MAX)
        // );
        // if (Result.isSuccess(monthAmountResult)) {
        //     monthAmount = monthAmountResult.getData();
        // }
        //
        // // 判斷週禮金或月禮金開啟時如有對應到領取過的禮金表示不可領取
        // if (memberFreeBetGiftSettings.size() == 1) {
        //     MemberFreeBetGiftSetting memberFreeBetGiftSetting = memberFreeBetGiftSettings.stream()
        //             .findFirst()
        //             .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));
        //
        //     if ((Objects.equals(memberFreeBetGiftSetting.getType(), FreeBetGiftTypeEnum.WEEKLY.getValue()) &&
        //             memberFreeBetGiftRecords.stream().anyMatch(giftRecord -> Objects.equals(giftRecord.getType(), FreeBetGiftTypeEnum.WEEKLY.getValue()))) ||
        //             (Objects.equals(memberFreeBetGiftSetting.getType(), FreeBetGiftTypeEnum.MONTHLY.getValue()) &&
        //                     memberFreeBetGiftRecords.stream().anyMatch(giftRecord -> Objects.equals(giftRecord.getType(), FreeBetGiftTypeEnum.MONTHLY.getValue())))) {
        //         freeBetCondition = Boolean.FALSE;
        //     }
        // }
        // h5GiftNotificationVO.setNotification(Boolean.TRUE.equals(freeBetCondition) ? 1 : 0);
        // return h5GiftNotificationVO;
    }

    /**
     * 判斷生日禮金領取條件
     *
     * @param memberId 會員ID
     * @param now      現在時間
     * @return
     */
    private H5GiftNotificationVO getBirthdayReceiveCondition(Long memberId, LocalDate now) {
        MemberBirthdayGiftVO memberBirthdayGift = this.findMemberBirthdayGift(memberId);
        int notification = 0;
        if (Objects.equals(memberBirthdayGift.getState(), BirthdayGiftStateEnum.UN_RECEIVE.getValue())) {
            notification = 1;
        }

        // Member member = iMemberService.getById(memberId);
        // if (member.getBirthday() != null) {
        //     notification = iMemberBirthdayGiftRecordService.lambdaQuery()
        //             .eq(MemberBirthdayGiftRecord::getMemberId, memberId)
        //             .between(BaseEntity::getGmtCreate, LocalDate.of(now.getYear(), Month.JANUARY, 1), LocalDate.of(now.getYear(), Month.DECEMBER, 31))
        //             .count() == 0 ? 1 : 0;
        // }
        H5GiftNotificationVO h5GiftNotificationVO = new H5GiftNotificationVO();
        h5GiftNotificationVO.setGift("birthday");
        h5GiftNotificationVO.setNotification(notification);
        return h5GiftNotificationVO;
    }
}
