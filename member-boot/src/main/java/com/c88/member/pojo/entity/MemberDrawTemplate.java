package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 輪盤模組
 *
 * @TableName member_draw_template
 */
@TableName(value = "member_draw_template")
@Data
public class MemberDrawTemplate extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 模組名稱
     */
    @TableField(value = "name")
    private String name;

    /**
     * 抽獎類型
     */
    @TableField(value = "draw_type")
    private Integer drawType;

    /**
     * 昨日充值要求
     */
    @TableField(value = "yesterday_recharge_amount")
    private BigDecimal yesterdayRechargeAmount;

    /**
     * 昨日流水要求
     */
    @TableField(value = "yesterday_bet_amount")
    private BigDecimal yesterdayBetAmount;

    /**
     * 免費抽獎時間間隔
     */
    @TableField(value = "free_interval")
    private Integer freeInterval;

    /**
     * 每日抽獎次數限制 -1=無限
     */
    @TableField(value = "daily_gift_number")
    private Integer dailyGiftNumber;

    /**
     * 重置時間
     */
    @TableField(value = "reset_time")
    private LocalTime resetTime;

    /**
     * 開始時間
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 結束時間
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * 啟用 0停用 1啟用
     */
    @TableField(value = "enable")
    private Integer enable;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}