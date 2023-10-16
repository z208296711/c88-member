package com.c88.member.service.impl;

import com.c88.member.C88MemberApplication;
import com.c88.member.pojo.form.ModifyDrawpoolSortTopBottomForm;
import com.c88.member.pojo.vo.MemberDrawVO;
import com.c88.member.service.IMemberDrawPoolService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest(classes = C88MemberApplication.class,properties = "spring.profiles.active:local")
public class MemberDrawPoolServiceImplTest {
    @Autowired
    private IMemberDrawPoolService memberDrawPoolService;

    @Test
    void list(){
        List<MemberDrawVO> list = memberDrawPoolService.list(8l);
        list.stream().forEach(System.out::println);
    }

    @Test
    void modifyDrawpoolSortTopBottom(){
        ModifyDrawpoolSortTopBottomForm form = new ModifyDrawpoolSortTopBottomForm();
        form.setSortType(1);
        form.setId(3l);

        memberDrawPoolService.modifyDrawpoolSortTopBottom(form);

    }
}