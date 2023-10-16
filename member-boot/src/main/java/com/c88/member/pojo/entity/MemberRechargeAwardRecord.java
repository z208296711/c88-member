package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 存送優惠領取紀錄
 *
 * @TableName member_recharge_award_record
 */
@TableName(value = "member_recharge_award_record")
@Data
public class MemberRechargeAwardRecord extends BaseEntity {
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
     * 存送優惠模組ID
     */
    @TableField(value = "template_id")
    private Long templateId;

    /**
     * 優惠名稱
     */
    @TableField(value = "name")
    private String name;

    /**
     * 存送類型 1平台 2個人
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 存送模式 1比例 2固定
     */
    @TableField(value = "mode")
    private Integer mode;

    /**
     * 優惠比例
     */
    @TableField(value = "rate")
    private BigDecimal rate;

    /**
     * 打碼倍數
     */
    @TableField(value = "bet_rate")
    private BigDecimal betRate;

    /**
     * 贈送金額
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 充值金額
     */
    @TableField(value = "recharge_amount")
    private BigDecimal rechargeAmount;

    /**
     * 來源
     */
    @TableField(value = "source")
    private String source;

    /**
     * 連結模式ID
     */
    @TableField(value = "link_mode_id")
    private Integer linkModeId;

    /**
     * 使用時間
     */
    @TableField(value = "use_time")
    private LocalDateTime useTime;

    /**
     * 取消時間
     */
    @TableField(value = "cancel_time")
    private LocalDateTime cancelTime;

    /**
     * 狀態 0無 1已使用 2未使用 3已取消
     */
    @TableField(value = "state")
    private Integer state;

}