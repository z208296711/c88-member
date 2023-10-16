package com.c88.member.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.mapstruct.MemberLevelUpGiftConverter;
import com.c88.member.pojo.entity.MemberLevelUpGiftRecord;
import com.c88.member.pojo.entity.MemberLevelUpGiftSetting;
import com.c88.member.pojo.form.GetLevelUpGiftForm;
import com.c88.member.pojo.form.UpdateMemberLevelUpGiftForm;
import com.c88.member.pojo.vo.MemberLevelUpGiftRecordVO;
import com.c88.member.service.IMemberLevelUpGiftRecordService;
import com.c88.member.service.IMemberLevelUpGiftSettingService;
import com.c88.member.service.IMemberVipConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "『後台』 晉級禮金")
@RequestMapping("/api/v1/level/up/gift")
public class MemberLevelUpGiftController {

    private final IMemberLevelUpGiftSettingService iMemberLevelUpGiftSettingService;

    private final IMemberLevelUpGiftRecordService iMemberLevelUpGiftRecordService;

    private final MemberLevelUpGiftConverter memberLevelUpGiftConverter;

    private final IMemberVipConfigService iMemberVipConfigService;

    @Operation(summary = "取得-晉級禮包設定")
    @GetMapping("/setting")
    public Result<MemberLevelUpGiftSetting> findLevelUpGiftSetting() {
        return Result.success(iMemberLevelUpGiftSettingService.findLevelUpGiftSetting());
    }

    @Operation(summary = "修改-晉級禮包設定")
    @PutMapping("/setting")
    public Result<Boolean> modifySetting(@RequestBody @Validated UpdateMemberLevelUpGiftForm form) {
        return Result.success(iMemberLevelUpGiftSettingService.updateLevelUpGiftSetting(form));
    }

    @Operation(summary = "取得-晉級禮包領取紀錄")
    @GetMapping("/record")
    public PageResult<MemberLevelUpGiftRecordVO> getLevelUpGiftRecord(@ParameterObject @Validated GetLevelUpGiftForm form) {
        IPage<MemberLevelUpGiftRecordVO> memberLevelUpGiftRecordIPage = iMemberLevelUpGiftRecordService.lambdaQuery()
                .like(StringUtils.isNotBlank(form.getUsername()), MemberLevelUpGiftRecord::getUsername, form.getUsername())
                .eq(StringUtils.isNotBlank(form.getVipId()), MemberLevelUpGiftRecord::getVipId, form.getVipId())
                .lt(StringUtils.isNotBlank(form.getEndTime()), MemberLevelUpGiftRecord::getGmtCreate, form.getEndTime())
                .gt(StringUtils.isNotBlank(form.getBeginTime()), MemberLevelUpGiftRecord::getGmtCreate, form.getBeginTime())
                .orderByDesc(MemberLevelUpGiftRecord::getGmtCreate)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberLevelUpGiftConverter::toVo);

        return PageResult.success(memberLevelUpGiftRecordIPage);
    }

}
