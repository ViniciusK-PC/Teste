package com.example.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequestDTO {

    @NotBlank(message = "O codigo do cupom e obrigatorio")
    @Schema(description = "Codigo do cupom", example = "PROMO123")
    private String code;

    @NotNull(message = "O valor do desconto e obrigatorio")
    @Positive(message = "O valor do desconto deve ser positivo")
    @Schema(description = "Valor decimal do desconto", example = "15.50")
    private BigDecimal discountValue;

    @NotNull(message = "A data de expiracao e obrigatoria")
    @Schema(description = "Data de expiracao (AAAA-MM-DD)", example = "2026-12-31")
    private LocalDate expirationDate;
}
