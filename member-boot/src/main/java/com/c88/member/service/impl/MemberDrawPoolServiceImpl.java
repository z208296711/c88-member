package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.web.enums.SortTypeEnum;
import com.c88.member.common.enums.DrawTypeEnum;
import com.c88.member.mapper.MemberDrawPoolMapper;
import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.entity.MemberDrawPool;
import com.c88.member.pojo.form.ModifyDrawpoolSortTopBottomForm;
import com.c88.member.pojo.vo.EnableDrawItemCountVO;
import com.c88.member.pojo.vo.MInMaxVO;
import com.c88.member.pojo.vo.MemberDrawVO;
import com.c88.member.service.IMemberDrawAwardItemService;
import com.c88.member.service.IMemberDrawPoolService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@AllArgsConstructor
public class MemberDrawPoolServiceImpl extends ServiceImpl<MemberDrawPoolMapper, MemberDrawPool>
    implements IMemberDrawPoolService {

    private final IMemberDrawAwardItemService memberDrawAwardItemService;

    @Override
    public List<MemberDrawVO> getDrawPoolsByTemplateId(long templateId) {
        List<MemberDrawVO> memberDrawPools = baseMapper.getDrawpoolByTemplateId(templateId);
        return memberDrawPools;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "activityDrawItem", allEntries = true)
    })
    public boolean update(Long templateId, List<MemberDrawPool> pools) {
        List<MemberDrawPool> poolInDB = this.baseMapper.selectList(new LambdaQueryWrapper<MemberDrawPool>().eq(MemberDrawPool::getTemplateId, templateId));
        List<MemberDrawPool> memberDrawPools = poolInDB.stream().peek(p -> p.setEnable(0)).collect(Collectors.toList());
        baseMapper.batchSaveOrUpdateDrawpool(memberDrawPools);
        baseMapper.batchSaveOrUpdateDrawpool(pools);
        return true;
    }

    @Override
    public boolean insert(List<MemberDrawPool> pools) {
        return false;
    }

    @Override
    public List<MemberDrawVO> list(Long templateId) {

        //所有已啟用將項
        List<MemberDrawAwardItem> totalAwardList = memberDrawAwardItemService.lambdaQuery().eq(MemberDrawAwardItem::getEnable, 1).list();

        List<MemberDrawVO> pools = baseMapper.getDrawpoolByTemplateId(templateId).stream().sorted(Comparator.comparing(new Function<MemberDrawVO, Integer>() {
            @Override
            public Integer apply(MemberDrawVO vo){
                return vo.getSort();
            }
        })).collect(Collectors.toList());

        List<Long> poolsAwardId = pools.stream().map(MemberDrawVO::getAwardId).collect(Collectors.toList());
        Optional<MemberDrawVO> memberDrawVO = pools.stream().findFirst();
        MInMaxVO minMaxSort;
        if(memberDrawVO.isPresent()){
            minMaxSort = baseMapper.getMinMaxSort(memberDrawVO.get().getId()).get(0);
        }else {
            minMaxSort = MInMaxVO.builder().max(0).min(0).build();
        }

        int maxSort = minMaxSort.getMax();
        for (int i = 0; i < totalAwardList.size(); i++) {
            MemberDrawAwardItem p = totalAwardList.get(i);
            if (!poolsAwardId.contains(p.getId())) {
                pools.add(MemberDrawVO.builder()
                        .templateId(templateId)
                        .drawType(DrawTypeEnum.COMMON_DRAW.getValue())
                        .awardId(p.getId())
                        .prizeCount(0)
                        .prizeToday(0)
                        .percent(0)
                        .itemName(p.getName())
                        .awardType(p.getAwardType())
                        .sort(++maxSort)
                        .dailyNumber(p.getDailyNumber())
                        .totalNumber(p.getTotalNumber())
                        .id(p.getId()).build());
            }
        }
        return pools;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "activityDrawItem", allEntries = true)
    })
    public Boolean modifyDrawpoolSortTopBottom(ModifyDrawpoolSortTopBottomForm form) {
        MInMaxVO minMaxSort = baseMapper.getMinMaxSort(form.getId()).get(0);

        SortTypeEnum sortType = SortTypeEnum.getEnum(form.getSortType());

        return this.lambdaUpdate().eq(MemberDrawPool::getId, form.getId())
                .set(MemberDrawPool::getSort, sortType==SortTypeEnum.TOP ? minMaxSort.getMin()-1 : minMaxSort.getMax()+1)
                .update();
    }

    @Override
    public List<EnableDrawItemCountVO> findEnableDrawItemByTemplateId(List<Integer> templateIdList) {
        return this.baseMapper.findEnableDrawItemByTemplateId(templateIdList);
    }

    @Override
    public boolean checkDrawItemStatus(List<MemberDrawAwardItem> items) {
        for (MemberDrawAwardItem item: items){
            if(item.getEnable()==0)
                return false;
        }
        return true;
    }
}




