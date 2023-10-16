package com.c88.member.service.impl;

import com.c88.common.core.result.Result;
import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.vo.MemberDataCheckVO;
import com.c88.member.service.ISafeCenterService;
import com.c88.payment.client.MemberBankClient;
import com.c88.payment.client.MemberCryptoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class SafeCenterImplService implements ISafeCenterService {

    private final MemberServiceImpl memberService;

    private final MemberBankClient memberBankClient;

    private final MemberCryptoClient memberCryptoClient;

    public MemberDataCheckVO memberDataCheck(Long memberId) {
        Member member = memberService.lambdaQuery()
                .eq(Member::getId, memberId)
                .oneOpt()
                .orElseThrow(() -> new RuntimeException("無此帳號"));

        // 檢查提款方式,檢查銀行卡如果沒有在檢查提幣地址(其中一項有回傳1否則0
        int withdrawModeValid = 0;
        Result<Boolean> memberBankResult = memberBankClient.checkMemberBankExist();
        if (Result.isSuccess(memberBankResult)) {
            withdrawModeValid = memberBankResult.getData() == Boolean.TRUE ? 1 : 0;
        }
        if (withdrawModeValid == 0) {
            Result<Boolean> memberVirtualCurrencyResult = memberCryptoClient.checkMemberCryptoExist();
            if (Result.isSuccess(memberVirtualCurrencyResult)) {
                withdrawModeValid = memberVirtualCurrencyResult.getData() == Boolean.TRUE ? 1 : 0;
            }
        }

        return MemberDataCheckVO.builder()
                .phoneValid(member.getMobile())
                .emailValid(member.getEmail())
                .withdrawModeValid(withdrawModeValid)
                .passwordValid(StringUtils.isNotBlank(member.getPassword()) ? 1 : 0)
                .withdrawPasswordValid(StringUtils.isNotBlank(member.getWithdrawPassword()) ? 1 : 0)
                .isReceivePhoneValidAward(checkoutPhoneValidAward(memberId))
                .isReceiveEmailValidAward(checkoutEmailValidAward(memberId))
                .build();
    }

    private int checkoutPhoneValidAward(Long memberId) {
        Random r = new Random();

        //todo 檢查手機有無領取獎勵

        return r.nextBoolean() ? 1 : 0;
    }

    private int checkoutEmailValidAward(Long memberId) {
        Random r = new Random();

        //todo 檢查信箱有無領取獎勵

        return r.nextBoolean() ? 1 : 0;
    }

}
