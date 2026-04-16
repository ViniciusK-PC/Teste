package com.example.coupon.domain.model;

import com.example.coupon.exception.BusinessException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @Setter
    private Long id;

    private String code;

    private BigDecimal discountValue;

    private LocalDate expirationDate;

    private boolean deleted = false;

    private Coupon(String code, BigDecimal discountValue, LocalDate expirationDate) {
        this.code = code;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
    }

    public static Coupon create(String code, BigDecimal discountValue, LocalDate expirationDate) {
        validateExpirationDate(expirationDate);
        validateDiscountValue(discountValue);
        String sanitizedCode = sanitizeCode(code);
        return new Coupon(sanitizedCode, discountValue, expirationDate);
    }

    public void update(String newCode, BigDecimal newDiscountValue, LocalDate newExpirationDate) {
        if (this.deleted) {
            throw new BusinessException("Cannot update a deleted coupon");
        }
        validateDiscountValue(newDiscountValue);
        validateExpirationDate(newExpirationDate);
        
        // Se o codigo mudou, sanitizamos o novo
        if (newCode != null && !newCode.equals(this.code)) {
            this.code = sanitizeCode(newCode);
        }
        
        this.discountValue = newDiscountValue;
        this.expirationDate = newExpirationDate;
    }

    public void delete() {
        if (this.deleted) {
            throw new BusinessException("Cupom ja deletado");
        }
        this.deleted = true;
        // Ao deletar logicamente, alteramos o codigo para liberar o original 
        // para novas criacoes, evitando conflito de Unique Constraint.
        this.code = "DEL_" + this.id + "_" + this.code;
    }

    private static void validateExpirationDate(LocalDate expirationDate) {
        int currentYear = LocalDate.now().getYear();
        if (expirationDate == null) {
            throw new BusinessException("A data de expiracao e obrigatoria");
        }
        if (expirationDate.getYear() != currentYear) {
            throw new BusinessException("O cupom deve expirar obrigatoriamente no ano atual (" + currentYear + ")");
        }
        if (expirationDate.isBefore(LocalDate.now())) {
            throw new BusinessException("A data de expiracao nao pode ser no passado");
        }
    }

    private static void validateDiscountValue(BigDecimal discountValue) {
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Discount value must be greater than zero");
        }
    }

    public static String sanitizeCode(String code) {
        if (code == null)
            throw new BusinessException("Code cannot be null");
        String sanitized = code.replaceAll("[^a-zA-Z0-9]", "");
        if (sanitized.length() < 6) {
            throw new BusinessException("O codigo deve ter pelo menos 6 caracteres alfanumericos");
        }
        return sanitized.substring(0, 6).toUpperCase();
    }
}
