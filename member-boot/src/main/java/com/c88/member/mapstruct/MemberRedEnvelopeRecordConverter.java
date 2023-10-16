package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberRedEnvelopeRecord;
import com.c88.member.pojo.vo.MemberRedEnvelopeRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberRedEnvelopeRecordConverter extends BaseConverter<MemberRedEnvelopeRecord, MemberRedEnvelopeRecordVO> {
}
