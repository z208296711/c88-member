package com.c88.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.common.core.base.BaseEntity;
import com.c88.common.core.constant.GlobalConstants;
import com.c88.common.core.constant.RedisConstants;
import com.c88.common.core.constant.TopicConstants;
import com.c88.common.core.enums.BalanceChangeTypeLinkEnum;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.core.util.CronUtils;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.UserUtils;
import com.c88.common.xxljob.dto.UpdateXxlJobDto;
import com.c88.common.xxljob.service.XxlJobService;
import com.c88.game.adapter.api.GameFeignClient;
import com.c88.member.common.enums.AwardTypeEnum;
import com.c88.member.common.enums.DrawRecordStateEnum;
import com.c88.member.common.enums.DrawTemplateConditionTypeEnum;
import com.c88.member.constants.RedisKey;
import com.c88.member.enums.RechargeAwardRecordModelActionEnum;
import com.c88.member.enums.RechargeAwardRecordStateEnum;
import com.c88.member.event.RechargeAwardRecordModel;
import com.c88.member.mapstruct.MemberDrawAwardItemConverter;
import com.c88.member.mapstruct.MemberDrawRecordConverter;
import com.c88.member.mapstruct.MemberDrawTemplateConditionConverter;
import com.c88.member.mapstruct.MemberDrawTemplateConverter;
import com.c88.member.pojo.entity.*;
import com.c88.member.pojo.form.SearchDrawAwardItemForm;
import com.c88.member.pojo.form.SearchDrawRecordForm;
import com.c88.member.pojo.form.SearchDrawTemplateForm;
import com.c88.member.pojo.form.SearchTodayDrawRecordForm;
import com.c88.member.pojo.vo.*;
import com.c88.member.service.*;
import com.c88.member.service.draw.*;
import com.c88.member.vo.OptionVO;
import com.c88.payment.client.MemberRechargeClient;
import com.c88.payment.dto.AddBalanceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.c88.common.core.constant.TopicConstants.RECHARGE_AWARD_RECORD;
import static com.c88.common.core.util.CronUtils.DATEFORMAT_EVERYDAY;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrawServiceImpl implements IDrawService {

    private static final Integer APPROVE_TIME = 1;

    private final IMemberCoinService iMemberCoinService;

    private final IMemberDrawPoolService iMemberDrawPoolService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final IMemberDrawRecordService iMemberDrawRecordService;

    private final IMemberDrawTemplateService iMemberDrawTemplateService;

    private final IMemberDrawTemplateConditionService iMemberDrawTemplateConditionService;

    private final IMemberDrawAwardItemService iMemberDrawAwardItemService;

    private final IMemberService iMemberService;

    private final MemberDrawRecordConverter memberDrawRecordConverter;

    private final MemberDrawTemplateConverter memberDrawTemplateConverter;

    private final MemberDrawTemplateConditionConverter memberDrawTemplateConditionConverter;

    private final MemberDrawAwardItemConverter memberDrawAwardItemConverter;

    private final SingleRechargeDrawTemplateConditionTypeHandler singleRechargeDrawTemplateConditionTypeHandler;

    private final TotalRechargeDrawTemplateConditionTypeHandler totalRechargeDrawTemplateConditionTypeHandler;

    private final FreeDrawTemplateConditionTypeHandler freeDrawTemplateConditionTypeHandler;

    private final CoinExchangeDrawTemplateConditionTypeHandler coinExchangeDrawTemplateConditionTypeHandler;

    protected final RedissonClient redissonClient;

    private final MemberRechargeClient memberRechargeClient;

    private final GameFeignClient gameFeignClient;

    private final ObjectMapper objectMapper;

    private final XxlJobService xxlJobService;

    private final KafkaTemplate kafkaTemplate;

    private final IMemberRechargeAwardTemplateService iMemberRechargeAwardTemplateService;

    private void updateResetTimeToXxlJOB(String cron) {

        UpdateXxlJobDto dto = new UpdateXxlJobDto();
        dto.setId(9);
        dto.setJobGroup(3);
        dto.setJobDesc("重置抽獎紀錄");
        dto.setAuthor("gary");
        dto.setScheduleType("CRON");
        dto.setScheduleConf(cron);
        dto.setExecutorHandler("resetDrawTemplate");
        dto.setExecutorRouteStrategy("FIRST");
        dto.setMisfireStrategy("DO_NOTHING");
        dto.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobService.updateJob(dto);
        log.info("XXX:{}", JSON.toJSONString(dto));


    }

    public LocalDateTime getResetStartTime() {
        Integer resetTimestamp = (Integer) redisTemplate.opsForValue().get(RedisKey.MEMBER_DRAW_RESET_TIME);
        if (resetTimestamp == null) {
            LocalDateTime now = LocalDateTime.now();
            redisTemplate.opsForValue().set(RedisKey.MEMBER_DRAW_RESET_TIME, now.toEpochSecond(ZoneOffset.UTC));
            return now;
        }
        return Instant.ofEpochSecond(resetTimestamp.longValue()).atZone(ZoneOffset.UTC).toLocalDateTime();
    }

    public LocalDateTime getPreConditionStartTime(MemberDrawTemplate memberDrawTemplate) {
        Integer resetTimestamp = (Integer) redisTemplate.opsForValue().get(RedisKey.MEMBER_DRAW_PRECONDITION_START_TIME);
        if (resetTimestamp == null) {
            LocalDateTime now = LocalDateTime.of(LocalDate.now().minusDays(1), memberDrawTemplate.getResetTime());
            redisTemplate.opsForValue().set(RedisKey.MEMBER_DRAW_PRECONDITION_START_TIME, now.toEpochSecond(ZoneOffset.UTC));
            return now;
        }
        return Instant.ofEpochSecond(resetTimestamp.longValue()).atZone(ZoneOffset.UTC).toLocalDateTime();
    }

    public void cleanMemberDrawInfo() {
        redisTemplate.delete(RedisKey.MEMBER_DRAW_PRECONDITION_START_TIME);
        redisTemplate.delete(RedisKey.MEMBER_DRAW_RESET_TIME);
        Set<String> keys1 = redisTemplate.keys(RedisKey.MEMBER_TODAY_DRAW + ":" + "*");
        if (CollectionUtils.isNotEmpty(keys1)) {
            redisTemplate.delete(keys1);
        }
        this.getResetStartTime();
        this.getPreConditionStartTime(iMemberDrawTemplateService.findActivityDrawTemplate());
    }

    @Override
    public IPage<AdminMemberDrawRecordVO> findDrawRecord(SearchDrawRecordForm form) {
        LambdaQueryChainWrapper<MemberDrawRecord> lambdaQueryChainWrapper = iMemberDrawRecordService.lambdaQuery()
                .eq(form.getAwardType() != null, MemberDrawRecord::getAwardType, form.getAwardType())
                .eq(form.getState() != null, MemberDrawRecord::getState, form.getState())
                .like(StringUtils.isNotBlank(form.getUsername()), MemberDrawRecord::getUsername, form.getUsername());

        if (Objects.equals(form.getSearchType(), APPROVE_TIME)) {
            lambdaQueryChainWrapper.isNotNull(MemberDrawRecord::getApproveTime);
            lambdaQueryChainWrapper.ge(StringUtils.isNotBlank(form.getStartTime()), MemberDrawRecord::getApproveTime, form.getStartTime());
            lambdaQueryChainWrapper.le(StringUtils.isNotBlank(form.getEndTime()), MemberDrawRecord::getApproveTime, form.getEndTime());
            lambdaQueryChainWrapper.orderByDesc(MemberDrawRecord::getApproveTime);
        } else {
            lambdaQueryChainWrapper.ge(StringUtils.isNotBlank(form.getStartTime()), MemberDrawRecord::getGmtCreate, form.getStartTime());
            lambdaQueryChainWrapper.le(StringUtils.isNotBlank(form.getEndTime()), MemberDrawRecord::getGmtCreate, form.getEndTime());
            lambdaQueryChainWrapper.orderByDesc(MemberDrawRecord::getGmtCreate);
        }
        return lambdaQueryChainWrapper
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberDrawRecordConverter::toVo);
    }

    @Override
    public IPage<AdminMemberDrawTemplateVO> findDrawTemplatePage(SearchDrawTemplateForm form) {
        IPage<MemberDrawTemplate> memberDrawTemplatePage = iMemberDrawTemplateService.lambdaQuery()
                .like(StringUtils.isNotBlank(form.getName()), MemberDrawTemplate::getName, form.getName())
                .eq(form.getDrawType() != null, MemberDrawTemplate::getDrawType, form.getDrawType())
                .eq(form.getEnable() != null, MemberDrawTemplate::getEnable, form.getEnable())
                .orderByDesc(MemberDrawTemplate::getGmtModified)
                .page(new Page<>(form.getPageNum(), form.getPageSize()));

        List<Integer> templateIdList = memberDrawTemplatePage.getRecords().stream().map(MemberDrawTemplate::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(templateIdList)) {
            return new Page<>(form.getPageNum(), form.getPageSize());
        }

        Map<Integer, Integer> templateEnableDrawItemMap = iMemberDrawPoolService.findEnableDrawItemByTemplateId(templateIdList)
                .stream()
                .collect(Collectors.toMap(EnableDrawItemCountVO::getTemplateId, EnableDrawItemCountVO::getCount));

        Map<Integer, List<AdminMemberDrawTemplateConditionVO>> map = iMemberDrawTemplateConditionService.lambdaQuery()
                .in(MemberDrawTemplateCondition::getTemplateId, templateIdList)
                .list()
                .stream()
                .map(memberDrawTemplateConditionConverter::toVo)
                .collect(Collectors.groupingBy(AdminMemberDrawTemplateConditionVO::getTemplateId));
        return memberDrawTemplatePage.convert(x -> memberDrawTemplateConverter.toVO(x, map, templateEnableDrawItemMap));
    }

    @Override
    public List<OptionVO> findDrawTemplateOption() {
        return iMemberDrawTemplateService.lambdaQuery()
                .select(MemberDrawTemplate::getId, MemberDrawTemplate::getName)
                .list()
                .stream()
                .map(x -> new OptionVO(x.getId(), x.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public IPage<AdminMemberDrawAwardItemVO> findDrawItems(SearchDrawAwardItemForm form) {
        return iMemberDrawAwardItemService.lambdaQuery()
                .like(StringUtils.isNotBlank(form.getName()), MemberDrawAwardItem::getName, form.getName())
                .eq(form.getAwardType() != null, MemberDrawAwardItem::getAwardType, form.getAwardType())
                .eq(form.getDrawType() != null, MemberDrawAwardItem::getDrawType, form.getDrawType())
                .eq(form.getEnable() != null, MemberDrawAwardItem::getEnable, form.getEnable())
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberDrawAwardItemConverter::toVo);
    }

    @Override
    public IPage<AdminMemberDrawItemVO> findTodayDrawRecord(SearchTodayDrawRecordForm form) {
        return iMemberDrawRecordService.lambdaQuery()
                .eq(StringUtils.isNotBlank(form.getUsername()), MemberDrawRecord::getUsername, form.getUsername())
                .eq(form.getTemplateId() != null, MemberDrawRecord::getTemplateId, form.getTemplateId())
                .between(BaseEntity::getGmtCreate, LocalDateTime.of(LocalDate.now(), LocalTime.MIN), LocalDateTime.of(LocalDate.now(), LocalTime.MAX))
                .orderByDesc(MemberDrawRecord::getId)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberDrawRecordConverter::toDrawItemVO);
    }

    @Override
    public AdminTodayDrawVO findTodayDrawInfo(SearchTodayDrawRecordForm form) {

        Member member = iMemberService.findMemberByUsername(form.getUsername());

        MemberDrawTemplate memberDrawTemplate = iMemberDrawTemplateService.lambdaQuery()
                .eq(MemberDrawTemplate::getId, form.getTemplateId())
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

        List<MemberDrawTemplateCondition> conditionList = iMemberDrawTemplateConditionService.findByTemplateId(memberDrawTemplate.getId());

        int canDraw;
        Integer totalDraw = conditionList.stream()
                .mapToInt(MemberDrawTemplateCondition::getMaxNum)
                .sum();

        MemberTodayCurrentDrawVO memberTodayCurrentDrawVO = this.getMemberCurrentDrawVO(member.getId());
        int todayDrawTimes = memberTodayCurrentDrawVO.getFreeDraw() + memberTodayCurrentDrawVO.getTotalRechargeDraw() + memberTodayCurrentDrawVO.getSingleRechargeDraw() + memberTodayCurrentDrawVO.getCoinExchangeDraw();
        int dailyDrawTimes = memberDrawTemplate.getDailyGiftNumber();
        if (dailyDrawTimes == -1 && totalDraw == 0) {
            canDraw = -1;
        } else if (dailyDrawTimes != -1) {
            int times = Math.min(dailyDrawTimes, totalDraw);
            canDraw = Math.max(times - todayDrawTimes, 0);
        } else {
            canDraw = totalDraw - todayDrawTimes;
        }

        Integer totalFreeDraw = conditionList.stream()
                .filter(x -> x.getType().equals(DrawTemplateConditionTypeEnum.FREE.getValue()))
                .mapToInt(MemberDrawTemplateCondition::getMaxNum).sum();

        Integer currentFreeDrawCount = this.getMemberCurrentDrawVO(member.getId()).getFreeDraw();

        List<MemberDrawTemplateCondition> memberDrawTemplateConditionList = iMemberDrawTemplateConditionService.findByTemplateId(memberDrawTemplate.getId());
        List<DrawTemplateConditionTypeHandler> drawTemplateConditionTypeHandlerList = memberDrawTemplateConditionList
                .stream()
                .map(condition -> {
                    switch (DrawTemplateConditionTypeEnum.fromIntValue(condition.getType())) {
                        case FREE:
                            return freeDrawTemplateConditionTypeHandler;
                        case SINGLE_RECHARGE:
                            return singleRechargeDrawTemplateConditionTypeHandler;
                        case TOTAL_RECHARGE:
                            return totalRechargeDrawTemplateConditionTypeHandler;
                        case COIN_EXCHANGE:
                            return coinExchangeDrawTemplateConditionTypeHandler;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        DrawConditionVO drawConditionVO = new DrawConditionVO();
        drawConditionVO.setMember(member);
        drawConditionVO.setMemberDrawTemplate(memberDrawTemplate);
        drawConditionVO.setCurrentDrawVO(this.getMemberCurrentDrawVO(member.getId()));
        drawConditionVO.setConditionList(memberDrawTemplateConditionList);
        drawConditionVO.setResetTime(this.getResetStartTime());

        LocalDateTime startTime = this.getPreConditionStartTime(memberDrawTemplate);
        // 昨日充值金額
        BigDecimal yesterdayRechargeAmount = this.getYesterdayRechargeAmount(member.getId(), startTime);
        // 昨日充值金額
        BigDecimal yesterdayValidBetAmount = this.getYesterdayValidBetAmount(member.getId(), startTime);

        if (yesterdayValidBetAmount.compareTo(memberDrawTemplate.getYesterdayBetAmount()) >= 0 &&
                yesterdayRechargeAmount.compareTo(memberDrawTemplate.getYesterdayRechargeAmount()) >= 0) {
            drawConditionVO.setPrecondition(true);
        }
        int freeRemainDrawCount = drawTemplateConditionTypeHandlerList.stream()
                .mapToInt(handler -> handler.getRemainingFreeDraw(drawConditionVO))
                .sum();

        int drawCount = -1;
        int sum = conditionList.stream().mapToInt(MemberDrawTemplateCondition::getMaxNum).sum();
        Integer dailyNumber = memberDrawTemplate.getDailyGiftNumber();

        if (dailyNumber != -1 && conditionList.stream().anyMatch(x -> x.getMaxNum() == 0)) {
            drawCount = dailyNumber;
        } else if (dailyNumber != -1 && conditionList.stream().noneMatch(x -> x.getMaxNum() == 0)) {
            drawCount = Math.min(sum, dailyNumber);
        } else if (dailyNumber == -1 && conditionList.stream().noneMatch(x -> x.getMaxNum() == 0)) {
            drawCount = sum;
        }

        return AdminTodayDrawVO.builder()
                .drawCount(drawCount)
                .todayFreeDrawRemainCount(freeRemainDrawCount)
                .build();
    }

    @Override
    public MemberTodayCurrentDrawVO getMemberCurrentDrawVO(@NotNull Long memberId) {
        String key = RedisKey.MEMBER_TODAY_DRAW + ":" + memberId;
        Map<Object, Object> info = redisTemplate.opsForHash().entries(RedisKey.MEMBER_TODAY_DRAW + ":" + memberId);
        if (info.isEmpty()) {
            MemberTodayCurrentDrawVO memberTodayCurrentDrawVO = new MemberTodayCurrentDrawVO();
            info = objectMapper.convertValue(memberTodayCurrentDrawVO, Map.class);
            redisTemplate.opsForHash().putAll(key, info);
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }
        return objectMapper.convertValue(info, MemberTodayCurrentDrawVO.class);
    }

    @Override
    public void setMemberTodayCurrentDrawVO(Long memberId, MemberTodayCurrentDrawVO memberTodayCurrentDrawVO) {
        Map<Object, Object> info = objectMapper.convertValue(memberTodayCurrentDrawVO, Map.class);
        String key = RedisKey.MEMBER_TODAY_DRAW + ":" + memberId;
        redisTemplate.opsForHash().putAll(key, info);
    }

    @Override
    @Transactional
    public Boolean operationDrawRecord(Long recordId, DrawRecordStateEnum recordStateEnum) {
        MemberDrawRecord memberDrawRecord = iMemberDrawRecordService
                .lambdaQuery()
                .in(MemberDrawRecord::getState, Set.of(DrawRecordStateEnum.PRE_APPROVE.getValue(), DrawRecordStateEnum.IN_PROGRESS.getValue()))
                .eq(MemberDrawRecord::getId, recordId)
                .oneOpt().orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

        if (recordStateEnum.equals(DrawRecordStateEnum.IN_PROGRESS) ||
                recordStateEnum.equals(DrawRecordStateEnum.REJECT) ||
                recordStateEnum.equals(DrawRecordStateEnum.SEND)) {
            memberDrawRecord.setApproveTime(LocalDateTime.now());
        }

        memberDrawRecord.setState(recordStateEnum.getValue());
        memberDrawRecord.setApproveBy(Optional.ofNullable(UserUtils.getUsername()).orElse(""));

        if (AwardTypeEnum.fromIntValue(memberDrawRecord.getAwardType()).equals(AwardTypeEnum.BONUS) &&
                DrawRecordStateEnum.fromIntValue(memberDrawRecord.getState()).equals(DrawRecordStateEnum.SEND)) {
            kafkaTemplate.send(TopicConstants.BALANCE_CHANGE, AddBalanceDTO.builder()
                    .memberId(memberDrawRecord.getMemberId())
                    .balance(memberDrawRecord.getAmount())
                    .balanceChangeTypeLinkEnum(BalanceChangeTypeLinkEnum.COMMON_DRAW)
                    .type(BalanceChangeTypeLinkEnum.COMMON_DRAW.getType())
                    .betRate(memberDrawRecord.getBetRate())
                    .note(BalanceChangeTypeLinkEnum.COMMON_DRAW.getI18n())
                    .gmtCreate(memberDrawRecord.getApproveTime())
                    .bonusReviewUsername(Optional.ofNullable(UserUtils.getUsername()).orElse(""))
                    .build());
        } else if (AwardTypeEnum.fromIntValue(memberDrawRecord.getAwardType()).equals(AwardTypeEnum.COIN) &&
                DrawRecordStateEnum.fromIntValue(memberDrawRecord.getState()).equals(DrawRecordStateEnum.SEND)) {
            iMemberCoinService.addCoin(memberDrawRecord.getMemberId(), memberDrawRecord.getAmount().intValue());
        } else if (AwardTypeEnum.fromIntValue(memberDrawRecord.getAwardType()).equals(AwardTypeEnum.RECHARGE_PROMOTION) &&
                DrawRecordStateEnum.fromIntValue(memberDrawRecord.getState()).equals(DrawRecordStateEnum.SEND)) {
            MemberDrawAwardItem memberDrawAwardItem = iMemberDrawAwardItemService.getById(memberDrawRecord.getAwardId());
            MemberRechargeAwardTemplate memberRechargeAwardTemplate = iMemberRechargeAwardTemplateService.getById(memberDrawAwardItem.getPromotionId());
            kafkaTemplate.send(RECHARGE_AWARD_RECORD,
                    RechargeAwardRecordModel.builder()
                            .action(RechargeAwardRecordModelActionEnum.ADD.getCode())
                            .memberId(memberDrawRecord.getMemberId())
                            .username(memberDrawRecord.getUsername())
                            .templateId(memberRechargeAwardTemplate.getId())
                            .name(memberRechargeAwardTemplate.getName())
                            .type(memberRechargeAwardTemplate.getType())
                            .mode(memberRechargeAwardTemplate.getMode())
                            .rate(memberRechargeAwardTemplate.getRate())
                            .betRate(memberRechargeAwardTemplate.getBetRate())
                            .amount(memberRechargeAwardTemplate.getAmount())
                            .source("deposit_delivery_offer_personal.column_03_01")
                            .state(RechargeAwardRecordStateEnum.UNUSED.getCode())
                            .build()
            );
        }

        return iMemberDrawRecordService.updateById(memberDrawRecord);
    }

    @Override
    @Transactional
    public Boolean updateDrawRecordAddress(UpdateDrawRecordAddressForm form) {
        MemberDrawRecord memberDrawRecord = iMemberDrawRecordService.getById(form.getRecordId());
        if (!AwardTypeEnum.fromIntValue(memberDrawRecord.getAwardType()).equals(AwardTypeEnum.ENTITY)) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        memberDrawRecord.setRecipient(form.getRecipient());
        memberDrawRecord.setPhone(form.getPhone());
        memberDrawRecord.setAddress(form.getAddress());
        memberDrawRecord.setState(DrawRecordStateEnum.PRE_APPROVE.getValue());
        return iMemberDrawRecordService.updateById(memberDrawRecord);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "draw", allEntries = true),
            @CacheEvict(cacheNames = "activityDrawTemplate", allEntries = true)
    })
    @Transactional
    public Boolean modifyDrawTemplate(AdminMemberDrawTemplateVO form) {
        MemberDrawTemplate memberDrawTemplate = memberDrawTemplateConverter.toEntity(form);
        if (Objects.equals(form.getEnable(), GlobalConstants.STATUS_YES)) {
            MemberDrawTemplate activityDrawTemplate = iMemberDrawTemplateService.findActivityDrawTemplate();
            if (activityDrawTemplate != null && !form.getId().equals(activityDrawTemplate.getId())) {
                throw new BizException(ResultCode.PROMOTION_CATEGORY_USING);
            }
        }

        iMemberDrawTemplateService.updateById(memberDrawTemplate);
        if (CollectionUtils.isNotEmpty(form.getConditions())) {
            List<MemberDrawTemplateCondition> dbDrawTemplateConditions = iMemberDrawTemplateConditionService.lambdaQuery()
                    .eq(MemberDrawTemplateCondition::getTemplateId, form.getId())
                    .list();

            List<Integer> idList = form.getConditions().stream().map(AdminMemberDrawTemplateConditionVO::getId).collect(Collectors.toList());

            List<MemberDrawTemplateCondition> preRemoveDrawTemplateConditions = dbDrawTemplateConditions.stream()
                    .filter(x -> !idList.contains(x.getId())).collect(Collectors.toList());

            List<MemberDrawTemplateCondition> memberDrawTemplateConditions = memberDrawTemplateConditionConverter.toEntity(form.getConditions());
            memberDrawTemplateConditions.forEach(condition -> condition.setTemplateId(memberDrawTemplate.getId()));
            iMemberDrawTemplateConditionService.saveOrUpdateBatch(memberDrawTemplateConditions);
            iMemberDrawTemplateConditionService.removeByIds(preRemoveDrawTemplateConditions.stream().map(MemberDrawTemplateCondition::getId).collect(Collectors.toList()));
        }
        if (form.getResetTime() != null && Objects.equals(form.getEnable(), GlobalConstants.STATUS_YES)) {
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), memberDrawTemplate.getResetTime());
            ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
            Date date = Date.from(zdt.toInstant());
            String cron = CronUtils.getCron(date, DATEFORMAT_EVERYDAY);
            this.updateResetTimeToXxlJOB(cron);
            this.cleanMemberDrawInfo();

        }
        return true;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "draw", allEntries = true),
            @CacheEvict(cacheNames = "activityDrawTemplate", allEntries = true)
    })
    public Boolean addDrawTemplate(AdminMemberDrawTemplateVO form) {

        MemberDrawTemplate memberDrawTemplate = memberDrawTemplateConverter.toEntity(form);
        iMemberDrawTemplateService.save(memberDrawTemplate);

        List<MemberDrawTemplateCondition> memberDrawTemplateConditions = memberDrawTemplateConditionConverter.toEntity(form.getConditions());
        memberDrawTemplateConditions.forEach(condition -> condition.setTemplateId(memberDrawTemplate.getId()));
        iMemberDrawTemplateConditionService.saveBatch(memberDrawTemplateConditions);
        return true;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "activityDrawItem", allEntries = true)
    })
    public Boolean addDrawItem(AdminMemberDrawAwardItemVO itemVO) {
        MemberDrawAwardItem memberDrawAwardItem = memberDrawAwardItemConverter.toEntity(itemVO);
        return iMemberDrawAwardItemService.save(memberDrawAwardItem);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "activityDrawItem", allEntries = true)
    })
    public Boolean modifyDrawItem(AdminMemberDrawAwardItemVO itemVO) {
        MemberDrawAwardItem memberDrawAwardItem = memberDrawAwardItemConverter.toEntity(itemVO);
        return iMemberDrawAwardItemService.updateById(memberDrawAwardItem);
    }

    @Override
    public H5DrawInfoVO getDrawInfo(Long memberId) {
        log.info("getDrawInfo:{}", memberId);

        MemberDrawTemplate memberDrawTemplate = Optional.ofNullable(iMemberDrawTemplateService.findActivityDrawTemplate())
                .filter(x -> x.getStartTime().compareTo(LocalDateTime.now()) < 0 && x.getEndTime().compareTo(LocalDateTime.now()) > 0)
                .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

        List<MemberDrawTemplateCondition> conditionList = iMemberDrawTemplateConditionService.findByTemplateId(memberDrawTemplate.getId());
        H5DrawInfoVO drawInfoVO = new H5DrawInfoVO();
        //判斷是否有金幣兌換的條件
        conditionList.stream()
                .filter(x -> DrawTemplateConditionTypeEnum.fromIntValue(x.getType()).equals(DrawTemplateConditionTypeEnum.COIN_EXCHANGE))
                .findFirst()
                .ifPresent(condition -> drawInfoVO.setExchangeCoin(condition.getConditionNum()));

        List<H5DrawItemVO> itemList = iMemberDrawAwardItemService.findEnableDrawItem(memberDrawTemplate.getId());
        drawInfoVO.setDrawItemList(itemList);

        if (null == memberId) {
            drawInfoVO.setYesterdayRechargeAmount(memberDrawTemplate.getYesterdayRechargeAmount());
            drawInfoVO.setYesterdayBetAmount(memberDrawTemplate.getYesterdayBetAmount());
            drawInfoVO.setResetTime(memberDrawTemplate.getResetTime());
            drawInfoVO.setConditions(memberDrawTemplateConditionConverter.toH5VO(conditionList));
            return drawInfoVO;
        }

        Member member = iMemberService.getById(memberId);

        MemberCoin memberCoin = iMemberCoinService.lambdaQuery()
                .eq(MemberCoin::getMemberId, memberId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

        List<MemberDrawTemplateCondition> memberDrawTemplateConditionList = iMemberDrawTemplateConditionService.findByTemplateId(memberDrawTemplate.getId());
        List<DrawTemplateConditionTypeHandler> drawTemplateConditionTypeHandlerList = memberDrawTemplateConditionList
                .stream()
                .map(condition -> {
                    switch (DrawTemplateConditionTypeEnum.fromIntValue(condition.getType())) {
                        case FREE:
                            return freeDrawTemplateConditionTypeHandler;
                        case SINGLE_RECHARGE:
                            return singleRechargeDrawTemplateConditionTypeHandler;
                        case TOTAL_RECHARGE:
                            return totalRechargeDrawTemplateConditionTypeHandler;
                        case COIN_EXCHANGE:
                            return coinExchangeDrawTemplateConditionTypeHandler;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        DrawConditionVO drawConditionVO = new DrawConditionVO();
        drawConditionVO.setMember(member);
        drawConditionVO.setMemberDrawTemplate(memberDrawTemplate);
        drawConditionVO.setCurrentDrawVO(this.getMemberCurrentDrawVO(memberId));
        drawConditionVO.setConditionList(memberDrawTemplateConditionList);
        drawConditionVO.setResetTime(this.getResetStartTime());

        LocalDateTime startTime = this.getPreConditionStartTime(memberDrawTemplate);
        // 昨日充值金額
        BigDecimal yesterdayRechargeAmount = this.getYesterdayRechargeAmount(memberId, startTime);
        // 昨日充值金額
        BigDecimal yesterdayValidBetAmount = this.getYesterdayValidBetAmount(memberId, startTime);

        if (yesterdayValidBetAmount.compareTo(memberDrawTemplate.getYesterdayBetAmount()) >= 0 &&
                yesterdayRechargeAmount.compareTo(memberDrawTemplate.getYesterdayRechargeAmount()) >= 0) {
            drawConditionVO.setPrecondition(true);
        }

        int freeRemainDrawCount = drawTemplateConditionTypeHandlerList.stream()
                .mapToInt(handler -> handler.getRemainingFreeDraw(drawConditionVO))
                .sum();

        DrawTemplateConditionTypeHandler.Builder<Object> builder = new DrawTemplateConditionTypeHandler.Builder<>();
        drawTemplateConditionTypeHandlerList.stream().forEach(builder::addHandler);
        Integer nextConditionType = builder.build().getNextDrawConditionType(drawConditionVO);
        builder.clean();

        MemberTodayCurrentDrawVO drawVO = this.getMemberCurrentDrawVO(memberId);
        drawInfoVO.setTodayDrawCount(drawVO.getFreeDraw() + drawVO.getSingleRechargeDraw() + drawVO.getTotalRechargeDraw() + drawVO.getCoinExchangeDraw());
        drawInfoVO.setFreeRemainDrawCount(freeRemainDrawCount);
        drawInfoVO.setCoin(memberCoin.getCoin());
        drawInfoVO.setNextDrawConditionType(nextConditionType);
        drawInfoVO.setYesterdayRechargeAmount(memberDrawTemplate.getYesterdayRechargeAmount());
        drawInfoVO.setYesterdayBetAmount(memberDrawTemplate.getYesterdayBetAmount());
        drawInfoVO.setYesterdayMemberRechargeAmount(yesterdayRechargeAmount);
        drawInfoVO.setYesterdayMemberBetAmount(yesterdayValidBetAmount);

        drawInfoVO.setResetTime(memberDrawTemplate.getResetTime());
        drawInfoVO.setConditions(memberDrawTemplateConditionConverter.toH5VO(conditionList));
        return drawInfoVO;
    }

    @Override
    public H5RouletteVO getRouletteVO(Long memberId) {
        MemberDrawTemplate memberDrawTemplate = iMemberDrawTemplateService.findActivityDrawTemplate();
        Long currentInterval = redisTemplate.getExpire(RedisKey.DRAW_ACTION + ":" + memberId);
        H5RouletteVO rouletteVO = new H5RouletteVO();
        rouletteVO.setRoulette(memberDrawTemplate != null);
        rouletteVO.setFreeInterval(currentInterval == -2 ? null : currentInterval);
        return rouletteVO;
    }

    @Transactional
    public H5DrawItemVO doDrawAction(Long memberId) {

        String key = RedisUtils.buildKey(RedisConstants.DRAW_LOCK);
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock(10, TimeUnit.SECONDS);
            MemberDrawTemplate memberDrawTemplate = Optional.ofNullable(iMemberDrawTemplateService.findActivityDrawTemplate())
                    .filter(x -> x.getStartTime().compareTo(LocalDateTime.now()) < 0 && x.getEndTime().compareTo(LocalDateTime.now()) > 0)
                    .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));

            MemberTodayCurrentDrawVO memberTodayCurrentDrawVO = this.getMemberCurrentDrawVO(memberId);
            int todayDrawTimes = memberTodayCurrentDrawVO.getFreeDraw() + memberTodayCurrentDrawVO.getTotalRechargeDraw() + memberTodayCurrentDrawVO.getSingleRechargeDraw() + memberTodayCurrentDrawVO.getCoinExchangeDraw();
            int dailyDrawTimes = memberDrawTemplate.getDailyGiftNumber();
            if (dailyDrawTimes != -1 && todayDrawTimes > dailyDrawTimes) {
                throw new BizException(ResultCode.OVER_DRAW_TIMES);
            }

            LocalDateTime startTime = this.getPreConditionStartTime(memberDrawTemplate);
            // 昨日充值金額
            BigDecimal yesterdayRechargeAmount = this.getYesterdayRechargeAmount(memberId, startTime);
            // 昨日充值金額
            BigDecimal yesterdayValidBetAmount = this.getYesterdayValidBetAmount(memberId, startTime);

            if (yesterdayValidBetAmount.compareTo(memberDrawTemplate.getYesterdayBetAmount()) < 0) {
                throw new BizException(ResultCode.NOT_ACHIEVE_VALID_BET_AMOUNT);
            }

            if (yesterdayRechargeAmount.compareTo(memberDrawTemplate.getYesterdayRechargeAmount()) < 0) {
                throw new BizException(ResultCode.NOT_ACHIEVE_RECHARGE_AMOUNT);
            }

            List<MemberDrawTemplateCondition> memberDrawTemplateConditionList = iMemberDrawTemplateConditionService.findByTemplateId(memberDrawTemplate.getId());

            List<DrawTemplateConditionTypeHandler> drawTemplateConditionTypeHandlerList = memberDrawTemplateConditionList
                    .stream()
                    .map(condition -> {
                        switch (DrawTemplateConditionTypeEnum.fromIntValue(condition.getType())) {
                            case FREE:
                                return freeDrawTemplateConditionTypeHandler;
                            case SINGLE_RECHARGE:
                                return singleRechargeDrawTemplateConditionTypeHandler;
                            case TOTAL_RECHARGE:
                                return totalRechargeDrawTemplateConditionTypeHandler;
                            case COIN_EXCHANGE:
                                return coinExchangeDrawTemplateConditionTypeHandler;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<MemberDrawRecord> memberDrawRecordList = iMemberDrawRecordService.lambdaQuery()
                    .eq(MemberDrawRecord::getMemberId, memberId)
                    .list();

            final Member member = iMemberService.getById(memberId);

            final MemberTodayCurrentDrawVO currentDrawVO = this.getMemberCurrentDrawVO(memberId);

            DrawConditionVO drawConditionVO = new DrawConditionVO();
            drawConditionVO.setMember(member);
            drawConditionVO.setMemberDrawTemplate(memberDrawTemplate);
            drawConditionVO.setCurrentDrawVO(currentDrawVO);
            drawConditionVO.setConditionList(memberDrawTemplateConditionList);
            drawConditionVO.setRecordList(memberDrawRecordList);
            drawConditionVO.setPrecondition(true);
            drawConditionVO.setResetTime(this.getResetStartTime());

            DrawTemplateConditionTypeHandler.Builder<Object> builder = new DrawTemplateConditionTypeHandler.Builder<>();
            drawTemplateConditionTypeHandlerList.stream().forEach(builder::addHandler);
            MemberDrawAwardItem memberDrawAwardItem = builder.build().doDrawHandler(drawConditionVO);
            return memberDrawAwardItemConverter.toH5VO(memberDrawAwardItem);

        } finally {
            lock.unlock();
        }
    }

    private BigDecimal getYesterdayRechargeAmount(Long memberId, LocalDateTime startTime) {
        BigDecimal yesterdayRechargeAmount = BigDecimal.ZERO;
        Result<BigDecimal> yesterdayRechargeAmountResult = memberRechargeClient.memberTotalRechargeFromTo(memberId,
                startTime,
                startTime.plusDays(1)
        );
        if (Result.isSuccess(yesterdayRechargeAmountResult)) {
            yesterdayRechargeAmount = yesterdayRechargeAmountResult.getData();
        }
        return yesterdayRechargeAmount;
    }

    private BigDecimal getYesterdayValidBetAmount(Long memberId, LocalDateTime startTime) {
        BigDecimal validBetAmount = BigDecimal.ZERO;
        Result<BigDecimal> currentValidBetAmountResult = gameFeignClient.getMemberValidBet(
                memberId,
                null,
                null,
                null,
                startTime,
                startTime.plusDays(1)
        );
        if (Result.isSuccess(currentValidBetAmountResult)) {
            validBetAmount = currentValidBetAmountResult.getData();
        }
        return validBetAmount;
    }


}
