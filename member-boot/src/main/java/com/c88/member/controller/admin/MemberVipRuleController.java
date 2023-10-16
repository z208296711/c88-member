package com.c88.member.controller.admin;

import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.pojo.vo.MemberVipRuleVO;
import com.c88.member.service.IMemberVipRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "『後台』會員 VIP 規則")
@RequestMapping("/api/v1/member/vip/rule")
public class MemberVipRuleController {

    private final IMemberVipRuleService iMemberVipRuleService;

    @Operation(summary = "取得-VIP規則")
    @GetMapping
    public Result<MemberVipRuleVO> getVipRule() {
        return Result.success(iMemberVipRuleService.getVipRule());
    }

    @Operation(summary = "取得-VIP規則")
    @PutMapping
    public Result<Boolean> getVipRule(@RequestBody MemberVipRuleVO memberVipRuleVO) {
        try {
            iMemberVipRuleService.modifyMemberVipRule(memberVipRuleVO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(ResultCode.WORD_SIZE_LIMIT);
        }
        return Result.success(true);
    }

}
