package com.c88.member.mapper;

import com.c88.member.pojo.entity.MemberCoin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author mac
* @description 针对表【member_coin】的数据库操作Mapper
* @createDate 2022-09-30 11:36:17
* @Entity com.c88.member.pojo.entity.MemberCoin
*/
public interface MemberCoinMapper extends BaseMapper<MemberCoin> {
    void batchSaveOrUpdateCoin(List<MemberCoin> list);
}




