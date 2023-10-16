package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.core.enums.EnableEnum;
import com.c88.member.pojo.entity.MemberDrawTemplate;
import com.c88.member.service.IMemberDrawTemplateService;
import com.c88.member.mapper.MemberDrawTemplateMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 *
 */
@Service
public class MemberDrawTemplateServiceImpl extends ServiceImpl<MemberDrawTemplateMapper, MemberDrawTemplate>
        implements IMemberDrawTemplateService {

    @Override
    @Cacheable(cacheNames = "activityDrawTemplate", unless = "#result == null")
    public MemberDrawTemplate findActivityDrawTemplate() {
        return this.lambdaQuery()
                .le(MemberDrawTemplate::getStartTime, LocalDateTime.now())
                .ge(MemberDrawTemplate::getEndTime, LocalDateTime.now())
                .eq(MemberDrawTemplate::getEnable, EnableEnum.START.getCode())
                .oneOpt()
                .orElse(null);
    }
}




