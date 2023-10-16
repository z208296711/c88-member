package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @TableName member_free_bet_gift_record
 */
@TableName(value = "member_free_bet_gift_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberFreeBetGiftRecord extends BaseEntity implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 會員id
     */
    @TableField(value = "member_id")
    private Long memberId;

    /**
     * 會員帳號
     */
    @TableField(value = "username")
    private String username;

    /**
     * VIP等級ID
     */
    @TableField(value = "vip_id")
    private Integer vipId;

    /**
     * VIP等級名稱
     */
    @TableField(value = "vip_name")
    private String vipName;

    /**
     * 獎勵金額
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 領取週期: 1:每週, 2:每月
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 打碼倍率
     */
    @TableField(value = "bet_rate")
    private Integer betRate;

    /**
     * 流水要求
     */
    @TableField(value = "exp")
    private Integer exp;

    /**
     * 充值條件
     */
    @TableField(value = "recharge_condition")
    private Integer rechargeCondition;

}