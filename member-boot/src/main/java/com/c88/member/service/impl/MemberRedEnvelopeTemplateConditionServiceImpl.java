package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.mapper.MemberRedEnvelopeTemplateConditionMapper;
import com.c88.member.pojo.entity.MemberRedEnvelopeTemplateCondition;
import com.c88.member.service.IMemberRedEnvelopeTemplateConditionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author user
 * @description 针对表【member_red_envelope_template_condition(白菜紅包領取條件)】的数据库操作Service实现
 * @createDate 2022-10-03 11:18:12
 */
@Service
public class MemberRedEnvelopeTemplateConditionServiceImpl extends ServiceImpl<MemberRedEnvelopeTemplateConditionMapper, MemberRedEnvelopeTemplateCondition>
        implements IMemberRedEnvelopeTemplateConditionService {

    @Override
    public List<MemberRedEnvelopeTemplateCondition> getByTemplateId(Long templateId) {
        return this.lambdaQuery()
                .eq(MemberRedEnvelopeTemplateCondition::getTemplateId, templateId)
                .list();
    }
}




