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

import java.math.BigDecimal;

/**
 * 白菜紅包領取條件
 *
 * @TableName member_red_envelope_template_condition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "member_red_envelope_template_condition")
public class MemberRedEnvelopeTemplateCondition extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 白菜紅包模型ID
     */
    @TableField(value = "template_id")
    private Long templateId;

    /**
     * 項目階層
     */
    @TableField(value = "level")
    private Integer level;

    /**
     * 最大階層
     */
    @TableField(value = "max_level")
    private Integer maxLevel;

    /**
     * 充值要求
     */
    @TableField(value = "recharge_amount")
    private BigDecimal rechargeAmount;

    /**
     * 有效投注
     */
    @TableField(value = "valid_bet_amount")
    private BigDecimal validBetAmount;

    /**
     * 總充值要求
     */
    @TableField(value = "recharge_amount_total")
    private BigDecimal rechargeAmountTotal;

    /**
     * 總有效投注
     */
    @TableField(value = "valid_bet_amount_total")
    private BigDecimal validBetAmountTotal;

    /**
     * 紅包金額
     */
    @TableField(value = "amount")
    private BigDecimal amount;

}