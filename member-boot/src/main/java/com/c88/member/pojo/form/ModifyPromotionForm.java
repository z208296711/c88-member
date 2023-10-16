package com.c88.member.pojo.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Schema(title = "修改優惠表單")
public class ModifyPromotionForm {

    @NotNull(message = "ID不得為空")
    @Schema(title = "ID")
    private Integer id;

    @Schema(title = "標題")
    private String title;

    @Schema(title = "分類ID")
    private Integer categoryId;

    @Schema(title = "活動時間文字")
    private String timeText;

    @Schema(title = "開始顯示時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(title = "結束顯示時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(title = "活動類型", description = "1站內活動 2外部活動")
    private Integer type;

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

    @Schema(title = "啟用狀態", description = "0停用 1啟用")
    private Integer enable;

    @Schema(title = "外部連結")
    private String url;

    @Schema(title = "PC圖片")
    private String pcImage;

    @Schema(title = "H5圖片")
    private String h5Image;
}
