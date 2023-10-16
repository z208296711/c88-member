package com.c88.member.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.enums.SortTypeEnum;
import com.c88.common.web.exception.BizException;
import com.c88.member.common.enums.AwardTypeEnum;
import com.c88.common.core.enums.RechargeAwardTypeEnum;
import com.c88.member.mapper.MemberRechargeAwardTemplateMapper;
import com.c88.member.mapstruct.MemberRechargeAwardTemplateConverter;
import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.entity.MemberRechargeAwardTemplate;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.MemberRechargeAwardTemplateDrawOptionVO;
import com.c88.member.pojo.vo.MemberRechargeAwardTemplateVO;
import com.c88.member.pojo.vo.RechargeAwardTemplateLinkModeVO;
import com.c88.member.service.IMemberDrawAwardItemService;
import com.c88.member.service.IMemberRechargeAwardTemplateService;
import com.c88.member.vo.MemberRechargeAwardTemplateClientVO;
import com.c88.member.vo.OptionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author user
 * @description 针对表【member_recharge_award_template(存送優惠模型)】的数据库操作Service实现
 * @createDate 2022-10-25 14:09:22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRechargeAwardTemplateServiceImpl extends ServiceImpl<MemberRechargeAwardTemplateMapper, MemberRechargeAwardTemplate>
        implements IMemberRechargeAwardTemplateService {

    public final MemberRechargeAwardTemplateConverter memberRechargeAwardTemplateConverter;

    private final IMemberDrawAwardItemService iMemberDrawAwardItemService;

    @Override
    public IPage<MemberRechargeAwardTemplateVO> findRechargeAwardTemplate(FindRechargeAwardTemplateForm form) {
        return this.lambdaQuery()
                .like(StringUtils.isNotBlank(form.getName()), MemberRechargeAwardTemplate::getName, form.getName())
                .eq(Objects.nonNull(form.getEnable()), MemberRechargeAwardTemplate::getEnable, form.getEnable())
                .orderByAsc(MemberRechargeAwardTemplate::getSort)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberRechargeAwardTemplateConverter::toVo);
    }

    @Override
    @Transactional
    public Boolean addRechargeAwardTemplate(AddRechargeAwardTemplateForm form) {
        MemberRechargeAwardTemplate template = memberRechargeAwardTemplateConverter.toEntity(form);
        boolean save = this.save(template);

        if (save) {
            int minSort = this.lambdaQuery()
                    .select(MemberRechargeAwardTemplate::getSort)
                    .ne(MemberRechargeAwardTemplate::getId, template.getId())
                    .orderByAsc(MemberRechargeAwardTemplate::getSort)
                    .last("limit 1")
                    .oneOpt()
                    .orElse(MemberRechargeAwardTemplate.builder().sort(this.count()).build())
                    .getSort() - 1;
            this.lambdaUpdate()
                    .eq(MemberRechargeAwardTemplate::getId, template.getId())
                    .set(MemberRechargeAwardTemplate::getSort, minSort)
                    .update();
        }
        return save;
    }

    @Override
    @Transactional
    public Boolean modifyRechargeAwardTemplate(ModifyRechargeAwardTemplateForm form) {
        boolean update = this.updateById(memberRechargeAwardTemplateConverter.toEntity(form));
        if (update && Objects.isNull(form.getEnable())) {
            this.update(null, Wrappers.<MemberRechargeAwardTemplate>lambdaUpdate()
                    .eq(MemberRechargeAwardTemplate::getId, form.getId())
                    .set(MemberRechargeAwardTemplate::getRegisterStartTime, form.getRegisterStartTime())
                    .set(MemberRechargeAwardTemplate::getRegisterEndTime, form.getRegisterEndTime())
            );
        }

        return update;
    }

    @Override
    public Boolean deleteRechargeAwardTemplate(List<Long> ids) {
        if (iMemberDrawAwardItemService.lambdaQuery()
                .eq(MemberDrawAwardItem::getAwardType, AwardTypeEnum.RECHARGE_PROMOTION.getValue())
                .in(MemberDrawAwardItem::getPromotionId, ids)
                .count() != 0) {
            throw new BizException(ResultCode.NEED_FIRST_DELETE_DRAW_ITEM_BY_RECHARGE_AWARD);
        }

        return this.removeByIds(ids);
    }

    @Override
    public Boolean modifyAdvertisementSort(Map<Long, Integer> map) {
        return this.updateBatchById(
                map.entrySet()
                        .stream()
                        .map(sort -> MemberRechargeAwardTemplate.builder().id(sort.getKey()).sort(sort.getValue()).build())
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public Boolean modifyAdvertisementSortTopBottom(ModifyRechargeAwardTemplateSortTopBottomForm form) {
        SortTypeEnum sortType = SortTypeEnum.getEnum(form.getSortType());

        switch (sortType) {
            case TOP:
                int minSort = this.lambdaQuery()
                        .select(MemberRechargeAwardTemplate::getSort)
                        .ne(MemberRechargeAwardTemplate::getId, form.getId())
                        .orderByAsc(MemberRechargeAwardTemplate::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberRechargeAwardTemplate.builder().sort(this.count()).build())
                        .getSort() - 1;
                return this.lambdaUpdate()
                        .eq(MemberRechargeAwardTemplate::getId, form.getId())
                        .set(MemberRechargeAwardTemplate::getSort, minSort)
                        .update();
            case BOTTOM:
                int maxSort = this.lambdaQuery()
                        .select(MemberRechargeAwardTemplate::getSort)
                        .ne(MemberRechargeAwardTemplate::getId, form.getId())
                        .orderByDesc(MemberRechargeAwardTemplate::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberRechargeAwardTemplate.builder().sort(this.count()).build())
                        .getSort() + 1;
                return this.lambdaUpdate()
                        .eq(MemberRechargeAwardTemplate::getId, form.getId())
                        .set(MemberRechargeAwardTemplate::getSort, maxSort)
                        .update();
            default:
                return Boolean.FALSE;
        }
    }

    @Override
    public Boolean modifyRechargeAwardTemplateLinkMode(ModifyRechargeAwardTemplateLinkModeForm form) {
        return this.updateBatchById(form.getTemplateIdToSort()
                .entrySet()
                .stream()
                .map(x ->
                        MemberRechargeAwardTemplate.builder()
                                .linkModeId(RandomUtil.randomInt())
                                .id(x.getKey())
                                .linkSort(x.getValue())
                                .mode(form.getMode())
                                .build()
                )
                .collect(Collectors.toList())
        );
    }

    @Override
    public List<RechargeAwardTemplateLinkModeVO> findRechargeAwardTemplateLinkMode(Integer id) {
        return this.lambdaQuery()
                .eq(MemberRechargeAwardTemplate::getLinkModeId, id)
                .orderByDesc(MemberRechargeAwardTemplate::getLinkSort)
                .list()
                .stream()
                .map(memberRechargeAwardTemplateConverter::toLinkModeVO)
                .collect(Collectors.toList());
    }

    @Override
    public MemberRechargeAwardTemplateClientVO findRechargeAwardTemplate(Long id) {
        return this.lambdaQuery()
                .eq(MemberRechargeAwardTemplate::getId, id)
                .oneOpt()
                .map(memberRechargeAwardTemplateConverter::toClientVO)
                .orElseThrow(() -> new BizException(ResultCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public Boolean deleteRechargeAwardTemplateLinkMode(Integer id) {
        return this.lambdaUpdate().eq(MemberRechargeAwardTemplate::getLinkModeId, id)
                .set(MemberRechargeAwardTemplate::getLinkModeId, 0)
                .set(MemberRechargeAwardTemplate::getLinkMode, 0)
                .set(MemberRechargeAwardTemplate::getLinkSort, 0)
                .update();
    }

    @Override
    public List<OptionVO<Long>> findRechargeAwardTemplateOption() {
        return this.lambdaQuery()
                .list()
                .stream()
                .filter(x->x.getEnable().equals(1))
                .filter(x->x.getEndTime().isAfter(LocalDateTime.now()))
                .map(x -> OptionVO.<Long>builder().value(x.getId()).label(x.getName()).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberRechargeAwardTemplateDrawOptionVO> findRechargeAwardTemplateDrawOption(Integer enable) {
        return this.lambdaQuery()
                .eq(MemberRechargeAwardTemplate::getType, RechargeAwardTypeEnum.PERSONAL.getCode())
                .eq(enable != null, MemberRechargeAwardTemplate::getEnable, enable)
                .list()
                .stream()
                .map(memberRechargeAwardTemplateConverter::toDrawVO)
                .collect(Collectors.toList());
    }

}




