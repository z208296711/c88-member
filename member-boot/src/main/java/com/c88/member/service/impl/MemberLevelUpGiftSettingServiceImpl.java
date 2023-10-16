package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.pojo.entity.MemberLevelUpGiftSetting;
import com.c88.member.pojo.form.UpdateMemberLevelUpGiftForm;
import com.c88.member.service.IMemberLevelUpGiftSettingService;
import com.c88.member.mapper.MemberLevelUpGiftSettingMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *
 */
@Service
public class MemberLevelUpGiftSettingServiceImpl extends ServiceImpl<MemberLevelUpGiftSettingMapper, MemberLevelUpGiftSetting> implements IMemberLevelUpGiftSettingService {


    @Override
    @Cacheable(cacheNames = "member", key = "'memberLevelUpGiftSetting'")
    public MemberLevelUpGiftSetting findLevelUpGiftSetting() {
        Optional<MemberLevelUpGiftSetting> memberLevelUpGiftSettingOpt = this.lambdaQuery().oneOpt();
        return memberLevelUpGiftSettingOpt.orElse(new MemberLevelUpGiftSetting());
    }

    @Override
    @CacheEvict(cacheNames = "member", key = "'memberLevelUpGiftSetting'")
    public boolean updateLevelUpGiftSetting(UpdateMemberLevelUpGiftForm form) {
        Optional<MemberLevelUpGiftSetting> memberLevelUpGiftSettingOpt = this.lambdaQuery().oneOpt();
        MemberLevelUpGiftSetting memberLevelUpGiftSetting = memberLevelUpGiftSettingOpt.orElse(new MemberLevelUpGiftSetting());
        memberLevelUpGiftSetting.setBetRate(form.getBetRate());
        return this.saveOrUpdate(memberLevelUpGiftSetting);
    }
}




