package com.c88.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.member.pojo.entity.MemberRechargeAwardTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.MemberRechargeAwardTemplateDrawOptionVO;
import com.c88.member.pojo.vo.MemberRechargeAwardTemplateVO;
import com.c88.member.vo.MemberRechargeAwardTemplateClientVO;
import com.c88.member.pojo.vo.RechargeAwardTemplateLinkModeVO;
import com.c88.member.vo.OptionVO;

import java.util.List;
import java.util.Map;

/**
 * @author user
 * @description 针对表【member_recharge_award_template(存送優惠模型)】的数据库操作Service
 * @createDate 2022-10-25 14:09:22
 */
public interface IMemberRechargeAwardTemplateService extends IService<MemberRechargeAwardTemplate> {

    IPage<MemberRechargeAwardTemplateVO> findRechargeAwardTemplate(FindRechargeAwardTemplateForm form);

    Boolean addRechargeAwardTemplate(AddRechargeAwardTemplateForm form);

    Boolean modifyRechargeAwardTemplate(ModifyRechargeAwardTemplateForm form);

    Boolean deleteRechargeAwardTemplate(List<Long> ids);

    Boolean modifyAdvertisementSort(Map<Long, Integer> map);

    Boolean modifyAdvertisementSortTopBottom(ModifyRechargeAwardTemplateSortTopBottomForm form);

    Boolean modifyRechargeAwardTemplateLinkMode(ModifyRechargeAwardTemplateLinkModeForm form);

    List<RechargeAwardTemplateLinkModeVO> findRechargeAwardTemplateLinkMode(Integer id);

    MemberRechargeAwardTemplateClientVO findRechargeAwardTemplate(Long id);

    Boolean deleteRechargeAwardTemplateLinkMode(Integer id);

    List<OptionVO<Long>> findRechargeAwardTemplateOption();

    List<MemberRechargeAwardTemplateDrawOptionVO> findRechargeAwardTemplateDrawOption(Integer enable);

}
