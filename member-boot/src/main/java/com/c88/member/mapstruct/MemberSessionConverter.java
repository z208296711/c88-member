package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberSession;
import com.c88.member.pojo.vo.MemberSessionVO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberSessionConverter extends BaseConverter<MemberSession, MemberSessionVO> {

    @Named(value = "useMe")
    @Mapping(target = "ipArea", expression="java(item.getLoginIp() +'/'+item.getArea())")
    MemberSessionVO toVO(MemberSession item);

}