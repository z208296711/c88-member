package com.c88.member.api.fallback;

import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.member.api.MemberFeignClient;
import com.c88.member.dto.*;
import com.c88.member.form.MemberCheckForm;
import com.c88.member.vo.OptionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class MemberFeignFallbackClient implements MemberFeignClient {

    @Override
    public Result getMemberByUserName(String userName) {
        log.error("feign远程调用系统用户服务异常后的降级方法");
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result getMemberByMobile(String mobile) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result getMemberByEmail(String email) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<MemberDTO> getMemberById(Long id) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<MemberInfoDTO> getMemberInfo(Long id) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<MemberInfoDTO> getMemberInfo(String userName) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<List<MemberInfoDTO>> getMemberInfoByUsernames(List<String> usernames) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<Set<Integer>> getMemberTagIds(Long id) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<MemberDTO> getMemberSimilarUsername(Long id, String username) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<MemberDTO> getMemberSameColumn(Long id, String column, String value) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<Boolean> lockDown(MemberLockDownDTO dto) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    // @Override
    // public Result<Boolean> updateBalanceAndWithdrawLimit(AddBalanceAndWithdrawLimitDTO form) {
    //     return Result.failed(ResultCode.DEGRADATION);
    // }

    // @Override
    // public Result<Boolean> addBalance(AddBalanceDTO form) {
    //     return Result.failed(ResultCode.DEGRADATION);
    // }

    @Override
    public Result<MemberSessionCheckDTO> getSessionCheck(MemberCheckForm form) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<MemberAssociationRateDTO> getAssociation() {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<MemberAssociationRateDTO> userAssociation(String username) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<List<OptionVO<Integer>>> getTypes() {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<Map<Integer, String>> findMemberVipConfigMap() {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<List<MemberInfoDTO>> getMemberVips(String ids) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<Integer> getRegisterCount(String startTime, String endTime){
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<List<MemberRebateConfigDTO>> getMemberRebateAll() {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<List<MemberVipConfigDTO>> getMemberVipConfigAll() {
        return Result.failed(ResultCode.DEGRADATION);
    }

}
