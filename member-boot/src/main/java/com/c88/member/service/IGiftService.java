package com.c88.member.service;

import com.c88.member.pojo.vo.H5FreeBetGiftVO;
import com.c88.member.pojo.vo.H5GiftNotificationVO;
import com.c88.member.pojo.vo.H5LevelUpGiftVO;
import com.c88.member.pojo.vo.MemberBirthdayGiftVO;

import java.util.List;

public interface IGiftService {

    List<H5GiftNotificationVO>  findGiftNotification(Long memberId, String username);

    List<H5FreeBetGiftVO> getFreeBetGiftList(Long memberId);

    Boolean receiveFreeBetGift(Long memberId, Integer type);

    List<H5LevelUpGiftVO> getLevelUpGiftList(Long memberId);

    Boolean receiveLevelUpGift(Long memberId, Integer vipId);

    MemberBirthdayGiftVO findMemberBirthdayGift(Long memberId);

    Boolean receiveMemberBirthdayGift(Long memberId);
}
