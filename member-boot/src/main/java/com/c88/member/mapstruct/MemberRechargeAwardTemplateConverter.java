package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberRechargeAwardTemplate;
import com.c88.member.pojo.form.AddRechargeAwardTemplateForm;
import com.c88.member.pojo.form.ModifyRechargeAwardTemplateForm;
import com.c88.member.pojo.vo.H5RechargeAwardVO;
import com.c88.member.pojo.vo.MemberRechargeAwardTemplateDrawOptionVO;
import com.c88.member.pojo.vo.MemberRechargeAwardTemplateVO;
import com.c88.member.pojo.vo.RechargeAwardTemplateLinkModeVO;
import com.c88.member.vo.MemberRechargeAwardTemplateClientVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberRechargeAwardTemplateConverter extends BaseConverter<MemberRechargeAwardTemplate, MemberRechargeAwardTemplateVO> {

    MemberRechargeAwardTemplate toEntity(AddRechargeAwardTemplateForm form);

    MemberRechargeAwardTemplateClientVO toClientVO(MemberRechargeAwardTemplate entity);

    MemberRechargeAwardTemplate toEntity(ModifyRechargeAwardTemplateForm form);

    RechargeAwardTemplateLinkModeVO toLinkModeVO(MemberRechargeAwardTemplate entity);

    H5RechargeAwardVO toH5VO(MemberRechargeAwardTemplate entity);

    MemberRechargeAwardTemplateDrawOptionVO toDrawVO(MemberRechargeAwardTemplate entity);

}
