package com.c88.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegisterDTO {

    private Long id;

    private String username;

    private String promotionCode;

    private LocalDateTime gmtCreate;


}
