package com.c88.member.pojo.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(title = "修改存送優惠表單")
public class ModifyRechargeAwardTemplateForm {

    @NotNull(message = "ID不得為空")
    @Schema(title = "ID")
    private Long id;

    @Schema(title = "模型名稱")
    private String name;

    @Schema(title = "驗證手機要求", description = "0否1是")
    private Integer validMobile;

    @Schema(title = "拒絕關聯帳戶", description = "0否1是")
    private Integer validLink;

    @Schema(title = "需綁提款方式", description = "0否1是")
    private Integer validWithdraw;

    @Schema(title = "可用會員等級")
    private List<Integer> vipIds;

    @Schema(title = "限制會員標籤")
    private List<Integer> tagIds;

    @Schema(title = "限制支付類型")
    private List<Integer> rechargeTypeIds;

    @Schema(title = "限制註冊 開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerStartTime;

    @Schema(title = "限制註冊 結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerEndTime;

    @Schema(title = "活動開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(title = "活動結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(title = "關聯模式", description = "0無 1水平 2垂直")
    private Integer linkMode;

    @Schema(title = "關聯模式垂直排序")
    private Integer linkSort;

    @Schema(title = "啟用狀態", description = "0停用 1啟用")
    private Integer enable;

    @Schema(title = "排序")
    private Integer sort;
}
