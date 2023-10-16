package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberRedEnvelopeTemplateCondition;
import com.c88.member.pojo.form.AddMemberRedEnvelopeTemplateConditionForm;
import com.c88.member.pojo.vo.MemberRedEnvelopeTemplateConditionVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberRedEnvelopeTemplateConditionConverter extends BaseConverter<MemberRedEnvelopeTemplateCondition, MemberRedEnvelopeTemplateConditionVO> {

    MemberRedEnvelopeTemplateCondition toEntity(AddMemberRedEnvelopeTemplateConditionForm form);

}
