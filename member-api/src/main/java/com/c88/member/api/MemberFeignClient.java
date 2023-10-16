package com.c88.member.api;

import com.c88.common.core.result.Result;
import com.c88.member.api.fallback.MemberFeignFallbackClient;
import com.c88.member.dto.*;
import com.c88.member.form.MemberCheckForm;
import com.c88.member.vo.OptionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(name = "c88-member", path = "/member", fallback = MemberFeignFallbackClient.class)
public interface MemberFeignClient {

    @GetMapping("/api/v1/member/userName/{userName}")
    Result<AuthUserDTO> getMemberByUserName(@PathVariable String userName);

    @GetMapping("/api/v1/member/mobile/{mobile}")
    Result<AuthUserDTO> getMemberByMobile(@PathVariable String mobile);

    @GetMapping("/api/v1/member/email/{email}")
    Result<AuthUserDTO> getMemberByEmail(@PathVariable String email);

    @GetMapping("/api/v1/member/{id}/dto")
    Result<MemberDTO> getMemberById(@PathVariable Long id);

    @GetMapping("/api/v1/member/{id}/info")
    Result<MemberInfoDTO> getMemberInfo(@PathVariable Long id);

    @PostMapping("/api/v1/membersession/association")
    Result<MemberSessionCheckDTO> getSessionCheck(@RequestBody MemberCheckForm form);

    @GetMapping("/api/v1/membersession/getAssociation")
    Result<MemberAssociationRateDTO> getAssociation();

    @GetMapping("/api/v1/membersession/userAssociation/{username}")
    Result<MemberAssociationRateDTO> userAssociation(@PathVariable String username);

    @GetMapping("/api/v1/membersession/association/types")
    Result<List<OptionVO<Integer>>> getTypes();

    @GetMapping("/api/v1/member/vip/config/map")
    Result<Map<Integer, String>> findMemberVipConfigMap();

    @GetMapping("/api/v1/member/vip/{ids}")
    Result<List<MemberInfoDTO>> getMemberVips(@PathVariable String ids);

    @GetMapping(value = "/api/v1/member/userName/{userName}/info")
    Result<MemberInfoDTO> getMemberInfo(@PathVariable String userName);

    @GetMapping(value = "/api/v1/member/username/info")
    Result<List<MemberInfoDTO>> getMemberInfoByUsernames(@RequestParam List<String> usernames);

    @GetMapping("/api/v1/member/tags/{id}")
    Result<Set<Integer>> getMemberTagIds(@PathVariable Long id);

    @GetMapping("/api/v1/member/similarUsername/{id}/{username}")
    Result<MemberDTO> getMemberSimilarUsername(@PathVariable Long id, @PathVariable String username);

    @GetMapping("/api/v1/member/sameColumn/{id}/{column}/{value}")
    Result<MemberDTO> getMemberSameColumn(@PathVariable Long id, @PathVariable String column, @PathVariable String value);

    @PostMapping("/api/v1/member/lock/down")
    Result<Boolean> lockDown(@RequestBody MemberLockDownDTO dto);

    // @PutMapping("/api/v1/member/update/balance/withdraw/limit")
    // Result<Boolean> updateBalanceAndWithdrawLimit(@RequestBody AddBalanceAndWithdrawLimitDTO form);

    // @PutMapping("/api/v1/member/balance")
    // Result<Boolean> addBalance(@RequestBody AddBalanceDTO form);
    @GetMapping("api/v1/member/register/count")
    Result<Integer> getRegisterCount(@RequestParam String startTime, @RequestParam String endTime);

    @GetMapping("api/v1/member/rebate/all")
    Result<List<MemberRebateConfigDTO>> getMemberRebateAll();

    @GetMapping("/api/v1/member/vip/config/all")
    Result<List<MemberVipConfigDTO>> getMemberVipConfigAll();


}
