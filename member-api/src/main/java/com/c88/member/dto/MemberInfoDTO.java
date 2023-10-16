package com.c88.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "會員資訊")
public class MemberInfoDTO {

    @Schema(title = "ID")
    private Long id;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "會員真實名")
    private String realName;

    @Schema(title = "會員手機號")
    private String mobile;

    @Schema(title = "會員當前等級ID")
    private Integer currentVipId;

    @Schema(title = "狀態：1（啟用），2（凍結）")
    private Byte status;

    @Schema(title = "會員當前等級名稱")
    private String currentVipName;

    @Schema(title = "會員註冊IP")
    private String registerIp;

    @Schema(title = "會員註冊時間")
    private LocalDateTime registerTime;

    @Schema(title = "最後登入時間")
    private LocalDateTime lastLoginTime;

    @Schema(title = "會員標籤")
    private List<Integer> tagIdList;

    @Schema(title = "提款控制", description = "0關閉提款 1開啟提款")
    private Integer withdrawControllerState;

    @Schema(title = "VIP當日充值金額上限")
    private BigDecimal dailyWithdrawAmount;

    @Schema(title = "提款密碼")
    private String withdrawPassword;

}
