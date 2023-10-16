package com.c88.member.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeAwardRecordModel {

    /**
     * 此次動作 1新增 2更新
     */
    private Integer action;

    /**
     * ID
     */
    private Long id;

    /**
     * 會員ID
     */
    private Long memberId;

    /**
     * 會員帳號
     */
    private String username;

    /**
     * 存送優惠模組ID
     */
    private Long templateId;

    /**
     * 優惠名稱
     */
    private String name;

    /**
     * 存送類型 1平台 2個人
     */
    private Integer type;

    /**
     * 存送模式 1比例 2固定
     */
    private Integer mode;

    /**
     * 優惠比例
     */
    private BigDecimal rate;

    /**
     * 打碼倍數
     */
    private BigDecimal betRate;

    /**
     * 贈送金額
     */
    private BigDecimal amount;

    /**
     * 充值金額
     */
    private BigDecimal rechargeAmount;

    /**
     * 來源
     */
    private String source;

    /**
     * 連結模式ID
     */
    private Integer linkModeId;

    /**
     * 使用時間
     */
    private LocalDateTime useTime;

    /**
     * 取消時間
     */
    private LocalDateTime cancelTime;

    /**
     * 狀態 0無 1已使用 2未使用 3已取消
     */
    private Integer state;
}
