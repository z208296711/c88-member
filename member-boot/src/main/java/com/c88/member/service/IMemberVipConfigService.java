package com.c88.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.common.core.base.BasePageQuery;
import com.c88.member.pojo.entity.MemberVipConfig;
import com.c88.member.pojo.form.AddMemberVipForm;
import com.c88.member.pojo.form.DelMemberVipConfigForm;
import com.c88.member.pojo.form.ModifyMemberVipForm;
import com.c88.member.pojo.vo.MemberVipConfigVO;
import com.c88.member.vo.OptionVO;

import java.util.List;
import java.util.Map;

public interface IMemberVipConfigService extends IService<MemberVipConfig> {

    IPage<MemberVipConfigVO> findMemberVipConfigPage(BasePageQuery pageQuery);

    List<MemberVipConfig> findAutoLevelUpVipConfigs();

    Map<Integer, String> findVipConfigMap();

    List<MemberVipConfig> findAllVipConfig();

    List<OptionVO<Integer>> findMemberVipConfigOption();

    Boolean addMemberVipConfig(AddMemberVipForm form);

    Boolean modifyMemberVipConfig(ModifyMemberVipForm form);

    Boolean delMemberVipConfig(DelMemberVipConfigForm form);

    String findMemberVipName(Long memberId);

    Map<Long,String> findMembersVipNameMap(List<Long> memberIds);
}
