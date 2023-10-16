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

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 優惠活動
 *
 * @TableName member_promotion
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "member_promotion")
public class MemberPromotion extends BaseEntity implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 標題
     */
    @TableField(value = "title")
    private String title;

    /**
     * 分類ID
     */
    @TableField(value = "category_id")
    private Integer categoryId;

    /**
     * 活動時間文字
     */
    @TableField(value = "time_text")
    private String timeText;

    /**
     * 開始顯示時間
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 結束顯示時間
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * 活動類型 1站內活動 2外部活動
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 活動對象文字
     */
    @TableField(value = "target_text")
    private String targetText;

    /**
     * 申請方式文字
     */
    @TableField(value = "apply_text")
    private String applyText;

    /**
     * 參與遊戲平台文字
     */
    @TableField(value = "platform_text")
    private String platformText;

    /**
     * 活動詳情
     */
    @TableField(value = "detail")
    private String detail;

    /**
     * 活動規則
     */
    @TableField(value = "rule")
    private String rule;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 啟用狀態 0停用 1啟用
     */
    @TableField(value = "enable")
    private Integer enable;

    /**
     * 外部連結
     */
    @TableField(value = "url")
    private String url;

    /**
     * PC圖片
     */
    @TableField(value = "pc_image")
    private String pcImage;

    /**
     * H5圖片
     */
    @TableField(value = "h5_image")
    private String h5Image;

}