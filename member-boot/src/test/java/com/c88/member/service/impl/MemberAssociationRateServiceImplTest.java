package com.c88.member.service.impl;

import com.c88.member.C88MemberApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;


@SpringBootTest(classes = C88MemberApplication.class)
@ContextConfiguration
@TestPropertySource("classpath:application.yml")
@ActiveProfiles({"local"})
class MemberAssociationRateServiceImplTest {

    @Autowired
    MemberAssociationRateServiceImpl service;

    @Test
    void getAssociateRateByType(){
        service.getAssociateRateByType("allen01",1);
//        System.out.println("123");
    }
}