package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberLevelUpGiftRecord;
import com.c88.member.pojo.vo.MemberLevelUpGiftRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberLevelUpGiftConverter extends BaseConverter<MemberLevelUpGiftRecord, MemberLevelUpGiftRecordVO> {

}
