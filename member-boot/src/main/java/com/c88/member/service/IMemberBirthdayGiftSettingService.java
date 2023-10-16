package com.c88.member.service;

import com.c88.member.pojo.entity.MemberBirthdayGiftSetting;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.form.ModifyMemberBirthdayGiftSettingForm;
import com.c88.member.pojo.vo.MemberBirthdayGiftSettingVO;

/**
* @author user
* @description 针对表【member_birthday_gift_setting(生日禮金設定)】的数据库操作Service
* @createDate 2022-09-12 16:01:07
*/
public interface IMemberBirthdayGiftSettingService extends IService<MemberBirthdayGiftSetting> {

    MemberBirthdayGiftSettingVO findMemberBirthdayGiftSetting();

    Boolean modifyMemberBirthdayGiftSetting(ModifyMemberBirthdayGiftSettingForm form);
}
