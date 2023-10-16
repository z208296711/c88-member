package com.c88.member.service;

import com.c88.member.pojo.entity.MemberRedEnvelopeTemplateCondition;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author user
* @description 针对表【member_red_envelope_template_condition(白菜紅包領取條件)】的数据库操作Service
* @createDate 2022-10-03 11:18:12
*/
public interface IMemberRedEnvelopeTemplateConditionService extends IService<MemberRedEnvelopeTemplateCondition> {

    List<MemberRedEnvelopeTemplateCondition> getByTemplateId(Long templateId);
}
