package com.c88.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.entity.MemberVipRule;
import com.c88.member.pojo.vo.MemberVipRuleVO;


public interface IMemberVipRuleService extends IService<MemberVipRule> {

    MemberVipRuleVO getVipRule();

    Boolean modifyMemberVipRule(MemberVipRuleVO memberVipRuleVO);
}
