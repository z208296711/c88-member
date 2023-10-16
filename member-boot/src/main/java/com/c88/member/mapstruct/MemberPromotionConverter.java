package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberPromotion;
import com.c88.member.pojo.form.AddPromotionForm;
import com.c88.member.pojo.form.ModifyPromotionForm;
import com.c88.member.pojo.vo.H5MemberPromotionVO;
import com.c88.member.pojo.vo.H5MemberSimplePromotionVO;
import com.c88.member.pojo.vo.MemberPromotionSortVO;
import com.c88.member.pojo.vo.MemberPromotionVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberPromotionConverter extends BaseConverter<MemberPromotion, MemberPromotionVO> {

    MemberPromotion toEntity(AddPromotionForm form);
    MemberPromotion toEntity(ModifyPromotionForm form);
    MemberPromotionSortVO toSortVo(MemberPromotion entity);
    H5MemberSimplePromotionVO toH5SimpleVo(MemberPromotion entity);
    H5MemberPromotionVO toH5Vo(MemberPromotion entity);
}
