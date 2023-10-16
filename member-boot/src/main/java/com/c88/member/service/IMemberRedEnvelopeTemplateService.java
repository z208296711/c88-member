package com.c88.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.entity.MemberRedEnvelopeTemplate;
import com.c88.member.pojo.form.AddRedEnvelopeTemplateForm;
import com.c88.member.pojo.form.FindRedEnvelopeTemplateForm;
import com.c88.member.pojo.form.ModifyRedEnvelopeTemplateForm;
import com.c88.member.pojo.vo.MemberRedEnvelopeTemplateVO;

import java.util.List;

/**
 * @author user
 * @description 针对表【member_red_envelope_template(白菜紅包模型)】的数据库操作Service
 * @createDate 2022-10-03 11:18:12
 */
public interface IMemberRedEnvelopeTemplateService extends IService<MemberRedEnvelopeTemplate> {

    IPage<MemberRedEnvelopeTemplateVO> findRedEnvelopeTemplate(FindRedEnvelopeTemplateForm form);

    Boolean addRedEnvelopeTemplate(AddRedEnvelopeTemplateForm form);

    Boolean modifyRedEnvelopeTemplate(ModifyRedEnvelopeTemplateForm form);

    Boolean deleteRedEnvelopeTemplate(List<Integer> ids);

    MemberRedEnvelopeTemplate findById(Long templateId);
}
