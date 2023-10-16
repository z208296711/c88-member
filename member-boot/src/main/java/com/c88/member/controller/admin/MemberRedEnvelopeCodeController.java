package com.c88.member.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.common.enums.RedEnvelopeCodeStateEnum;
import com.c88.member.common.enums.RedEnvelopeRecordStateEnum;
import com.c88.member.mapstruct.MemberRedEnvelopeCodeConverter;
import com.c88.member.pojo.entity.MemberRedEnvelopeCode;
import com.c88.member.pojo.entity.MemberRedEnvelopeRecord;
import com.c88.member.pojo.form.FindRedEnvelopeTemplateCodeForm;
import com.c88.member.pojo.vo.MemberRedEnvelopeCodeVO;
import com.c88.member.pojo.vo.MemberRedEnvelopeTemplateCodeVO;
import com.c88.member.service.IMemberRedEnvelopeCodeService;
import com.c88.member.service.IMemberRedEnvelopeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Tag(name = "『後台』白菜紅包代碼")
@RestController
@RequestMapping("/api/v1/red/envelope/code")
@RequiredArgsConstructor
public class MemberRedEnvelopeCodeController {

    private final IMemberRedEnvelopeCodeService iMemberRedEnvelopeCodeService;

    private final IMemberRedEnvelopeRecordService iMemberRedEnvelopeRecordService;

    private final MemberRedEnvelopeCodeConverter memberRedEnvelopeCodeConverter;

    @Operation(summary = "找會員白菜紅包代碼")
    @GetMapping("/{code}")
    public Result<MemberRedEnvelopeCodeVO> findMemberRedEnvelopeCode(@PathVariable("code") String code) {
        MemberRedEnvelopeCodeVO memberRedEnvelopeCodeVO = iMemberRedEnvelopeCodeService.lambdaQuery()
                .eq(MemberRedEnvelopeCode::getCode, code)
                .oneOpt()
                .map(memberRedEnvelopeCodeConverter::toVo)
                .orElseThrow(() -> new BizException(ResultCode.NOT_RED_ENVELOPE));

        MemberRedEnvelopeRecord memberRedEnvelopeRecord = iMemberRedEnvelopeRecordService.lambdaQuery()
                .eq(MemberRedEnvelopeRecord::getCode, code)
                .oneOpt()
                .orElse(MemberRedEnvelopeRecord.builder()
                        .username("")
                        .state(RedEnvelopeRecordStateEnum.NULL.getCode())
                        .build()
                );

        memberRedEnvelopeCodeVO.setUsername(memberRedEnvelopeRecord.getUsername());
        memberRedEnvelopeCodeVO.setReviewState(memberRedEnvelopeRecord.getState());

        return Result.success(memberRedEnvelopeCodeVO);
    }

    @Operation(summary = "找模組白菜紅包代碼")
    @GetMapping("/template")
    public PageResult<MemberRedEnvelopeTemplateCodeVO> findRedEnvelopeTemplateCode(@Validated @ParameterObject FindRedEnvelopeTemplateCodeForm form) {
        return PageResult.success(
                iMemberRedEnvelopeCodeService.lambdaQuery()
                        .eq(Objects.nonNull(form.getId()), MemberRedEnvelopeCode::getTemplateId, form.getId())
                        .eq(StringUtils.isNotBlank(form.getCode()), MemberRedEnvelopeCode::getCode, form.getCode())
                        .eq(StringUtils.isNotBlank(form.getUsername()), MemberRedEnvelopeCode::getUsername, form.getUsername())
                        .eq(Objects.nonNull(form.getState()), MemberRedEnvelopeCode::getState, form.getState())
                        .page(new Page<>(form.getPageNum(), form.getPageSize()))
                        .convert(memberRedEnvelopeCodeConverter::toTemplateVo)
        );
    }

    @Transactional
    @Operation(summary = "回收白菜紅包代碼")
    @PutMapping("/recycle/{id}")
    public Result<Boolean> recycleRedEnvelopeCode(@Parameter(description = "模組ID") @PathVariable("id") Integer id) {
        return Result.success(
                iMemberRedEnvelopeCodeService.lambdaUpdate()
                        .eq(MemberRedEnvelopeCode::getTemplateId, id)
                        .eq(MemberRedEnvelopeCode::getState, RedEnvelopeCodeStateEnum.UNUSED.getCode())
                        .set(MemberRedEnvelopeCode::getState, RedEnvelopeCodeStateEnum.RECYCLED.getCode())
                        .update()
        );
    }


}
