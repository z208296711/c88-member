package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.game.adapter.api.GameFeignClient;
import com.c88.game.adapter.dto.GameCategoryVO;
import com.c88.member.mapstruct.MemberRebateConverter;
import com.c88.member.pojo.entity.MemberRebateConfig;
import com.c88.member.pojo.entity.MemberVipConfig;
import com.c88.member.pojo.form.UpdateMemberRebateForm;
import com.c88.member.service.IMemberRebateConfigService;
import com.c88.member.mapper.MemberRebateConfigMapper;
import com.c88.member.service.IMemberVipConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author terry
 * @description 针对表【member_rebate_config】的数据库操作Service实现
 * @createDate 2023-03-06 16:01:17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRebateConfigServiceImpl extends ServiceImpl<MemberRebateConfigMapper, MemberRebateConfig>
        implements IMemberRebateConfigService {
    private final IMemberVipConfigService memberVipConfigService;

    private final GameFeignClient gameFeignClient;

    private final MemberRebateConverter memberRebateConverter;
    @Cacheable(cacheNames = "rebate")
    @Override
    public List<MemberRebateConfig> getOrUpdateMemberRebate() {
        List<MemberRebateConfig> memberRebateConfigs = this.lambdaQuery().orderByAsc(MemberRebateConfig::getId).list();
        if (CollectionUtils.isEmpty(memberRebateConfigs)) {
            List<MemberRebateConfig> insetLists = new ArrayList<>();
            List<MemberVipConfig> vips = memberVipConfigService.lambdaQuery()
                    .orderByDesc(MemberVipConfig::getVip0)
                    .orderByAsc(MemberVipConfig::getLevelUpMode)
                    .orderByAsc(MemberVipConfig::getLevelUpRecharge)
                    .orderByAsc(MemberVipConfig::getSort).list();
            List<GameCategoryVO> gameCategoryVOSs = gameFeignClient.findGameCategory().getData();
            for (MemberVipConfig v : vips) {
                for (GameCategoryVO g : gameCategoryVOSs) {
                    MemberRebateConfig memberRebateConfig = new MemberRebateConfig();
                    memberRebateConfig.setCategoryId(g.getId());
                    memberRebateConfig.setGmtCreate(LocalDateTime.now());
                    memberRebateConfig.setGmtModified(LocalDateTime.now());
                    memberRebateConfig.setRebate(0d);
                    memberRebateConfig.setVipId(v.getId());
                    insetLists.add(memberRebateConfig);
                }
            }
            this.saveBatch(insetLists);
            return insetLists;
        }
        return memberRebateConfigs;
    }

    @Override
    public Map<String, Object> getMemberRebate() {
        List<Map<String, Object>> lists = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        List<MemberVipConfig> vips = memberVipConfigService.lambdaQuery()
                .orderByDesc(MemberVipConfig::getVip0)
                .orderByAsc(MemberVipConfig::getLevelUpMode)
                .orderByAsc(MemberVipConfig::getLevelUpRecharge)
                .orderByAsc(MemberVipConfig::getSort).list();
        List<String> headers = vips.stream().map(MemberVipConfig::getName).collect(Collectors.toList());
        List<GameCategoryVO> gameCategoryVOSs = gameFeignClient.findGameCategory().getData();
        List<MemberRebateConfig> memberRebates = getOrUpdateMemberRebate();
        Map<Integer, List<MemberRebateConfig>> rebateGroupByCategory = memberRebates.stream().collect(Collectors.groupingBy(MemberRebateConfig::getCategoryId));
        for (GameCategoryVO gameCategory : gameCategoryVOSs) {
            if (rebateGroupByCategory.containsKey(gameCategory.getId())) {
                Map<String, Object> res = new HashMap<>();
                res.put("categoryName", gameCategory.getName());
                res.put("vips", rebateGroupByCategory.get(gameCategory.getId()));
                lists.add(res);
            }
        }
        response.put("header", headers);
        response.put("data", lists);
        return response;
    }
    @Caching(evict = {
            @CacheEvict(cacheNames = "rebate", allEntries = true)
    })
    @Override
    public Boolean updateMemberRebate(List<UpdateMemberRebateForm> forms) {
        this.saveOrUpdateBatch(forms.stream().map(memberRebateConverter::toEntity).collect(Collectors.toList()));
        return null;
    }

}




