package com.c88.member.pojo.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Schema(title = "修改廣告表單")
public class ModifyAdvertisementForm {

    @NotNull(message = "廣告ID不得為空")
    @Schema(title = "廣告ID")
    private String id;

    @Schema(title = "活動名稱")
    private String name;

    @Schema(title = "PC圖片網址")
    private String pcImage;

    @Schema(title = "H5圖片網址")
    private String h5Image;

    @Schema(title = "樣式ID")
    private Integer styleId;

    @Schema(title = "顯示開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(title = "顯數結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(title = "廣告鏈接網址")
    private String url;

    @Schema(title = "啟用狀態", description = "0停用 1啟用")
    private Integer enable;

    @Schema(title = "鏈接類型", description = "1內站 2外站")
    private Integer linkInOut;

}
