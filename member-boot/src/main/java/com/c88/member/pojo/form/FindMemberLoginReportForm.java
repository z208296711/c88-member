package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(title = "查找玩家登入統計報表")
public class FindMemberLoginReportForm extends BasePageQuery {

    @Schema(title = "開始時間")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(title = "結束時間")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(title = "開始時間",hidden = true)
    private LocalDateTime startTime;

    @Schema(title = "結束時間" ,hidden = true)
    private LocalDateTime endTime;

    @Schema(title = "時區", description = "+8:00")
    private String zone = "+8:00";
}
