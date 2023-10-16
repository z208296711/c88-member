package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import com.c88.member.common.enums.LevelUpModeEnum;
import com.c88.member.common.enums.LevelUpReviewEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

import java.math.BigDecimal;

/**
 * VIP等級管理設定
 *
 * @TableName member_vip_config
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "member_vip_config", autoResultMap = true)
public class MemberVipConfig extends BaseEntity {
    /**
     * level
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名稱
     */
    @TableField(value = "name")
    private String name;

    /**
     * 等級圖標
     */
    @TableField(value = "logo")
    private String logo;

    /**
     * 升級模式 0:自動 1:手動
     */
    @TableField(value = "level_up_mode", typeHandler = EnumOrdinalTypeHandler.class, javaType = true)
    private LevelUpModeEnum levelUpMode;

    /**
     * 因自動提升類型之會員等級顯示在前
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 降級審核 0:不審 1:要審
     */
    @TableField(value = "level_up_review", typeHandler = EnumOrdinalTypeHandler.class, javaType = true)
    private LevelUpReviewEnum levelUpReview;

    /**
     * 升級條件-經驗值
     */
    @TableField(value = "level_up_exp")
    private BigDecimal levelUpExp;

    /**
     * 升級條件-充值金額
     */
    @TableField(value = "level_up_recharge")
    private BigDecimal levelUpRecharge;

    /**
     * 維持條件-經驗值
     */
    @TableField(value = "keep_exp")
    private BigDecimal keepExp;

    /**
     * 維持條件-充值金額
     */
    @TableField(value = "keep_recharge")
    private Integer keepRecharge;

    /**
     * 晉級禮金
     */
    @TableField(value = "level_up_gift")
    private Integer levelUpGift;

    /**
     * 生日禮金
     */
    @TableField(value = "birthday_gift")
    private Integer birthdayGift;

    /**
     * 救援金比例
     */
    @TableField(value = "emergency_rate")
    private BigDecimal emergencyRate;

    /**
     * 金幣兌換折扣
     */
    @TableField(value = "coin_exchange_discount")
    private BigDecimal coinExchangeDiscount;

    /**
     * 單日提款金額上限
     */
    @TableField(value = "daily_withdraw_amount")
    private Integer dailyWithdrawAmount;

    /**
     * 每日反水領取上限
     */
    @TableField(value = "daily_backwater_limit")
    private BigDecimal dailyBackwaterLimit;

    /**
     * 升級條件備註-手動升級用
     */
    @TableField(value = "level_up_note")
    private String levelUpNote;

    /**
     * 維持條件備註-手動升級用
     */
    @TableField(value = "keep_level_note")
    private String keepLevelNote;

    /**
     * 每週免費籌碼
     */
    @TableField(value = "weekly_free_bet")
    private BigDecimal weeklyFreeBet;

    /**
     * 每月免費籌碼
     */
    @TableField(value = "monthly_free_bet")
    private BigDecimal monthlyFreeBet;

    /**
     * 是否為 初始值VIP0 (不得刪除)
     */
    @TableField(value = "vip0")
    private Integer vip0;

    /**
     * 是否刪除 否:0,是:1
     */
    @TableField(value = "deleted")
    private Integer deleted;

}