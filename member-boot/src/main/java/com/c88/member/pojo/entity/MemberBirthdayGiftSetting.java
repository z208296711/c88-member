package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 生日禮金設定
 *
 * @TableName member_birthday_gift_setting
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "member_birthday_gift_setting")
public class MemberBirthdayGiftSetting implements Serializable {
    /**
     * 是否需要寄站內信
     */
    @TableField(value = "inbox")
    private Integer inbox;

    /**
     * 領取效期
     */
    @TableField(value = "days")
    private Integer days;

    /**
     * 打碼倍數
     */
    @TableField(value = "bet_rate")
    private BigDecimal betRate;

}