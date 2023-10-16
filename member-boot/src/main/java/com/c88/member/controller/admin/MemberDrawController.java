package com.c88.member.controller.admin;

import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.common.enums.DrawRecordStateEnum;
import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.entity.MemberDrawPool;
import com.c88.member.pojo.form.*;
import com.c88.member.pojo.vo.*;
import com.c88.member.service.IDrawService;
import com.c88.member.service.IMemberDrawAwardItemService;
import com.c88.member.service.IMemberDrawPoolService;
import com.c88.member.vo.OptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Tag(name = "『後台』會員抽獎相關")
@RequestMapping("/api/v1/draw")
@RequiredArgsConstructor
public class MemberDrawController {

    private final IDrawService iDrawService;

    private final IMemberDrawPoolService iMemberDrawPoolService;

    private final IMemberDrawAwardItemService iMemberDrawAwardItemService;

    @Operation(summary = "取得-抽獎紀錄")
    @GetMapping("/record")
    public PageResult<AdminMemberDrawRecordVO> findDrawRecord(@ParameterObject SearchDrawRecordForm form) {
        return PageResult.success(iDrawService.findDrawRecord(form));
    }

    @Operation(summary = "取得-今日抽獎紀錄")
    @GetMapping("/today/record")
    public PageResult<AdminMemberDrawItemVO> findTodayDrawRecord(@ParameterObject SearchTodayDrawRecordForm form) {
        return PageResult.success(iDrawService.findTodayDrawRecord(form));
    }

    @Operation(summary = "取得-今日抽獎資訊")
    @GetMapping("/today/draw/info")
    public Result<AdminTodayDrawVO> findTodayDrawInfo(@ParameterObject SearchTodayDrawRecordForm form) {
        return Result.success(iDrawService.findTodayDrawInfo(form));
    }

    @Operation(summary = "操作-抽獎紀錄（2審核, 拒絕3, 已發送4）")
    @PutMapping("/record/{id}/operation")
    public Result<Boolean> operationDrawRecord(@PathVariable Long id,
                                               @RequestBody OperationDrawRecordForm form) {
        return Result.success(iDrawService.operationDrawRecord(id, DrawRecordStateEnum.fromIntValue(form.getState())));
    }


    @Operation(summary = "取得-抽獎模型資訊")
    @GetMapping("/template")
    public PageResult<AdminMemberDrawTemplateVO> findDrawTemplate(@ParameterObject SearchDrawTemplateForm form) {
        return PageResult.success(iDrawService.findDrawTemplatePage(form));
    }

    @Operation(summary = "取得-抽獎模型資訊選項")
    @GetMapping("/template/option")
    public Result<List<OptionVO>> findDrawTemplateOption() {
        return Result.success(iDrawService.findDrawTemplateOption());
    }

    @Operation(summary = "編輯-抽獎模型資訊")
    @PutMapping("/template")
    public Result<Boolean> modifyDrawTemplate(@RequestBody AdminMemberDrawTemplateVO form) {
        return Result.success(iDrawService.modifyDrawTemplate(form));
    }

    @Operation(summary = "新增-抽獎模型資訊")
    @PostMapping("/template")
    public Result<Boolean> addDrawTemplate(@RequestBody AdminMemberDrawTemplateVO form) {
        return Result.success(iDrawService.addDrawTemplate(form));
    }

    @Operation(summary = "查找-獎項資訊")
    @GetMapping("/item")
    public PageResult<AdminMemberDrawAwardItemVO> getDrawItem(@ParameterObject SearchDrawAwardItemForm form) {
        return PageResult.success(iDrawService.findDrawItems(form));
    }

    @Operation(summary = "新增-獎項資訊")
    @PostMapping("/item")
    public Result<Boolean> addDrawItem(@RequestBody AdminMemberDrawAwardItemVO item) {
        return Result.success(iDrawService.addDrawItem(item));
    }

    @Operation(summary = "編輯-獎項資訊")
    @PutMapping("/item")
    public Result<Boolean> modifyDrawItem(@RequestBody AdminMemberDrawAwardItemVO item) {
        return Result.success(iDrawService.modifyDrawItem(item));
    }

    @Operation(summary = "設置獎項列表")
    @GetMapping("/drawpool/list/{templateId}")
    public Result<List<MemberDrawVO>> getMemberDrawList(@PathVariable long templateId) {
        List<MemberDrawVO> list = iMemberDrawPoolService.list(templateId);
        return Result.success(list);
    }

    @Operation(summary = "設置獎項修改")
    @PostMapping("/drawpool/update")
    public Result<Boolean> updateMemberDrawList(@RequestBody MemberDrawpoolForm form) {
        List<Long> ids = form.getPools().stream().filter(p->p.getEnable()==1).mapToLong(MemberDrawPool::getAwardId).boxed().collect(Collectors.toList());
        List<MemberDrawAwardItem> items = iMemberDrawAwardItemService.getItemsByIds(ids);
        if(!iMemberDrawPoolService.checkDrawItemStatus(items)){
            throw new BizException(ResultCode.DRAW_ITEM_REFRESHED);
        }
        return Result.success(iMemberDrawPoolService.update(form.getTemplateId(), form.getPools()));
    }

    @Operation(summary = "設置獎項置頂置底")
    @PutMapping("/drawpool/sort")
    public Result<Boolean> modifyDrawpoolSortTopBottom(@Validated @RequestBody ModifyDrawpoolSortTopBottomForm form) {
        return Result.success(iMemberDrawPoolService.modifyDrawpoolSortTopBottom(form));
    }

}
