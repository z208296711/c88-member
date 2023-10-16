package com.c88.member.mapstruct;

import com.c88.member.pojo.entity.MemberAdvertisement;
import com.c88.member.pojo.form.AddAdvertisementForm;
import com.c88.member.pojo.form.FindAdvertisementSortForm;
import com.c88.member.pojo.form.ModifyAdvertisementForm;
import com.c88.member.pojo.vo.H5MemberAdvertisementVO;
import com.c88.member.pojo.vo.MemberAdvertisementSortVO;
import com.c88.member.pojo.vo.MemberAdvertisementVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberAdvertisementConverter {

    H5MemberAdvertisementVO toH5Vo(MemberAdvertisement entity);

    MemberAdvertisementVO toVo(MemberAdvertisement entity);

    MemberAdvertisement toEntity(MemberAdvertisementVO vo);

    List<MemberAdvertisementVO> toVo(List<MemberAdvertisement> entity);

    List<MemberAdvertisement> toEntity(List<MemberAdvertisementVO> vo);

    MemberAdvertisement toEntity(AddAdvertisementForm form);

    MemberAdvertisement toEntity(ModifyAdvertisementForm form);

    MemberAdvertisementSortVO toSortVo(MemberAdvertisement entity);

}
