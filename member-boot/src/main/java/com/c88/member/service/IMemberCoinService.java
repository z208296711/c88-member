package com.c88.member.service;

import com.c88.member.pojo.entity.MemberCoin;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author mac
 * @description 针对表【member_coin】的数据库操作Service
 * @createDate 2022-09-30 11:36:17
 */
public interface IMemberCoinService extends IService<MemberCoin> {

    public MemberCoin getMemberByUserName(String username);

    Boolean deductCoin(Long memberId, Integer amount);

    Boolean addCoin(Long memberId, Integer amount);


    void batchSaveOrUpdateCoin(List<MemberCoin> list);
}
