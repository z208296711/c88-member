package com.c88.member.controller.admin;

import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.MemberPromotionSortVO;
import com.c88.member.pojo.vo.MemberPromotionVO;
import com.c88.member.service.IMemberPromotionCategoryService;
import com.c88.member.service.IMemberPromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "『後台』優惠活動")
@RestController
@RequestMapping("/api/v1/promotion")
@RequiredArgsConstructor
public class MemberPromotionController {

    private final IMemberPromotionService iMemberPromotionService;

    private final IMemberPromotionCategoryService iMemberPromotionCategoryService;

    @Operation(summary = "找優惠活動")
    @GetMapping
    public PageResult<MemberPromotionVO> findPromotion(@ParameterObject FindPromotionForm form) {
        return PageResult.success(iMemberPromotionService.findPromotion(form));
    }

    @Operation(summary = "找優惠活動排序")
    @GetMapping("/sort")
    public PageResult<MemberPromotionSortVO> findPromotionSort(@ParameterObject FindPromotionSortForm form) {
        return PageResult.success(iMemberPromotionService.findPromotionSort(form, iMemberPromotionCategoryService.list()));
    }

    @Operation(summary = "新增優惠活動")
    @PostMapping
    public Result<Boolean> addPromotion(@RequestBody AddPromotionForm form) {
        return Result.success(iMemberPromotionService.addPromotion(form));
    }

    @Operation(summary = "修改優惠活動")
    @PutMapping
    public Result<Boolean> modifyPromotion(@RequestBody ModifyPromotionForm form) {
        return Result.success(iMemberPromotionService.modifyPromotion(form));
    }

    @Operation(summary = "刪除優惠活動")
    @DeleteMapping
    public Result<Boolean> deletePromotion(@Parameter(description = "優惠活動ID") @RequestBody List<Integer> ids) {
        return Result.success(iMemberPromotionService.deletePromotion(ids));
    }

    @Operation(summary = "修改優惠活動排序")
    @PutMapping("/sort")
    public Result<Boolean> modifyPromotionSort(@Parameter(description = "排序 ID:SORT") @RequestBody Map<Integer, Integer> map) {
        return Result.success(iMemberPromotionService.modifyPromotionSort(map));
    }

    @Operation(summary = "修改優惠活動排序 置頂置底", description = "0置頂1置底")
    @PutMapping("/sort/top/bottom")
    public Result<Boolean> modifyPromotionSortTopBottom(@Validated @RequestBody ModifyPromotionSortTopBottomForm form) {
        return Result.success(iMemberPromotionService.modifyPromotionSortTopBottom(form));
    }

}
