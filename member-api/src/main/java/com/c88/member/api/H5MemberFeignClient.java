package com.c88.member.api;

import com.c88.common.core.result.Result;
import com.c88.member.api.fallback.H5MemberFeignFallbackClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "c88-member", path = "/member/h5/member", fallback = H5MemberFeignFallbackClient.class)
public interface H5MemberFeignClient {

    @PutMapping(value = "/info/{id}/{realName}")
    Result<Boolean> modifyMemberInfo(@PathVariable("id") Long id, @PathVariable("realName") String realName);

}
