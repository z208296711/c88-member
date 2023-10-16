package com.c88.member.controller.admin;

import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.mapper.MemberSessionMapper;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.AdminMemberLoginReportVO;
import com.c88.member.pojo.vo.MemberVipRuleVO;
import com.c88.member.service.IMemberSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "『後台』登入報表")
@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {

    private final IMemberSessionService iMemberSessionService;

    @Operation(summary = "取得-登入報表")
    @GetMapping("/login")
    public PageResult<AdminMemberLoginReportVO> findMemberLoginReport(@ParameterObject FindMemberLoginReportForm form) {
        return PageResult.success(iMemberSessionService.findMemberLoginReport(form));
    }

}
