package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.c88.common.core.base.BaseEntity;
import lombok.Builder;
import lombok.Data;

/**
 * @TableName member_coin
 */
@TableName(value = "member_coin")
@Data
@Builder
public class MemberCoin extends BaseEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
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
     * 金幣
     */
    @TableField(value = "coin")
    private Integer coin;

    @TableField(value = "version")
    @Version
    private Integer version;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}