package com.c88.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.entity.MemberDrawTemplate;

/**
 *
 */
public interface IMemberDrawTemplateService extends IService<MemberDrawTemplate> {

    MemberDrawTemplate findActivityDrawTemplate();

}
