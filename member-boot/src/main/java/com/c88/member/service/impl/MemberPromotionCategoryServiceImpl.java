package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.web.enums.SortTypeEnum;
import com.c88.member.mapper.MemberPromotionCategoryMapper;
import com.c88.member.mapstruct.MemberPromotionCategoryConverter;
import com.c88.member.pojo.entity.MemberPromotionCategory;
import com.c88.member.pojo.form.AddPromotionCategoryForm;
import com.c88.member.pojo.form.FindPromotionCategoryForm;
import com.c88.member.pojo.form.ModifyPromotionCategoryForm;
import com.c88.member.pojo.form.ModifyPromotionCategorySortTopBottomForm;
import com.c88.member.pojo.vo.MemberPromotionCategoryVO;
import com.c88.member.service.IMemberPromotionCategoryService;
import com.c88.member.vo.OptionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author user
 * @description 针对表【member_promotion_category(優惠活動分類)】的数据库操作Service实现
 * @createDate 2022-09-05 18:27:47
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberPromotionCategoryServiceImpl extends ServiceImpl<MemberPromotionCategoryMapper, MemberPromotionCategory>
        implements IMemberPromotionCategoryService {

    private final MemberPromotionCategoryConverter memberPromotionCategoryConverter;

    @Override
    public IPage<MemberPromotionCategoryVO> findPromotionCategory(FindPromotionCategoryForm form) {
        return this.lambdaQuery()
                .orderByAsc(MemberPromotionCategory::getSort)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberPromotionCategoryConverter::toVo);
    }

    @Override
    public List<OptionVO<Integer>> findPromotionCategoryOption() {
        return this.list()
                .stream()
                .map(promotionCategory ->
                        OptionVO.<Integer>builder()
                                .value(promotionCategory.getId())
                                .label(promotionCategory.getName())
                                .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public Boolean addPromotionCategory(AddPromotionCategoryForm form) {
        MemberPromotionCategory memberPromotionCategory = memberPromotionCategoryConverter.toEntity(form);
        memberPromotionCategory.setSort(
                this.lambdaQuery()
                        .select(MemberPromotionCategory::getSort)
                        .orderByAsc(MemberPromotionCategory::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberPromotionCategory.builder().sort(this.count()).build())
                        .getSort() - 1
        );
        return this.save(memberPromotionCategory);
    }

    @Override
    public Boolean modifyPromotionCategory(ModifyPromotionCategoryForm form) {
        return this.updateById(memberPromotionCategoryConverter.toEntity(form));
    }

    @Override
    public Boolean deletePromotionCategory(List<Integer> ids) {
        return this.removeByIds(ids);
    }

    @Override
    public Boolean modifyPromotionCategorySort(Map<Integer, Integer> map) {
        return this.updateBatchById(
                map.entrySet()
                        .stream()
                        .map(sort -> MemberPromotionCategory.builder().id(sort.getKey()).sort(sort.getValue()).build())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Boolean modifyPromotionCategorySortTopBottom(ModifyPromotionCategorySortTopBottomForm form) {
        SortTypeEnum sortType = SortTypeEnum.getEnum(form.getSortType());
        switch (sortType) {
            case TOP:
                int minSort = this.lambdaQuery()
                        .select(MemberPromotionCategory::getSort)
                        .ne(MemberPromotionCategory::getId, form.getId())
                        .orderByAsc(MemberPromotionCategory::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberPromotionCategory.builder().sort(this.count()).build())
                        .getSort() - 1;
                return this.lambdaUpdate()
                        .eq(MemberPromotionCategory::getId, form.getId())
                        .set(MemberPromotionCategory::getSort, minSort)
                        .update();
            case BOTTOM:
                int maxSort = this.lambdaQuery()
                        .select(MemberPromotionCategory::getSort)
                        .ne(MemberPromotionCategory::getId, form.getId())
                        .orderByDesc(MemberPromotionCategory::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberPromotionCategory.builder().sort(this.count()).build())
                        .getSort() + 1;
                return this.lambdaUpdate()
                        .eq(MemberPromotionCategory::getId, form.getId())
                        .set(MemberPromotionCategory::getSort, maxSort)
                        .update();
            default:
                return Boolean.FALSE;
        }
    }
}




