package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 會員和標籤關聯表
 * @TableName member_tag
 */
@TableName(value ="member_tag")
@Data
public class MemberTag implements Serializable {
    /**
     * 會員ID
     */
    @TableField(value = "member_id")
    private Long memberId;

    /**
     * 標籤ID
     */
    @TableField(value = "tag_id")
    private Integer tagId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}