package com.c88.member.service;

import com.c88.member.pojo.entity.MemberLevelUpGiftSetting;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.form.UpdateMemberLevelUpGiftForm;

/**
 *
 */
public interface IMemberLevelUpGiftSettingService extends IService<MemberLevelUpGiftSetting> {

    MemberLevelUpGiftSetting findLevelUpGiftSetting();

    boolean updateLevelUpGiftSetting(UpdateMemberLevelUpGiftForm form);

}
