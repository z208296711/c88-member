package com.c88.member.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.common.enums.VipLevelRecordStateEnum;
import com.c88.member.dto.*;
import com.c88.member.mapstruct.MemberVipRecordConverter;
import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.entity.MemberTag;
import com.c88.member.pojo.entity.MemberVipLevelRecord;
import com.c88.member.pojo.form.FindMemberLoginRecordForm;
import com.c88.member.pojo.form.ModifyMemberWithdrawControllerStateForm;
import com.c88.member.pojo.form.ModifyMemberWithdrawPasswordForm;
import com.c88.member.pojo.form.SearchMemberVipLevelRecordForm;
import com.c88.member.pojo.query.MemberPageQuery;
import com.c88.member.pojo.vo.AdminMemberVO;
import com.c88.member.pojo.vo.MemberDetailVipRecordVO;
import com.c88.member.pojo.vo.MemberSessionVO;
import com.c88.member.service.IMemberService;
import com.c88.member.service.IMemberSessionService;
import com.c88.member.service.IMemberTagService;
import com.c88.member.service.IMemberVipLevelRecordService;
import com.google.common.base.CaseFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 會員管理
 */
@Tag(name = "『後台』會員管理")
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final IMemberVipLevelRecordService iMemberVipLevelRecordService;

    private final MemberVipRecordConverter memberVipRecordConverter;

    private final IMemberService memberService;

    private final IMemberTagService iMemberTagService;

    private final IMemberSessionService iMemberSessionService;

    @Operation(summary = "根据會員帳號获取认证信息", description = "提供用于會員登录认证信息", hidden = true)
    @GetMapping("/userName/{userName}")
    public Result<AuthUserDTO> getMemberByUserName(
            @Parameter(description = "會員帳號") @PathVariable String userName) {
        Member member = memberService.getMemberByUserName(userName);
        return Result.success(BeanUtil.copyProperties(member, AuthUserDTO.class));
    }

    @Operation(summary = "根据會員手機获取认证信息", description = "提供用于會員登录认证信息", hidden = true)
    @GetMapping("/mobile/{mobile}")
    public Result<AuthUserDTO> getMemberByMobile(
            @Parameter(description = "會員手機") @PathVariable String mobile) {
        Member member = memberService.getMemberByMobile(mobile);
        return Result.success(BeanUtil.copyProperties(member, AuthUserDTO.class));
    }

    @Operation(summary = "根据會員電子郵件获取认证信息", description = "提供用于會員登录认证信息", hidden = true)
    @GetMapping("/email/{email}")
    public Result<AuthUserDTO> getMemberByEmail(
            @Parameter(description = "會員電子郵件") @PathVariable String email) {
        Member member = memberService.getMemberByEmail(email);
        return Result.success(BeanUtil.copyProperties(member, AuthUserDTO.class));
    }

    @GetMapping(value = "/{id}/info")
    public Result<MemberInfoDTO> findMemberInfoById(@PathVariable Long id) {
        return Result.success(memberService.findMemberInfoById(id));
    }

    @GetMapping(value = "/userName/{userName}/info")
    public Result<MemberInfoDTO> findMemberInfoByUsername(@PathVariable String userName) {
        return Result.success(memberService.findMemberInfoByUsername(userName));
    }

    @GetMapping(value = "/username/info")
    public Result<List<MemberInfoDTO>> findMemberInfoByUsernames(@RequestParam List<String> usernames) {
        return Result.success(memberService.findMemberInfoByUsernames(usernames));
    }

    @Operation(summary = "會員列表")
    @PostMapping("/query")
    public PageResult<AdminMemberVO> findMember(@RequestBody MemberPageQuery memberPageQuery) {
        // return PageResult.success(memberService.queryMember(memberPageQuery));
        return PageResult.success(memberService.findMembers(memberPageQuery));
    }

    @Operation(summary = "會員詳情")
    @GetMapping(value = "/{id}")
    public Result<AdminMemberVO> id(@PathVariable Long id) {
        AdminMemberVO adminMemberVO = memberService.getDetail(id);

        return Result.success(adminMemberVO);
    }

    @GetMapping(value = "/{id}/dto")
    public Result<MemberDTO> idDto(@PathVariable Long id) {
        MemberDTO memberDTO = new MemberDTO();
        AdminMemberVO adminMemberVO = memberService.getDetail(id);
        BeanUtils.copyProperties(adminMemberVO, memberDTO);
        return Result.success(memberDTO);
    }

    @GetMapping(value = "/tags/{id}")
    public Result<Set<Integer>> tagIds(@PathVariable Long id) {
        Set<Integer> tagIds = iMemberTagService.lambdaQuery()
                .eq(MemberTag::getMemberId, id)
                .list()
                .stream()
                .map(MemberTag::getTagId)
                .collect(Collectors.toSet());

        return Result.success(tagIds);
    }

    @GetMapping("/similarUsername/{id}/{username}")
    Result<MemberDTO> getMemberSimilarUsername(@PathVariable Long id, @PathVariable String username) {
        Member member = memberService.getOne(new LambdaQueryWrapper<Member>().select(Member::getId)
                .ne(Member::getId, id)
                .and(queryWrapper -> queryWrapper.likeLeft(Member::getUserName, username)
                        .or().likeRight(Member::getUserName, username)).last("limit 1"));
        MemberDTO memberDTO = new MemberDTO();
        if (member != null) {
            BeanUtils.copyProperties(member, memberDTO);
        }
        return Result.success(member != null ? memberDTO : null);
    }

    @GetMapping("/sameColumn/{id}/{column}/{value}")
    Result<MemberDTO> getMemberSameColumn(@PathVariable Long id, @PathVariable String column, @PathVariable String value) {
        Member member = memberService.getOne(new QueryWrapper<Member>()
                .eq(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column), value).lambda()
                .select(Member::getId)
                .ne(Member::getId, id).last("limit 1"));
        MemberDTO memberDTO = new MemberDTO();
        if (member != null) {
            BeanUtils.copyProperties(member, memberDTO);
        }
        return Result.success(member != null ? memberDTO : null);
    }

    @Operation(summary = "取得-VIP升降級紀錄")
    @GetMapping("/{memberId}/vip/level/up/record")
    public PageResult<MemberDetailVipRecordVO> getVipLevelRecordPage(@PathVariable Long memberId, @ParameterObject SearchMemberVipLevelRecordForm form) {
        IPage<MemberVipLevelRecord> memberVipLevelRecordIPage = iMemberVipLevelRecordService.lambdaQuery()
                .eq(MemberVipLevelRecord::getMemberId, memberId)
                .in(MemberVipLevelRecord::getState, List.of(VipLevelRecordStateEnum.LEVEL_UP.getValue(), VipLevelRecordStateEnum.LEVEL_DOWN.getValue()))
                .ge(StringUtils.isNotBlank(form.getStartTime()), MemberVipLevelRecord::getLevelChangeTime, form.getStartTime())
                .le(StringUtils.isNotBlank(form.getEndTime()), MemberVipLevelRecord::getLevelChangeTime, form.getEndTime())
                .orderByDesc(MemberVipLevelRecord::getGmtCreate)
                .page(new Page<>(form.getPageNum(), form.getPageSize()));
        return PageResult.success(memberVipLevelRecordIPage.convert(memberVipRecordConverter::toMemberDetailVO));
    }

    @Operation(summary = "凍結會員-代理專用", hidden = true)
    @PostMapping("/lock/down")
    public Result<Boolean> lockDown(@RequestBody MemberLockDownDTO dto) {
        return Result.success(memberService.lockDown(dto.getMemberIdList()));
    }

    @Operation(summary = "新註冊會員")
    @GetMapping("/register/count")
    public Result<Integer> getRegisterCount(@RequestParam String startTime, @RequestParam String endTime) {
        return Result.success(memberService.getRegisterCount(startTime, endTime));
    }

    @Operation(summary = "查詢會員登錄記錄")
    @GetMapping("/loginInfo")
    public PageResult<MemberSessionVO> getMemberLoginInfo(@ParameterObject FindMemberLoginRecordForm form) {
        return PageResult.success(iMemberSessionService.findMemberSession(form));
    }

    @Operation(summary = "資本概括 修改提款密碼")
    @PutMapping("/member/withdraw/password")
    public Result<Boolean> modifyMemberWithdrawPassword(@Validated @RequestBody ModifyMemberWithdrawPasswordForm form) {
        return Result.success(memberService.modifyMemberWithdrawPassword(form));
    }

    @Operation(summary = "資本概括 修改提款控制", description = "0關閉提款 1開啟提款")
    @PutMapping("/member/withdraw/controller/state")
    public Result<Boolean> modifyMemberWithdrawControllerState(@Validated @RequestBody ModifyMemberWithdrawControllerStateForm form) {
        return Result.success(memberService.modifyMemberWithdrawControllerState(form));
    }

}
