package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "AdminMemberDrawAwardItemVO")
public class AdminMemberDrawAwardItemVO {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "獎項名稱")
    private String name;

    @Schema(title = "可用模組 0:一般抽獎")
    private Integer drawType;

    @Schema(title = "獎項類型 0:未中獎 1:紅利, 2:金幣, 3:實體, 4:存送")
    private Integer awardType;

    @Schema(title = "獎勵金額")
    private BigDecimal amount;

    @Schema(title = "流水倍數")
    private BigDecimal betRate;

    @Schema(title = "優惠模型ID")
    private Integer promotionId;

    @Schema(title = "每日限量 -1=無限")
    private Integer dailyNumber;

    @Schema(title = "全部限量 -1=無限")
    private Integer totalNumber;

    @Schema(title = "項目圖片")
    private String image;

    @Schema(title = "商品描述")
    private String note;

    @Schema(title = "審核 0不需審核 1需審核")
    private Integer review;

    @Schema(title = "啟用 0停用 1啟用")
    private Integer enable;

}