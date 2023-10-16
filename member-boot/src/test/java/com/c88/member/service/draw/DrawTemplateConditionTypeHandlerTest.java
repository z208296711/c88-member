package com.c88.member.service.draw;

import com.c88.member.C88MemberApplication;
import com.c88.member.pojo.vo.MemberDrawVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = C88MemberApplication.class,properties = "spring.profiles.active:local")
class DrawTemplateConditionTypeHandlerTest {

    @Qualifier("freeDrawTemplateConditionTypeHandler")
    @Autowired
    private FreeDrawTemplateConditionTypeHandler drawTemplateConditionTypeHandler;

    @Test
    void doDraw(){
        List<MemberDrawVO> memberDrawVOS = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            MemberDrawVO memberDrawVO = drawTemplateConditionTypeHandler.doDraw(1l);
            memberDrawVOS.add(memberDrawVO);
        }
//        memberDrawVOS.stream().filter(m -> m.getAwardId() == 1).mapToLong(MemberDrawVO::getAwardId).forEach(System.out::println);
    }

}