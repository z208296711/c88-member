package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.core.util.StringCamelUtil;
import com.c88.member.common.enums.MemberSessionCheckEnum;
import com.c88.member.mapper.MemberAssociationRateMapper;
import com.c88.member.pojo.entity.MemberAssociationRate;
import com.c88.member.service.IMemberAssociationRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static com.c88.common.core.constant.TopicConstants.ASSOCIATION_CONFIG;

/**
 * 服务接口实现
 *
 * @author Allen
 * @description 由 Mybatisplus Code Generator 创建
 * @since 2022-06-05 23:45:46
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MemberAssociationRateServiceImpl extends ServiceImpl<MemberAssociationRateMapper, MemberAssociationRate> implements IMemberAssociationRateService {

    private static final String SYSTEM = "system";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MemberAssociationRate getAssociation() {
        return getAssociation(SYSTEM);
    }

    public MemberAssociationRate getAssociation(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<MemberAssociationRate>().eq(MemberAssociationRate::getUsername, username));
    }

    public int updateSystemAssociation(MemberAssociationRate rate) {
//        if(memberAssociationRate != null) {
        MemberAssociationRate m = new MemberAssociationRate();
        BeanUtils.copyProperties(rate, m);
        LambdaUpdateWrapper<MemberAssociationRate> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MemberAssociationRate::getUsername, SYSTEM);
        int count = this.getBaseMapper().update(m, updateWrapper);
        if (count > 0) {
            kafkaTemplate.send(ASSOCIATION_CONFIG, m);
        }
        return count;
    }

    public void updateUserAssociation(String username, Integer rateType, BigDecimal value) {
        MemberAssociationRate memberAssociationRate = this.getBaseMapper().selectOne(new LambdaQueryWrapper<MemberAssociationRate>().eq(MemberAssociationRate::getUsername, username));
        MemberSessionCheckEnum checkEnum = MemberSessionCheckEnum.fromIntValue(rateType);
        UpdateWrapper<MemberAssociationRate> updateWrapper = new UpdateWrapper<>();
        if (memberAssociationRate != null) { //update
            updateWrapper.eq("username", username);
            updateWrapper.set(checkEnum.getDbName(), value);

            this.getBaseMapper().update(null, updateWrapper);
        } else { //insert
            MemberAssociationRate associationRate = MemberAssociationRate.builder()
                    .account(rateType == MemberSessionCheckEnum.ACCOUNT.getValue() ? BigDecimal.ZERO : new BigDecimal(-1))
                    .realName(rateType == MemberSessionCheckEnum.REAL_NAME.getValue() ? BigDecimal.ZERO : new BigDecimal(-1))
                    .gameIp(rateType == MemberSessionCheckEnum.GAME_IP.getValue() ? BigDecimal.ZERO : new BigDecimal(-1))
                    .loginIp(rateType == MemberSessionCheckEnum.LOGIN_IP.getValue() ? BigDecimal.ZERO : new BigDecimal(-1))
                    .regIp(rateType == MemberSessionCheckEnum.REG_IP.getValue() ? BigDecimal.ZERO : new BigDecimal(-1))
                    .uuid(rateType == MemberSessionCheckEnum.UUID.getValue() ? BigDecimal.ZERO : new BigDecimal(-1))
                    .withdrawIp(rateType == MemberSessionCheckEnum.WITHDRAW_IP.getValue() ? BigDecimal.ZERO : new BigDecimal(-1))
                    .username(username).build();

            this.getBaseMapper().insert(associationRate);
        }
    }

    @Override
    public BigDecimal getAssociateRateByType(String username, int type) {
        MemberAssociationRate memberAssociationRate = this.getBaseMapper().selectOne(new LambdaQueryWrapper<MemberAssociationRate>().eq(MemberAssociationRate::getUsername, username));
        if (memberAssociationRate == null) return BigDecimal.ZERO;

        Class c = MemberAssociationRate.class;
        MemberSessionCheckEnum[] checkEnums = MemberSessionCheckEnum.values();
        for (MemberSessionCheckEnum checkEnum : checkEnums) {
            if (type == checkEnum.getValue()) {
                try {
                    Field field = c.getDeclaredField(StringCamelUtil.camel(new StringBuffer(checkEnum.toString().toLowerCase())).toString());
                    field.setAccessible(true);
                    return new BigDecimal(field.get(memberAssociationRate).toString());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Boolean checkUsernameValue(String username) {
        List<MemberAssociationRate> memberAssociationRates = this.lambdaQuery()
                .in(MemberAssociationRate::getUsername, List.of(username, SYSTEM))
                .list();

        MemberAssociationRate memberAssociationRateBySystem = memberAssociationRates.stream()
                .filter(memberAssociationRate -> SYSTEM.equals(memberAssociationRate.getUsername()))
                .findFirst()
                .orElse(MemberAssociationRate.builder().build());

        MemberAssociationRate memberAssociationRateByUsername = memberAssociationRates.stream()
                .filter(memberAssociationRate -> memberAssociationRate.getUsername().equals(username))
                .findFirst()
                .orElse(MemberAssociationRate.builder()
                        .regIp(new BigDecimal(-1))
                        .loginIp(new BigDecimal(-1))
                        .withdrawIp(new BigDecimal(-1))
                        .gameIp(new BigDecimal(-1))
                        .uuid(new BigDecimal(-1))
                        .realName(new BigDecimal(-1))
                        .account(new BigDecimal(-1))
                        .build()
                );

        BigDecimal value = BigDecimal.ZERO;
        // 註冊ip
        if (memberAssociationRateByUsername.getRegIp().compareTo(BigDecimal.ZERO) < 0) {
            value = value.add(memberAssociationRateBySystem.getRegIp());
        }

        // 登入ip
        if (memberAssociationRateByUsername.getLoginIp().compareTo(BigDecimal.ZERO) < 0) {
            value = value.add(memberAssociationRateBySystem.getLoginIp());
        }

        // 提款ip
        if (memberAssociationRateByUsername.getWithdrawIp().compareTo(BigDecimal.ZERO) < 0) {
            value = value.add(memberAssociationRateBySystem.getWithdrawIp());
        }

        // 遊戲ip
        if (memberAssociationRateByUsername.getGameIp().compareTo(BigDecimal.ZERO) < 0) {
            value = value.add(memberAssociationRateBySystem.getGameIp());
        }

        // 電腦標識
        if (memberAssociationRateByUsername.getUuid().compareTo(BigDecimal.ZERO) < 0) {
            value = value.add(memberAssociationRateBySystem.getUuid());
        }

        // 真實姓名
        if (memberAssociationRateByUsername.getRealName().compareTo(BigDecimal.ZERO) < 0) {
            value = value.add(memberAssociationRateBySystem.getRealName());
        }

        // 相似帳號
        if (memberAssociationRateByUsername.getAccount().compareTo(BigDecimal.ZERO) < 0) {
            value = value.add(memberAssociationRateBySystem.getAccount());
        }

        return value.compareTo(memberAssociationRateBySystem.getThreshold()) > 0;
    }

}