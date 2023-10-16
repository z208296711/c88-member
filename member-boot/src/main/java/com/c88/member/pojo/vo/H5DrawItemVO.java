package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class H5DrawItemVO {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "recordId")
    private Long recordId;

    @Schema(title = "項目圖片")
    private String image;

    @Schema(title = "note")
    private String note;

    @Schema(title = "獎項類型 0:未中獎 1:紅利, 2:金幣, 3:實體, 4:存送")
    private String awardType;

    @Schema(title = "名稱")
    private String name;

    @Schema(title = "獎勵金額")
    private BigDecimal amount;

}
