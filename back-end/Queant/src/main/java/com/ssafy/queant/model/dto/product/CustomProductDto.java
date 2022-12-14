package com.ssafy.queant.model.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CustomProductDto {
    private int productId;
    private String institutionName;
    private String productName;
    private boolean isDeposit;
    private float baseRate;
    @Builder.Default
    private float specialRate =0L;
    private boolean isFixedRsrv;
    private String etc; // 기타 사항
    private Long amount; // 예금일때 한번에 넣을 금액
    private int saveTerm;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date endDate;
}