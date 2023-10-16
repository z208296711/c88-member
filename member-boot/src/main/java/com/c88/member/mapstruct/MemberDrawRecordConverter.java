package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberDrawRecord;
import com.c88.member.pojo.vo.AdminMemberDrawItemVO;
import com.c88.member.pojo.vo.AdminMemberDrawRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberDrawRecordConverter extends BaseConverter<MemberDrawRecord, AdminMemberDrawRecordVO> {

    AdminMemberDrawItemVO toDrawItemVO(MemberDrawRecord memberDrawRecord);

    List<AdminMemberDrawItemVO> toDrawItemVO(List<MemberDrawRecord> memberDrawRecord);

}
