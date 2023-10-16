package com.c88.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MemberLockDownDTO {
    @Schema(title = "會員Id 清單")
    private List<Long> memberIdList;
}
