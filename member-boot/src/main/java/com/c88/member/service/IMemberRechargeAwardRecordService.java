package com.c88.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.entity.MemberRechargeAwardRecord;
import com.c88.member.pojo.form.FindPersonalRechargeAwardRecordForm;
import com.c88.member.pojo.form.FindRechargeAwardRecordForm;
import com.c88.member.vo.AllMemberPersonalRechargeAwardRecordByTemplateIdVO;
import com.c88.member.pojo.vo.MemberPersonalRechargeAwardRecordVO;
import com.c88.member.pojo.vo.MemberRechargeAwardRecordVO;

import java.util.List;

/**
 * @author user
 * @description 针对表【member_recharge_award_record(存送優惠領取紀錄)】的数据库操作Service
 * @createDate 2022-10-25 14:09:22
 */
public interface IMemberRechargeAwardRecordService extends IService<MemberRechargeAwardRecord> {

    IPage<MemberRechargeAwardRecordVO> findRechargeAwardRecord(FindRechargeAwardRecordForm form);

    IPage<MemberPersonalRechargeAwardRecordVO> findPersonalRechargeAwardRecord(FindPersonalRechargeAwardRecordForm form);

    Boolean cancelPersonalRechargeAwardRecord(Long id);

    List<AllMemberPersonalRechargeAwardRecordByTemplateIdVO> findAllPersonalRechargeAwardRecordByTemplateId(Long templateId, Long memberId);
}
