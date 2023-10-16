package com.c88.member.controller.admin;

import com.c88.common.core.result.Result;
import com.c88.member.pojo.form.UpdateMemberVipForm;
import com.c88.member.service.IVipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "『後台』會員 VIP 更動")
@RequestMapping("/api/v1/member/vip")
public class MemberVipController {

    private final IVipService iVipService;

    @Operation(summary = "更新 會員VIP等級")
    @PostMapping("/level")
    public Result<Boolean> updateMemberVip(@RequestBody @Validated UpdateMemberVipForm form) {
        return Result.success(iVipService.doManualVipLevelUpDownAction(form));
    }

}
