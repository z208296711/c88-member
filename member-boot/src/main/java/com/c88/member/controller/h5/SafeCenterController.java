package com.c88.member.controller.h5;

import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.util.MemberUtils;
import com.c88.member.pojo.vo.MemberDataCheckVO;
import com.c88.member.service.ISafeCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "安全中心")
@RestController
@RequiredArgsConstructor
@RequestMapping("/h5/safeCenter")
public class SafeCenterController {

    private final ISafeCenterService iSafeCenterService;

    @Operation(summary = "檢查安全中心各項資料")
    @GetMapping("/memberDataCheck")
    public Result<MemberDataCheckVO> memberDataCheck() {
        Long memberId = MemberUtils.getMemberId();
        if (memberId == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }

        return Result.success(iSafeCenterService.memberDataCheck(memberId));
    }
}
