package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import com.c88.member.common.enums.RedEnvelopeRecordStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 白菜紅包領取紀錄
 *
 * @TableName member_red_envelope_record
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "member_red_envelope_record")
public class MemberRedEnvelopeRecord extends BaseEntity {
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
     * 活動類型
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 活動名稱
     */
    @TableField(value = "name")
    private String name;

    /**
     * 白菜金額
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 流水倍數
     */
    @TableField(value = "bet_rate")
    private BigDecimal betRate;

    /**
     * 紅包代碼
     */
    @TableField(value = "code")
    private String code;

    /**
     * 模組ID
     */
    @TableField(value = "template_id")
    private Long templateId;

    /**
     * 階層
     */
    @TableField(value = "level")
    private Integer level;

    /**
     * 輪數
     */
    @TableField(value = "cycle")
    private Integer cycle;

    /**
     * 狀態 1待審核 2已通過 3已拒絕 4已回收
     * @see RedEnvelopeRecordStateEnum
     */
    @TableField(value = "state")
    private Integer state;

    /**
     * 審核時間
     */
    @TableField(value = "review_time")
    private LocalDateTime reviewTime;

}