package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import com.c88.common.mybatis.handler.ListIntegerTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 存送優惠模型
 *
 * @TableName member_recharge_award_template
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "member_recharge_award_template", autoResultMap = true)
public class MemberRechargeAwardTemplate extends BaseEntity {
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
     * 打碼倍數
     */
    @TableField(value = "bet_rate")
    private BigDecimal betRate;

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
     * 贈送金額
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 最低參與金額
     */
    @TableField(value = "min_join_amount")
    private BigDecimal minJoinAmount;

    /**
     * 贈送最高上限
     */
    @TableField(value = "max_award_amount")
    private BigDecimal maxAwardAmount;

    /**
     * 昨日充值要求
     */
    @TableField(value = "yesterday_recharge_amount")
    private BigDecimal yesterdayRechargeAmount;

    /**
     * 上週充值要求
     */
    @TableField(value = "last_week_recharge_amount")
    private BigDecimal lastWeekRechargeAmount;

    /**
     * 累積充值要求
     */
    @TableField(value = "total_recharge_amount")
    private BigDecimal totalRechargeAmount;

    /**
     * 累積充值要求 開始時間
     */
    @TableField(value = "total_recharge_start_time")
    private LocalDateTime totalRechargeStartTime;

    /**
     * 累積充值要求 結束時間
     */
    @TableField(value = "total_recharge_end_time")
    private LocalDateTime totalRechargeEndTime;

    /**
     * 每日次數限制
     */
    @TableField(value = "day_number")
    private Integer dayNumber;

    /**
     * 每週次數限制
     */
    @TableField(value = "week_number")
    private Integer weekNumber;

    /**
     * 每月次數限制
     */
    @TableField(value = "month_number")
    private Integer monthNumber;

    /**
     * 累積次數限制
     */
    @TableField(value = "total_number")
    private Integer totalNumber;

    /**
     * 驗證手機要求 0否1是
     */
    @TableField(value = "valid_mobile")
    private Integer validMobile;

    /**
     * 拒絕關聯帳戶 0否1是
     */
    @TableField(value = "valid_link")
    private Integer validLink;

    /**
     * 需綁提款方式 0否1是
     */
    @TableField(value = "valid_withdraw")
    private Integer validWithdraw;

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
     * 限制支付類型
     */
    @TableField(value = "recharge_type_ids", typeHandler = ListIntegerTypeHandler.class)
    private List<Integer> rechargeTypeIds;

    /**
     * 限制註冊 開始時間
     */
    @TableField(value = "register_start_time")
    private LocalDateTime registerStartTime;

    /**
     * 限制註冊 結束時間
     */
    @TableField(value = "register_end_time")
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
     * 關聯模式ID
     */
    @TableField(value = "link_mode_id")
    private Integer linkModeId;

    /**
     * 關聯模式 0無 1水平 2垂直
     */
    @TableField(value = "link_mode")
    private Integer linkMode;

    /**
     * 關聯模式垂直排序
     */
    @TableField(value = "link_sort")
    private Integer linkSort;

    /**
     * 啟用 0停用 1啟用
     */
    @TableField(value = "enable")
    private Integer enable;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

}