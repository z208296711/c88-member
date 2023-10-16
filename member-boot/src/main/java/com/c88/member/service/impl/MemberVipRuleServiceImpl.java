package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.mapper.MemberVipRuleMapper;
import com.c88.member.mapstruct.MemberVipRuleConverter;
import com.c88.member.pojo.entity.MemberVipRule;
import com.c88.member.pojo.vo.MemberVipRuleVO;
import com.c88.member.service.IMemberVipRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class MemberVipRuleServiceImpl extends ServiceImpl<MemberVipRuleMapper, MemberVipRule> implements IMemberVipRuleService {

    private final MemberVipRuleConverter memberVipRuleConverter;

    @Override
    public MemberVipRuleVO getVipRule() {
        MemberVipRule memberVipRule = this.lambdaQuery().one();
        return memberVipRuleConverter.toVo(memberVipRule);
    }

    @Override
    public Boolean modifyMemberVipRule(MemberVipRuleVO memberVipRuleVO) {
        MemberVipRule memberVipRule = memberVipRuleConverter.toEntity(memberVipRuleVO);
        return this.updateById(memberVipRule);
    }
    
}




