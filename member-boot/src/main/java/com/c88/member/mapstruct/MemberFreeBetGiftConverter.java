package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberFreeBetGiftRecord;
import com.c88.member.pojo.vo.MemberFreeBetGiftRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberFreeBetGiftConverter extends BaseConverter<MemberFreeBetGiftRecord, MemberFreeBetGiftRecordVO> {

}
