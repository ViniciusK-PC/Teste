package com.example.coupon.service;

import com.example.coupon.domain.model.Coupon;
import com.example.coupon.domain.repository.CouponRepository;
import com.example.coupon.dto.request.CouponRequestDTO;
import com.example.coupon.dto.request.CouponUpdateDTO;
import com.example.coupon.dto.response.CouponResponseDTO;
import com.example.coupon.exception.BusinessException;
import com.example.coupon.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("Deve criar cupom com sucesso quando os dados são válidos")
    void deveCriarCupomComSucesso() {
        // Arrange
        CouponRequestDTO request = new CouponRequestDTO("NEW123", BigDecimal.TEN, LocalDate.now().plusDays(5));
        when(couponRepository.findByCodeAndDeletedFalse(anyString())).thenReturn(Optional.empty());
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CouponResponseDTO response = couponService.create(request);

        // Assert
        assertNotNull(response);
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Deve atualizar cupom com sucesso")
    void deveAtualizarCupomComSucesso() {
        // Arrange
        CouponUpdateDTO request = new CouponUpdateDTO("NEW123", BigDecimal.TEN, LocalDate.now().plusDays(5));
        Coupon coupon = mock(Coupon.class);
        when(coupon.getCode()).thenReturn("OLD123");
        when(couponRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CouponResponseDTO response = couponService.update(1L, request);

        // Assert
        assertNotNull(response);
        verify(couponRepository).findByIdAndDeletedFalse(1L);
        verify(coupon).update(request.getCode(), request.getDiscountValue(), request.getExpirationDate());
        verify(couponRepository).save(coupon);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o código (sanitizado) já existe")
    void deveLancarExcecaoQuandoCodigoJaExiste() {
        // Arrange
        CouponRequestDTO request = new CouponRequestDTO("promo-10!", BigDecimal.TEN, LocalDate.now().plusDays(5));
        String expectedSanitizedCode = "PROMO1"; 
        
        when(couponRepository.findByCodeAndDeletedFalse(expectedSanitizedCode)).thenReturn(Optional.of(mock(Coupon.class)));
        
        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> couponService.create(request));
        assertTrue(ex.getMessage().contains(expectedSanitizedCode));
        
        verify(couponRepository).findByCodeAndDeletedFalse(expectedSanitizedCode);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cupom inexistente")
    void deveLancarExcecaoAoBuscarCupomInexistente() {
        // Arrange
        when(couponRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> couponService.findById(1L));
    }
}
