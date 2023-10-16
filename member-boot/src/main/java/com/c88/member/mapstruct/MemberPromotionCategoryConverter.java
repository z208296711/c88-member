package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberPromotionCategory;
import com.c88.member.pojo.form.AddPromotionCategoryForm;
import com.c88.member.pojo.form.ModifyPromotionCategoryForm;
import com.c88.member.pojo.vo.MemberPromotionCategoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberPromotionCategoryConverter extends BaseConverter<MemberPromotionCategory, MemberPromotionCategoryVO> {

    MemberPromotionCategory toEntity(AddPromotionCategoryForm form);
    MemberPromotionCategory toEntity(ModifyPromotionCategoryForm form);
}
