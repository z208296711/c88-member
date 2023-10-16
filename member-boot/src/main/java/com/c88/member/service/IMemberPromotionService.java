package com.c88.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.member.pojo.entity.MemberPromotion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.entity.MemberPromotionCategory;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.MemberPromotionSortVO;
import com.c88.member.pojo.vo.MemberPromotionVO;

import java.util.List;
import java.util.Map;

/**
* @author user
* @description 针对表【member_promotion(優惠活動)】的数据库操作Service
* @createDate 2022-09-05 18:27:47
*/
public interface IMemberPromotionService extends IService<MemberPromotion> {

    IPage<MemberPromotionVO> findPromotion(FindPromotionForm form);

    IPage<MemberPromotionSortVO> findPromotionSort(FindPromotionSortForm form, List<MemberPromotionCategory> list);

    Boolean addPromotion(AddPromotionForm form);

    Boolean modifyPromotion(ModifyPromotionForm form);

    Boolean deletePromotion(List<Integer> ids);

    Boolean modifyPromotionSort(Map<Integer, Integer> map);

    Boolean modifyPromotionSortTopBottom(ModifyPromotionSortTopBottomForm form);

}
