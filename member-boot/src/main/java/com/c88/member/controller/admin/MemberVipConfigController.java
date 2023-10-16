package com.c88.member.controller.admin;

import com.c88.common.core.base.BasePageQuery;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.pojo.entity.MemberVipConfig;
import com.c88.member.pojo.form.AddMemberVipForm;
import com.c88.member.pojo.form.DelMemberVipConfigForm;
import com.c88.member.pojo.form.ModifyMemberVipForm;
import com.c88.member.pojo.vo.MemberVipConfigVO;
import com.c88.member.service.IMemberVipConfigService;
import com.c88.member.vo.OptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "『後台』會員 VIP 配置")
@RequestMapping("/api/v1/member/vip/config")
public class MemberVipConfigController {

    private final IMemberVipConfigService iMemberVipConfigService;

    @Operation(summary = "查看-VIP配置")
    @GetMapping
    public PageResult<MemberVipConfigVO> findMemberVipConfigPage(@ParameterObject BasePageQuery pageQuery) {
        return PageResult.success(iMemberVipConfigService.findMemberVipConfigPage(pageQuery));
    }

    @Operation(summary = "查看-VIP配置選項")
    @GetMapping("/option")
    public Result<List<OptionVO<Integer>>> findMemberVipConfigOption() {
        return Result.success(iMemberVipConfigService.findMemberVipConfigOption());
    }

    @Operation(summary = "內部使用-VIP配置選項", description = "內部調用", hidden = true)
    @GetMapping("/map")
    public Result<Map<Integer, String>> findVipConfigMap() {
        return Result.success(iMemberVipConfigService.findVipConfigMap());
    }

    @Operation(summary = "新增-VIP配置")
    @PostMapping
    public Result<Boolean> addMemberVipConfig(@RequestBody @Validated AddMemberVipForm memberVipForm) {
        return Result.success(iMemberVipConfigService.addMemberVipConfig(memberVipForm));
    }

    @Operation(summary = "編輯-VIP配置")
    @PutMapping
    public Result<Boolean> modifyMemberVipConfig(@RequestBody ModifyMemberVipForm memberVipForm) {
        return Result.success(iMemberVipConfigService.modifyMemberVipConfig(memberVipForm));
    }

    @Operation(summary = "刪除-VIP配置")
    @DeleteMapping
    @Transactional
    public Result<Boolean> delMemberVipConfig(@RequestBody DelMemberVipConfigForm form) {
        return Result.success(iMemberVipConfigService.delMemberVipConfig(form));
    }

    @Operation(summary = "內部使用-list VIP配置", description = "內部調用", hidden = true)
    @GetMapping("/all")
    public Result<List<MemberVipConfig>> findVipConfigAll() {
        return Result.success(iMemberVipConfigService.findAllVipConfig());
    }


}
