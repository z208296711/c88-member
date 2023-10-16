package com.c88.member.controller.h5;

import com.c88.common.core.enums.EnableEnum;
import com.c88.common.core.result.Result;
import com.c88.member.mapstruct.MemberAdvertisementConverter;
import com.c88.member.pojo.entity.MemberAdvertisement;
import com.c88.member.pojo.vo.H5MemberAdvertisementVO;
import com.c88.member.service.IMemberAdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "『前台』廣告")
@RestController
@RequestMapping("/h5/advertisement")
@RequiredArgsConstructor
public class H5MemberAdvertisementController {

    private final IMemberAdvertisementService iMemberAdvertisementService;

    private final MemberAdvertisementConverter memberAdvertisementConverter;

    @Operation(summary = "找廣告")
    @GetMapping
    public Result<List<H5MemberAdvertisementVO>> findAdvertisement() {
        LocalDateTime now = LocalDateTime.now();

        List<H5MemberAdvertisementVO> advertisementVOS = iMemberAdvertisementService.lambdaQuery()
                .eq(MemberAdvertisement::getEnable, EnableEnum.START.getCode())
                .le(MemberAdvertisement::getStartTime, now)
                .ge(MemberAdvertisement::getEndTime, now)
                .orderByAsc(MemberAdvertisement::getSort)
                .list()
                .stream()
                .map(memberAdvertisementConverter::toH5Vo)
                .collect(Collectors.toList());

        return Result.success(advertisementVOS);
    }


}
