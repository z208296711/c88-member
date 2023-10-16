package com.c88.member.service;

import com.c88.member.pojo.form.ReceiveChineseCabbageForm;
import com.c88.member.pojo.vo.H5ChineseCabbageStateVO;

import java.time.LocalDate;
import java.util.List;

public interface IMemberRedEnvelopeService {
    List<H5ChineseCabbageStateVO> findChineseCabbageState(Long memberId, String username);

    Boolean receiveChineseCabbage(Long memberId, String username, ReceiveChineseCabbageForm form);

    Boolean receiveCode(Long memberId, String code, String s);

    Integer getLevel(Long memberId, Long templateId);

    Integer getCycle(Long memberId, Long templateId);

    Integer getDailyRedEnvelopeNumber(LocalDate nowDate, Long templateId);

    String getDailyRedEnvelopeNumberKey(LocalDate nowDate, Long templateId);

    Integer getRedEnvelopNumber(Long templateId);

    String getRedEnvelopeNumberKey(Long templateId);

    Integer getMemberDailyRedEnvelopNumber(LocalDate nowDate, Long templateId, Long memberId);

    String getMemberDailyRedEnvelopNumberKey(LocalDate nowDate, Long templateId, Long memberId);

    Integer getMemberRedEnvelopRecordNumber(Long templateId, Long memberId);

    String getMemberRedEnvelopNumberKey(Long templateId, Long memberId);

    String getLevelKey(Long memberId, Long templateId);

    String getCycleKey(Long memberId, Long templateId);
}
