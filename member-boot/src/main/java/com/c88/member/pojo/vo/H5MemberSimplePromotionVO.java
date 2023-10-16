package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "前台優惠活動")
public class H5MemberSimplePromotionVO {

    @Schema(title = "優惠ID")
    private Integer id;

    @JsonIgnore
    @Schema(title = "活動類型", description = "1站內活動 2外部活動")
    private Integer type;

    @Schema(title = "標題")
    private String title;

    @Schema(title = "活動時間文字")
    private String timeText;

    @Schema(title = "PC圖片")
    private String pcImage;

    @Schema(title = "H5圖片")
    private String h5Image;

    @Schema(title = "網址")
    private String url;

}
