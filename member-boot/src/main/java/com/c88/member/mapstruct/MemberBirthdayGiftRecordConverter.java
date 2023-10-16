package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberBirthdayGiftRecord;
import com.c88.member.pojo.vo.MemberBirthdayGiftRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberBirthdayGiftRecordConverter extends BaseConverter<MemberBirthdayGiftRecord, MemberBirthdayGiftRecordVO> {
}
