package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.web.enums.SortTypeEnum;
import com.c88.member.mapper.MemberPromotionMapper;
import com.c88.member.mapstruct.MemberPromotionConverter;
import com.c88.member.pojo.entity.MemberPromotion;
import com.c88.member.pojo.entity.MemberPromotionCategory;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.MemberPromotionSortVO;
import com.c88.member.pojo.vo.MemberPromotionVO;
import com.c88.member.service.IMemberPromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author user
 * @description 针对表【member_promotion(優惠活動)】的数据库操作Service实现
 * @createDate 2022-09-05 18:27:47
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberPromotionServiceImpl extends ServiceImpl<MemberPromotionMapper, MemberPromotion>
        implements IMemberPromotionService {

    private final MemberPromotionConverter memberPromotionConverter;

    @Override
    public IPage<MemberPromotionVO> findPromotion(FindPromotionForm form) {
        return this.lambdaQuery()
                .like(StringUtils.isNotBlank(form.getTitle()), MemberPromotion::getTitle, form.getTitle())
                .eq(Objects.nonNull(form.getCategoryId()), MemberPromotion::getCategoryId, form.getCategoryId())
                .orderByAsc(MemberPromotion::getSort)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberPromotionConverter::toVo);
    }

    @Override
    public IPage<MemberPromotionSortVO> findPromotionSort(FindPromotionSortForm form, List<MemberPromotionCategory> memberPromotionCategories) {
        Map<Integer, String> promotionCategoryMap = memberPromotionCategories.stream()
                .collect(Collectors.toMap(MemberPromotionCategory::getId, MemberPromotionCategory::getName));

        return this.lambdaQuery()
                .orderByAsc(MemberPromotion::getSort)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(x -> {
                            MemberPromotionSortVO memberPromotionSortVO = memberPromotionConverter.toSortVo(x);
                            memberPromotionSortVO.setCategoryName(promotionCategoryMap.getOrDefault(x.getCategoryId(), ""));
                            return memberPromotionSortVO;
                        }
                );
    }

    @Override
    public Boolean addPromotion(AddPromotionForm form) {
        MemberPromotion memberPromotion = memberPromotionConverter.toEntity(form);

        memberPromotion.setSort(
                this.lambdaQuery()
                        .select(MemberPromotion::getSort)
                        .orderByAsc(MemberPromotion::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberPromotion.builder().sort(this.count()).build())
                        .getSort() - 1
        );

        return this.save(memberPromotion);
    }

    @Override
    @Transactional
    public Boolean modifyPromotion(ModifyPromotionForm form) {
        boolean update = this.updateById(memberPromotionConverter.toEntity(form));
        if (update) {
            this.update(null,
                    Wrappers.<MemberPromotion>lambdaUpdate()
                            .eq(MemberPromotion::getId, form.getId())
                            .set(MemberPromotion::getCategoryId, form.getCategoryId())
            );
        }

        return update;
    }

    @Override
    public Boolean deletePromotion(List<Integer> ids) {
        return this.removeByIds(ids);
    }

    @Override
    public Boolean modifyPromotionSort(Map<Integer, Integer> map) {
        return this.updateBatchById(
                map.entrySet()
                        .stream()
                        .map(sort -> MemberPromotion.builder().id(sort.getKey()).sort(sort.getValue()).build())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Boolean modifyPromotionSortTopBottom(ModifyPromotionSortTopBottomForm form) {
        SortTypeEnum sortType = SortTypeEnum.getEnum(form.getSortType());
        switch (sortType) {
            case TOP:
                int minSort = this.lambdaQuery()
                        .select(MemberPromotion::getSort)
                        .ne(MemberPromotion::getId, form.getId())
                        .orderByAsc(MemberPromotion::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberPromotion.builder().sort(this.count()).build())
                        .getSort() - 1;
                return this.lambdaUpdate()
                        .eq(MemberPromotion::getId, form.getId())
                        .set(MemberPromotion::getSort, minSort)
                        .update();
            case BOTTOM:
                int maxSort = this.lambdaQuery()
                        .select(MemberPromotion::getSort)
                        .ne(MemberPromotion::getId, form.getId())
                        .orderByDesc(MemberPromotion::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberPromotion.builder().sort(this.count()).build())
                        .getSort() + 1;
                return this.lambdaUpdate()
                        .eq(MemberPromotion::getId, form.getId())
                        .set(MemberPromotion::getSort, maxSort)
                        .update();
            default:
                return Boolean.FALSE;
        }
    }


}




