package com.c88.member.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.amqp.AuthToken;
import com.c88.member.dto.AddBalanceAndWithdrawLimitDTO;
import com.c88.member.dto.MemberInfoDTO;
import com.c88.payment.dto.AddBalanceDTO;
import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.entity.MemberVip;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.query.MemberPageQuery;
import com.c88.member.pojo.vo.AdminMemberVO;
import com.c88.member.pojo.vo.MemberVO;
import com.c88.member.pojo.vo.MemberVipInfoVO;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface IMemberService extends IService<Member> {

    Member getMemberByUserName(String userName);

    Member getMemberByMobile(String mobile);

    Member getMemberByEmail(String email);

    Boolean checkMember(String userName);

    Boolean lockDown(List<Long> memberIdList);

//    Member saveMember(MemberForm memberForm, String ip, String domain);

    Member registerMember(HttpServletRequest request, MemberForm memberForm);

    String sendSmsCode(String mobile) throws Exception;

    String sendEmailCode(String email) throws Exception;

    Boolean verifySms(String mobile, String uuid, String code);

    Boolean verifyEmail(String email, String uuid, String code);

    Boolean forgetPassword(MemberForm memberForm);

    void increaseErrorPasswordCount(AuthToken token);

    void resetErrorPasswordCount(AuthToken token);

    Boolean addWithdrawPassword(String username, WithdrawPasswordAddForm form);

    Boolean modifyWithdrawPassword(String username, WithdrawPasswordModifyForm form);

    Boolean modifyMemberInfo(String username, MemberInfoModifyForm form);

    Boolean modifyPassword(String username, PasswordModifyForm form);

    Member findMemberByUsername(String username);

    boolean freezeUsers(List<String> usernames);

    boolean freezeUser(String username);

    Boolean modifyMemberRealName(Long id, String realName);

    MemberVO findMember(Long memberId);

    Page<AdminMemberVO> queryMember(MemberPageQuery memberPageQuery);

    AdminMemberVO getDetail(Long id);

    MemberVip getMemberVip(Long memberId);

    MemberInfoDTO findMemberInfoByUsername(String userName);

    MemberInfoDTO findMemberInfoById(Long id);

    IPage<MemberVipInfoVO> findMembersVipInfo(IPage<MemberVipInfoVO> page, QueryWrapper<MemberVipInfoVO> queryWrapper);

    boolean updateWithdrawLimit(Long id, BigDecimal value);

    // Boolean addBalanceAndWithdrawLimit(AddBalanceAndWithdrawLimitDTO value);

    Set<Integer> getMemberTagIds(Long memberId);

    IPage<AdminMemberVO> findMembers(MemberPageQuery memberPageQuery);

    List<MemberInfoDTO> findMemberInfoByUsernames(List<String> usernames);

    Integer getRegisterCount(String startTime, String endtime);

    Boolean modifyMemberWithdrawPassword(ModifyMemberWithdrawPasswordForm form);

    Boolean modifyMemberWithdrawControllerState(ModifyMemberWithdrawControllerStateForm form);

}
