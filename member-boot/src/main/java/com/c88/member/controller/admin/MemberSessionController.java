package com.c88.member.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.c88.common.core.result.Result;
import com.c88.member.common.enums.MemberSessionCheckEnum;
import com.c88.member.dto.MemberAssociationRateDTO;
import com.c88.member.dto.MemberSessionCheckDTO;
import com.c88.member.form.MemberCheckForm;
import com.c88.member.pojo.entity.MemberAssociationRate;
import com.c88.member.service.IMemberAssociationRateService;
import com.c88.member.service.IMemberSessionService;
import com.c88.member.service.impl.MemberServiceImpl;
import com.c88.member.vo.OptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "『後台』會員登入管理")
@RestController
@RequestMapping("/api/v1/membersession")
@RequiredArgsConstructor
@Slf4j
public class MemberSessionController {
    @Autowired
    private IMemberSessionService memberSessionService;

    @Autowired
    private MemberServiceImpl memberService;

    @Autowired
    private IMemberAssociationRateService memberAssociationRateService;

    @Operation(summary = "關聯記錄", description = "關聯記錄報表")
    @PostMapping("/association")
    public Result<MemberSessionCheckDTO> getSessionCheck(@Valid @RequestBody MemberCheckForm form) {
        MemberSessionCheckDTO dto = memberSessionService.getCheckList(form.getUsername(), form.getType());
        BigDecimal rateByType = memberAssociationRateService.getAssociateRateByType(form.getUsername(), form.getType());
        dto.setRate(rateByType);
        return Result.success(dto);
    }

    @Operation(summary = "關聯記錄類型", description = "關聯記錄類型下表選單")
    @GetMapping("/association/types")
    public Result<List<OptionVO<Integer>>> getTypes() {
        List<OptionVO<Integer>> collect = Arrays.stream(MemberSessionCheckEnum.values()).map(m -> new OptionVO<>(m.getValue(), m.getI18nName())).collect(Collectors.toList());
        return Result.success(collect);
    }

    @Operation(summary = "一鍵凍結關聯帳號", description = "一鍵凍結關聯帳號")
    @PostMapping("/association/freezeUsers")
    public Result<Boolean> lockUsers(@RequestBody List<String> usernames) {
        return Result.success(memberService.freezeUsers(usernames));
    }

    @Operation(summary = "凍結關聯帳號", description = "一鍵凍結關聯帳號")
    @PutMapping("/association/freezeUser/{username}")
    public Result<Boolean> lockUser(@PathVariable("username") String username) {
        return Result.success(memberService.freezeUser(username));
    }

    @Operation(summary = "目前關聯項警告門檻", description = "目前關聯項警告門檻")
    @GetMapping("/getAssociation")
    public Result<MemberAssociationRateDTO> getAssociation() {
        MemberAssociationRate association = memberAssociationRateService.getAssociation();
        MemberAssociationRateDTO dto = new MemberAssociationRateDTO();
        BeanUtils.copyProperties(association, dto);
        return Result.success(dto);
    }

    @GetMapping("/userAssociation/{username}")
    public Result<MemberAssociationRateDTO> userAssociation(@PathVariable String username) {
        MemberAssociationRate association = memberAssociationRateService.getAssociation(username);
        MemberAssociationRateDTO dto = null;
        if (association != null) {
            dto = new MemberAssociationRateDTO();
            BeanUtils.copyProperties(association, dto);
        }
        return Result.success(dto);
    }

    @Operation(summary = "更改關聯項警告門檻", description = "更改關聯項警告門檻")
    @PostMapping("/updateSystemAssociation")
    public Result<MemberAssociationRate> updateAssociation(@RequestBody MemberAssociationRate rate) {
        memberAssociationRateService.updateSystemAssociation(rate);
        return Result.success();
    }

    @Operation(summary = "警告已啟用", description = "更改個人警告({'username':'allen', 'rateType':1, 'value':10})")
    @PostMapping("/updateAssociation")
    public Result<MemberAssociationRate> updateAssociation(@RequestBody String json) {
        JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
        String username = jsonObject.getString("username");
        int rateType = jsonObject.getInteger("rateType");
        BigDecimal value = jsonObject.getBigDecimal("value");

        memberAssociationRateService.updateUserAssociation(username, rateType, value);
        return Result.success();
    }

    @Operation(summary = "會員詳情關聯項", description = "顯示與會員關聯的次數(1:註冊ip, 5:uuid)")
    @GetMapping("/getIPAssociationCount/{username}/{type}")
    public Result<Integer> getUUIDAssociationCount(@Parameter(description = "帳號") @PathVariable("username") String username, @Parameter(description = "類型") @PathVariable("type") int type) {
        MemberSessionCheckDTO checkList = memberSessionService.getCheckList(username, type);
        return Result.success(checkList.getConnectNames().size());
    }
}
