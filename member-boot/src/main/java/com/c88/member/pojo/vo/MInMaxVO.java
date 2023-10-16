package com.c88.member.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MInMaxVO {
    private Integer min;
    private Integer max;
}
