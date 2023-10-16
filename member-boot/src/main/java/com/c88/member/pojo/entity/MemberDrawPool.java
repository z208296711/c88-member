package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 抽獎模型獎池
 * @TableName member_draw_pool
 */
@TableName(value ="member_draw_pool")
@Data
@Builder
@AllArgsConstructor
public class MemberDrawPool extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模型ID
     */
    @TableField(value = "template_id")
    @Schema(title = "模型ID")
    private Long templateId;

    /**
     * 抽獎類型
     */
    @TableField(value = "draw_type")
    @Schema(title = "抽獎類型")
    private Integer drawType;

    /**
     * 獎項ID
     */
    @TableField(value = "award_id")
    @Schema(title = "獎項ID")
    private Long awardId;

    /**
     * 累積中獎次數
     */
    @TableField(value = "prize_count")
    @Schema(title = "累積中獎次數, 未中獎設為-1")
    private Integer prizeCount;

    /**
     * 今日中獎次數
     */
    @TableField(value = "prize_today")
    @Schema(title = "今日中獎次數, 未中獎設為-1")
    private Integer prizeToday;

    /**
     * 機率
     */
    @TableField(value = "percent")
    @Schema(title = "機率")
    private Integer percent;

    /**
     * 排序
     */
    @TableField(value = "sort")
    @Schema(title = "排序")
    private Integer sort;

    @TableField(value = "enable")
    @Schema(title = "是否啟用 0否 1是")
    private Integer enable;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}