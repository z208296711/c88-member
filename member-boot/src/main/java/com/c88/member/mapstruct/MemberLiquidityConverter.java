package com.c88.member.mapstruct;

import com.c88.member.pojo.entity.MemberLiquidity;
import com.c88.member.pojo.vo.MemberLiquidityVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberLiquidityConverter {

    MemberLiquidityVO toVO(MemberLiquidity entity);

}
