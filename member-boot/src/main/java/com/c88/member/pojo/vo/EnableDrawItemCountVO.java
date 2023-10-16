package com.c88.member.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnableDrawItemCountVO {
    private Integer templateId;
    private Integer count;
}
