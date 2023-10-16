package com.c88.member.controller.h5;

import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.MemberUtils;
import com.c88.member.pojo.vo.H5DrawInfoVO;
import com.c88.member.pojo.vo.H5DrawItemVO;
import com.c88.member.pojo.vo.H5RouletteVO;
import com.c88.member.pojo.vo.UpdateDrawRecordAddressForm;
import com.c88.member.service.IDrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "『前台』抽獎活動")
@RestController
@RequestMapping("/h5/draw")
@RequiredArgsConstructor
public class H5MemberDrawController {

    private final IDrawService iDrawService;

    @Operation(summary = "取得抽獎資訊")
    @GetMapping("/info")
    public Result<H5DrawInfoVO> getDrawInfo() {
        Long memberId = MemberUtils.getMemberId();
        return Result.success(iDrawService.getDrawInfo(memberId));
    }

    @Operation(summary = "取得輪盤資訊")
    @GetMapping("/roulette")
    public Result<H5RouletteVO> getRoulette() {
        Long memberId = MemberUtils.getMemberId();
        return Result.success(iDrawService.getRouletteVO(memberId));
    }

    @Operation(summary = "抽獎")
    @PostMapping
    public Result<H5DrawItemVO> doDrawAction() {
        Long memberId = MemberUtils.getMemberId();
        if (null == memberId) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        H5DrawItemVO h5DrawItemVO = iDrawService.doDrawAction(memberId);
        if (h5DrawItemVO == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return Result.success(h5DrawItemVO);
    }

    @Operation(summary = "更新地址")
    @PutMapping("/send/address")
    public Result<Boolean> updateDrawRecordAddress(@RequestBody UpdateDrawRecordAddressForm form) {
        return Result.success(iDrawService.updateDrawRecordAddress(form));
    }

}
