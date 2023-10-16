package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * (member_association_rate)实体类
 *
 * @author Allen
 * @description 由 Mybatisplus Code Generator 创建
 * @since 2022-06-05 23:45:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("member_association_rate")
public class MemberAssociationRate extends Model<MemberAssociationRate> implements Serializable {
    @ApiModelProperty("用戶名")
    private String username;
    /**
     * threshold
     */
    @ApiModelProperty("門檻")
    private BigDecimal threshold;
    /**
     * regIp
     */
    @ApiModelProperty("註冊ip")
    private BigDecimal regIp;
    /**
     * loginIp
     */
    @ApiModelProperty("登入ip")
    private BigDecimal loginIp;
    /**
     * withdrawIp
     */
    @ApiModelProperty("提款ip")
    private BigDecimal withdrawIp;

    @ApiModelProperty("遊戲ip")
    private BigDecimal gameIp;
    /**
     * uuid
     */
    @ApiModelProperty("電腦標識")
    private BigDecimal uuid;
    /**
     * realName
     */
    @ApiModelProperty("真實姓名")
    private BigDecimal realName;
    /**
     * account
     */
    @ApiModelProperty("相似帳號")
    private BigDecimal account;
}