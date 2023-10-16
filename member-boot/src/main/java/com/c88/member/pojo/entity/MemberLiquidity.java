package com.c88.member.pojo.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.math.BigDecimal;

import com.c88.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * (member_liquidity)实体类
 *
 * @author Allen
 * @since 2022-06-02 15:38:32
 * @description 由 Mybatisplus Code Generator 创建
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("member_liquidity")
public class MemberLiquidity extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 會員ID
     */
    @TableField(value = "member_id")
    private Long memberId;

    /**
     * 會員帳號
     */
    @TableField(value = "username")
	private String username;

    /**
     * 累計充值
     */
    @TableField(value = "recharge_amount")
    private BigDecimal rechargeAmount;

    /**
     * 累計充值資數
     */
    @TableField(value = "recharge_count")
    private Integer rechargeCount;

    /**
     * 累計提取
     */
    @TableField(value = "withdraw_amount")
    private BigDecimal withdrawAmount;

    /**
     * 累計提取次數
     */
    @TableField(value = "withdraw_count")
    private Integer withdrawCount;

    /**
     * 累計優惠
     */
    @ApiModelProperty("累計優惠")
    @TableField(value = "promotion_amount")
    private BigDecimal promotionAmount;

    /**
     * 累計優惠次數
     */
    @TableField(value = "promotion_count")
    private Integer promotionCount;

    /**
     * 累計紅利
     */
    @TableField(value = "bonus_amount")
    private BigDecimal bonusAmount;

    /**
     * 累計紅利次數
     */
    @TableField(value = "bonus_count")
    private Integer bonusCount;

    /**
     * 總流水
     */
    @TableField(value = "bet_amount")
    private BigDecimal betAmount;

}