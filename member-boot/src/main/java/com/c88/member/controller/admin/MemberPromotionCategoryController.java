package com.c88.member.controller.admin;

import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.pojo.entity.MemberPromotion;
import com.c88.member.pojo.form.AddPromotionCategoryForm;
import com.c88.member.pojo.form.FindPromotionCategoryForm;
import com.c88.member.pojo.form.ModifyPromotionCategoryForm;
import com.c88.member.pojo.form.ModifyPromotionCategorySortTopBottomForm;
import com.c88.member.pojo.vo.MemberPromotionCategoryVO;
import com.c88.member.service.IMemberPromotionCategoryService;
import com.c88.member.service.IMemberPromotionService;
import com.c88.member.vo.OptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "『後台』優惠活動分類")
@RestController
@RequestMapping("/api/v1/promotion/category")
@RequiredArgsConstructor
public class MemberPromotionCategoryController {

    private final IMemberPromotionCategoryService iMemberPromotionCategoryService;

    private final IMemberPromotionService iMemberPromotionService;

    @Operation(summary = "找優惠活動分類")
    @GetMapping
    public PageResult<MemberPromotionCategoryVO> findPromotionCategory(@ParameterObject FindPromotionCategoryForm form) {
        return PageResult.success(iMemberPromotionCategoryService.findPromotionCategory(form));
    }

    @Operation(summary = "找優惠活動分類Option")
    @GetMapping("/option")
    public Result<List<OptionVO<Integer>>> findPromotionCategoryOption() {
        return Result.success(iMemberPromotionCategoryService.findPromotionCategoryOption());
    }

    @Operation(summary = "新增優惠活動分類")
    @PostMapping
    public Result<Boolean> addPromotionCategory(@RequestBody AddPromotionCategoryForm form) {
        return Result.success(iMemberPromotionCategoryService.addPromotionCategory(form));
    }

    @Operation(summary = "修改優惠活動分類")
    @PutMapping
    public Result<Boolean> modifyPromotionCategory(@RequestBody ModifyPromotionCategoryForm form) {
        return Result.success(iMemberPromotionCategoryService.modifyPromotionCategory(form));
    }

    @Operation(summary = "刪除優惠活動分類")
    @DeleteMapping
    public Result<Boolean> deletePromotionCategory(@Parameter(description = "優惠活動分類ID") @RequestBody List<Integer> ids) {
        // 檢查優惠活動有無在使用
        Set<Integer> categoryId = iMemberPromotionService.lambdaQuery()
                .select(MemberPromotion::getCategoryId)
                .isNotNull(MemberPromotion::getCategoryId)
                .list()
                .stream()
                .map(MemberPromotion::getCategoryId)
                .collect(Collectors.toSet());
        if (ids.stream().anyMatch(categoryId::contains)) {
            throw new BizException(ResultCode.PROMOTION_CATEGORY_USING);
        }

        return Result.success(iMemberPromotionCategoryService.deletePromotionCategory(ids));
    }

    @Operation(summary = "修改優惠活動分類排序")
    @PutMapping("/sort")
    public Result<Boolean> modifyPromotionCategorySort(@Parameter(description = "排序 ID:SORT") @RequestBody Map<Integer, Integer> map) {
        return Result.success(iMemberPromotionCategoryService.modifyPromotionCategorySort(map));
    }

    @Operation(summary = "修改優惠活動分類排序 置頂置底", description = "0置頂1置底")
    @PutMapping("/sort/top/bottom")
    public Result<Boolean> modifyPromotionCategorySortTopBottom(@Validated @RequestBody ModifyPromotionCategorySortTopBottomForm form) {
        return Result.success(iMemberPromotionCategoryService.modifyPromotionCategorySortTopBottom(form));
    }

}
