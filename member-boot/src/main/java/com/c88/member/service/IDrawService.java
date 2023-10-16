package com.c88.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.member.common.enums.DrawRecordStateEnum;
import com.c88.member.pojo.form.SearchDrawAwardItemForm;
import com.c88.member.pojo.form.SearchDrawRecordForm;
import com.c88.member.pojo.form.SearchDrawTemplateForm;
import com.c88.member.pojo.form.SearchTodayDrawRecordForm;
import com.c88.member.pojo.vo.*;
import com.c88.member.vo.OptionVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
public interface IDrawService {

    IPage<AdminMemberDrawRecordVO> findDrawRecord(SearchDrawRecordForm form);

    IPage<AdminMemberDrawTemplateVO> findDrawTemplatePage(SearchDrawTemplateForm form);

    List<OptionVO> findDrawTemplateOption();

    IPage<AdminMemberDrawAwardItemVO> findDrawItems(SearchDrawAwardItemForm form);

    IPage<AdminMemberDrawItemVO> findTodayDrawRecord(SearchTodayDrawRecordForm form);

    AdminTodayDrawVO findTodayDrawInfo(SearchTodayDrawRecordForm form);

    Boolean operationDrawRecord(Long recordId, DrawRecordStateEnum state);

    Boolean updateDrawRecordAddress(UpdateDrawRecordAddressForm state);

    Boolean modifyDrawTemplate(AdminMemberDrawTemplateVO form);

    Boolean addDrawTemplate(AdminMemberDrawTemplateVO form);

    Boolean addDrawItem(AdminMemberDrawAwardItemVO itemVO);

    Boolean modifyDrawItem(AdminMemberDrawAwardItemVO itemVO);

    H5DrawInfoVO getDrawInfo(Long memberId);

    H5RouletteVO getRouletteVO(Long memberId);

    H5DrawItemVO doDrawAction(Long memberId);

    MemberTodayCurrentDrawVO getMemberCurrentDrawVO(Long memberId);

    void setMemberTodayCurrentDrawVO(Long memberId, MemberTodayCurrentDrawVO memberTodayCurrentDrawVO);

    LocalDateTime getResetStartTime();

    void cleanMemberDrawInfo();

}
