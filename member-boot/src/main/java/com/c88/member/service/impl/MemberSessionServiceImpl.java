package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.amqp.AuthToken;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.common.enums.MemberSessionCheckEnum;
import com.c88.member.dto.MemberSessionCheckDTO;
import com.c88.member.mapper.MemberLiquidityMapper;
import com.c88.member.mapper.MemberSessionMapper;
import com.c88.member.mapstruct.MemberLiquidityConverter;
import com.c88.member.mapstruct.MemberSessionConverter;
import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.entity.MemberLiquidity;
import com.c88.member.pojo.entity.MemberSession;
import com.c88.member.pojo.form.FindMemberLoginRecordForm;
import com.c88.member.pojo.form.FindMemberLoginReportForm;
import com.c88.member.pojo.vo.AdminMemberLoginReportVO;
import com.c88.member.pojo.vo.MemberLiquidityVO;
import com.c88.member.pojo.vo.MemberSessionVO;
import com.c88.member.service.IMemberSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 會員登入log服务接口实现
 *
 * @author Allen
 * @description 由 Mybatisplus Code Generator 创建
 * @since 2022-05-23 15:26:25
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MemberSessionServiceImpl extends ServiceImpl<MemberSessionMapper, MemberSession> implements IMemberSessionService {
    private final MemberSessionMapper memberSessionMapper;
    private final MemberLiquidityMapper memberLiquidityMapper;
    private final MemberServiceImpl memberService;

    private final MemberLiquidityConverter memberLiquidityConverter;

    private final MemberSessionConverter memberSessionConverter;

    public MemberSessionCheckDTO getCheckList(String username, int type) {
        List<Member> members = new ArrayList<>();
        MemberSessionCheckDTO dto = new MemberSessionCheckDTO();

        Member member = Optional.ofNullable(memberService.getMemberByUserName(username))
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

        MemberSessionCheckEnum checkEnum = MemberSessionCheckEnum.fromIntValue(type);
        switch (checkEnum) {
            case LOGIN_IP:
                dto.setCheckItemValue(member.getLastLoginIp());
                break;
            case UUID:
                dto.setCheckItemValue(member.getDeviceCode());
                break;
            case REAL_NAME:
                dto.setCheckItemValue(member.getRealName());
                break;
            case REG_IP:
                dto.setCheckItemValue(member.getRegisterIp());
                break;
            case ACCOUNT:
                dto.setCheckItemValue(member.getUserName());
                break;
            //TODO
            case WITHDRAW_IP:
                dto.setCheckItemValue("");
                break;
            case GAME_IP:
                dto.setCheckItemValue("");
                break;

            default:

        }

        if (type == MemberSessionCheckEnum.LOGIN_IP.getValue() || type == MemberSessionCheckEnum.UUID.getValue()) {
            members = memberSessionMapper.checkSessionListForLastLoginIp(username, type);
        }
        if (type == MemberSessionCheckEnum.REAL_NAME.getValue() || type == MemberSessionCheckEnum.REG_IP.getValue()) {
            members = memberSessionMapper.checkSessionList(username, type);
        }
        if (type == MemberSessionCheckEnum.ACCOUNT.getValue()) {
            members = memberSessionMapper.checkSimilarAccount(username);
        }

        // TODO
        if (type == MemberSessionCheckEnum.WITHDRAW_IP.getValue()) {

        }
        if (type == MemberSessionCheckEnum.GAME_IP.getValue()) {

        }

        dto.setUsername(username);
        dto.setCheckItem(type);

        if (CollectionUtils.isNotEmpty(members)) {
            List<String> usernames = members.stream()
                    .map(Member::getUserName)
                    .collect(Collectors.toList());

            Map<String, MemberLiquidity> memberLiquidityMap = memberLiquidityMapper.selectList(
                            new LambdaQueryWrapper<MemberLiquidity>()
                                    .in(MemberLiquidity::getUsername, usernames)
                    )
                    .stream()
                    .collect(Collectors.toMap(MemberLiquidity::getUsername, Function.identity()));

            List<MemberLiquidityVO> memberLiquidityVOS = members.stream()
                    .map(m -> {
                                MemberLiquidity memberLiquidity = memberLiquidityMap.getOrDefault(m.getUserName(), MemberLiquidity.builder().username(m.getUserName()).build());
                                MemberLiquidityVO memberLiquidityVO = memberLiquidityConverter.toVO(memberLiquidity);
                                memberLiquidityVO.setStatus(m.getStatus().intValue());
                                memberLiquidityVO.setUid(m.getId());

                                return memberLiquidityVO;
                            }
                    )
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(memberLiquidityVOS)) {
                dto.setConnectNames(memberLiquidityVOS);
            }

        }

        return dto;
    }

    @Override
    public void saveMemberSession(AuthToken token) {
        MemberSession session = MemberSession.builder()
                .loginIp(token.getIp())
                .username(token.getUserName())
                .uuid(token.getDeviceCode())
                .device(token.getDevice())
                .loginTime(LocalDateTime.now())
                .area(token.getArea())
                .loginDomain(token.getLoginDomain()).build();
        memberSessionMapper.insert(session);
    }

    @Override
    public IPage<AdminMemberLoginReportVO> findMemberLoginReport(FindMemberLoginReportForm form) {
        form.setStartTime(LocalDateTime.of(form.getStartDate(), LocalTime.MIN));
        form.setEndTime(LocalDateTime.of(form.getEndDate(), LocalTime.MAX));
        Map<LocalDate, AdminMemberLoginReportVO> dailyTotalVOMap = this.getBaseMapper().findMemberLoginTotalReport(form);
        Map<LocalDate, AdminMemberLoginReportVO> dailyDistinctVOMap = this.getBaseMapper().findMemberLoginDistinctReport(form);
        List<AdminMemberLoginReportVO> dailyRechargeWithdrawVOList = new ArrayList<>();

        form.getStartDate()
                .datesUntil(form.getEndDate().plusDays(1))
                .forEach(date -> {
                    AdminMemberLoginReportVO total = dailyTotalVOMap.getOrDefault(date, new AdminMemberLoginReportVO(date));
                    AdminMemberLoginReportVO distinct = dailyDistinctVOMap.getOrDefault(date, new AdminMemberLoginReportVO(date));

                    AdminMemberLoginReportVO adminMemberLoginReportVO = new AdminMemberLoginReportVO();
                    adminMemberLoginReportVO.setDate(date);
                    adminMemberLoginReportVO.setDistinctLogin(total.getDistinctLogin());
                    adminMemberLoginReportVO.setDistinctPCLogin(distinct.getDistinctPCLogin());
                    adminMemberLoginReportVO.setDistinctAndroidLogin(distinct.getDistinctAndroidLogin());
                    adminMemberLoginReportVO.setDistinctIosLogin(distinct.getDistinctIosLogin());
                    adminMemberLoginReportVO.setDistinctH5Login(distinct.getDistinctH5Login());
                    adminMemberLoginReportVO.setTotalLogin(total.getTotalLogin());
                    adminMemberLoginReportVO.setTotalPCLogin(total.getTotalPCLogin());
                    adminMemberLoginReportVO.setTotalAndroidLogin(total.getTotalAndroidLogin());
                    adminMemberLoginReportVO.setTotalIosLogin(total.getTotalIosLogin());
                    adminMemberLoginReportVO.setTotalH5Login(total.getTotalH5Login());
                    dailyRechargeWithdrawVOList.add(adminMemberLoginReportVO);
                });
        IPage<AdminMemberLoginReportVO> page = new Page<>();
        page.setRecords(dailyRechargeWithdrawVOList.stream()
                .sorted(Comparator.comparing(AdminMemberLoginReportVO::getDate).reversed())
                .skip((long) (form.getPageNum() - 1) * form.getPageSize())
                .limit(form.getPageSize())
                .collect(Collectors.toList()));
        page.setCurrent(form.getPageNum());
        page.setPages(dailyRechargeWithdrawVOList.size() / form.getPageSize());
        page.setSize(form.getPageSize());
        page.setTotal(dailyRechargeWithdrawVOList.size());
        return page;
    }

    @Override
    public IPage<MemberSessionVO> findMemberSession(FindMemberLoginRecordForm form) {
        return this.lambdaQuery()
                .eq(MemberSession::getUsername, form.getUserName())
                .between(MemberSession::getLoginTime, toOptional(form.getBeginTime(), true), toOptional(form.getEndTime(), false))
                .orderByDesc(MemberSession::getLoginTime)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberSessionConverter::toVO);
    }

    private LocalDateTime toOptional(LocalDateTime timeOf, boolean isStart) {
        return Optional.ofNullable(timeOf).orElse(isStart ? LocalDateTime.now().minusYears(1) : LocalDateTime.now());
    }
}