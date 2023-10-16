package com.c88.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.member.pojo.entity.MemberPromotionCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.form.AddPromotionCategoryForm;
import com.c88.member.pojo.form.FindPromotionCategoryForm;
import com.c88.member.pojo.form.ModifyPromotionCategoryForm;
import com.c88.member.pojo.form.ModifyPromotionCategorySortTopBottomForm;
import com.c88.member.pojo.vo.MemberPromotionCategoryVO;
import com.c88.member.vo.OptionVO;

import java.util.List;
import java.util.Map;

/**
* @author user
* @description 针对表【member_promotion_category(優惠活動分類)】的数据库操作Service
* @createDate 2022-09-05 18:27:47
*/
public interface IMemberPromotionCategoryService extends IService<MemberPromotionCategory> {

    IPage<MemberPromotionCategoryVO> findPromotionCategory(FindPromotionCategoryForm form);

    List<OptionVO<Integer>> findPromotionCategoryOption();

    Boolean addPromotionCategory(AddPromotionCategoryForm form);

    Boolean modifyPromotionCategory(ModifyPromotionCategoryForm form);

    Boolean deletePromotionCategory(List<Integer> ids);

    Boolean modifyPromotionCategorySort(Map<Integer, Integer> map);

    Boolean modifyPromotionCategorySortTopBottom(ModifyPromotionCategorySortTopBottomForm form);
}
