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
@Schema(title = "晉級通知")
public class H5VIPNotificationVO {

    @Schema(title = "levelUp",description = "是否升級 0:未升級 1:已升級")
    private Integer levelUp;

    @Schema(title = "notification", description = "0:尚未領取 1:查看權益 ")
    private Integer notification;

}
