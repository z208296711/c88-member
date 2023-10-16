package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.c88.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 輪盤領取紀錄
 *
 * @TableName member_draw_record
 */
@TableName(value = "member_draw_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDrawRecord extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 會員Id
     */
    @TableField(value = "member_id")
    private Long memberId;

    /**
     * 模型ID
     */
    @TableField(value = "template_id")
    private Integer templateId;

    /**
     * 模型條件ID
     */
    @TableField(value = "template_condition_id")
    private Integer templateConditionId;

    /**
     * 項目階層
     */
    @TableField(value = "username")
    private String username;

    /**
     * 抽獎類型
     */
    @TableField(value = "draw_type")
    private Integer drawType;

    /**
     * 獎項ID
     */
    @TableField(value = "award_id")
    private Long awardId;

    /**
     * 獎項名稱(結果)
     */
    @TableField(value = "award_name")
    private String awardName;

    /**
     * 獎項類型
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
     * 審核時間
     */
    @TableField(value = "approve_time")
    private LocalDateTime approveTime;

    /**
     * 審核人
     */
    @TableField(value = "approve_by")
    private String approveBy;

    /**
     * 0: 待申請：當用戶尚未填寫「收件資料」時, 1: 待審核, 2: 已拒絕, 3: 已領取
     */
    @TableField(value = "state")
    private Integer state;


    @TableField(value = "version")
    @Version
    private Integer version;

    @TableField(value = "recipient")
    private String recipient;

    /**
     * 聯絡電話
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 收件地址
     */
    @TableField(value = "address")
    private String address;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}