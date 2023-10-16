package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberDrawTemplateCondition;
import com.c88.member.pojo.vo.AdminMemberDrawTemplateConditionVO;
import com.c88.member.pojo.vo.H5DrawConditionVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberDrawTemplateConditionConverter extends BaseConverter<MemberDrawTemplateCondition, AdminMemberDrawTemplateConditionVO> {

    H5DrawConditionVO toH5VO(MemberDrawTemplateCondition memberDrawTemplateCondition);

    List<H5DrawConditionVO> toH5VO(List<MemberDrawTemplateCondition> memberDrawTemplateCondition);

}
