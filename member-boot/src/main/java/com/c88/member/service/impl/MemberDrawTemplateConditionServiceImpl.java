package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.pojo.entity.MemberDrawTemplateCondition;
import com.c88.member.service.IMemberDrawTemplateConditionService;
import com.c88.member.mapper.MemberDrawTemplateConditionMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class MemberDrawTemplateConditionServiceImpl extends ServiceImpl<MemberDrawTemplateConditionMapper, MemberDrawTemplateCondition> implements IMemberDrawTemplateConditionService {

    @Override
    @Cacheable(cacheNames = "draw", key = "'drawtemplateCondition:'+#templateId")
    public List<MemberDrawTemplateCondition> findByTemplateId(Integer templateId) {
        return this.lambdaQuery()
                .eq(MemberDrawTemplateCondition::getTemplateId, templateId)
                .orderByAsc(MemberDrawTemplateCondition::getType)
                .list();
    }
}




