package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 輪盤抽獎條件
 * @TableName member_draw_template_condition
 */
@TableName(value ="member_draw_template_condition")
@Data
public class MemberDrawTemplateCondition extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 輪盤模組ID
     */
    @TableField(value = "template_id")
    private Integer templateId;

    /**
     * 項目階層
     */
    @TableField(value = "level")
    private Integer level;

    /**
     * 項目
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 條件
     */
    @TableField(value = "condition_num")
    private Integer conditionNum;

    /**
     * 上限次數
     */
    @TableField(value = "max_num")
    private Integer maxNum;


}