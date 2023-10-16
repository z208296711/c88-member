package com.c88.member.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.mapstruct.MemberFreeBetGiftConverter;
import com.c88.member.pojo.entity.MemberFreeBetGiftRecord;
import com.c88.member.pojo.entity.MemberFreeBetGiftSetting;
import com.c88.member.pojo.form.GetFreeBetGiftForm;
import com.c88.member.pojo.vo.MemberFreeBetGiftRecordVO;
import com.c88.member.service.IMemberFreeBetGiftRecordService;
import com.c88.member.service.IMemberFreeBetGiftSettingService;
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
@Tag(name = "『後台』 免費禮金")
@RequestMapping("/api/v1/free/bet/gift")
public class MemberFreeBetGiftController {

    private final IMemberFreeBetGiftRecordService iMemberFreeBetGiftRecordService;

    private final IMemberFreeBetGiftSettingService iMemberFreeBetGiftSettingService;

    private final MemberFreeBetGiftConverter memberFreeBetGiftConverter;

    @Operation(summary = "取得-免費禮金設定")
    @GetMapping("/{type}/setting")
    public Result<MemberFreeBetGiftSetting> findFreeBetGiftSetting(@PathVariable Integer type) {
        MemberFreeBetGiftSetting setting = iMemberFreeBetGiftSettingService.lambdaQuery()
                .eq(MemberFreeBetGiftSetting::getType, type)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));
        return Result.success(setting);
    }

    @Operation(summary = "修改-免費禮金設定")
    @PutMapping("/{type}/setting")
    public Result<Boolean> modifySetting(@PathVariable Integer type,
                                         @RequestBody @Validated MemberFreeBetGiftSetting form) {
        return Result.success(iMemberFreeBetGiftSettingService.lambdaUpdate()
                .eq(MemberFreeBetGiftSetting::getType, type)
                .set(MemberFreeBetGiftSetting::getBetRate, form.getBetRate())
                .set(MemberFreeBetGiftSetting::getAmount, form.getAmount())
                .set(MemberFreeBetGiftSetting::getEnable, form.getEnable())
                .set(MemberFreeBetGiftSetting::getValue, form.getValue())
                .update());
    }

    @Operation(summary = "取得-免費禮金領取紀錄")
    @GetMapping("/record")
    public PageResult<MemberFreeBetGiftRecordVO> getLevelUpGiftRecord(@ParameterObject @Validated GetFreeBetGiftForm form) {
        IPage<MemberFreeBetGiftRecordVO> memberLevelUpGiftRecordIPage = iMemberFreeBetGiftRecordService.lambdaQuery()
                .like(StringUtils.isNotBlank(form.getUsername()), MemberFreeBetGiftRecord::getUsername, form.getUsername())
                .eq(StringUtils.isNotBlank(form.getVipName()), MemberFreeBetGiftRecord::getVipName, form.getVipName())
                .eq(form.getType() != null, MemberFreeBetGiftRecord::getType, form.getType())
                .lt(StringUtils.isNotBlank(form.getEndTime()), MemberFreeBetGiftRecord::getGmtCreate, form.getEndTime())
                .gt(StringUtils.isNotBlank(form.getBeginTime()), MemberFreeBetGiftRecord::getGmtCreate, form.getBeginTime())
                .orderByDesc(MemberFreeBetGiftRecord::getGmtCreate)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberFreeBetGiftConverter::toVo);
        return PageResult.success(memberLevelUpGiftRecordIPage);
    }

}
