package com.c88.member.controller.h5;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.common.core.base.BasePageQuery;
import com.c88.common.core.enums.EnableEnum;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.common.enums.PromotionTypeEnum;
import com.c88.member.mapstruct.MemberPromotionConverter;
import com.c88.member.pojo.entity.MemberPromotion;
import com.c88.member.pojo.entity.MemberPromotionCategory;
import com.c88.member.pojo.vo.H5MemberPromotionVO;
import com.c88.member.pojo.vo.H5MemberSimplePromotionVO;
import com.c88.member.service.IMemberPromotionCategoryService;
import com.c88.member.service.IMemberPromotionService;
import com.c88.member.vo.OptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag(name = "『前台』優惠活動")
@RestController
@RequestMapping("/h5/promotion")
@RequiredArgsConstructor
public class H5MemberPromotionController {

    private final IMemberPromotionService iMemberPromotionService;

    private final IMemberPromotionCategoryService iMemberPromotionCategoryService;

    private final MemberPromotionConverter memberPromotionConverter;

    @Operation(summary = "找優惠活動類型")
    @GetMapping("/category/option")
    public Result<List<OptionVO<Integer>>> findSimplePromotion() {
        List<OptionVO<Integer>> promotionCategoryOption = iMemberPromotionCategoryService.lambdaQuery()
                .orderByAsc(MemberPromotionCategory::getSort)
                .list()
                .stream()
                .map(x -> OptionVO.<Integer>builder().value(x.getId()).label(x.getName()).build())
                .collect(Collectors.toList());

        return Result.success(promotionCategoryOption);
    }

    @Operation(summary = "找優惠活動簡圖", description = "categoryId=0 全部")
    @GetMapping("/{categoryId}")
    public PageResult<H5MemberSimplePromotionVO> findSimplePromotion(@PathVariable("categoryId") Integer categoryId, @ParameterObject BasePageQuery page) {
        LocalDateTime now = LocalDateTime.now();

        IPage<H5MemberSimplePromotionVO> memberSimplePromotionVOS = iMemberPromotionService.lambdaQuery()
                .eq(categoryId != 0, MemberPromotion::getCategoryId, categoryId)
                .eq(MemberPromotion::getEnable, EnableEnum.START.getCode())
                .le(MemberPromotion::getStartTime, now)
                .ge(MemberPromotion::getEndTime, now)
                .orderByAsc(MemberPromotion::getSort)
                .page(new Page<>(page.getPageNum(), page.getPageSize()))
                .convert(memberPromotionConverter::toH5SimpleVo);

        // 內站活動時清除網址
        memberSimplePromotionVOS.getRecords().forEach(vo -> {
                    if (Objects.equals(vo.getType(), PromotionTypeEnum.IN.getCode())) {
                        vo.setUrl("");
                    }
                }
        );

        return PageResult.success(memberSimplePromotionVOS);
    }

    @Operation(summary = "找優惠活動內容")
    @GetMapping("/content/{promotionId}")
    public Result<H5MemberPromotionVO> findPromotionContent(@PathVariable("promotionId") Integer promotionId) {
        return Result.success(memberPromotionConverter.toH5Vo(iMemberPromotionService.getById(promotionId)));
    }

}
