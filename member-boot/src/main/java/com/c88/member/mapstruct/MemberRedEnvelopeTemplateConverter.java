package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberRedEnvelopeTemplate;
import com.c88.member.pojo.form.AddRedEnvelopeTemplateForm;
import com.c88.member.pojo.form.ModifyRedEnvelopeTemplateForm;
import com.c88.member.pojo.vo.MemberRedEnvelopeTemplateVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberRedEnvelopeTemplateConverter extends BaseConverter<MemberRedEnvelopeTemplate, MemberRedEnvelopeTemplateVO> {

    MemberRedEnvelopeTemplate toEntity(AddRedEnvelopeTemplateForm form);

    MemberRedEnvelopeTemplate toEntity(ModifyRedEnvelopeTemplateForm form);


}
