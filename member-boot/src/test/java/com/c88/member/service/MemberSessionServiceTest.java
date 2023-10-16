package com.c88.member.service;

import com.c88.member.C88MemberApplication;
import com.c88.member.service.impl.MemberServiceImpl;
import com.c88.member.service.impl.MemberSessionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = C88MemberApplication.class,properties = "spring.profiles.active:local")
class MemberSessionServiceTest {

    @Autowired
    MemberSessionServiceImpl memberSessionService;

    @Autowired
    MemberServiceImpl memberService;

    @Autowired
    IVipService iVipService;

    @Test
    void doVipLevelUpAction() {
        iVipService.doVipLevelUpAction(117L);
    }


    @Test
    void getCheckList() {
//        System.out.println("123");
        memberSessionService.getCheckList("allen01",1); //登入IP
//        memberSessionService.getCheckList("kimi",3); //真實姓名
//        memberSessionService.getCheckList("kimi",9); //相似帳號
//        memberSessionService.getCheckList("kimi",6); //銀行卡號
//        memberSessionService.getCheckList("kimi",7); //最後提款ip
//        memberSessionService.getCheckList("kimi",8); //最後遊戲ip
    }
}