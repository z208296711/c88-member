package com.c88.member.controller.h5;

import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.MemberUtils;
import com.c88.member.pojo.form.ReceiveChineseCabbageForm;
import com.c88.member.pojo.vo.H5ChineseCabbageStateVO;
import com.c88.member.service.IMemberRedEnvelopeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Tag(name = "『前台』白菜紅包")
@RestController
@RequestMapping("/h5/red/envelope")
@RequiredArgsConstructor
public class H5MemberRedEnvelopeController {

    private final IMemberRedEnvelopeService iMemberRedEnvelopeService;

    @Operation(summary = "白菜領取狀態")
    @GetMapping("/chinese/cabbage")
    public Result<List<H5ChineseCabbageStateVO>> findChineseCabbageState() {
        Long memberId = MemberUtils.getMemberId();
        String username = MemberUtils.getUsername();
        if (Objects.isNull(memberId) || StringUtils.isBlank(username)) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(iMemberRedEnvelopeService.findChineseCabbageState(memberId,username));
    }

    @Operation(summary = "領取白菜")
    @PostMapping("/chinese/cabbage")
    public Result<Boolean> receiveChineseCabbage(@Validated @RequestBody ReceiveChineseCabbageForm form) {
        Long memberId = MemberUtils.getMemberId();
        String username = MemberUtils.getUsername();
        if (Objects.isNull(memberId) || StringUtils.isBlank(username)) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(iMemberRedEnvelopeService.receiveChineseCabbage(memberId, username, form));
    }

    @Operation(summary = "領取代碼")
    @PostMapping("/code/{code}")
    public Result<Boolean> receiveCode(@PathVariable("code") String code) {
        Long memberId = MemberUtils.getMemberId();
        String username = MemberUtils.getUsername();
        if (Objects.isNull(memberId) || StringUtils.isBlank(username)) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(iMemberRedEnvelopeService.receiveCode(memberId, username, code));
    }

}
