package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import com.c88.member.common.enums.RedEnvelopeCodeStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 白菜紅包代碼
 *
 * @TableName member_red_envelope_code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "member_red_envelope_code")
public class MemberRedEnvelopeCode extends BaseEntity {
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
     * 白菜紅包ID
     */
    @TableField(value = "template_id")
    private Long templateId;

    /**
     * 代碼
     */
    @TableField(value = "code")
    private String code;

    /**
     * 狀態
     *
     * @see RedEnvelopeCodeStateEnum
     */
    @TableField(value = "state")
    private Integer state;

}