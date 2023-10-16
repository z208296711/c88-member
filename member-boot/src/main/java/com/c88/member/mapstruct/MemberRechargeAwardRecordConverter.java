package com.c88.member.mapstruct;

import com.c88.member.event.RechargeAwardRecordModel;
import com.c88.member.pojo.entity.MemberRechargeAwardRecord;
import com.c88.member.vo.AllMemberPersonalRechargeAwardRecordByTemplateIdVO;
import com.c88.member.pojo.vo.MemberPersonalRechargeAwardRecordVO;
import com.c88.member.pojo.vo.MemberRechargeAwardRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberRechargeAwardRecordConverter {

    MemberRechargeAwardRecordVO toVO(MemberRechargeAwardRecord entity);

    AllMemberPersonalRechargeAwardRecordByTemplateIdVO toAllPersonalVO(MemberRechargeAwardRecord entity);

    MemberPersonalRechargeAwardRecordVO toPersonalVO(MemberRechargeAwardRecord entity);

    MemberRechargeAwardRecord toEntity(RechargeAwardRecordModel rechargeAwardRecord);
}
