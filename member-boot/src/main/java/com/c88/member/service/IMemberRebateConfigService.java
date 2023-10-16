package com.c88.member.service;

import com.c88.member.pojo.entity.MemberRebateConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.form.UpdateMemberRebateForm;

import java.util.List;
import java.util.Map;

/**
* @author user
* @description 针对表【member_rebate_config】的数据库操作Service
* @createDate 2023-03-06 16:01:17
*/
public interface IMemberRebateConfigService extends IService<MemberRebateConfig> {

    List<MemberRebateConfig> getOrUpdateMemberRebate();
    Map<String,Object> getMemberRebate();

    Boolean updateMemberRebate(List<UpdateMemberRebateForm> forms);

}
