package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(title = "廣告")
public class MemberAdvertisementVO {

    @Schema(title = "ID")
    private Integer id;

    @Schema(title = "名稱")
    private String name;

    @Schema(title = "PC首圖")
    private String pcImage;

    @Schema(title = "H5首圖")
    private String h5Image;

    @Schema(title = "樣式ID")
    private Integer styleId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "開始時間")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "結束時間")
    private LocalDateTime endTime;

    @Schema(title = "啟用狀態", description = "0停用1啟用")
    private Integer enable;

    @Schema(title = "網址")
    private String url;

    @Schema(title = "鏈接類型", description = "1內站 2外站")
    private Integer linkInOut;

}
