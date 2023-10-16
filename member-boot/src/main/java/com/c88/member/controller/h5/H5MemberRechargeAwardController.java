package com.c88.member.controller.h5;

import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.MemberUtils;
import com.c88.member.pojo.vo.H5RechargeAwardCategoryVO;
import com.c88.member.service.IMemberRechargeAwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@Tag(name = "『前台』存送優惠")
@RestController
@RequestMapping("/h5/recharge/award")
@RequiredArgsConstructor
public class H5MemberRechargeAwardController {

    private final IMemberRechargeAwardService iMemberRechargeAwardService;

    @Operation(summary = "取得可用的存送優惠")
    @GetMapping
    public Result<List<H5RechargeAwardCategoryVO>> findRechargeAward() {
        Long memberId = MemberUtils.getMemberId();
        if (Objects.isNull(memberId)) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(iMemberRechargeAwardService.findRechargeAward(memberId));
    }

}
