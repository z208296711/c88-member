package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(title = "前台優惠活動")
public class H5MemberPromotionVO {

    @Schema(title = "標題")
    private String title;

    @Schema(title = "活動時間文字")
    private String timeText;

    @Schema(title = "活動對象文字")
    private String targetText;

    @Schema(title = "申請方式文字")
    private String applyText;

    @Schema(title = "參與遊戲平台文字")
    private String platformText;

    @Schema(title = "活動詳情")
    private String detail;

    @Schema(title = "活動規則")
    private String rule;

    @Schema(title = "外部連結")
    private String url;

    @Schema(title = "PC圖片")
    private String pcImage;

    @Schema(title = "H5圖片")
    private String h5Image;
}
