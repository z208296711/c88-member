package com.c88.member.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuthUserDTO {

    /**
     * 會員ID
     */
    private Long id;

    /**
     * 會員帳號
     */
    private String userName;

    /**
     * 真實姓名
     */
    private String realName;

    /**
     * 會員密码
     */
    private String password;

    /**
     * 會員狀態(1:正常;0:禁用)
     */
    private Integer status;

    /**
     * 用户角色编码集合 ["ROOT","ADMIN"]
     */
    private List<String> roles;

    /**
     * 部门ID
     */
    private Long deptId;

}
