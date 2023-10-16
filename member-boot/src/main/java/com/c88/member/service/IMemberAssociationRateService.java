package com.c88.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.entity.MemberAssociationRate;

import java.math.BigDecimal;

/**
 * 服务接口
 *
 * @author Allen
 * @description 由 Mybatisplus Code Generator 创建
 * @since 2022-06-05 23:45:46
 */
public interface IMemberAssociationRateService extends IService<MemberAssociationRate> {

    MemberAssociationRate getAssociation();

    MemberAssociationRate getAssociation(String username);

    int updateSystemAssociation(MemberAssociationRate rate);

    void updateUserAssociation(String username, Integer rateType, BigDecimal value);

    BigDecimal getAssociateRateByType(String username, int type);

    Boolean checkUsernameValue(String username);
}
