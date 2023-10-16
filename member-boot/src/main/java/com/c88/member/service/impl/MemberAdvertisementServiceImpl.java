package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.web.enums.SortTypeEnum;
import com.c88.member.mapper.MemberAdvertisementMapper;
import com.c88.member.mapstruct.MemberAdvertisementConverter;
import com.c88.member.pojo.entity.MemberAdvertisement;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.MemberAdvertisementSortVO;
import com.c88.member.pojo.vo.MemberAdvertisementVO;
import com.c88.member.service.IMemberAdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author user
 * @description 针对表【member_advertisement(廣告)】的数据库操作Service实现
 * @createDate 2022-09-05 18:27:47
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAdvertisementServiceImpl extends ServiceImpl<MemberAdvertisementMapper, MemberAdvertisement>
        implements IMemberAdvertisementService {

    private final MemberAdvertisementConverter memberAdvertisementConverter;

    @Override
    public IPage<MemberAdvertisementVO> findAdvertisement(FindAdvertisementForm form) {
        return this.lambdaQuery()
                .like(StringUtils.isNotBlank(form.getName()), MemberAdvertisement::getName, form.getName())
                .eq(Objects.nonNull(form.getEnable()), MemberAdvertisement::getEnable, form.getEnable())
                .orderByAsc(MemberAdvertisement::getSort)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberAdvertisementConverter::toVo);
    }

    @Override
    public IPage<MemberAdvertisementSortVO> findAdvertisementSort(FindAdvertisementSortForm form) {
        return this.lambdaQuery()
                .orderByAsc(MemberAdvertisement::getSort)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberAdvertisementConverter::toSortVo);
    }

    @Transactional
    @Override
    public Boolean addAdvertisement(AddAdvertisementForm form) {
        MemberAdvertisement memberAdvertisement = memberAdvertisementConverter.toEntity(form);

        memberAdvertisement.setSort(
                this.lambdaQuery()
                        .select(MemberAdvertisement::getSort)
                        .orderByAsc(MemberAdvertisement::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberAdvertisement.builder().sort(this.count()).build())
                        .getSort() - 1
        );
        return this.save(memberAdvertisement);
    }

    @Override
    public Boolean modifyAdvertisement(ModifyAdvertisementForm form) {
        return this.updateById(memberAdvertisementConverter.toEntity(form));
    }

    @Override
    public Boolean deleteAdvertisement(List<Integer> ids) {
        return this.removeByIds(ids);
    }

    @Override
    public Boolean modifyAdvertisementSort(Map<Integer, Integer> map) {
        return this.updateBatchById(
                map.entrySet()
                        .stream()
                        .map(sort -> MemberAdvertisement.builder().id(sort.getKey()).sort(sort.getValue()).build())
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    @Override
    public Boolean modifyAdvertisementSortTopBottom(ModifyAdvertisementSortTopBottomForm form) {
        SortTypeEnum sortType = SortTypeEnum.getEnum(form.getSortType());

        switch (sortType) {
            case TOP:
                int minSort = this.lambdaQuery()
                        .select(MemberAdvertisement::getSort)
                        .ne(MemberAdvertisement::getId, form.getId())
                        .orderByAsc(MemberAdvertisement::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberAdvertisement.builder().sort(this.count()).build())
                        .getSort() - 1;
                return this.lambdaUpdate()
                        .eq(MemberAdvertisement::getId, form.getId())
                        .set(MemberAdvertisement::getSort, minSort)
                        .update();
            case BOTTOM:
                int maxSort = this.lambdaQuery()
                        .select(MemberAdvertisement::getSort)
                        .ne(MemberAdvertisement::getId, form.getId())
                        .orderByDesc(MemberAdvertisement::getSort)
                        .last("limit 1")
                        .oneOpt()
                        .orElse(MemberAdvertisement.builder().sort(this.count()).build())
                        .getSort() + 1;
                return this.lambdaUpdate()
                        .eq(MemberAdvertisement::getId, form.getId())
                        .set(MemberAdvertisement::getSort, maxSort)
                        .update();
            default:
                return Boolean.FALSE;
        }
    }


}




