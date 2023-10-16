package com.c88.member.service.impl;

import com.c88.common.core.enums.EnableEnum;
import com.c88.common.core.enums.RechargeAwardTypeEnum;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.redis.constant.RedisKey;
import com.c88.common.web.exception.BizException;
import com.c88.member.common.enums.RechargeAwardLinkModeEnum;
import com.c88.member.common.enums.ValidEnum;
import com.c88.member.enums.RechargeAwardRecordStateEnum;
import com.c88.member.mapstruct.MemberRechargeAwardTemplateConverter;
import com.c88.member.pojo.entity.*;
import com.c88.member.pojo.vo.H5RechargeAwardCategoryVO;
import com.c88.member.pojo.vo.H5RechargeAwardVO;
import com.c88.member.service.*;
import com.c88.member.vo.OptionVO;
import com.c88.payment.client.MemberBankClient;
import com.c88.payment.client.MemberRechargeTypeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRechargeAwardServiceImpl implements IMemberRechargeAwardService {

    private final IMemberRechargeAwardTemplateService iMemberRechargeAwardTemplateService;

    private final IMemberRechargeAwardRecordService iMemberRechargeAwardRecordService;

    private final IMemberService iMemberService;

    private final IMemberAssociationRateService iMemberAssociationRateService;

    private final MemberRechargeAwardTemplateConverter memberRechargeAwardTemplateConverter;

    private final MemberRechargeTypeClient memberRechargeTypeClient;

    private final MemberBankClient memberBankClient;

    private final IMemberTagService iMemberTagService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public List<H5RechargeAwardCategoryVO> findRechargeAward(Long memberId) {
        // 取得充值類型分類
        Result<List<OptionVO<Integer>>> rechargeTypeOption = memberRechargeTypeClient.findRechargeTypeOption();
        if (!Result.isSuccess(rechargeTypeOption)) {
            throw new BizException(ResultCode.RESOURCE_NOT_FOUND);
        }
        List<OptionVO<Integer>> rechargeTypeOptions = rechargeTypeOption.getData();

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

        // 取得個人存送優惠模組ID
        Set<Long> personalTemplateId = iMemberRechargeAwardRecordService.lambdaQuery()
                .select(MemberRechargeAwardRecord::getTemplateId)
                .eq(MemberRechargeAwardRecord::getMemberId, memberId)
                .eq(MemberRechargeAwardRecord::getType, RechargeAwardTypeEnum.PERSONAL.getCode())
                .eq(MemberRechargeAwardRecord::getState, RechargeAwardRecordStateEnum.UNUSED.getCode())
                .list()
                .stream()
                .map(MemberRechargeAwardRecord::getTemplateId)
                .collect(Collectors.toSet());

        // todo 關聯模式未實做

        // 取得可使用的存送優惠
        List<MemberRechargeAwardTemplate> rechargeAwardTemplates = iMemberRechargeAwardTemplateService.lambdaQuery()
                .and(wrapper -> wrapper.eq(MemberRechargeAwardTemplate::getType, RechargeAwardTypeEnum.PLATFORM.getCode())
                        .eq(MemberRechargeAwardTemplate::getLinkMode, RechargeAwardLinkModeEnum.NULL.getCode())
                        .eq(MemberRechargeAwardTemplate::getEnable, EnableEnum.START.getCode())
                        .le(MemberRechargeAwardTemplate::getStartTime, LocalDateTime.now())
                        .ge(MemberRechargeAwardTemplate::getEndTime, LocalDateTime.now())
                )
                .or(CollectionUtils.isNotEmpty(personalTemplateId), wrapper -> wrapper.in(MemberRechargeAwardTemplate::getId, personalTemplateId))
                .list();

        LocalDate nowDate = LocalDate.now();
        List<H5RechargeAwardVO> rechargeAwards = rechargeAwardTemplates.stream()
                .filter(template -> {
                            // 判斷日次數
                            Integer dayNumber = template.getDayNumber();
                            if (dayNumber != 0 && dayNumber <= this.getRechargeAwardByDay(nowDate, template.getId(), memberId)) {
                                return Boolean.FALSE;
                            }

                            // 判斷周次數
                            Integer weekNumber = template.getWeekNumber();
                            if (weekNumber != 0 && weekNumber <= this.getRechargeAwardByWeek(template.getId(), memberId)) {
                                return Boolean.FALSE;
                            }

                            // 判斷月次數
                            Integer monthNumber = template.getMonthNumber();
                            if (monthNumber != 0 && monthNumber <= this.getRechargeAwardByMonth(nowDate, template.getId(), memberId)) {
                                return Boolean.FALSE;
                            }

                            // 判斷累計次數
                            Integer totalNumber = template.getTotalNumber();
                            if (totalNumber != 0 && totalNumber <= this.getRechargeAwardByTotal(template.getId(), memberId)) {
                                return Boolean.FALSE;
                            }

                            // 個人後面不檢核直接通過且有庫存
                            if (Objects.equals(template.getType(), RechargeAwardTypeEnum.PERSONAL.getCode())) {
                                if (iMemberRechargeAwardRecordService.lambdaQuery()
                                        .eq(MemberRechargeAwardRecord::getTemplateId, template.getId())
                                        .eq(MemberRechargeAwardRecord::getMemberId, memberId)
                                        .ne(MemberRechargeAwardRecord::getState, RechargeAwardRecordStateEnum.CANCEL.getCode())
                                        .count() <= this.getRechargeAwardByTotal(template.getId(), memberId)) {
                                    return Boolean.FALSE;
                                }
                                return Boolean.TRUE;
                            }

                            // 判斷手機要求
                            if (Objects.equals(template.getValidMobile(), ValidEnum.NEED_VALID.getValue()) && StringUtils.isBlank(member.getMobile())) {
                                return Boolean.FALSE;
                            }

                            // 判斷拒絕關聯戶
                            if (Objects.equals(template.getValidLink(), ValidEnum.NEED_VALID.getValue()) && Boolean.TRUE.equals(iMemberAssociationRateService.checkUsernameValue(member.getUserName()))) {
                                return Boolean.FALSE;
                            }

                            // 判斷是否需要綁卡
                            if (Objects.equals(template.getValidWithdraw(), ValidEnum.NEED_VALID.getValue())) {
                                Result<Boolean> memberBankExistResult = memberBankClient.checkMemberBankExist();
                                if (Boolean.FALSE.equals(memberBankExistResult.getData())) {
                                    return Boolean.FALSE;
                                }
                            }

                            // 判斷會員等級
                            if (!template.getVipIds().contains(0) && !template.getVipIds().contains(memberVip.getCurrentVipId())) {
                                return Boolean.FALSE;
                            }

                            // 判斷會員標籤
                            if (!template.getTagIds().contains(0) && template.getTagIds().stream().noneMatch(tags::contains)) {
                                return Boolean.FALSE;
                            }

                            // 判斷註冊時間
                            if (Objects.nonNull(template.getRegisterStartTime()) && member.getGmtCreate().isBefore(template.getRegisterStartTime())) {
                                return Boolean.FALSE;
                            }
                            if (Objects.nonNull(template.getRegisterEndTime()) && member.getGmtCreate().isAfter(template.getRegisterEndTime())) {
                                return Boolean.FALSE;
                            }

                            return Boolean.TRUE;
                        }
                )
                .sorted(Comparator.comparing(MemberRechargeAwardTemplate::getSort))
                .map(memberRechargeAwardTemplateConverter::toH5VO)
                .collect(Collectors.toList());

        return rechargeTypeOptions.stream()
                .map(option ->
                        H5RechargeAwardCategoryVO.builder()
                                .rechargeTypeId(option.getValue())
                                .rechargeAwards(rechargeAwards.stream()
                                        .filter(filter -> Objects.isNull(filter.getRechargeTypeIds()) ||
                                                filter.getRechargeTypeIds().contains(0) ||
                                                filter.getRechargeTypeIds().contains(option.getValue())
                                        )
                                        .collect(Collectors.toList())
                                )
                                .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 取得累積充值要求
     *
     * @param templateId 模組ID
     * @param memberId   會員ID
     * @return
     */
    private Integer getRechargeAwardByTotal(Long templateId, Long memberId) {
        String key = RedisKey.getRechargeAwardByTotalKey(templateId, memberId);
        Integer num = (Integer) redisTemplate.opsForValue().get(key);
        return Objects.isNull(num) ? 0 : num;
    }

    /**
     * 每月次數
     *
     * @param nowDate    日期
     * @param templateId 模組ID
     * @param memberId   會員ID
     * @return
     */
    private Integer getRechargeAwardByMonth(LocalDate nowDate, Long templateId, Long memberId) {
        String key = RedisKey.getRechargeAwardByMonthKey(nowDate, templateId, memberId);
        Integer num = (Integer) redisTemplate.opsForValue().get(key);
        return Objects.isNull(num) ? 0 : num;
    }

    /**
     * 每週次數
     *
     * @param templateId 模組ID
     * @param memberId   會員ID
     * @return
     */
    private Integer getRechargeAwardByWeek(Long templateId, Long memberId) {
        String key = RedisKey.getRechargeAwardByWeekKey(templateId, memberId);
        Integer num = (Integer) redisTemplate.opsForValue().get(key);
        return Objects.isNull(num) ? 0 : num;
    }

    /**
     * 每日次數
     *
     * @param nowDate    日期
     * @param templateId 模組ID
     * @param memberId   會員ID
     * @return
     */
    private Integer getRechargeAwardByDay(LocalDate nowDate, Long templateId, Long memberId) {
        String key = RedisKey.getRechargeAwardByDayKey(nowDate, templateId, memberId);
        Integer num = (Integer) redisTemplate.opsForValue().get(key);
        return Objects.isNull(num) ? 0 : num;
    }

}
