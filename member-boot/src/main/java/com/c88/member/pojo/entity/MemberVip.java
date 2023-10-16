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
 * VIP會員表
 *
 * @TableName member_vip
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "member_vip")
public class MemberVip extends BaseEntity {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 會員ID
     */
    @TableField(value = "member_id")
    private Long memberId;

    /**
     * 用戶帳號
     */
    @TableField(value = "username")
    private String username;

    /**
     * 當前 vip等級 Id
     */
    @TableField(value = "current_vip_id")
    private Integer currentVipId;

    /**
     * 當前 vip等級 名稱
     */
    @TableField(value = "current_vip_name")
    private String currentVipName;


    /**
     * 當前 vip等級 名稱
     */
    @TableField(value = "current_vip_valid_bet")
    private BigDecimal currentVipValidBet;

    /**
     * 上次 vip等級
     */
    @TableField(value = "previous_vip_id")
    private Integer previousVipId;

    /**
     * 上次 vip等級 名稱
     */
    @TableField(value = "previous_vip_name")
    private String previousVipName;

    /**
     * 最近一次升級時間
     */
    @TableField(value = "level_up_time")
    private LocalDateTime levelUpTime;

    /**
     * 最近一次降級時間
     */
    @TableField(value = "level_down_time")
    private LocalDateTime levelDownTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}