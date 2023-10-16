package com.c88.member.controller.admin;

import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.MemberAdvertisementSortVO;
import com.c88.member.pojo.vo.MemberAdvertisementVO;
import com.c88.member.service.IMemberAdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "『後台』廣告")
@RestController
@RequestMapping("/api/v1/advertisement")
@RequiredArgsConstructor
public class MemberAdvertisementController {

    private final IMemberAdvertisementService iMemberAdvertisementService;

    @Operation(summary = "找廣告")
    @GetMapping
    public PageResult<MemberAdvertisementVO> findAdvertisement(@ParameterObject FindAdvertisementForm form) {
        return PageResult.success(iMemberAdvertisementService.findAdvertisement(form));
    }

    @Operation(summary = "找廣告排序")
    @GetMapping("/sort")
    public PageResult<MemberAdvertisementSortVO> findAdvertisementSort(@ParameterObject FindAdvertisementSortForm form) {
        return PageResult.success(iMemberAdvertisementService.findAdvertisementSort(form));
    }

    @Operation(summary = "新增廣告")
    @PostMapping
    public Result<Boolean> addAdvertisement(@RequestBody AddAdvertisementForm form) {
        return Result.success(iMemberAdvertisementService.addAdvertisement(form));
    }

    @Operation(summary = "修改廣告")
    @PutMapping
    public Result<Boolean> modifyAdvertisement(@RequestBody ModifyAdvertisementForm form) {
        return Result.success(iMemberAdvertisementService.modifyAdvertisement(form));
    }

    @Operation(summary = "刪除廣告")
    @DeleteMapping
    public Result<Boolean> deleteAdvertisement(@Parameter(description = "廣告ID") @RequestBody List<Integer> ids) {
        return Result.success(iMemberAdvertisementService.deleteAdvertisement(ids));
    }

    @Operation(summary = "修改廣告排序")
    @PutMapping("/sort")
    public Result<Boolean> modifyAdvertisementSort(@Parameter(description = "排序 ID:SORT") @RequestBody Map<Integer, Integer> map) {
        return Result.success(iMemberAdvertisementService.modifyAdvertisementSort(map));
    }

    @Operation(summary = "修改廣告排序 置頂置底", description = "0置頂1置底")
    @PutMapping("/sort/top/bottom")
    public Result<Boolean> modifyAdvertisementSortTopBottom(@Validated @RequestBody ModifyAdvertisementSortTopBottomForm form) {
        return Result.success(iMemberAdvertisementService.modifyAdvertisementSortTopBottom(form));
    }

}
