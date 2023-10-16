package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Forget {
    @Schema(title = "會員綁定手機，或沒綁定則為 null")
    String mobile;
    @Schema(title = "會員綁定電子郵件，或沒綁定則為 null")
    String email;
}
