package com.c88.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.entity.MemberRedEnvelopeRecord;
import com.c88.member.pojo.form.ApproveRedEnvelopeRecordForm;
import com.c88.member.pojo.form.FindRedEnvelopeRecordForm;
import com.c88.member.pojo.form.RejectRedEnvelopeRecordForm;
import com.c88.member.pojo.vo.MemberRedEnvelopeRecordVO;

/**
 * @author user
 * @description 针对表【member_red_envelope_record(白菜紅包領取紀錄)】的数据库操作Service
 * @createDate 2022-10-03 11:18:12
 */
public interface IMemberRedEnvelopeRecordService extends IService<MemberRedEnvelopeRecord> {

    IPage<MemberRedEnvelopeRecordVO> findRedEnvelopeRecord(FindRedEnvelopeRecordForm form);

}
