package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MemberSession {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("login_ip")
    private String loginIp;

    @TableField("device")
    private String device;

    @Builder.Default
    private String uuid="";

    @TableField("login_time")
    private LocalDateTime loginTime;

    @TableField("login_domain")
    private String loginDomain;

    @TableField("area")
    private String area;

}
