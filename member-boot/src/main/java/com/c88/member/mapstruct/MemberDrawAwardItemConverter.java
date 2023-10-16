package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.vo.AdminMemberDrawAwardItemVO;
import com.c88.member.pojo.vo.H5DrawItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberDrawAwardItemConverter extends BaseConverter<MemberDrawAwardItem, AdminMemberDrawAwardItemVO> {

    @Mappings({
            @Mapping(target = "recordId", source = "memberDrawRecord.id")
    })
    H5DrawItemVO toH5VO(MemberDrawAwardItem item);

    List<H5DrawItemVO> toH5VO(List<MemberDrawAwardItem> item);
}
