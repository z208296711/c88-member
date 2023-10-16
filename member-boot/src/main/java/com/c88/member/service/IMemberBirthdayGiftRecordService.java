package com.c88.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.member.pojo.entity.MemberBirthdayGiftRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.form.FindMemberBirthdayGiftRecordForm;
import com.c88.member.pojo.vo.MemberBirthdayGiftRecordVO;

/**
* @author user
* @description 针对表【member_birthday_gift_record】的数据库操作Service
* @createDate 2022-09-12 14:52:17
*/
public interface IMemberBirthdayGiftRecordService extends IService<MemberBirthdayGiftRecord> {

    IPage<MemberBirthdayGiftRecordVO> findMemberBirthdayGiftRecord(FindMemberBirthdayGiftRecordForm form);
}
