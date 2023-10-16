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
import java.time.LocalDateTime;

/**
 * VIP等級管理設定
 */
@TableName(value ="member_vip_level_record")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberVipLevelRecord extends BaseEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 會員-帳號
     */
    @TableField(value = "username")
    private String username;

    /**
     * 會員-id
     */
    @TableField(value = "member_id")
    private Long memberId;

    /**
     * 類型 0:降級 1:升級 2:手動
     */
    @TableField(value = "type")
    private Integer type;


    /**
     * 原本VIP ID
     */
    @TableField(value = "origin_vip_id")
    private Integer originVipId;
    /**
     * 原等級
     */
    @TableField(value = "origin_level")
    private String originLevel;

    /**
     * 原充值金額
     */
    @TableField(value = "origin_recharge")
    private BigDecimal originRecharge;

    /**
     * 原經驗值
     */
    @TableField(value = "origin_exp")
    private BigDecimal originExp;

    /**
     * 原vip-維持條件-充值
     */
    @TableField(value = "keep_recharge")
    private BigDecimal keepRecharge;

    /**
     * 原vip-維持條件-流水
     */
    @TableField(value = "keep_exp")
    private BigDecimal keepExp;

    /**
     * 目標 VIP ID
     */
    @TableField(value = "target_vip_id")
    private Integer targetVipId;

    /**
     * 目標等級
     */
    @TableField(value = "target_level")
    private String targetLevel;

    /**
     * 目標充值金額
     */
    @TableField(value = "target_recharge")
    private BigDecimal targetRecharge;

    /**
     * 目標經驗值
     */
    @TableField(value = "target_exp")
    private BigDecimal targetExp;

    /**
     * 累積充值
     */
    @TableField(value = "exp")
    private BigDecimal exp;

    /**
     * 累積經驗值
     */
    @TableField(value = "recharge")
    private BigDecimal recharge;

    /**
     * 審核時間
     */
    @TableField(value = "approve_time")
    private LocalDateTime approveTime;

    /**
     * 升/降級 時間
     */
    @TableField(value = "level_change_time")
    private LocalDateTime levelChangeTime;


    /**
     *
     */
    @TableField(value = "level_up_mode")
    private Integer levelUpMode;

    /**
     * 審核時間
     */
    @TableField(value = "approve_by")
    private String approveBy;

    /**
     * 狀態 0:待審核 1:已保級 2:已降級 3:已升級
     */
    @TableField(value = "state")
    private Integer state;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}