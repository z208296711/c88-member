package com.c88.member.controller.h5;

import com.c88.common.core.constant.GrantType;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.core.util.GameUtil;
import com.c88.common.core.util.Validate;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.MemberUtils;
import com.c88.common.web.util.UserUtils;
import com.c88.feign.AuthFeignClient;
import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.Forget;
import com.c88.member.pojo.vo.MemberVO;
import com.c88.member.pojo.vo.VerifyEmail;
import com.c88.member.pojo.vo.VerifySms;
import com.c88.member.service.impl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 會員管理
 */
@Slf4j
@Tag(name = "H5會員管理")
@RestController
@RequestMapping("/h5/member")
@RequiredArgsConstructor
public class H5MemberController {

    private final MemberServiceImpl memberService;

    private final AuthFeignClient authFeignClient;

    private final PasswordEncoder passwordEncoder;

    @Value("${auth.header:null}")
    private String authHeader;

    @Operation(summary = "設定提款密碼")
    @PostMapping(value = "/withdrawPassword")
    public Result<Boolean> addWithdrawPassword(
            @Valid @RequestBody WithdrawPasswordAddForm form) {
        String username = MemberUtils.getUsername();
        if (!StringUtils.hasText(username)) {
            Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberService.addWithdrawPassword(username, form));
    }

    @Operation(summary = "修改提款密碼")
    @PutMapping(value = "/withdrawPassword")
    public Result<Boolean> modifyWithdrawPassword(
            @Valid @RequestBody WithdrawPasswordModifyForm form) {
        String username = MemberUtils.getUsername();
        if (!StringUtils.hasText(username)) {
            Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberService.modifyWithdrawPassword(username, form));
    }

    @Operation(summary = "修改會員基本資料")
    @PutMapping(value = "/info")
    public Result<Boolean> modifyMemberInfo(
            @Valid @RequestBody MemberInfoModifyForm form) {
        String username = MemberUtils.getUsername();
        if (!StringUtils.hasText(username)) {
            Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberService.modifyMemberInfo(username, form));
    }

    @Operation(summary = "修改密碼")
    @PutMapping(value = "/password")
    public Result<Boolean> modifyPassword(
            @Valid @RequestBody PasswordModifyForm form) {
        String username = MemberUtils.getUsername();
        if (!StringUtils.hasText(username)) {
            Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberService.modifyPassword(username, form));
    }

    @Operation(summary = "修改會員真實名")
    @PutMapping(value = "/info/{id}/{realName}")
    public Result<Boolean> modifyMemberRealName(@PathVariable("id") Long id, @PathVariable("realName") String realName) {
        return Result.success(memberService.modifyMemberRealName(id, realName));
    }

    @Operation(summary = "取得當前登入的會員資訊")
    @GetMapping("/me")
    public Result<MemberVO> me() {
        return Result.success(memberService.findMember(UserUtils.getMemberId()));
    }

