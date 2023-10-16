package com.c88.member.controller.h5;

import com.c88.common.core.constant.RedisConstants;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.MemberUtils;
import com.c88.member.common.enums.LevelUpModeEnum;
import com.c88.member.common.enums.LevelUpNotificationStateEnum;
import com.c88.member.mapstruct.MemberVipConfigConverter;
import com.c88.member.pojo.entity.MemberLevelUpGiftRecord;
import com.c88.member.pojo.entity.MemberVip;
import com.c88.member.pojo.entity.MemberVipConfig;
import com.c88.member.pojo.vo.H5VIPNotificationVO;
import com.c88.member.pojo.vo.MemberVipRuleVO;
import com.c88.member.pojo.vo.VipCenterVO;
import com.c88.member.pojo.vo.VipDetailVO;
import com.c88.member.service.IMemberLevelUpGiftRecordService;
import com.c88.member.service.IMemberVipConfigService;
import com.c88.member.service.IMemberVipRuleService;
import com.c88.member.service.IMemberVipService;
import com.c88.member.service.impl.MemberServiceImpl;
import com.c88.payment.client.MemberRechargeClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 會員管理
 */
@Slf4j
@Tag(name = "H5會員等級(VIP)")
@RestController
@RequestMapping("/h5/vip")
@RequiredArgsConstructor
public class H5MemberVipController {

    private final MemberServiceImpl memberService;

    private final IMemberVipConfigService iMemberVipConfigService;

    private final IMemberVipRuleService iMemberVipRuleService;

    private final MemberVipConfigConverter memberVipConfigConverter;

    private final MemberRechargeClient memberRechargeClient;

    private final RedisTemplate<String, Object> redisTemplate;

    private final IMemberVipService iMemberVipService;

    private final IMemberLevelUpGiftRecordService iMemberLevelUpGiftRecordService;

    @Operation(summary = "取得 VIP 晉級通知")
    @GetMapping("/level/up/notification")
    public Result<H5VIPNotificationVO> getVipNotification() {
        Long memberId = MemberUtils.getMemberId();
        if (null == memberId) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }

        Integer levelup = (Integer) redisTemplate.opsForValue().get(RedisUtils.buildKey(RedisConstants.VIP_LEVEL_UP_NOTIFICATION, memberId));
        if (levelup == null) {
            levelup = 0;
        }

        MemberVip memberVip = iMemberVipService.lambdaQuery()
                .eq(MemberVip::getMemberId, memberId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.PARAM_ERROR));

        Integer vipGiftRecordCount = iMemberLevelUpGiftRecordService.lambdaQuery()
                .eq(MemberLevelUpGiftRecord::getMemberId, memberId)
                .eq(MemberLevelUpGiftRecord::getVipId, memberVip.getCurrentVipId())
                .count();

        redisTemplate.delete(RedisUtils.buildKey(RedisConstants.VIP_LEVEL_UP_NOTIFICATION, memberId));
        return Result.success(H5VIPNotificationVO
                .builder()
                .levelUp(levelup)
                .notification(vipGiftRecordCount == 0 ? LevelUpNotificationStateEnum.UN_RECEIVE.getValue() : LevelUpNotificationStateEnum.READ_VIP_AUTH.getValue())
                .build());
    }

    @Operation(summary = "取得VIP中心資訊")
    @GetMapping("/center")
    public Result<VipCenterVO> getVipCenter() {

        Long memberId = MemberUtils.getMemberId();
        String username = MemberUtils.getUsername();
        List<MemberVipConfig> memberVipConfigList = iMemberVipConfigService.findAllVipConfig();

        List<MemberVipConfig> autoLevelList = memberVipConfigList
                .stream()
                .filter(config -> config.getLevelUpMode().equals(LevelUpModeEnum.AUTO))
                .collect(Collectors.toList());

        // 處理當前等級 顯示 下一個等級 升級條件資訊
        memberVipConfigList.forEach(config -> {
            if (config.getLevelUpMode().equals(LevelUpModeEnum.MANUAL)) {
                return;
            }
            int level = autoLevelList.indexOf(config);
            if (level != autoLevelList.size() - 1) {
                MemberVipConfig memberVipConfig = autoLevelList.get(level + 1);
                config.setLevelUpRecharge(memberVipConfig.getLevelUpRecharge());
                config.setLevelUpExp(memberVipConfig.getLevelUpExp());
            }
        });

        List<VipDetailVO> vipDetailVOList = memberVipConfigConverter.toVipDetailVO(memberVipConfigList);

        MemberVipRuleVO ruleVO = iMemberVipRuleService.getVipRule();

        Integer vipId = null;
        String vipName = null;
        BigDecimal recharge = BigDecimal.ZERO;
        BigDecimal exp = BigDecimal.ZERO;
        //判斷是否登入
        if (memberId != null) {
            MemberVip memberVip = memberService.getMemberVip(memberId);
            vipId = memberVip.getCurrentVipId();
            vipName = memberVipConfigList.stream()
                    .filter(config -> config.getId().equals(memberVip.getCurrentVipId()))
                    .findFirst()
                    .map(MemberVipConfig::getName)
                    .orElse("");

            Result<BigDecimal> totalRechargeResult = memberRechargeClient.memberTotalRechargeFromTo(memberId, null, null);
            if (Result.isSuccess(totalRechargeResult)) {
                recharge = totalRechargeResult.getData();
            }
            // 取有效流水
            exp = memberVip.getCurrentVipValidBet();
        }

        return Result.success(VipCenterVO.builder()
                .vipId(vipId)
                .vipName(vipName)
                .recharge(recharge)
                .exp(exp)
                .vipList(vipDetailVOList)
                .rule(ruleVO.getContent())
                .build());
    }

}
