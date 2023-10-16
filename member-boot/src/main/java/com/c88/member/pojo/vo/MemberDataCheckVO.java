package com.c88.member.pojo.vo;

import com.c88.common.core.desensitization.annotation.Sensitive;
import com.c88.common.core.desensitization.enums.DesensitizedType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "檢查安全中心各項資料表單")
public class MemberDataCheckVO {

    @Sensitive(strategy = DesensitizedType.MOBILE_PHONE)
    @Schema(title = "檢查手機號碼結果", description = "驗證成功會回傳遮蔽後的手機號")
    private String phoneValid;

    @Sensitive(strategy = DesensitizedType.EMAIL)
    @Schema(title = "檢查郵箱結果", description = "0無1有")
    private String emailValid;

    @Schema(title = "檢查有無提款方式結果", description = "0無1有")
    private Integer withdrawModeValid;

    @Schema(title = "檢查有無登入密碼結果", description = "0無1有")
    private Integer passwordValid;

    @Schema(title = "檢查有無提款密碼結果", description = "0無1有")
    private Integer withdrawPasswordValid;

    @Schema(title = "有無領取手機驗證獎勵", description = "0無1有")
    private Integer isReceivePhoneValidAward;

    @Schema(title = "有無領取郵箱驗證獎勵", description = "0無1有")
    private Integer isReceiveEmailValidAward;

}
