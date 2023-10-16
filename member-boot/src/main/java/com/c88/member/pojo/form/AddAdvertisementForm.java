package com.c88.member.pojo.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Schema(title = "新增廣告表單")
public class AddAdvertisementForm {

    @NotNull(message = "活動名稱不得為空")
    @Schema(title = "活動名稱")
    private String name;

    @NotNull(message = "PC圖片網址不得為空")
    @Schema(title = "PC圖片網址")
    private String pcImage;

    @NotNull(message = "H5圖片網址不得為空")
    @Schema(title = "H5圖片網址")
    private String h5Image;

    @NotNull(message = "樣式ID不得為空")
    @Schema(title = "樣式ID")
    private Integer styleId;

    @NotNull(message = "顯示開始時間不得為空")
    @Schema(title = "顯示開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "顯示結束時間不得為空")
    @Schema(title = "顯示結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "廣告鏈接網址不得為空")
    @Schema(title = "廣告鏈接網址")
    private String url;

    @NotNull(message = "鏈接類型不得為空")
    @Schema(title = "鏈接類型", description = "1內站 2外站")
    private Integer linkInOut;

}
