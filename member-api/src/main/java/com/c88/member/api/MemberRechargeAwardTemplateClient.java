package com.c88.member.api;

import com.c88.common.core.result.Result;
import com.c88.member.vo.MemberRechargeAwardTemplateClientVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "c88-member", path = "/member/api/v1/recharge/award/template")
public interface MemberRechargeAwardTemplateClient {

    @GetMapping("/{id}")
    Result<MemberRechargeAwardTemplateClientVO> findRechargeAwardTemplate(@PathVariable("id") Long id);

}
