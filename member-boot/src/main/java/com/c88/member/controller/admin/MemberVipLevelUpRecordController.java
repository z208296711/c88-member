package com.c88.member.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.mapstruct.MemberVipRecordConverter;
import com.c88.member.pojo.entity.MemberVipLevelRecord;
import com.c88.member.pojo.form.SearchVipLevelRecordForm;
import com.c88.member.pojo.vo.MemberVipRecordVO;
import com.c88.member.service.IMemberVipLevelRecordService;
import com.c88.member.service.IVipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "『後台』會員 VIP 升降級")
@RequestMapping("/api/v1/member/vip/level/record")
public class MemberVipLevelUpRecordController {

    private final IVipService iVipService;

    private final IMemberVipLevelRecordService iMemberVipLevelRecordService;

    private final MemberVipRecordConverter memberVipRecordConverter;

    @Operation(summary = "取得-VIP升降級紀錄")
    @GetMapping
    public PageResult<MemberVipRecordVO> getVipLevelRecordPage(@ParameterObject SearchVipLevelRecordForm form) {
        IPage<MemberVipLevelRecord> memberVipLevelRecordIPage = iMemberVipLevelRecordService.lambdaQuery()
                .eq(StringUtils.isNotBlank(form.getUsername()), MemberVipLevelRecord::getUsername, form.getUsername())
                .eq(form.getType() != null, MemberVipLevelRecord::getType, form.getType())
                .eq(form.getVipId() != null, MemberVipLevelRecord::getTargetVipId, form.getVipId())
                .eq(form.getState() != null, MemberVipLevelRecord::getState, form.getState())
                .orderByDesc(MemberVipLevelRecord::getGmtCreate)
                .page(new Page<>(form.getPageNum(), form.getPageSize()));
        return PageResult.success(memberVipLevelRecordIPage.convert(memberVipRecordConverter::toVo));
    }

    @Operation(summary = "保級")
    @PatchMapping("/keep/{ids}")
    public Result<Boolean> keepLevel(@Parameter(description = "删除角色，多个以英文逗号(,)分割") @PathVariable String ids) {
        List<Long> idList = Arrays.asList(ids.split(","))
                .stream()
                .map(Long::parseLong).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(idList)) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        return Result.success(iVipService.levelKeep(idList));
    }

    @Operation(summary = "降級")
    @PatchMapping("/down/{ids}")
    public Result<Boolean> levelDown(@Parameter(description = "删除角色，多个以英文逗号(,)分割") @PathVariable String ids) {
        List<Long> idList = Arrays.asList(ids.split(","))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(idList)) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        return Result.success(iVipService.levelDown(idList));
    }

}
