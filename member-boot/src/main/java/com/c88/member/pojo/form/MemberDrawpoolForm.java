package com.c88.member.pojo.form;

import com.c88.member.pojo.entity.MemberDrawPool;
import lombok.Data;

import java.util.List;

@Data
public class MemberDrawpoolForm {
    long templateId;
    List<MemberDrawPool> pools;
}
