package com.c88.member.service;

import com.c88.member.C88MemberApplication;
import com.c88.member.common.enums.FreeBetGiftTypeEnum;
import com.c88.member.pojo.vo.H5FreeBetGiftVO;
import com.c88.member.pojo.vo.H5GiftNotificationVO;
import com.c88.member.pojo.vo.H5LevelUpGiftVO;
import com.c88.member.pojo.vo.MemberBirthdayGiftVO;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@SpringBootTest(classes = C88MemberApplication.class, properties = "spring.profiles.active:local")
public class IGiftServiceTest {

    @Resource
    private IGiftService iGiftService;

    /**
     * 測試dev帳號jacky
     */
    private static Long memberId = 116L;
    private static String username = "jacky123";

    public static void main(String[] args) {
        System.out.println(LocalDateTime.of(LocalDate.from(LocalDateTime.now().minusMonths(2).with(TemporalAdjusters.firstDayOfMonth())), LocalTime.MIN));
    }

    public void findGiftNotificationTest() {
        List<H5GiftNotificationVO> giftNotification = iGiftService.findGiftNotification(memberId, username);
    }

    public void getFreeBetGiftListTest() {
        List<H5FreeBetGiftVO> freeBetGiftList = iGiftService.getFreeBetGiftList(memberId);
    }

    public void receiveFreeBetGiftTest() {
        // 週
        Boolean aBoolean = iGiftService.receiveFreeBetGift(memberId, FreeBetGiftTypeEnum.WEEKLY.getValue());

        // 月
        Boolean aBoolean1 = iGiftService.receiveFreeBetGift(memberId, FreeBetGiftTypeEnum.MONTHLY.getValue());
    }

    public void getLevelUpGiftListTest() {
        List<H5LevelUpGiftVO> levelUpGiftList = iGiftService.getLevelUpGiftList(memberId);
    }

    public void receiveLevelUpGiftTest() {
        // default VIP0
        iGiftService.receiveLevelUpGift(memberId, 1);
    }

    public void findMemberBirthdayGiftTest() {
        MemberBirthdayGiftVO memberBirthdayGift = iGiftService.findMemberBirthdayGift(memberId);
    }

    public void receiveMemberBirthdayGiftTest() {
        Boolean aBoolean = iGiftService.receiveMemberBirthdayGift(memberId);
    }

}
