package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 廣告
 *
 * @TableName member_advertisement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "member_advertisement")
public class MemberAdvertisement extends BaseEntity implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名稱
     */
    @TableField(value = "name")
    private String name;

    /**
     * PC首圖
     */
    @TableField(value = "pc_image")
    private String pcImage;

    /**
     * H5首圖
     */
    @TableField(value = "h5_image")
    private String h5Image;

    /**
     * 樣式ID
     */
    @TableField(value = "style_id")
    private Integer styleId;

    /**
     * 開始時間
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 結束時間
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * 0停用1啟用
     */
    @TableField(value = "enable")
    private Integer enable;

    /**
     * 網址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 鏈接類型 1內站 2外站
     */
    @TableField(value = "link_in_out")
    private Integer linkInOut;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

}