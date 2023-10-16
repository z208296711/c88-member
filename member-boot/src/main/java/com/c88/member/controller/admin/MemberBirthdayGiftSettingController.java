package com.c88.member.controller.admin;

import com.c88.common.core.result.Result;
import com.c88.member.pojo.form.ModifyMemberBirthdayGiftSettingForm;
import com.c88.member.pojo.vo.MemberBirthdayGiftSettingVO;
import com.c88.member.service.IMemberBirthdayGiftSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "『後台』生日禮金設定")
@RestController
@RequestMapping("/api/v1/member/birthday/gift/setting")
@RequiredArgsConstructor
public class MemberBirthdayGiftSettingController {

    private final IMemberBirthdayGiftSettingService iMemberBirthdayGiftSettingService;

    @Operation(summary = "找生日禮金設定")
    @GetMapping
    public Result<MemberBirthdayGiftSettingVO> findMemberBirthdayGiftSetting() {
        return Result.success(iMemberBirthdayGiftSettingService.findMemberBirthdayGiftSetting());
    }

    @Operation(summary = "修改生日禮金設定")
    @PutMapping
    public Result<Boolean> modifyMemberBirthdayGiftSetting(@RequestBody ModifyMemberBirthdayGiftSettingForm form) {
        return Result.success(iMemberBirthdayGiftSettingService.modifyMemberBirthdayGiftSetting(form));
    }

}
