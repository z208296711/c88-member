package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.mapper.MemberLiquidityMapper;
import com.c88.member.pojo.entity.MemberLiquidity;
import com.c88.member.service.IMemberLiquidityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 服务接口实现
 *
 * @author Allen
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MemberLiquidityServiceImpl extends ServiceImpl<MemberLiquidityMapper, MemberLiquidity> implements IMemberLiquidityService {
}