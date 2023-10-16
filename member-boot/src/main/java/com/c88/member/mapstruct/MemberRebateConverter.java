package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberRebateConfig;
import com.c88.member.pojo.form.UpdateMemberRebateForm;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberRebateConverter extends BaseConverter<MemberRebateConfig, UpdateMemberRebateForm> {

}
