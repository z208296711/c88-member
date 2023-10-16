package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.vo.AdminMemberVO;
import com.c88.member.pojo.vo.MemberVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberConverter extends BaseConverter<Member, MemberVO> {

    AdminMemberVO toAdminMemberVO(Member member);
}
