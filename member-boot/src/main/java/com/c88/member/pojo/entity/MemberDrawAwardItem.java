package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 輪盤獎勵模組
 * @TableName member_draw_award_item
 */
@TableName(value ="member_draw_award_item")
@Data
public class MemberDrawAwardItem extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 獎項名稱
     */
    @TableField(value = "name")
    private String name;

    /**
     * 可用模組 0:一般抽獎
     */
    @TableField(value = "draw_type")
    private Integer drawType;

    /**
     * 獎項類型 0:紅利, 1:金幣, 2:實體, 3:存送
     */
    @TableField(value = "award_type")
    private Integer awardType;

    /**
     * 獎勵金額
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 流水倍數
     */
    @TableField(value = "bet_rate")
    private BigDecimal betRate;

    /**
     * 優惠模型ID
     */
    @TableField(value = "promotion_id")
    private Integer promotionId;

    /**
     * 每日限量 -1=無限
     */
    @TableField(value = "daily_number")
    private Integer dailyNumber;

    /**
     * 全部限量 -1=無限
     */
    @TableField(value = "total_number")
    private Integer totalNumber;

    /**
     * 項目圖片
     */
    @TableField(value = "image")
    private String image;

    /**
     * 商品描述
     */
    @TableField(value = "note")
    private String note;

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


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 抽中的紀錄
     */
    @TableField(exist = false)
    private MemberDrawRecord memberDrawRecord;
}