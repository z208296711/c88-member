package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.c88.common.core.base.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName member_coin_record
 */
@TableName(value ="member_coin_record")
@Data
@Builder
@EqualsAndHashCode
public class MemberCoinRecord extends BaseEntity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 會員ID
     */
    @TableField(value = "member_id")
    private Long memberId;

    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private Integer beforeCoin;

    private Integer coin;

    /**
     * 
     */
    private Integer afterCoin;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}