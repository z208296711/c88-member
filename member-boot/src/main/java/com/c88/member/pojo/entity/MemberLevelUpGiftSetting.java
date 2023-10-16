package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @TableName member_level_up_gift_setting
 */
@TableName(value = "member_level_up_gift_setting")
@Data
@Schema(title = "MemberLevelUpGiftSetting")
public class MemberLevelUpGiftSetting implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 打碼倍數
     */
    @TableField(value = "bet_rate")
    @Schema(title = "打碼倍數")
    private BigDecimal betRate = BigDecimal.ZERO;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}