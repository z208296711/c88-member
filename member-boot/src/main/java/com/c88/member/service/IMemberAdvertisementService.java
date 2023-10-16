package com.c88.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.member.pojo.entity.MemberAdvertisement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.MemberAdvertisementSortVO;
import com.c88.member.pojo.vo.MemberAdvertisementVO;

import java.util.List;
import java.util.Map;

/**
* @author user
* @description 针对表【member_advertisement(廣告)】的数据库操作Service
* @createDate 2022-09-05 18:27:47
*/
public interface IMemberAdvertisementService extends IService<MemberAdvertisement> {

    IPage<MemberAdvertisementVO> findAdvertisement(FindAdvertisementForm form);

    IPage<MemberAdvertisementSortVO> findAdvertisementSort(FindAdvertisementSortForm form);

    Boolean addAdvertisement(AddAdvertisementForm form);

    Boolean modifyAdvertisement(ModifyAdvertisementForm form);

    Boolean deleteAdvertisement(List<Integer> ids);

    Boolean modifyAdvertisementSort(Map<Integer, Integer> form);

    Boolean modifyAdvertisementSortTopBottom(ModifyAdvertisementSortTopBottomForm form);

}
