package com.c88.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.entity.MemberSession;
import com.c88.member.pojo.form.FindMemberLoginReportForm;
import com.c88.member.pojo.vo.AdminMemberLoginReportVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface MemberSessionMapper extends BaseMapper<MemberSession> {

    List<Member> checkSessionListForLastLoginIp(@Param("username") String username, @Param("type") int type);

    List<Member> checkSessionList(@Param("username") String username, @Param("type") int type);

    List<Member> checkSimilarAccount(@Param("username") String username);

    List<Member> checkBankCard(@Param("username") String username);

    @MapKey("date")
    Map<LocalDate, AdminMemberLoginReportVO> findMemberLoginTotalReport(@Param("form") FindMemberLoginReportForm form);

    @MapKey("date")
    Map<LocalDate, AdminMemberLoginReportVO> findMemberLoginDistinctReport(@Param("form") FindMemberLoginReportForm form);

}
