package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * VIP等級管理設定
 * @TableName member_vip_rule
 */
@TableName(value ="member_vip_rule")
@Data
public class MemberVipRule implements Serializable {
    /**
     * level
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 規則內容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 是否刪除 否:0,是:1
     */
    @TableField(value = "deleted")
    private Integer deleted;

    /**
     * 創建時間
     */
    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 更新時間
     */
    @TableField(value = "gmt_update")
    private LocalDateTime gmtUpdate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}