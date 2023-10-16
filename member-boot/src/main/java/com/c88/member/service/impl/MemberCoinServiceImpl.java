package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.mapper.MemberCoinMapper;
import com.c88.member.pojo.entity.MemberCoin;
import com.c88.member.service.IMemberCoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mac
 * @description 针对表【member_coin】的数据库操作Service实现
 * @createDate 2022-09-30 11:36:17
 */
@Service
@RequiredArgsConstructor
public class MemberCoinServiceImpl extends ServiceImpl<MemberCoinMapper, MemberCoin>
        implements IMemberCoinService {

    private final MemberCoinMapper memberCoinMapper;

    @Override
    public MemberCoin getMemberByUserName(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<MemberCoin>().eq(MemberCoin::getUsername, username));
    }

    @Override
    public Boolean deductCoin(Long memberId, Integer amount) {
        MemberCoin memberCoin = this.lambdaQuery()
                .eq(MemberCoin::getMemberId, memberId)
                .oneOpt().orElseThrow(() -> new BizException("無金幣資訊"));
        if (memberCoin.getCoin() - amount < 0) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        memberCoin.setCoin(memberCoin.getCoin() - amount);
        return this.updateById(memberCoin);
    }

    @Override
    public Boolean addCoin(Long memberId, Integer amount) {
        MemberCoin memberCoin = this.lambdaQuery()
                .eq(MemberCoin::getMemberId, memberId)
                .oneOpt().orElseThrow(() -> new BizException("無金幣資訊"));
        memberCoin.setCoin(memberCoin.getCoin() + amount);
        return this.updateById(memberCoin);
    }

    @Override
    public void batchSaveOrUpdateCoin(List<MemberCoin> list) {
        memberCoinMapper.batchSaveOrUpdateCoin(list);
    }
}




