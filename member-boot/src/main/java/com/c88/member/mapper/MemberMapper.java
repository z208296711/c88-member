package com.c88.member.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.query.MemberPageQuery;
import com.c88.member.pojo.vo.AdminMemberVO;
import com.c88.member.pojo.vo.MemberVO;
import com.c88.member.pojo.vo.MemberVipInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    @Select(" select user_name    userName," +
            "       real_name    realName," +
            "       mvc.name     vip," +
            "       m.mobile     mobile," +
            "       m.email      email," +
            "       m.birthday   birthday," +
            "       m.gmt_create gmtCreate" +
            " from member m" +
            "         left join member_vip mv on m.id = mv.member_id" +
            "         left join member_vip_config mvc on mv.current_vip_id = mvc.id" +
            " where m.id = #{memberId}")
    MemberVO findMember(Long memberId);


    List<AdminMemberVO> queryMember(@Param("queryParams") MemberPageQuery memberPageQuery);


    IPage<MemberVipInfoVO> findMembersVipInfo(IPage<MemberVipInfoVO> page, @Param(Constants.WRAPPER) QueryWrapper<MemberVipInfoVO> queryWrapper);
}
