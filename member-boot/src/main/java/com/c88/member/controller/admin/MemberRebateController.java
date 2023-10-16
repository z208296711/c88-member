package com.c88.member.controller.admin;

import com.c88.common.core.result.Result;
import com.c88.member.pojo.entity.MemberRebateConfig;
import com.c88.member.pojo.form.UpdateMemberRebateForm;
import com.c88.member.service.IMemberRebateConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "『後台』會員 返點設置功能")
@RequestMapping("/api/v1/member")
public class MemberRebateController {


    private final IMemberRebateConfigService memberRebateConfigService;

    @Operation(summary = "查詢返點設置")
    @GetMapping("/rebate")
    public Result<Map<String, Object>> getMemberRebate() {
        Map<String, Object> memberRebate = memberRebateConfigService.getMemberRebate();
        return Result.success(memberRebate);
    }

    @Operation(summary = "查詢全部返點")
    @GetMapping("/rebate/all")
    public Result<List<MemberRebateConfig>> getMemberRebateAll() {
        List<MemberRebateConfig> rebateConfigs = memberRebateConfigService.getOrUpdateMemberRebate();
        return Result.success(rebateConfigs);
    }

    @Operation(summary = "修改返點設置")
    @PutMapping("/rebate")
    public Result<Boolean> updateMemberRebate(@RequestBody List<UpdateMemberRebateForm> forms) {
        for(UpdateMemberRebateForm form : forms){
            Result<Boolean> res;
            if((res = validate(form)) != null){
                return res;
            }
        }
        Boolean aBoolean = memberRebateConfigService.updateMemberRebate(forms);
        return Result.success(aBoolean);
    }

    private Result<Boolean> validate(@Valid UpdateMemberRebateForm form) {
        Set<ConstraintViolation<@Valid UpdateMemberRebateForm >> validateSet = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(form);
        if (!CollectionUtils.isEmpty(validateSet)) {
            String messages = validateSet.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "；" + m2)
                    .orElse("参数输入有误！");
            return Result.failed(messages);
        }
        return null;
    }



}
