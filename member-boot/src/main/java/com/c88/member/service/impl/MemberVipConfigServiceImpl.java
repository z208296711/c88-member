package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.core.base.BasePageQuery;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.game.adapter.api.GameFeignClient;
import com.c88.game.adapter.dto.GameCategoryVO;
import com.c88.member.common.enums.LevelUpModeEnum;
import com.c88.member.dto.MemberVipConfigDTO;
import com.c88.member.mapper.MemberVipConfigMapper;
import com.c88.member.mapstruct.MemberVipConfigConverter;
import com.c88.member.pojo.entity.MemberRebateConfig;
import com.c88.member.pojo.entity.MemberVip;
import com.c88.member.pojo.entity.MemberVipConfig;
import com.c88.member.pojo.form.AddMemberVipForm;
import com.c88.member.pojo.form.DelMemberVipConfigForm;
import com.c88.member.pojo.form.ModifyMemberVipForm;
import com.c88.member.pojo.vo.MemberVipConfigVO;
import com.c88.member.service.IMemberRebateConfigService;
import com.c88.member.service.IMemberVipConfigService;
import com.c88.member.service.IMemberVipService;
import com.c88.member.vo.OptionVO;
import com.c88.payment.client.MemberChannelClient;
import com.c88.payment.client.MerchantPayClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MemberVipConfigServiceImpl extends ServiceImpl<MemberVipConfigMapper, MemberVipConfig> implements IMemberVipConfigService {

    public static final int VIP_LIMIT = 10;

    private final MemberVipConfigConverter memberVipConfigConverter;

    private final IMemberVipService iMemberVipService;

    private final MemberChannelClient memberChannelClient;

    private final MerchantPayClient merchantPayClient;

    private final GameFeignClient gameFeignClient;

    private final IMemberRebateConfigService memberRebateConfigService;

    @Override
    public IPage<MemberVipConfigVO> findMemberVipConfigPage(BasePageQuery pageQuery) {
        IPage<MemberVipConfig> memberVipConfigPage = this.lambdaQuery()
                .orderByDesc(MemberVipConfig::getVip0)
                .orderByAsc(MemberVipConfig::getLevelUpMode)
                .orderByAsc(MemberVipConfig::getLevelUpRecharge)
                .orderByAsc(MemberVipConfig::getSort)
                .page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()));
        return memberVipConfigPage.convert(memberVipConfigConverter::toVo);
    }

    /**
     * 取得充值金額 由大到小的vip 配置清單
     *
     * @return
     */
    @Override
    @Cacheable(cacheNames = "member", key = "'vipConfigs'")
    public List<MemberVipConfig> findAutoLevelUpVipConfigs() {
        return this.lambdaQuery()
                .eq(MemberVipConfig::getLevelUpMode, LevelUpModeEnum.AUTO.getValue())
                .orderByDesc(MemberVipConfig::getLevelUpRecharge)
                .list()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, String> findVipConfigMap() {
        return this.lambdaQuery()
                .select(MemberVipConfig::getId, MemberVipConfig::getName)
                .list()
                .stream()
                .map(memberVipConfigConverter::toMemberVipDTO)
                .collect(Collectors.toMap(MemberVipConfigDTO::getId, MemberVipConfigDTO::getName));
    }

    @Override
    public List<MemberVipConfig> findAllVipConfig() {
        return this.lambdaQuery()
                .orderByDesc(MemberVipConfig::getVip0)
                .orderByAsc(MemberVipConfig::getLevelUpMode)
                .orderByAsc(MemberVipConfig::getLevelUpRecharge)
                .orderByAsc(MemberVipConfig::getSort)
                .list();
    }

    public List<OptionVO<Integer>> findMemberVipConfigOption() {
        return this.lambdaQuery()
                .orderByDesc(MemberVipConfig::getVip0)
                .orderByAsc(MemberVipConfig::getLevelUpMode)
                .orderByAsc(MemberVipConfig::getLevelUpRecharge)
                .orderByAsc(MemberVipConfig::getSort)
                .select(MemberVipConfig::getId, MemberVipConfig::getName)
                .list()
                .stream()
                .map(x -> new OptionVO<>(x.getId(), x.getName())).collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "member", key = "'vipConfigs'"),
            @CacheEvict(cacheNames = "rebate", allEntries = true)
    })
    public Boolean addMemberVipConfig(AddMemberVipForm form) {
        if (this.count() >= VIP_LIMIT) {
            throw new BizException(ResultCode.VIP_SIZE_LIMIT);
        }
        MemberVipConfig memberVipConfig = memberVipConfigConverter.toEntity(form);
        boolean isSuccess = this.save(memberVipConfig);
        addMemberRebate(memberVipConfig);
        return isSuccess;
    }

    private void addMemberRebate(MemberVipConfig memberVipConfig) {
        List<MemberRebateConfig> insetLists = new ArrayList<>();
        List<GameCategoryVO> gameCategoryVOSs = gameFeignClient.findGameCategory().getData();
        for (GameCategoryVO g : gameCategoryVOSs) {
            MemberRebateConfig memberRebateConfig = new MemberRebateConfig();
            memberRebateConfig.setCategoryId(g.getId());
            memberRebateConfig.setGmtCreate(LocalDateTime.now());
            memberRebateConfig.setGmtModified(LocalDateTime.now());
            memberRebateConfig.setRebate(0d);
            memberRebateConfig.setVipId(memberVipConfig.getId());
            insetLists.add(memberRebateConfig);
        }
        memberRebateConfigService.saveBatch(insetLists);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "member", key = "'vipConfigs'")
    })
    public Boolean modifyMemberVipConfig(ModifyMemberVipForm form) {
        MemberVipConfig memberVipConfig = memberVipConfigConverter.toEntity(form);
        return this.updateById(memberVipConfig);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "member", key = "'vipConfigs'"),
            @CacheEvict(cacheNames = "rebate", allEntries = true)
    })
    @Transactional
    public Boolean delMemberVipConfig(DelMemberVipConfigForm form) {
        // 檢查會員通道使用的VIP
        checkMemberChannelUseStatus(form);

        // 檢查代付管理使用的VIP
        checkMerchantPayUseStatus(form);

        MemberVipConfig memberVipConfig = this.getById(form.getTargetVipId());

        iMemberVipService.lambdaUpdate()
                .set(MemberVip::getCurrentVipName, memberVipConfig.getName())
                .set(MemberVip::getCurrentVipId, memberVipConfig.getId())
                .eq(MemberVip::getCurrentVipId, form.getSourceVipId())
                .update();

        memberRebateConfigService.removeByIds(memberRebateConfigService.lambdaQuery().eq(MemberRebateConfig::getVipId, form.getSourceVipId()).list().stream().map(MemberRebateConfig::getId).collect(Collectors.toList()));
        return this.removeById(form.getSourceVipId());
    }

    @Override
    public String findMemberVipName(Long memberId) {
        List<MemberVipConfig> vipConfigs = this.findAllVipConfig();
        MemberVip memberVip = iMemberVipService.getById(memberId);
        return vipConfigs.stream()
                .filter(filter -> Objects.equals(filter.getId(), memberVip.getCurrentVipId()))
                .findFirst()
                .map(MemberVipConfig::getName)
                .orElse(vipConfigs.stream()
                        .filter(filter -> filter.getVip0() == 1)
                        .findFirst()
                        .map(MemberVipConfig::getName)
                        .orElse("")
                );
    }

    @Override
    public Map<Long, String> findMembersVipNameMap(List<Long> memberIds) {
        Map<Integer, String> vipConfigMap = this.findAllVipConfig()
                .stream()
                .collect(Collectors.toMap(MemberVipConfig::getId, MemberVipConfig::getName));

        return iMemberVipService.lambdaQuery()
                .in(MemberVip::getMemberId, memberIds)
                .list()
                .stream()
                .collect(Collectors.toMap(MemberVip::getMemberId, v -> vipConfigMap.getOrDefault(v.getCurrentVipId(), "")));
    }

    /**
     * 檢查代付管理使用的VIP
     *
     * @param form
     */
    private void checkMerchantPayUseStatus(DelMemberVipConfigForm form) {
        Result<Set<Integer>> merchantPayUseVipIds = merchantPayClient.findMerchantPayUseVipIds();
        if (!Result.isSuccess(merchantPayUseVipIds)) {
            throw new BizException(ResultCode.INTERNAL_SERVICE_CALLEE_ERROR);
        }

        // 檢查此vip是否在會員通道已使用
        Set<Integer> vipIds = merchantPayUseVipIds.getData();
        if (vipIds.contains(form.getSourceVipId())) {
            throw new BizException("vip.deletetips05");
        }
    }

    /**
     * 檢查會員通道使用的VIP
     *
     * @param form
     */
    private void checkMemberChannelUseStatus(DelMemberVipConfigForm form) {
        // 取的目前會員通道使用中的vip
        Result<Set<Long>> memberChannelUseVipIds = memberChannelClient.findMemberChannelUseVipIds();
        if (!Result.isSuccess(memberChannelUseVipIds)) {
            throw new BizException(ResultCode.INTERNAL_SERVICE_CALLEE_ERROR);
        }

        // 檢查此vip是否在會員通道已使用
        Set<Long> vipIds = memberChannelUseVipIds.getData();
        if (vipIds.contains(form.getSourceVipId().longValue())) {
            throw new BizException("vip.deletetips04");
        }
    }

}




