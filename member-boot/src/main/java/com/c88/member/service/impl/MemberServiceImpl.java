package com.c88.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.affiliate.api.dto.AffiliateMemberDTO;
import com.c88.affiliate.api.feign.AffiliateMemberClient;
import com.c88.amqp.AuthToken;
import com.c88.common.core.constant.DataConstants;
import com.c88.common.core.enums.EnableEnum;
import com.c88.common.core.enums.MemberStatusEnum;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.core.util.HttpUtil;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.I18nUtil;
import com.c88.common.web.util.Pageutil;
import com.c88.common.web.util.UserUtils;
import com.c88.feign.AuthFeignClient;
import com.c88.member.common.enums.I18nKey;
import com.c88.member.common.enums.LevelUpModeEnum;
import com.c88.member.common.enums.MemberOnlineEnum;
import com.c88.member.common.enums.MemberTimeTypeEnum;
import com.c88.member.dto.MemberInfoDTO;
import com.c88.member.dto.MemberRegisterDTO;
import com.c88.member.mapper.MemberMapper;
import com.c88.member.mapper.MemberSessionMapper;
import com.c88.member.mapstruct.MemberConverter;
import com.c88.member.mapstruct.MemberVipConfigConverter;
import com.c88.member.pojo.entity.*;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.query.MemberPageQuery;
import com.c88.member.pojo.vo.AdminMemberVO;
import com.c88.member.pojo.vo.MemberSessionVO;
import com.c88.member.pojo.vo.MemberVO;
import com.c88.member.pojo.vo.MemberVipInfoVO;
import com.c88.member.service.*;
import com.c88.payment.client.AdminMemberBankClient;
import com.c88.payment.client.MemberBalanceClient;
import com.c88.payment.dto.PaymentMemberBalanceDTO;
import com.c88.sms.MailService;
import com.c88.sms.SmsResultEnum;
import com.c88.sms.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.c88.common.core.constant.Constants.onlineTimeout;
import static com.c88.common.core.constant.RedisConstants.ONLINE_MEMBER;
import static com.c88.common.core.constant.TopicConstants.MEMBER_REGISTER;
import static com.c88.common.redis.utils.RedisUtils.buildKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

    private final SmsService smsService;

    private final MailService mailService;

    private final RedisUtils redisUtils;

    private final RedissonClient redisson;

    private final PasswordEncoder passwordEncoder;

    private final MemberConverter memberConverter;

    private final IMemberRemarkService iMemberRemarkService;

    private final IMemberVipService iMemberVipService;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Integer VIP_0 = 1;

    private final Environment environment;

    private static final long onlineTimeoutSeconds = onlineTimeout * 60;

    private final IMemberVipConfigService iMemberVipConfigService;

    private final IMemberTagService iMemberTagService;

    private final MemberVipConfigConverter memberVipConfigConverter;

    private final IMemberCoinService memberCoinService;

    private final MemberBalanceClient memberBalanceClient;

    private final AffiliateMemberClient affiliateMemberClient;

    private final IMemberAssociationRateService memberAssociationRateService;

    private final IMemberLiquidityService memberLiquidityService;

    private final MemberSessionMapper memberSessionMapper;

    private final ITagService iTagService;

    private final AdminMemberBankClient adminMemberBankClient;

    private final AuthFeignClient authFeignClient;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value(value = "${sms.expiration:5}")
    private int smsExpiration;// 分鐘

    @Value(value = "${email.expiration:10}")
    private int emailExpiration;// 分鐘

    @Value(value = "${freeze.count:10}")
    private int freezeCount;// 超過密碼錯誤次數後凍結帳號

    public Member getMemberByUserName(String userName) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Member>().eq(Member::getUserName, userName));
    }

    public Member getMemberByMobile(String mobile) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Member>().eq(Member::getMobile, mobile));
    }

    public Member getMemberByEmail(String email) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Member>().eq(Member::getEmail, email));
    }

    public Boolean checkMember(String userName) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Member>().eq(Member::getUserName, userName)) > 0;
    }

    @Override
    public Boolean lockDown(List<Long> memberIdList) {
        List<Member> memberList = this.lambdaQuery().in(Member::getId, memberIdList)
                .select(Member::getId, Member::getUserName)
                .list();

        memberList.forEach(x -> authFeignClient.cleanToken("c88-weapp", x.getUserName()));

        return this.lambdaUpdate().in(Member::getId, memberIdList)
                .set(Member::getStatus, EnableEnum.STOP.getCode())
                .update();
    }

    @Override
    @Transactional
    public Member registerMember(HttpServletRequest request, MemberForm memberForm) {
        authFeignClient.googleRecaptcha(memberForm.getUserName(), memberForm.getToken());
        String rawPassword = memberForm.getPassword();
        memberForm.setDisplayPassword(String.valueOf(memberForm.getPassword().charAt(0)) + memberForm.getPassword().charAt(memberForm.getPassword().length() - 1));
        memberForm.setPassword(passwordEncoder.encode(rawPassword));
        Member member = BeanUtil.copyProperties(memberForm, Member.class);
        member.setRegisterIp(ServletUtil.getClientIP(request));
        member.setRegisterDomain(request.getServerName());
        member.setGmtCreate(LocalDateTime.now());
        this.save(member);
        memberForm.setPassword(rawPassword);// for generate token

        MemberLiquidity liquidity = MemberLiquidity.builder().memberId(member.getId()).username(memberForm.getUserName()).build();
        memberLiquidityService.save(liquidity);
        memberCoinService.save(MemberCoin.builder().memberId(member.getId()).username(member.getUserName()).coin(0).build());
        memberAssociationRateService.save(MemberAssociationRate.builder()
                .username(member.getUserName())
                .build());

        kafkaTemplate.send(MEMBER_REGISTER, MemberRegisterDTO.builder()
                .id(member.getId())
                .username(member.getUserName())
                .promotionCode(memberForm.getPromotionCode())
                .gmtCreate(member.getGmtCreate())
                .build());

        return member;
    }

    public String sendSmsCode(String mobile) {
        String random5digit = RandomStringUtils.randomNumeric(5);
        SmsResultEnum result = SmsResultEnum.SUCCESS;//smsService.sendSms(mobile, random5digit);
        switch (result) {
            case SUCCESS:
                String uuid = UUID.randomUUID().toString();
                redisUtils.set(mobile + ':' + uuid, random5digit, smsExpiration * 60);
                if (Arrays.stream(environment.getActiveProfiles()).anyMatch(p -> p.matches(".*(dev|stg|pre).*"))) {// dev, stage 測試環境才發送驗證碼至Slack
                    HttpUtil.postByJson("https://hooks.slack.com/services/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", Map.of("text", "mobile:" + mobile + ", code:" + random5digit));
                }
                return uuid;
            default:
                throw new BizException(result.getMsg());
        }
    }

    public String sendEmailCode(String email) throws Exception {
        String subject = I18nUtil.get(DataConstants.EMAIL_SUBJECT);
        String text = I18nUtil.get(DataConstants.EMAIL_CONTENT);
        String random5digit = RandomStringUtils.randomNumeric(5);
        CompletableFuture<Boolean> future = mailService.sendMail(
                new MailService.MailUnit().setTo(email)
                        .setSubject(subject)
                        .setText(String.format(text, random5digit, emailExpiration)));
        if (!future.get()) {
            throw new Exception("error.sendEmail");
        }

        String uuid = UUID.randomUUID().toString();
        redisUtils.set(email + ':' + uuid, random5digit, emailExpiration * 60);
        return uuid;
    }

    public Boolean verifySms(String mobile, String uuid, String code) {
        boolean verify = verifyCode(mobile, uuid, code);
        Long id = UserUtils.getMemberId();
        if (id != null && verify) {// 已登入會員，修改綁定手機
            baseMapper.update(null, new LambdaUpdateWrapper<Member>()
                    .set(Member::getMobile, mobile)
                    .set(Member::getLastInfoModified, LocalDateTime.now())
                    .eq(Member::getId, id));
        }
        return verify;
    }

    public Boolean verifyEmail(String email, String uuid, String code) {
        boolean verify = verifyCode(email, uuid, code);
        Long id = UserUtils.getMemberId();
        if (id != null && verify) {// 已登入會員，修改綁定電子郵件
            baseMapper.update(null, new LambdaUpdateWrapper<Member>()
                    .set(Member::getEmail, email)
                    .eq(Member::getId, id));
        }
        return verify;
    }

    public Boolean forgetPassword(MemberForm memberForm) {
        Member member = getMemberByUserName(memberForm.getUserName());
        boolean isMobile = memberForm.getMobile() != null;
        String identity = isMobile ? member.getMobile() : member.getEmail();// 在忘記密碼時，前端不會獲得完整手機號碼或電子郵件
        if (verifyCode(identity, memberForm.getSessionId(), memberForm.getCode())) {
            return baseMapper.update(null, new LambdaUpdateWrapper<Member>()
                    .set(Member::getPassword, memberForm.getPassword())
                    .eq(isMobile ? Member::getMobile : Member::getEmail, identity)) > 0;
        }
        return false;
    }

    public void increaseErrorPasswordCount(AuthToken token) {
        baseMapper.update(null, new UpdateWrapper<Member>()
                .setSql("error_password_count=error_password_count+1")
                .setSql("status=CASE WHEN error_password_count>" + freezeCount + " THEN " + MemberStatusEnum.FREEZE.getStatus() + " ELSE status END")
                .lambda()
                .eq(StringUtils.hasText(token.getUserName()) ? Member::getUserName :
                        (token.isSms() ? Member::getMobile : Member::getEmail), token.getPrincipal()));
    }

    public void resetErrorPasswordCount(AuthToken token) {
        baseMapper.update(null, new UpdateWrapper<Member>()
                .setSql("login_count=login_count+1")
                .lambda()
                .set(Member::getErrorPasswordCount, 0)
                .set(Member::getLastLoginIp, token.getIp())// 同時更新最後登入ip與時間
                .set(Member::getLastLoginTime, LocalDateTime.now())
                .eq(Member::getId, token.getId()));
    }

    private Boolean verifyCode(String identity, String uuid, String code) {
        String key = identity + ':' + uuid;
        if (!redisUtils.hasKey(key)) {
            return false;
        }
        return redisUtils.get(key).equals(code);
    }

    @CacheEvict(cacheNames = "findMemberByUsername", key = "#username", allEntries = true)
    public Boolean addWithdrawPassword(String username, WithdrawPasswordAddForm form) {
        Member member = findMemberByUsername(username);

        if (StringUtils.hasText(StringUtils.trimAllWhitespace(member.getWithdrawPassword()))) {
            throw new BizException(I18nKey.A0213.name());
        }

        return this.lambdaUpdate()
                .eq(Member::getUserName, username)
                .set(Member::getWithdrawPassword, passwordEncoder.encode(form.getPassword()))
                .update();
    }

    @CacheEvict(cacheNames = "findMemberByUsername", key = "#username", allEntries = true)
    public Boolean modifyWithdrawPassword(String username, WithdrawPasswordModifyForm form) {
        Member member = findMemberByUsername(username);

        String oldWithdrawPassword = member.getWithdrawPassword();
        String oldWithdrawPasswordForm = form.getOldPassword();
        String withdrawPassword = form.getPassword();
        String confirmWithdrawPassword = form.getConfirmPassword();

        if (!passwordEncoder.matches(oldWithdrawPasswordForm, oldWithdrawPassword)) {
            throw new BizException(I18nKey.A0214.name());
        }

        if (!withdrawPassword.equals(confirmWithdrawPassword)) {
            throw new BizException(I18nKey.A0215.name());
        }

        if (passwordEncoder.matches(withdrawPassword, oldWithdrawPassword)) {
            throw new BizException(I18nKey.A0216.name());
        }

        return this.lambdaUpdate()
                .eq(Member::getUserName, username)
                .set(Member::getWithdrawPassword, passwordEncoder.encode(form.getPassword()))
                .update();
    }

    @CacheEvict(cacheNames = "findMemberByUsername", key = "#username", allEntries = true)
    public Boolean modifyMemberInfo(String username, MemberInfoModifyForm form) {
        String realName = form.getRealName();
        LocalDate birthday = form.getBirthday();
        if (realName == null && birthday == null) {
            return Boolean.FALSE;
        }

        return this.lambdaUpdate()
                .eq(StringUtils.hasText(username), Member::getUserName, username)
                .set(StringUtils.hasText(realName), Member::getRealName, realName)
                .set(StringUtils.hasText(realName), Member::getLastInfoModified, LocalDateTime.now())
                .set(birthday != null, Member::getBirthday, birthday)
                .update();
    }

    @CacheEvict(cacheNames = "findMemberByUsername", key = "#username", allEntries = true)
    public Boolean modifyPassword(String username, PasswordModifyForm form) {
        Member member = findMemberByUsername(username);

        String oldPassword = member.getPassword();
        String oldPasswordForm = form.getOldPassword();
        String password = form.getPassword();
        String confirmPassword = form.getConfirmPassword();

        if (!passwordEncoder.matches(oldPasswordForm, oldPassword)) {
            throw new BizException(I18nKey.A0214.name());
        }

        if (!password.equals(confirmPassword)) {
            throw new BizException(I18nKey.A0215.name());
        }

        if (passwordEncoder.matches(password, oldPassword)) {
            throw new BizException(I18nKey.A0216.name());
        }

        return this.lambdaUpdate()
                .eq(Member::getUserName, username)
                .set(Member::getPassword, passwordEncoder.encode(form.getPassword()))
                .update();
    }

    @Cacheable(cacheNames = "findMemberByUsername", key = "#username")
    public Member findMemberByUsername(String username) {
        return this.lambdaQuery()
                .eq(Member::getUserName, username)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));
    }

    public boolean freezeUsers(List<String> usernames) {
//        List<String> usernameList = Arrays.asList(StringUtils.split(usernames, ","));
        return this.lambdaUpdate().in(Member::getUserName, usernames)
                .set(Member::getStatus, 2).update();
    }

    public boolean freezeUser(String username) {
//        List<String> usernameList = Arrays.asList(StringUtils.split(usernames, ","));
        Member member = findMemberByUsername(username);
        return this.lambdaUpdate().eq(Member::getUserName, username)
                .set(Member::getStatus, member.getStatus() == 1 ? 2 : 1).update();
    }

    public Boolean modifyMemberRealName(Long id, String realName) {
        return this.lambdaUpdate()
                .eq(Member::getId, id)
                .set(Member::getRealName, realName)
                .set(Member::getLastInfoModified, LocalDateTime.now())
                .update();
    }

    @Transactional
    public MemberVO findMember(Long memberId) {

        List<MemberVipConfig> memberVipConfigList = iMemberVipConfigService.findAllVipConfig();

        List<MemberVipConfig> autoLevelList = memberVipConfigList
                .stream()
                .filter(config -> config.getLevelUpMode().equals(LevelUpModeEnum.AUTO))
                .collect(Collectors.toList());

        // 處理當前等級 顯示 下一個等級 升級條件資訊
        memberVipConfigList.forEach(config -> {
            if (config.getLevelUpMode().equals(LevelUpModeEnum.MANUAL)) {
                return;
            }
            int level = autoLevelList.indexOf(config);
            if (level != autoLevelList.size() - 1) {
                MemberVipConfig memberVipConfig = autoLevelList.get(level + 1);
                config.setLevelUpRecharge(memberVipConfig.getLevelUpRecharge());
                config.setLevelUpExp(memberVipConfig.getLevelUpExp());
            }
        });

        MemberVO memberVO = this.baseMapper.findMember(memberId);

        MemberVip memberVip = this.getMemberVip(memberId);

        MemberVipConfig memberVipConfig = memberVipConfigList.stream()
                .filter(x -> x.getId().equals(memberVip.getCurrentVipId()))
                .findFirst()
                .orElse(null);

        if (memberVipConfig != null) {
            memberVO.setVipDetailVO(memberVipConfigConverter.toVipDetailVO(memberVipConfig));
        }
        return memberVO;
    }

    public Page<AdminMemberVO> queryMember(MemberPageQuery memberPageQuery) {
        Set<String> onlineMemberIds = redisTemplate.keys(ONLINE_MEMBER + "*");
        memberPageQuery.setOnlineMemberIds(onlineMemberIds.parallelStream().mapToLong(e -> {
            String[] ary = e.split(":");
            return Long.parseLong(ary[ary.length - 1]);
        }).boxed().collect(Collectors.toSet()));
        List<AdminMemberVO> list = baseMapper.queryMember(memberPageQuery);
//        Page<AdminMemberVO> page = adminMemberVOPage;
//        List<AdminMemberVO> list = page.getRecords();
        if (!list.isEmpty()) {
            Map<String, Long> map = redisUtils.getMultiExpires(list.parallelStream()
                    .map(e -> buildKey(ONLINE_MEMBER, String.valueOf(e.getId()))).distinct().toArray(String[]::new));
            LocalDateTime now = LocalDateTime.now();
            list.parallelStream().forEach(e -> {
                Long expire = map.get(buildKey(ONLINE_MEMBER, String.valueOf(e.getId())));
                if (expire != null) {// 會員列表「最後訪問時間」 的定義是「會員最後有在網站進行操作的時間」，用redis 在線會員的expire時間來計算
                    e.setLastLoginTime(now.minusSeconds(onlineTimeoutSeconds - expire));
                }
            });
            //登入時間 降冪排序
            list = list.stream().sorted(Comparator.comparing(AdminMemberVO::getLastLoginTime, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
        }
        // 重新處理page
        Pageable pageable = PageRequest.of(memberPageQuery.getPageNum() - 1, memberPageQuery.getPageSize());
        org.springframework.data.domain.Page<AdminMemberVO> pageFromList = Pageutil.createPageFromList(list, pageable);
        Page<AdminMemberVO> page = new Page<>();
        page.setRecords(pageFromList.getContent());
        page.setTotal(list.size());
        page.setCurrent(memberPageQuery.getPageNum());
//        page.setPages(list.size()/memberPageQuery.getPageSize() +1);
        page.setSize(memberPageQuery.getPageSize());

        return page;
    }

    @Override
    public IPage<AdminMemberVO> findMembers(MemberPageQuery form) {

        //如果條件是找登入時間，先去 member_session找
        Set<String> loginList = Collections.emptySet();
        if(Objects.equals(form.getTimeType(), MemberTimeTypeEnum.LOGIN.getCode())){
            List<MemberSession> memberSessions = memberSessionMapper
                    .selectList(new LambdaQueryWrapper<MemberSession>()
                            .ge(Objects.nonNull(form.getBeginTime()), MemberSession::getLoginTime, form.getBeginTime())
                            .lt(Objects.nonNull(form.getEndTime()), MemberSession::getLoginTime, form.getEndTime()));
            loginList = memberSessions.stream().map(MemberSession::getUsername).collect(Collectors.toSet());
        }
        // 取得線上的會員帳號
        Set<String> keys = redisTemplate.keys("uname_to_access:c88-weapp:*");
        List<String> usernames = Optional.ofNullable(
                        keys.stream()
                                .map(x -> StringUtils.replace(x, "uname_to_access:c88-weapp:", ""))
                                .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());

        IPage<AdminMemberVO> adminMemberVOIPage = this.lambdaQuery()
                .eq(StringUtils.hasText(form.getUserName()), Member::getUserName, form.getUserName())
                .eq(StringUtils.hasText(form.getRealName()), Member::getRealName, form.getRealName())
                .eq(StringUtils.hasText(form.getEmail()), Member::getEmail, org.apache.commons.lang3.StringUtils.substring(form.getEmail(), 0, 9))
                .eq(StringUtils.hasText(form.getMobile()), Member::getMobile, org.apache.commons.lang3.StringUtils.substring(form.getMobile(), Optional.ofNullable(form.getMobile()).orElse("00000").length() - 5))
                .in(StringUtils.hasText(form.getBankAccount()) && CollUtil.isNotEmpty(this.getCurrencyBankAccountMembers(form.getBankAccount())), Member::getId, this.getCurrencyBankAccountMembers(form.getBankAccount()))
                .in(StringUtils.hasText(form.getParentUsername()) && CollUtil.isNotEmpty(this.getCurrencyParentsByParentUsername(form.getParentUsername())), Member::getId, this.getCurrencyParentsByParentUsername(form.getParentUsername()))
                .eq(Objects.nonNull(form.getStatus()), Member::getStatus, form.getStatus())
                .in(Objects.equals(form.getTimeType(), MemberTimeTypeEnum.LOGIN.getCode()) && Objects.nonNull(form.getBeginTime()) && CollUtil.isNotEmpty(loginList), Member::getUserName, loginList)
//                .le(Objects.equals(form.getTimeType(), MemberTimeTypeEnum.LOGIN.getCode()) && Objects.nonNull(form.getEndTime()), Member::getLastLoginTime, form.getEndTime())
                .ge(Objects.equals(form.getTimeType(), MemberTimeTypeEnum.REGISTER.getCode()) && Objects.nonNull(form.getBeginTime()), Member::getGmtCreate, form.getBeginTime())
                .le(Objects.equals(form.getTimeType(), MemberTimeTypeEnum.REGISTER.getCode()) && Objects.nonNull(form.getEndTime()), Member::getGmtCreate, form.getEndTime())
                .in(Objects.nonNull(form.getVipId()) && CollUtil.isNotEmpty(this.getCurrencyVipMembers(form.getVipId())), Member::getId, this.getCurrencyVipMembers(form.getVipId()))
                .in(Objects.nonNull(form.getTag()) && CollUtil.isNotEmpty(this.getCurrencyTagMembers(form.getTag())),  Member::getId, this.getCurrencyTagMembers(form.getTag()))
                .in(Objects.nonNull(form.getOnline()) && Objects.equals(form.getOnline(), MemberOnlineEnum.ONLINE.getCode()) && CollUtil.isNotEmpty(usernames), Member::getUserName, usernames)
                .notIn(Objects.nonNull(form.getOnline()) && Objects.equals(form.getOnline(), MemberOnlineEnum.OFFLINE.getCode()) && CollUtil.isNotEmpty(usernames), Member::getUserName, usernames)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberConverter::toAdminMemberVO);

        if (adminMemberVOIPage.getTotal() != 0) {
            // 取得目前過濾後的會員ID
            List<Long> memberIds = adminMemberVOIPage.getRecords()
                    .stream()
                    .map(AdminMemberVO::getId)
                    .collect(Collectors.toList());

            // 取得會員餘額
            Result<List<PaymentMemberBalanceDTO>> memberBalanceResult = memberBalanceClient.findPaymentMemberBalanceByMemberIdArray(memberIds);
            if (Result.isSuccess(memberBalanceResult)) {
                List<PaymentMemberBalanceDTO> memberBalances = memberBalanceResult.getData();
                adminMemberVOIPage.getRecords()
                        .forEach(member ->
                                member.setBalance(
                                        memberBalances.stream()
                                                .filter(memberBalance -> Objects.equals(memberBalance.getMemberId(), member.getId()))
                                                .map(PaymentMemberBalanceDTO::getBalance)
                                                .findFirst()
                                                .orElse(BigDecimal.ZERO)
                                )
                        );
            }

            // 取得會標籤
            Map<Long, List<Integer>> tags = iMemberTagService.lambdaQuery()
                    .in(MemberTag::getMemberId, memberIds).list().stream()
                    .collect(Collectors.groupingBy(MemberTag::getMemberId, Collectors.mapping(MemberTag::getTagId, Collectors.toList())));

            Map<Integer, String> tagConfigs = iTagService.lambdaQuery()
                    .list()
                    .stream()
                    .collect(Collectors.toMap(Tag::getId, Tag::getName));

            // 取得會員等級
            Map<Long, String> membersVipNameMap = iMemberVipConfigService.findMembersVipNameMap(memberIds);

            // 取得會員上層級總代理
            Result<List<AffiliateMemberDTO>> affiliateMembersResult = affiliateMemberClient.findAffiliateMembers(memberIds);
            if (Result.isSuccess(affiliateMembersResult)) {
                List<AffiliateMemberDTO> affiliateMembers = affiliateMembersResult.getData();
                adminMemberVOIPage.getRecords()
                        .forEach(member -> {
                                    Optional<AffiliateMemberDTO> affiliateMemberOpt = affiliateMembers.stream()
                                            .filter(memberBalance -> Objects.equals(memberBalance.getMemberId(), member.getId()))
                                            .findFirst();

                                    List<String> collect = tags.getOrDefault(member.getId(), Collections.emptyList())
                                            .stream()
                                            .map(tag -> tagConfigs.getOrDefault(tag, ""))
                                            .collect(Collectors.toList());

                                    member.setVipLevel(membersVipNameMap.getOrDefault(member.getId(), ""));
                                    member.setTags(collect.toArray(new String[]{}));
                                    member.setMasterUsername(affiliateMemberOpt.map(AffiliateMemberDTO::getMasterUsername).orElse(""));
                                    member.setParentUsername(affiliateMemberOpt.map(AffiliateMemberDTO::getParentUsername).orElse(""));
                                }
                        );
            }

        }

        return adminMemberVOIPage;
    }

    @Transactional
    @Override
    public List<MemberInfoDTO> findMemberInfoByUsernames(List<String> usernames) {
        List<Member> member = this.lambdaQuery()
                .in(Member::getUserName, usernames)
                .list();
        return getMembersInfoDTO(member);
    }

    @Override
    public Integer getRegisterCount(String startTime, String endtime) {
        return this.getBaseMapper().selectCount(new LambdaQueryWrapper<Member>().gt(Member::getGmtCreate, startTime).lt(Member::getGmtCreate, endtime));
    }

    @Override
    public Boolean modifyMemberWithdrawPassword(ModifyMemberWithdrawPasswordForm form) {
        if (form.getNewWithdrawPassword().equals(form.getConfirmWithdrawPassword())) {
            return this.lambdaUpdate()
                    .eq(Member::getId, form.getMemberId())
                    .set(Member::getWithdrawPassword, passwordEncoder.encode(form.getNewWithdrawPassword()))
                    .update();
        }

        return Boolean.FALSE;
    }

    @Override
    public Boolean modifyMemberWithdrawControllerState(ModifyMemberWithdrawControllerStateForm form) {
        return this.lambdaUpdate()
                .eq(Member::getId, form.getMemberId())
                .set(Member::getWithdrawControllerState, form.getWithdrawControllerState())
                .update();
    }

    private List<MemberInfoDTO> getMembersInfoDTO(List<Member> members) {
        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());
        Map<Long, Long> memberVipMap = iMemberVipService.lambdaQuery()
                .in(MemberVip::getMemberId, memberIds)
                .list()
                .stream()
                .collect(Collectors.toMap(MemberVip::getMemberId, memberVip -> Long.valueOf(memberVip.getCurrentVipId())));

        List<MemberVipConfig> memberVIpConfigs = iMemberVipConfigService.list();

        Map<Long, String> vipConfigNameMap = memberVIpConfigs.stream()
                .collect(Collectors.toMap(memberConfig -> Long.valueOf(memberConfig.getId()), MemberVipConfig::getName));

        Map<Long, BigDecimal> vipConfigDailyWithdrawAmountMap = memberVIpConfigs.stream()
                .collect(Collectors.toMap(memberConfig -> Long.valueOf(memberConfig.getId()), vipConfig -> BigDecimal.valueOf(vipConfig.getDailyWithdrawAmount())));

        Map<Long, List<Integer>> tags = iMemberTagService.lambdaQuery()
                .in(MemberTag::getMemberId, memberIds)
                .list()
                .stream()
                .collect(Collectors.groupingBy(MemberTag::getMemberId, Collectors.mapping(MemberTag::getTagId, Collectors.toList())));

        return members.stream()
                .map(member ->
                        MemberInfoDTO.builder()
                                .id(member.getId())
                                .username(member.getUserName())
                                .realName(member.getRealName())
                                .status(member.getStatus())
                                .registerIp(member.getRegisterIp())
                                .registerTime(member.getGmtCreate())
                                .currentVipId(Math.toIntExact(memberVipMap.getOrDefault(member.getId(), 0L)))
                                .currentVipName(vipConfigNameMap.getOrDefault(memberVipMap.get(member.getId()), ""))
                                .tagIdList(tags.getOrDefault(member.getId(), Collections.emptyList()))
                                .lastLoginTime(member.getLastLoginTime())
                                .dailyWithdrawAmount(vipConfigDailyWithdrawAmountMap.getOrDefault(memberVipMap.getOrDefault(member.getId(), 0L), BigDecimal.ZERO))
                                .withdrawControllerState(member.getWithdrawControllerState())
                                .withdrawPassword(member.getWithdrawPassword())
                                .mobile(member.getMobile())
                                .build()
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminMemberVO getDetail(Long id) {
        Member member = this.getById(id);

        AdminMemberVO memberVO = memberConverter.toAdminMemberVO(member);

        MemberRemark remark = iMemberRemarkService.getBaseMapper().selectOne(new LambdaQueryWrapper<MemberRemark>()
                .eq(MemberRemark::getUid, id)
                .orderByDesc(MemberRemark::getGmtCreate)
                .last("limit 1"));

        MemberVip memberVip = this.getMemberVip(id);
        MemberVipConfig memberVipConfig = iMemberVipConfigService.lambdaQuery()
                .eq(MemberVipConfig::getId, memberVip.getCurrentVipId())
                .one();
        memberVO.setVipLevel(memberVipConfig.getName());
        memberVO.setLastRemark(remark != null ? remark.getContent() : null);

        MemberCoin memberCoin = Optional.ofNullable(memberCoinService.getMemberByUserName(memberVip.getUsername()))
                .orElse(MemberCoin.builder().coin(0).build());
        memberVO.setCoin(memberCoin.getCoin());

        // 取得錢包的餘額
        Result<PaymentMemberBalanceDTO> paymentMemberBalanceResult = memberBalanceClient.findPaymentMemberBalanceByMemberId(id);
        if (!Result.isSuccess(paymentMemberBalanceResult)) {
            throw new BizException(ResultCode.INTERNAL_SERVICE_CALLEE_ERROR);
        }
        memberVO.setBalance(paymentMemberBalanceResult.getData().getBalance());

        // 取的會員所屬代理
        Result<List<AffiliateMemberDTO>> affiliateMembersResult = affiliateMemberClient.findAffiliateMembers(List.of(member.getId()));
        if (Result.isSuccess(affiliateMembersResult)) {
            List<AffiliateMemberDTO> affiliateMembers = affiliateMembersResult.getData();
            Optional<AffiliateMemberDTO> affiliateMemberOpt = affiliateMembers.stream()
                    .filter(memberBalance -> Objects.equals(memberBalance.getMemberId(), member.getId()))
                    .findFirst();
            memberVO.setParentUsername(affiliateMemberOpt.map(AffiliateMemberDTO::getParentUsername).orElse(""));
        }

        return memberVO;
    }

    @Override
    @Transactional
    public MemberVip getMemberVip(Long memberId) {
        MemberVip memberVip = iMemberVipService.lambdaQuery()
                .eq(MemberVip::getMemberId, memberId)
                .one();
        if (memberVip == null) {
            String username = this.lambdaQuery()
                    .eq(Member::getId, memberId)
                    .select(Member::getUserName)
                    .one()
                    .getUserName();
            memberVip = new MemberVip();
            memberVip.setMemberId(memberId);
            memberVip.setUsername(username);
            memberVip.setCurrentVipId(VIP_0);
            memberVip.setCurrentVipName("vip0");
//            memberVip.setLevelUpTime(LocalDateTime.now());
            iMemberVipService.save(memberVip);
        }
        return memberVip;
    }

    @Override
    public MemberInfoDTO findMemberInfoByUsername(String userName) {
        Member member = this.lambdaQuery()
                .eq(Member::getUserName, userName)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

        return getMembersInfoDTO(List.of(member))
                .stream().findFirst()
                .orElse(MemberInfoDTO.builder().build());
    }

    @Override
    public MemberInfoDTO findMemberInfoById(Long id) {
        Member member = this.lambdaQuery()
                .eq(Member::getId, id)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

        return this.getMembersInfoDTO(List.of(member))
                .stream()
                .findFirst()
                .orElse(MemberInfoDTO.builder().build());
    }

    @Override
    public IPage<MemberVipInfoVO> findMembersVipInfo(IPage<MemberVipInfoVO> page, QueryWrapper<MemberVipInfoVO> queryWrapper) {
        return this.baseMapper.findMembersVipInfo(page, queryWrapper);
    }

    // private MemberInfoDTO getMemberInfoDTO(Member member) {
    //     MemberVip memberVip = iMemberVipService.lambdaQuery()
    //             .eq(MemberVip::getMemberId, member.getId())
    //             .oneOpt()
    //             .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));
    //
    //     MemberVipConfig memberVipConfig = iMemberVipConfigService.lambdaQuery()
    //             .select(MemberVipConfig::getName, MemberVipConfig::getDailyWithdrawAmount)
    //             .eq(MemberVipConfig::getId, memberVip.getCurrentVipId())
    //             .oneOpt()
    //             .orElseThrow(() -> new BizException(ResultCode.PARAM_ERROR));
    //
    //     List<Integer> tagIdList = iMemberTagService.lambdaQuery()
    //             .eq(MemberTag::getMemberId, member.getId())
    //             .list()
    //             .stream()
    //             .map(MemberTag::getTagId)
    //             .collect(Collectors.toList());
    //
    //     MemberInfoDTO dto = new MemberInfoDTO();
    //     dto.setId(member.getId());
    //     dto.setRealName(member.getRealName());
    //     dto.setRegisterIp(member.getRegisterIp());
    //     dto.setRegisterTime(member.getGmtCreate());
    //     dto.setUsername(member.getUserName());
    //     dto.setCurrentVipId(memberVip.getCurrentVipId());
    //     dto.setCurrentVipName(memberVipConfig.getName());
    //     dto.setTagIdList(tagIdList);
    //     dto.setLastLoginTime(member.getLastLoginTime());
    //     dto.setDailyWithdrawAmount(BigDecimal.valueOf(memberVipConfig.getDailyWithdrawAmount()));
    //     log.info("get memberInfo:{}", JSON.toJSONString(dto));
    //     return dto;
    // }

    public boolean updateWithdrawLimit(Long id, BigDecimal value) {
        return baseMapper.update(null, new UpdateWrapper<Member>()
                .setSql("withdraw_limit=withdraw_limit+" + value)
                .lambda()
                .eq(Member::getId, id)) > 0;
    }

    @Override
    public Set<Integer> getMemberTagIds(Long memberId) {
        return iMemberTagService.lambdaQuery()
                .eq(MemberTag::getMemberId, memberId)
                .list()
                .stream()
                .map(MemberTag::getTagId)
                .collect(Collectors.toSet());
    }

    /**
     * 取的整個代理線會員By代理帳號
     *
     * @param parentUsername 代理帳號
     * @return
     */
    private List<Long> getCurrencyParentsByParentUsername(String parentUsername) {
        if (Objects.nonNull(parentUsername)) {
            Result<List<AffiliateMemberDTO>> affiliateMembersResult = affiliateMemberClient.findAffiliateMembersByParentUsername(parentUsername);
            if (Result.isSuccess(affiliateMembersResult)) {
                List<Long> memberIds = affiliateMembersResult.getData()
                        .stream().map(AffiliateMemberDTO::getMemberId)
                        .collect(Collectors.toList());

                return CollUtil.isNotEmpty(memberIds) ? memberIds : List.of(-999L);
            }
        }

        return List.of(-999L);
    }

    /**
     * 取得會員銀行卡後五碼匹配的會員ID
     *
     * @param bankAccount
     * @return
     */
    private List<Integer> getCurrencyBankAccountMembers(String bankAccount) {
        if (Objects.nonNull(bankAccount)) {
            Result<List<Integer>> memberBankFuzzyLastResult = adminMemberBankClient.findMemberBankFuzzyLast(bankAccount.substring(bankAccount.length() - 5));
            if (Result.isSuccess(memberBankFuzzyLastResult)) {
                return memberBankFuzzyLastResult.getData();
            }
        }

        return Collections.emptyList();
    }

    /**
     * 取得目前使用此Tag的會員ID
     *
     * @param tagId tag ID
     * @return
     */
    private List<Long> getCurrencyTagMembers(Integer tagId) {
        return iMemberTagService.lambdaQuery()
                .eq(MemberTag::getTagId, tagId)
                .list()
                .stream()
                .map(MemberTag::getMemberId)
                .collect(Collectors.toList());
    }

    /**
     * 取得目前使用此vip的會員ID
     *
     * @param vipId VIP ID
     * @return
     */
    private List<Long> getCurrencyVipMembers(Integer vipId) {
        return iMemberVipService.lambdaQuery()
                .eq(MemberVip::getCurrentVipId, vipId)
                .list()
                .stream()
                .map(MemberVip::getMemberId)
                .collect(Collectors.toList());
    }

}
