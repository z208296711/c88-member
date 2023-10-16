package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.mapper.MemberVipMapper;
import com.c88.member.pojo.entity.MemberVip;
import com.c88.member.service.IMemberVipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberVipServiceImpl extends ServiceImpl<MemberVipMapper, MemberVip> implements IMemberVipService {

//    private final MemberService memberService;

//    private final IMemberVipConfigService iMemberVipConfigService;

//    private final MemberRechargeClient memberRechargeClient;

//    private final Integer vip0 = 1;

//    @Override
//    @Transactional
//    public boolean modifyMemberVipLevel(DelMemberVipConfigForm form) {
//        this.lambdaUpdate()
//                .set(MemberVip::getCurrentVipId, form.getTargetVipId())
//                .eq(MemberVip::getCurrentVipId, form.getSourceVipId())
//                .update();
//        return this.removeById(form.getSourceVipId());
//    }
//
//    @Override
//    @Transactional
//    public List<MemberInfoDTO> getMemberVips(List<Long> memberIds) {
//        Map<Integer, String> vipConfigMap = iMemberVipConfigService.findVipConfigMap();
//        return this.lambdaQuery()
//                .in(MemberVip::getMemberId, memberIds)
//                .select(MemberVip::getMemberId, MemberVip::getCurrentVipId)
//                .list()
//                .stream()
//                .map(vip -> {
//                    MemberInfoDTO dto = new MemberInfoDTO();
//                    dto.setId(vip.getMemberId());
//                    dto.setCurrentVipId(vip.getCurrentVipId());
//                    return dto;
//                }).collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public MemberVip getMemberVip(Long memberId) {
//        MemberVip memberVip = this.lambdaQuery()
//                .eq(MemberVip::getMemberId, memberId)
//                .one();
//        if (memberVip == null) {
//            MemberVO memberVO = Optional
//                    .ofNullable(memberService.findMember(memberId))
//                    .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));
//            memberVip = new MemberVip();
//            memberVip.setMemberId(memberId);
//            memberVip.setUsername(memberVO.getUserName());
//            memberVip.setCurrentVipId(vip0);
//            memberVip.setLevelUpTime(LocalDateTime.now());
//            this.save(memberVip);
//        }
//        return memberVip;
//    }
//
//    @Override
//    @Transactional
//    public boolean doVipLevelUpAction(UpdateMemberVipForm form) {
//        MemberVip memberVip = this.getMemberVip(form.getMemberId());
//        int previousId = memberVip.getCurrentVipId();
//        memberVip.setCurrentVipId(form.getVipId());
//        memberVip.setPreviousVipId(previousId);
//        return this.updateById(memberVip);
//    }
//
//    @Override
//    public void doVipLevelUpAction(Long memberId) {
//
//        MemberVip memberVip = this.getMemberVip(memberId);
//
//        MemberVipConfig currentVipConfig = iMemberVipConfigService.getById(memberVip.getCurrentVipId());
//
//        Result<BigDecimal> periodRechargeResult = memberRechargeClient.memberTotalRechargeFromTo(memberId,
//                LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())), LocalTime.MIN),
//                LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth())), LocalTime.MAX));
//
//        if (!Result.isSuccess(periodRechargeResult)) {
//            //金額不符
//            return;
//        }
//
//        BigDecimal recharge = periodRechargeResult.getData();
//        //todo
////        BigDecimal exp = BigDecimal.valueOf(1000);
//        //倒序由大至小
//        List<MemberVipConfig> memberVipConfigList = iMemberVipConfigService.findAutoLevelUpVipConfigs();
//
//        memberVipConfigList
//                .stream()
//                .filter(config -> recharge.compareTo(BigDecimal.valueOf(config.getLevelUpRecharge())) >= 0)
////                .filter(config -> exp.compareTo(BigDecimal.valueOf(config.getLevelUpExp())) >= 0)
//                .findFirst()
//                .ifPresent(levelVipConfig -> {
//                    //判斷 VIP ID不相同 && 撈取到 VIP的充值金額 > 原本VIP充值金額 = 升級
//                    if (!memberVip.getCurrentVipId().equals(levelVipConfig.getId()) &&
//                            levelVipConfig.getLevelUpRecharge() > currentVipConfig.getLevelUpRecharge()) {
//                        int previousId = memberVip.getCurrentVipId();
//                        memberVip.setCurrentVipId(levelVipConfig.getId());
//                        memberVip.setPreviousVipId(previousId);
//                        this.updateById(memberVip);
//                    }
//                });
//    }
}




