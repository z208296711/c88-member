package com.c88.member.service;

import com.c88.member.pojo.entity.MemberFreeBetGiftSetting;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface IMemberFreeBetGiftSettingService extends IService<MemberFreeBetGiftSetting> {

    List<MemberFreeBetGiftSetting> findAll() ;

}
