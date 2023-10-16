package com.c88.member.service;

import com.c88.member.pojo.entity.MemberVipConfig;
import com.c88.member.pojo.form.UpdateMemberVipForm;

import java.util.List;

/**
 *
 */
public interface IVipService {

    Boolean levelDown(List<Long> memberVipRecordIdList);

    Boolean levelKeep(List<Long> memberVipRecordIdList);

    boolean doManualVipLevelUpDownAction(UpdateMemberVipForm form);

    void doVipLevelUpAction(Long memberId);

    void doLevelDownAction();

    MemberVipConfig getLevelDownVipConfig(List<MemberVipConfig> vipConfigList, MemberVipConfig currentVipConfig);

    List<MemberVipConfig> getAllLevelDownVipConfig( MemberVipConfig currentMemberVipConfig);
}
