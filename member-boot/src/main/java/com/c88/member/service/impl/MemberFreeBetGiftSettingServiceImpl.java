package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.mapper.MemberFreeBetGiftSettingMapper;
import com.c88.member.pojo.entity.MemberFreeBetGiftSetting;
import com.c88.member.service.IMemberFreeBetGiftSettingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class MemberFreeBetGiftSettingServiceImpl extends ServiceImpl<MemberFreeBetGiftSettingMapper, MemberFreeBetGiftSetting>
        implements IMemberFreeBetGiftSettingService {

    @Override
    public List<MemberFreeBetGiftSetting> findAll() {
        return this.lambdaQuery().list();
    }
}




