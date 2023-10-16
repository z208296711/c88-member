package com.c88.member.controller.admin;

import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.MemberRechargeAwardTemplateDrawOptionVO;
import com.c88.member.pojo.vo.MemberRechargeAwardTemplateVO;
import com.c88.member.pojo.vo.RechargeAwardTemplateLinkModeVO;
import com.c88.member.service.IMemberRechargeAwardTemplateService;
import com.c88.member.vo.MemberRechargeAwardTemplateClientVO;
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

@Tag(name = "『後台』存送優惠模組")
@RestController
@RequestMapping("/api/v1/recharge/award/template")
@RequiredArgsConstructor
public class MemberRechargeAwardTemplateController {

    private final IMemberRechargeAwardTemplateService iMemberRechargeAwardTemplateService;

    @Operation(summary = "找存送優惠模組")
    @GetMapping
    public PageResult<MemberRechargeAwardTemplateVO> findRechargeAwardTemplate(@ParameterObject FindRechargeAwardTemplateForm form) {
        return PageResult.success(iMemberRechargeAwardTemplateService.findRechargeAwardTemplate(form));
    }

    @Operation(summary = "找存送優惠模組Option")
    @GetMapping("/option")
    public Result<List<OptionVO<Long>>> findRechargeAwardTemplateOption() {
        return Result.success(iMemberRechargeAwardTemplateService.findRechargeAwardTemplateOption());
    }

    @Operation(summary = "找存送優惠模組輪盤Option")
    @GetMapping("/draw/option")
    public Result<List<MemberRechargeAwardTemplateDrawOptionVO>> findRechargeAwardTemplateDrawOption(@RequestParam(required = false ) Integer enable) {
        return Result.success(iMemberRechargeAwardTemplateService.findRechargeAwardTemplateDrawOption(enable));
    }

    @Operation(summary = "找存送優惠模組")
    @GetMapping("/{id}")
    public Result<MemberRechargeAwardTemplateClientVO> findRechargeAwardTemplate(@PathVariable("id") Long id) {
        return Result.success(iMemberRechargeAwardTemplateService.findRechargeAwardTemplate(id));
    }

    @Operation(summary = "新增存送優惠模組")
    @PostMapping
    public Result<Boolean> addRechargeAwardTemplate(@RequestBody AddRechargeAwardTemplateForm form) {
        return Result.success(iMemberRechargeAwardTemplateService.addRechargeAwardTemplate(form));
    }

    @Operation(summary = "修改存送優惠模組")
    @PutMapping
    public Result<Boolean> modifyRechargeAwardTemplate(@Validated @RequestBody ModifyRechargeAwardTemplateForm form) {
        return Result.success(iMemberRechargeAwardTemplateService.modifyRechargeAwardTemplate(form));
    }

    @Operation(summary = "刪除存送優惠模組")
    @DeleteMapping
    public Result<Boolean> deleteRechargeAwardTemplate(@Parameter(description = "存送優惠模組ID") @RequestBody List<Long> ids) {
        return Result.success(iMemberRechargeAwardTemplateService.deleteRechargeAwardTemplate(ids));
    }

    @Operation(summary = "修改存送優惠模組排序")
    @PutMapping("/sort")
    public Result<Boolean> modifyRechargeAwardTemplateSort(@Parameter(description = "排序 ID:SORT") @RequestBody Map<Long, Integer> map) {
        return Result.success(iMemberRechargeAwardTemplateService.modifyAdvertisementSort(map));
    }

    @Operation(summary = "修改存送優惠模組排序 置頂置底", description = "0置頂1置底")
    @PutMapping("/sort/top/bottom")
    public Result<Boolean> modifyRechargeAwardTemplateSortTopBottom(@Validated @RequestBody ModifyRechargeAwardTemplateSortTopBottomForm form) {
        return Result.success(iMemberRechargeAwardTemplateService.modifyAdvertisementSortTopBottom(form));
    }

    @Operation(summary = "找關聯模式組合")
    @PutMapping("/link/mode/{id}")
    public Result<List<RechargeAwardTemplateLinkModeVO>> findRechargeAwardTemplateLinkMode(@PathVariable("id") Integer id) {
        return Result.success(iMemberRechargeAwardTemplateService.findRechargeAwardTemplateLinkMode(id));
    }

    @Operation(summary = "修改關聯模式", description = "關聯模組不能只有一組")
    @PutMapping("/link/mode")
    public Result<Boolean> modifyRechargeAwardTemplateLinkMode(@RequestBody ModifyRechargeAwardTemplateLinkModeForm form) {
        return Result.success(iMemberRechargeAwardTemplateService.modifyRechargeAwardTemplateLinkMode(form));
    }

    @Operation(summary = "解除關聯模式組合")
    @DeleteMapping("/link/mode/{id}")
    public Result<Boolean> deleteRechargeAwardTemplateLinkMode(@PathVariable("id") Integer id) {
        return Result.success(iMemberRechargeAwardTemplateService.deleteRechargeAwardTemplateLinkMode(id));
    }

}