    @Operation(summary = "會員登入，包含手機或電子郵件登入")
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody MemberForm memberForm) {
        if (memberForm.getMobile() != null) {
            if (!Validate.phone(memberForm.getMobile())) {
                throw new BizException("login.phoneValidate");
            }
            return ResponseEntity.ok(token(memberForm, GrantType.MEMBER_SMS));
        }
        if (memberForm.getEmail() != null) {
            return ResponseEntity.ok(token(memberForm, GrantType.MEMBER_EMAIL));
        }

        return ResponseEntity.ok(token(memberForm, GrantType.MEMBER));
    }

    @Operation(summary = "會員註冊")
    @PostMapping("/register")
    public ResponseEntity<Object> registerMember(HttpServletRequest request, @RequestBody MemberForm memberForm) {
        memberService.registerMember(request,memberForm);
        return ResponseEntity.ok(token(memberForm, GrantType.MEMBER));
    }

    @Operation(summary = "發送SMS驗證碼")
    @GetMapping("/sms/{mobile}")
    public Result<VerifySms> sendSmsCode(
            @Parameter(description = "手機") @PathVariable String mobile) throws Exception {
        if (!Validate.phone(mobile)) {
            throw new BizException("login.phoneValidate");
        }
        if (UserUtils.getMemberId() == null && // 未登入會員發送SMS驗證碼時，檢查號碼是否已綁定會員
                memberService.getMemberByMobile(mobile) == null) {
            throw new BizException("error.mobileNotFound");
        }
        if (UserUtils.getMemberId() != null && // 已登入會員發送修改手機SMS驗證碼時，檢查號碼是否已綁定其他會員
                memberService.getMemberByMobile(mobile) != null) {
            throw new BizException("error.mobileDuplicate");
        }
        VerifySms verifySms = new VerifySms();
        verifySms.setSessionId(memberService.sendSmsCode(mobile));
        return Result.success(verifySms);
    }

    @Operation(summary = "發送電子郵件驗證碼")
    @GetMapping("/emailCode/{email}")
    public Result<VerifyEmail> sendEmailCode(
            @Parameter(description = "電子郵件") @PathVariable String email) throws Exception {
        if (!Validate.email(email)) {
            throw new BizException("login.emailValidate");
        }
        if (UserUtils.getMemberId() == null && // 未登入會員發送電子郵件驗證碼時，檢查郵件是否已綁定會員
                memberService.getMemberByEmail(email) == null) {
            throw new BizException("error.emailNotFound");
        }
        if (UserUtils.getMemberId() != null &&// 已登入會員發送修改電子郵件驗證碼時，檢查郵件是否已綁定其他會員
                memberService.getMemberByEmail(email) != null) {
            throw new BizException("error.emailDuplicate");
        }
        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setSessionId(memberService.sendEmailCode(email));
        return Result.success(verifyEmail);
    }

    @Operation(summary = "檢查帳號是否已存在")
    @GetMapping("/check/{userName}")
    public Result<Boolean> checkMemberByUserName(
            @Parameter(description = "會員帳號") @PathVariable String userName) {
        return Result.success(memberService.checkMember(userName));
    }

    @Operation(summary = "驗證SMS驗證碼，給已登入會員綁定手機")
    @PostMapping("/verifySms")
    public Result<Boolean> verifySmsCode(
            @Valid @RequestBody VerifySms verifySms) {
        if (memberService.getMemberByMobile(verifySms.getMobile()) != null) {
            throw new BizException("error.mobileDuplicate");
        }
        if (Boolean.FALSE.equals(memberService.verifySms(verifySms.getMobile(), verifySms.getSessionId(), verifySms.getCode()))) {
            throw new BizException("error.codeInvalid");
        }
        return Result.success();
    }

    @Operation(summary = "驗證電子郵件驗證碼，給已登入會員綁定電子郵件")
    @PostMapping("/verifyEmail")
    public Result<Boolean> verifyEmailCode(
            @Valid @RequestBody VerifyEmail verifyEmail) {
        if (memberService.getMemberByEmail(verifyEmail.getEmail()) != null) {
            throw new BizException("error.emailDuplicate");
        }
        if (Boolean.FALSE.equals(memberService.verifyEmail(verifyEmail.getEmail(), verifyEmail.getSessionId(), verifyEmail.getCode()))) {
            throw new BizException("error.codeInvalid");
        }
        return Result.success();
    }

    @Operation(summary = "忘記密碼，驗證SMS驗證碼")
    @PostMapping("/forgetPassword/verifySms")
    public Result<Boolean> forgetPasswordVerifySms(@Valid @RequestBody VerifySms verifySms) {
        Member member = memberService.getMemberByUserName(verifySms.getUserName());
        return Result.success(memberService.verifySms(member.getMobile(), verifySms.getSessionId(), verifySms.getCode()));
    }

    @Operation(summary = "忘記密碼，驗證電子郵件驗證碼")
    @PostMapping("/forgetPassword/verifyEmail")
    public Result<Boolean> forgetPasswordVerifyEmail(@Valid @RequestBody VerifyEmail verifyEmail) {
        Member member = memberService.getMemberByUserName(verifyEmail.getUserName());
        return Result.success(memberService.verifyEmail(member.getEmail(), verifyEmail.getSessionId(), verifyEmail.getCode()));
    }

    @Operation(summary = "忘記密碼，發送SMS驗證碼")
    @GetMapping("/forgetPassword/sms/{userName}")
    public Result<VerifySms> forgetPasswordSms(@Parameter(description = "會員帳號") @PathVariable String userName) throws Exception {
        Member member = memberService.getMemberByUserName(userName);
        return sendSmsCode(member.getMobile());
    }

    @Operation(summary = "忘記密碼，發送電子郵件驗證碼")
    @GetMapping("/forgetPassword/emailCode/{userName}")
    public Result<VerifyEmail> forgetPasswordEmail(@Parameter(description = "會員帳號") @PathVariable String userName) throws Exception {
        Member member = memberService.getMemberByUserName(userName);
        return sendEmailCode(member.getEmail());
    }

    @Operation(summary = "忘記密碼，包含手機或電子郵件驗證修改密碼")
    @PostMapping("/forgetPassword")
    public Result<Boolean> forgetPassword(@RequestBody MemberForm memberForm) {
        memberForm.setDisplayPassword(String.valueOf(memberForm.getPassword().charAt(0)) + memberForm.getPassword().charAt(memberForm.getPassword().length() - 1));
        memberForm.setPassword(passwordEncoder.encode(memberForm.getPassword()));
        return Result.success(memberService.forgetPassword(memberForm));
    }

    @Operation(summary = "忘記密碼，檢查會員是否有綁定手機或電子郵件")
    @GetMapping("/forgetPassword/check/{userName}")
    public Result<Forget> forgetPassword(@Parameter(description = "會員帳號") @PathVariable String userName) {
        Member member = memberService.getMemberByUserName(userName);
        Forget forget = new Forget();
        if (member != null) {
            forget.setMobile(StringUtils.hasText(member.getMobile()) ? GameUtil.maskCardNumber(member.getMobile()) : null);
            forget.setEmail(StringUtils.hasText(member.getEmail()) ? GameUtil.maskCardNumber(member.getEmail()) : null);
        }
        return Result.success(forget);
    }

    private Object token(MemberForm memberForm, String grantType) {
        if (authHeader == null) {
            log.error("authorization header is empty!");
        }
        return authFeignClient.token("Basic " + authHeader,
                generateMap(memberForm, grantType));
    }

    private Map<String, String> generateMap(MemberForm memberForm, String grantType) {
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", grantType);
        map.put("username", memberForm.getUserName());
        map.put("password", memberForm.getPassword());
        map.put("mobile", memberForm.getMobile());
        map.put("email", memberForm.getEmail());
        map.put("sessionId", memberForm.getSessionId());
        map.put("code", memberForm.getCode());
        map.put("token", memberForm.getToken());
        //有加密過的識別碼
        map.put("deviceCode", memberForm.getDeviceCode());
        //單純紀錄來源 h5, ios, android, pc
        map.put("device", memberForm.getDevice());
        map.put("loginDomain", memberForm.getLoginDomain());
        return map;
    }

}
