package com.example.coupon.domain.model;

import com.example.coupon.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    // =============================================
    // CRIAÇÃO
    // =============================================

    @Nested
    @DisplayName("create() - Criação de Cupom")
    class CreateTests {

        @Test
        @DisplayName("Deve criar cupom com código sanitizado e em caixa alta")
        void deveCriarCupomComCodigoSanitizado() {
            Coupon coupon = Coupon.create("abc-123!", BigDecimal.TEN, LocalDate.now().plusDays(1));
            assertEquals("ABC123", coupon.getCode());
        }

        @Test
        @DisplayName("Deve truncar o código para 6 caracteres")
        void deveTruncarCodigoParaSeisCaracteres() {
            Coupon coupon = Coupon.create("ABCDEFGHIJ", BigDecimal.TEN, LocalDate.now().plusDays(1));
            assertEquals("ABCDEF", coupon.getCode());
        }

        @Test
        @DisplayName("Deve lançar exceção quando código tem menos de 6 caracteres alfanuméricos após sanitização")
        void deveLancarExcecaoQuandoCodigoCurto() {
            assertThrows(BusinessException.class, () ->
                    Coupon.create("AB!@#", BigDecimal.TEN, LocalDate.now().plusDays(1)));
        }

        @Test
        @DisplayName("Deve lançar exceção quando código é nulo")
        void deveLancarExcecaoQuandoCodigoNulo() {
            assertThrows(BusinessException.class, () ->
                    Coupon.create(null, BigDecimal.TEN, LocalDate.now().plusDays(1)));
        }

        @Test
        @DisplayName("Deve lançar exceção para data de expiração no passado")
        void deveLancarExcecaoParaDataPassada() {
            assertThrows(BusinessException.class, () ->
                    Coupon.create("SAVE10", BigDecimal.TEN, LocalDate.now().minusDays(1)));
        }

        @Test
        @DisplayName("Deve lançar exceção para data de expiração nula")
        void deveLancarExcecaoParaDataNula() {
            assertThrows(BusinessException.class, () ->
                    Coupon.create("SAVE10", BigDecimal.TEN, null));
        }

        @Test
        @DisplayName("Deve lançar exceção para valor de desconto zero")
        void deveLancarExcecaoParaDescontoZero() {
            assertThrows(BusinessException.class, () ->
                    Coupon.create("SAVE10", BigDecimal.ZERO, LocalDate.now().plusDays(1)));
        }

        @Test
        @DisplayName("Deve lançar exceção para valor de desconto negativo")
        void deveLancarExcecaoParaDescontoNegativo() {
            assertThrows(BusinessException.class, () ->
                    Coupon.create("SAVE10", BigDecimal.valueOf(-5), LocalDate.now().plusDays(1)));
        }

        @Test
        @DisplayName("Deve lançar exceção para valor de desconto nulo")
        void deveLancarExcecaoParaDescontoNulo() {
            assertThrows(BusinessException.class, () ->
                    Coupon.create("SAVE10", null, LocalDate.now().plusDays(1)));
        }

        @Test
        @DisplayName("Deve aceitar a data de hoje como data de expiração válida")
        void deveAceitarDataDeHojeComoExpiracao() {
            Coupon coupon = Coupon.create("SAVE10", BigDecimal.TEN, LocalDate.now());
            assertNotNull(coupon);
            assertEquals(LocalDate.now(), coupon.getExpirationDate());
        }
    }

    // =============================================
    // EXCLUSÃO (Soft Delete)
    // =============================================

    @Nested
    @DisplayName("delete() - Exclusão Lógica")
    class DeleteTests {

        @Test
        @DisplayName("Deve realizar soft delete alterando o status e o codigo para liberar o original")
        void deveRealizarSoftDelete() {
            Coupon coupon = Coupon.create("SAVE10", BigDecimal.TEN, LocalDate.now().plusDays(1));
            String originalCode = coupon.getCode();
            assertFalse(coupon.isDeleted());

            coupon.delete();
            
            assertTrue(coupon.isDeleted());
            assertTrue(coupon.getCode().startsWith("DEL_"));
            assertTrue(coupon.getCode().endsWith(originalCode));
        }

        @Test
        @DisplayName("Deve lançar exceção ao excluir cupom já deletado")
        void deveLancarExcecaoAoExcluirCupomJaDeletado() {
            Coupon coupon = Coupon.create("SAVE10", BigDecimal.TEN, LocalDate.now().plusDays(1));
            coupon.delete();

            BusinessException ex = assertThrows(BusinessException.class, coupon::delete);
            assertEquals("Cupom ja deletado", ex.getMessage());
        }
    }

    // =============================================
    // ATUALIZAÇÃO
    // =============================================

    @Nested
    @DisplayName("update() - Atualização de Cupom")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar desconto e expiração com sucesso")
        void deveAtualizarComSucesso() {
            Coupon coupon = Coupon.create("SAVE10", BigDecimal.TEN, LocalDate.now().plusDays(1));
            LocalDate newDate = LocalDate.now().plusDays(30);

            coupon.update("NEWCODE", BigDecimal.valueOf(25), newDate);

            assertEquals("NEWCOD", coupon.getCode()); // Sanitizado para 6 chars
            assertEquals(BigDecimal.valueOf(25), coupon.getDiscountValue());
            assertEquals(newDate, coupon.getExpirationDate());
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar cupom deletado")
        void deveLancarExcecaoAoAtualizarCupomDeletado() {
            Coupon coupon = Coupon.create("SAVE10", BigDecimal.TEN, LocalDate.now().plusDays(1));
            coupon.delete();

            assertThrows(BusinessException.class, () ->
                    coupon.update("VALID", BigDecimal.valueOf(25), LocalDate.now().plusDays(30)));
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar com desconto inválido")
        void deveLancarExcecaoAoAtualizarComDescontoInvalido() {
            Coupon coupon = Coupon.create("SAVE10", BigDecimal.TEN, LocalDate.now().plusDays(1));

            assertThrows(BusinessException.class, () ->
                    coupon.update("VALID", BigDecimal.ZERO, LocalDate.now().plusDays(30)));
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar com data de expiração passada")
        void deveLancarExcecaoAoAtualizarComDataPassada() {
            Coupon coupon = Coupon.create("SAVE10", BigDecimal.TEN, LocalDate.now().plusDays(1));

            assertThrows(BusinessException.class, () ->
                    coupon.update("VALID", BigDecimal.valueOf(25), LocalDate.now().minusDays(1)));
        }
    }
}
