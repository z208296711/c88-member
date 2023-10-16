package com.c88.member.api.fallback;

import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.member.api.H5MemberFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Component
public class H5MemberFeignFallbackClient implements H5MemberFeignClient {

    @Override
    public Result<Boolean> modifyMemberInfo(@PathVariable("id") Long id, @PathVariable("realName") String realName) {
        return Result.failed(ResultCode.DEGRADATION);
    }

}
