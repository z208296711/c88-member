package com.c88.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.amqp.AuthToken;
import com.c88.member.dto.MemberSessionCheckDTO;
import com.c88.member.pojo.form.FindMemberLoginRecordForm;
import com.c88.member.pojo.form.FindMemberLoginReportForm;
import com.c88.member.pojo.vo.AdminMemberLoginReportVO;
import com.c88.member.pojo.vo.MemberSessionVO;

/**
 * 會員登入log服务接口
 *
 * @author Allen
 */
public interface IMemberSessionService {

    MemberSessionCheckDTO getCheckList(String username, int type);

    void saveMemberSession(AuthToken token);

    IPage<AdminMemberLoginReportVO> findMemberLoginReport(FindMemberLoginReportForm form);

    IPage<MemberSessionVO> findMemberSession(FindMemberLoginRecordForm form);
}
