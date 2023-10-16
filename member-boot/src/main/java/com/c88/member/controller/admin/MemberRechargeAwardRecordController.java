package com.c88.member.controller.admin;

import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.pojo.form.FindPersonalRechargeAwardRecordForm;
import com.c88.member.pojo.form.FindRechargeAwardRecordForm;
import com.c88.member.vo.AllMemberPersonalRechargeAwardRecordByTemplateIdVO;
import com.c88.member.pojo.vo.MemberPersonalRechargeAwardRecordVO;
import com.c88.member.pojo.vo.MemberRechargeAwardRecordVO;
import com.c88.member.service.IMemberRechargeAwardRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "『後台』存送優惠領取紀錄")
@RestController
@RequestMapping("/api/v1/recharge/award/record")
@RequiredArgsConstructor
public class MemberRechargeAwardRecordController {

    private final IMemberRechargeAwardRecordService iMemberRechargeAwardRecordService;

    @Operation(summary = "找存送優惠領取紀錄")
    @GetMapping
    public PageResult<MemberRechargeAwardRecordVO> findRechargeAwardRecord(@ParameterObject FindRechargeAwardRecordForm form) {
        return PageResult.success(iMemberRechargeAwardRecordService.findRechargeAwardRecord(form));
    }

    @Operation(summary = "找存送個人優惠領取紀錄")
    @GetMapping("/personal")
    public PageResult<MemberPersonalRechargeAwardRecordVO> findPersonalRechargeAwardRecord(@ParameterObject FindPersonalRechargeAwardRecordForm form) {
        return PageResult.success(iMemberRechargeAwardRecordService.findPersonalRechargeAwardRecord(form));
    }

    @Operation(summary = "找存送個人優惠領取紀錄By模組")
    @GetMapping("/personal/{templateId}/{memberId}")
    public Result<List<AllMemberPersonalRechargeAwardRecordByTemplateIdVO>> findAllPersonalRechargeAwardRecordByTemplateId(@PathVariable("templateId")Long templateId, @PathVariable("memberId")Long memberId) {
        return Result.success(iMemberRechargeAwardRecordService.findAllPersonalRechargeAwardRecordByTemplateId(templateId,memberId));
    }

    @Operation(summary = "存送個人優惠領取紀錄",description = "取消")
    @PutMapping("/personal/cancel/{id}")
    public Result<Boolean> cancelPersonalRechargeAwardRecord(@PathVariable("id") Long id) {
        return Result.success(iMemberRechargeAwardRecordService.cancelPersonalRechargeAwardRecord(id));
    }

}
