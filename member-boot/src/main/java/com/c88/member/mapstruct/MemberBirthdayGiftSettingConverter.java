package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberBirthdayGiftRecord;
import com.c88.member.pojo.entity.MemberBirthdayGiftSetting;
import com.c88.member.pojo.vo.MemberBirthdayGiftRecordVO;
import com.c88.member.pojo.vo.MemberBirthdayGiftSettingVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberBirthdayGiftSettingConverter extends BaseConverter<MemberBirthdayGiftSetting, MemberBirthdayGiftSettingVO> {
}
