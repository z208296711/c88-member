package com.c88.member.service.draw;

import lombok.Data;

@Data
public class CurrentConditionStateVO {

    private Integer type;

    public Integer templateId;

    public Integer templateConditionId;

    /**
     * 是否可抽獎
     */
    public Boolean checkDraw;

    private Integer currentDrawNum;

    private Integer maxNum;


}
