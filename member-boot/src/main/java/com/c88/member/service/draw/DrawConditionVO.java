package com.c88.member.service.draw;

import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.entity.MemberDrawRecord;
import com.c88.member.pojo.entity.MemberDrawTemplate;
import com.c88.member.pojo.entity.MemberDrawTemplateCondition;
import com.c88.member.pojo.vo.MemberTodayCurrentDrawVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DrawConditionVO {
    private Member member;
    private MemberDrawTemplate memberDrawTemplate;
    private MemberTodayCurrentDrawVO currentDrawVO;
    private boolean precondition;
    private LocalDateTime resetTime;
    private List<MemberDrawTemplateCondition> conditionList;
    private List<MemberDrawRecord> recordList;
}
