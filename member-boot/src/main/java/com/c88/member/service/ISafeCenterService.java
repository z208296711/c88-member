package com.c88.member.service;

import com.c88.member.pojo.vo.MemberDataCheckVO;

public interface ISafeCenterService {
    MemberDataCheckVO memberDataCheck(Long memberId);
}
