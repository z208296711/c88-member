package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.mapper.MemberBirthdayGiftSettingMapper;
import com.c88.member.mapstruct.MemberBirthdayGiftSettingConverter;
import com.c88.member.pojo.entity.MemberBirthdayGiftSetting;
import com.c88.member.pojo.form.ModifyMemberBirthdayGiftSettingForm;
import com.c88.member.pojo.vo.MemberBirthdayGiftSettingVO;
import com.c88.member.service.IMemberBirthdayGiftSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * @author user
 * @description 针对表【member_birthday_gift_setting(生日禮金設定)】的数据库操作Service实现
 * @createDate 2022-09-12 16:01:07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberBirthdayGiftSettingServiceImpl extends ServiceImpl<MemberBirthdayGiftSettingMapper, MemberBirthdayGiftSetting>
        implements IMemberBirthdayGiftSettingService {

    private final MemberBirthdayGiftSettingConverter memberBirthdayGiftSettingConverter;

    @Override
    public MemberBirthdayGiftSettingVO findMemberBirthdayGiftSetting() {
        return this.lambdaQuery()
                .oneOpt()
                .map(memberBirthdayGiftSettingConverter::toVo)
                .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public Boolean modifyMemberBirthdayGiftSetting(ModifyMemberBirthdayGiftSettingForm form) {
        return this.lambdaUpdate()
                .set(MemberBirthdayGiftSetting::getInbox, form.getInbox())
                .set(MemberBirthdayGiftSetting::getDays, form.getDays())
                .set(MemberBirthdayGiftSetting::getBetRate, form.getBetRate())
                .update();
    }

}




