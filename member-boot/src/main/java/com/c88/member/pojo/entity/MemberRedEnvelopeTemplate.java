package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.c88.common.core.base.BaseEntity;
import com.c88.common.core.vo.AssignPlatformGameVO;
import com.c88.common.mybatis.handler.AssignPlatformGameTypeHandler;
import com.c88.common.mybatis.handler.ListIntegerTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 白菜紅包模型
 *
 * @TableName member_red_envelope_template
 */
@TableName(value = "member_red_envelope_template", autoResultMap = true)
@Data
public class MemberRedEnvelopeTemplate extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模型名稱
     */
    @TableField(value = "name")
    private String name;

    /**
     * 白菜活動類型
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 打碼倍數
     */
    @TableField(value = "bet_rate")
    private BigDecimal betRate;

    /**
     * 遊戲平台類型ID 空表示全平台
     */
    @TableField(value = "assign_platform_game", typeHandler = AssignPlatformGameTypeHandler.class)
    private List<AssignPlatformGameVO> assignPlatformGame;

    /**
     * 紅包單日庫存總數
     */
    @TableField(value = "daily_red_envelope_total")
    private Integer dailyRedEnvelopeTotal;

    /**
     * 紅包累積庫存總數
     */
    @TableField(value = "red_envelope_total")
    private Integer redEnvelopeTotal;

    /**
     * 每人每日領取上限
     */
    @TableField(value = "daily_max_total")
    private Integer dailyMaxTotal;

    /**
     * 每人累計領取上限
     */
    @TableField(value = "max_total")
    private Integer maxTotal;

    /**
     * 昨日充值要求
     */
    @TableField(value = "yesterday_recharge_amount")
    private BigDecimal yesterdayRechargeAmount;

    /**
     * 當日充值要求
     */
    @TableField(value = "day_recharge_amount")
    private BigDecimal dayRechargeAmount;

    /**
     * 累積充值要求
     */
    @TableField(value = "total_recharge_amount")
    private BigDecimal totalRechargeAmount;

    /**
     * 累積充值要求 開始時間
     */
    @TableField(value = "total_recharge_start_time", updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime totalRechargeStartTime;

    /**
     * 累積充值要求 結束時間
     */
    @TableField(value = "total_recharge_end_time", updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime totalRechargeEndTime;

    /**
     * 驗證手機要求 0否1是
     */
    @TableField(value = "validated_mobile")
    private Integer validatedMobile;

    /**
     * 拒絕關聯帳戶 0否1是
     */
    @TableField(value = "validated_link")
    private Integer validatedLink;

    /**
     * 需綁提款方式 0否1是
     */
    @TableField(value = "validated_withdraw")
    private Integer validatedWithdraw;

    /**
     * 可用會員等級
     */
    @TableField(value = "vip_ids", typeHandler = ListIntegerTypeHandler.class)
    private List<Integer> vipIds;

    /**
     * 限制會員標籤
     */
    @TableField(value = "tag_ids", typeHandler = ListIntegerTypeHandler.class)
    private List<Integer> tagIds;

    /**
     * 限制註冊 開始時間
     */
    @TableField(value = "register_start_time", updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime registerStartTime;

    /**
     * 限制註冊 結束時間
     */
    @TableField(value = "register_end_time", updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime registerEndTime;

    /**
     * 活動開始時間
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 活動結束時間
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * 活動紅包金額
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 活動代碼個數
     */
    @TableField(value = "number")
    private Integer number;

    /**
     * 審核 0不需審核 1需審核
     */
    @TableField(value = "review")
    private Integer review;

    /**
     * 啟用 0停用 1啟用
     */
    @TableField(value = "enable")
    private Integer enable;

}