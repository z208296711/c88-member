package com.c88.member.job;

import com.c88.member.service.IDrawService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class DrawTemplateResetJob {

    private final IDrawService iDrawService;

    @XxlJob("resetDrawTemplate")
    public void run() {

        log.info("resetDrawTemplate");
        //業務邏輯
        iDrawService.cleanMemberDrawInfo();

    }



}
