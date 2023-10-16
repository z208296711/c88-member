package com.c88.member.pojo.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(title = "修改會員基本資料表單")
public class MemberInfoModifyForm {

    @Schema(title = "真實名")
    private String realName;

    @Schema(title = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

}
