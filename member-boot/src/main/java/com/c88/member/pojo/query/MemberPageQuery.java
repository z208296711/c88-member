package com.c88.member.pojo.query;

import com.c88.common.core.base.BasePageQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(title = "會員列表查詢參數")
public class MemberPageQuery extends BasePageQuery {

    @Schema(title = "時間類型", description = "1_登入時間, 2_註冊時間, 3_無時間限制")
    private Integer timeType;

    @Schema(title = "狀態", description = "1_啟用, 2_凍結")
    private Integer status;

    @Schema(title = "VIP-會員等級ID")
    private Integer vipId;

    @Schema(title = "會員標籤")
    private Integer tag;

    @Schema(title = "在線離線", description = "0_離線, 1_上線")
    private Integer online;

    @Schema(title = "會員帳號")
    private String userName;

    @Schema(title = "真實姓名")
    private String realName;

    @Schema(title = "代理線")
    private String parentUsername;

    @Schema(title = "推廣碼")
    private String inviteCode;

    @Schema(title = "郵箱前10碼")
    private String email;

    @Schema(title = "手機末5碼")
    private String mobile;

    @Schema(title = "銀行卡末5碼")
    private String bankAccount;

    @Schema(title = "開始時間(格式：yyyy-mm-ss hh:mm:ss)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @Schema(title = "結束時間(格式：yyyy-mm-ss hh:mm:ss)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 在線會員id集合，透過redis取得
     */
    @Schema(hidden = true)
    private Set<Long> onlineMemberIds;

    /**
     * 會員token生成後失效分鐘，由sys_oauth_client.access_token_validity（秒）來
     */
    @Schema(hidden = true)
    private Long tokenExpireMinutes = 60L;// 預設60分鐘

}
