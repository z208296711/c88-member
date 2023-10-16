package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "禮金通知")
public class H5GiftNotificationVO {

    @Schema(title = "禮金名稱")
    private String gift;

    @Schema(title = "生日禮金", description = "0無領取項目 1可領取")
    private Integer notification;
}
