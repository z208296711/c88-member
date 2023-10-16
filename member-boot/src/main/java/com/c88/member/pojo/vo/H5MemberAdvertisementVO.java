package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "前台廣告")
public class H5MemberAdvertisementVO {

    @Schema(title = "名稱")
    private String name;

    @Schema(title = "PC首圖")
    private String pcImage;

    @Schema(title = "H5首圖")
    private String h5Image;

    @Schema(title = "樣式ID")
    private Integer styleId;

    @Schema(title = "網址")
    private String url;

    @Schema(title = "鏈接類型", description = "1內站 2外站")
    private Integer linkInOut;

}
