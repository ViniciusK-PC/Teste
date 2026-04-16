package com.example.coupon.controller;

import com.example.coupon.domain.model.Coupon;
import com.example.coupon.domain.repository.CouponRepository;
import com.example.coupon.dto.request.CouponRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        couponRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/coupons - Deve criar cupom e retornar resposta padronizada")
    void deveCriarCupomComRespostaPadronizada() throws Exception {
        CouponRequestDTO request = new CouponRequestDTO("SAVE20", BigDecimal.valueOf(20), LocalDate.now().plusDays(10));

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Coupon created successfully"))
                .andExpect(jsonPath("$.data.code").value("SAVE20"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /api/coupons - Deve listar todos os cupons com resposta padronizada")
    void deveListarCuponsComRespostaPadronizada() throws Exception {
        Coupon coupon = Coupon.create("LIST01", BigDecimal.TEN, LocalDate.now().plusDays(1));
        couponRepository.save(coupon);

        mockMvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Coupons retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/coupons/{id} - Deve retornar cupom por ID")
    void deveRetornarCupomPorId() throws Exception {
        Coupon coupon = Coupon.create("FIND01", BigDecimal.TEN, LocalDate.now().plusDays(1));
        coupon = couponRepository.save(coupon);

        mockMvc.perform(get("/api/coupons/" + coupon.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.code").value("FIND01"));
    }

    @Test
    @DisplayName("DELETE /api/coupons/{id} - Deve realizar soft delete e não retornar na listagem")
    void deveRealizarSoftDeleteENaoRetornarNaListagem() throws Exception {
        Coupon coupon = Coupon.create("DEL123", BigDecimal.TEN, LocalDate.now().plusDays(1));
        coupon = couponRepository.save(coupon);
        Long id = coupon.getId();

        mockMvc.perform(delete("/api/coupons/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Coupon deleted successfully"));

        mockMvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/coupons - Deve retornar erros de validação com formato padronizado")
    void deveRetornarErrosDeValidacao() throws Exception {
        CouponRequestDTO request = new CouponRequestDTO("", null, null);

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("GET /api/coupons/{id} - Deve retornar erro de negócio quando cupom não encontrado")
    void deveRetornarErroDeNegocioQuandoNaoEncontrado() throws Exception {
        mockMvc.perform(get("/api/coupons/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Coupon not found"));
    }
}
