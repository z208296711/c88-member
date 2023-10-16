package com.c88.member.controller.admin;

import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.pojo.form.AddRedEnvelopeTemplateForm;
import com.c88.member.pojo.form.FindRedEnvelopeTemplateForm;
import com.c88.member.pojo.form.ModifyRedEnvelopeTemplateForm;
import com.c88.member.pojo.vo.MemberRedEnvelopeTemplateVO;
import com.c88.member.service.IMemberRedEnvelopeTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "『後台』白菜紅包模組")
@RestController
@RequestMapping("/api/v1/red/envelope/template")
@RequiredArgsConstructor
public class MemberRedEnvelopeTemplateController {

    private final IMemberRedEnvelopeTemplateService iMemberRedEnvelopeTemplateService;

    @Operation(summary = "找白菜紅包模組")
    @GetMapping
    public PageResult<MemberRedEnvelopeTemplateVO> findRedEnvelopeTemplate(@ParameterObject FindRedEnvelopeTemplateForm form) {
        return PageResult.success(iMemberRedEnvelopeTemplateService.findRedEnvelopeTemplate(form));
    }

    @Operation(summary = "新增白菜紅包模組")
    @PostMapping
    public Result<Boolean> addRedEnvelopeTemplate(@Validated @RequestBody AddRedEnvelopeTemplateForm form) {
        return Result.success(iMemberRedEnvelopeTemplateService.addRedEnvelopeTemplate(form));
    }

    @Operation(summary = "修改白菜紅包模組")
    @PutMapping
    public Result<Boolean> modifyRedEnvelopeTemplate(@Validated @RequestBody ModifyRedEnvelopeTemplateForm form) {
        return Result.success(iMemberRedEnvelopeTemplateService.modifyRedEnvelopeTemplate(form));
    }

    @Operation(summary = "刪除白菜紅包模組")
    @DeleteMapping
    public Result<Boolean> deleteRedEnvelopeTemplate(@Parameter(description = "白菜紅包模組ID") @RequestBody List<Integer> ids) {
        return Result.success(iMemberRedEnvelopeTemplateService.deleteRedEnvelopeTemplate(ids));
    }

}
