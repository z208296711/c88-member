package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @TableName member_free_bet_gift_setting
 */
@TableName(value ="member_free_bet_gift_setting")
@Data
public class MemberFreeBetGiftSetting implements Serializable {
    /**
     * 類型: 1:週, 2:月
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 是否啟用
     */
    @TableField(value = "enable")
    private Integer enable;

    /**
     * 領取效期
     */
    @TableField(value = "value")
    private Integer value;

    /**
     * 充值金額
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 打碼倍數
     */
    @TableField(value = "bet_rate")
    private BigDecimal betRate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}