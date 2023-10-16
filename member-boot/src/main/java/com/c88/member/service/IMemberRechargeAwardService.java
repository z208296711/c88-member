package com.c88.member.service;

import com.c88.member.pojo.vo.H5RechargeAwardCategoryVO;

import java.util.List;

public interface IMemberRechargeAwardService {
    List<H5RechargeAwardCategoryVO> findRechargeAward(Long memberId);
}
