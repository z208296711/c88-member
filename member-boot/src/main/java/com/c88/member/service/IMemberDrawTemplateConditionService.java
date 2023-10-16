package com.c88.member.service;

import com.c88.member.pojo.entity.MemberDrawTemplateCondition;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface IMemberDrawTemplateConditionService extends IService<MemberDrawTemplateCondition> {

    List<MemberDrawTemplateCondition> findByTemplateId(Integer templateId);

}
