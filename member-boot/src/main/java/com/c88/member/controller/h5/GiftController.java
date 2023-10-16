package com.c88.member.controller.h5;

import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.MemberUtils;
import com.c88.member.pojo.form.ReceiveFreeBetGiftForm;
import com.c88.member.pojo.form.ReceiveLevelUpGiftForm;
import com.c88.member.pojo.vo.H5FreeBetGiftVO;
import com.c88.member.pojo.vo.H5GiftNotificationVO;
import com.c88.member.pojo.vo.H5LevelUpGiftVO;
import com.c88.member.pojo.vo.MemberBirthdayGiftVO;
import com.c88.member.service.IGiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Tag(name = "『前台』免費籌碼相關")
@RestController
@RequiredArgsConstructor
@RequestMapping("/h5/gift")
public class GiftController {

    private final IGiftService iGiftService;

    @Operation(summary = "取得-VIP專屬通知 (週/月禮金, 免費籌碼, 晉級禮金)")
    @GetMapping("/notification")
    public Result<List<H5GiftNotificationVO>> findGiftNotification() {
        Long memberId = MemberUtils.getMemberId();
        String username = MemberUtils.getUsername();
        if (Objects.isNull(memberId) || StringUtils.isBlank(username)) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(iGiftService.findGiftNotification(memberId, username));
    }

    @Operation(summary = "取得-免費籌碼領取狀態")
    @GetMapping("/free/bet")
    public Result<List<H5FreeBetGiftVO>> findFreeBet() {
        Long memberId = MemberUtils.getMemberId();
        if (memberId == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(iGiftService.getFreeBetGiftList(memberId));
    }

    @Operation(summary = "領取-免費籌碼領取狀態")
    @PostMapping("/free/bet")
    public Result<Boolean> receiveFreeBet(@Validated @RequestBody ReceiveFreeBetGiftForm form) {
        Long memberId = MemberUtils.getMemberId();
        if (memberId == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(iGiftService.receiveFreeBetGift(memberId, form.getType()));
    }

    @Operation(summary = "取得-晉級禮金領取狀態")
    @GetMapping("/level/up")
    public Result<List<H5LevelUpGiftVO>> getLevelUpGiftList() {
        Long memberId = MemberUtils.getMemberId();
        if (memberId == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(iGiftService.getLevelUpGiftList(memberId));
    }

    @Operation(summary = "領取-晉級禮金領取狀態")
    @PostMapping("/level/up")
    public Result<Boolean> receiveLevelUpGift(@RequestBody ReceiveLevelUpGiftForm form) {
        Long memberId = MemberUtils.getMemberId();
        if (memberId == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(iGiftService.receiveLevelUpGift(memberId, form.getVipId()));
    }

    @Operation(summary = "取得-會員生日禮金內容")
    @GetMapping("/birthday")
    public Result<MemberBirthdayGiftVO> findMemberBirthdayGift() {
        Long memberId = MemberUtils.getMemberId();
        if (Objects.isNull(memberId)) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }

        return Result.success(iGiftService.findMemberBirthdayGift(memberId));
    }

    @Operation(summary = "領取-會員生日禮金")
    @PostMapping("/birthday")
    public Result<Boolean> receiveMemberBirthdayGift() {
        Long memberId = MemberUtils.getMemberId();
        if (Objects.isNull(memberId)) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(iGiftService.receiveMemberBirthdayGift(memberId));
    }

}
