package com.c88.member.api;

import com.c88.common.core.result.Result;
import com.c88.member.vo.AllMemberPersonalRechargeAwardRecordByTemplateIdVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "c88-member", path = "/member/api/v1/recharge/award/record")
public interface MemberRechargeAwardRecordClient {

    @GetMapping("/personal/{templateId}/{memberId}")
    Result<List<AllMemberPersonalRechargeAwardRecordByTemplateIdVO>> findAllPersonalRechargeAwardRecordByTemplateId(@PathVariable("templateId") Long templateId, @PathVariable("memberId") Long memberId);

}
