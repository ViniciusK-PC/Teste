package com.example.coupon.controller;

import com.example.coupon.dto.request.CouponRequestDTO;
import com.example.coupon.dto.request.CouponUpdateDTO;
import com.example.coupon.dto.response.ApiResponseDTO;
import com.example.coupon.dto.response.CouponResponseDTO;
import com.example.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Cupons", description = "API de Gerenciamento de Cupons de Desconto")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @Operation(summary = "Criar novo cupom", responses = {
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Entrada invalida ou violacao de regra de negocio")
    })
    public ResponseEntity<ApiResponseDTO<CouponResponseDTO>> create(@Valid @RequestBody CouponRequestDTO request) {

        CouponResponseDTO coupon = couponService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(201, "Cupom criado com sucesso", coupon));
    }

    @GetMapping
    @Operation(summary = "Listar todos os cupons ativos")
    public ResponseEntity<ApiResponseDTO<List<CouponResponseDTO>>> findAll() {

        List<CouponResponseDTO> coupons = couponService.findAll();
        return ResponseEntity.ok(ApiResponseDTO.success(200, "Cupons recuperados com sucesso", coupons));
    }

    @Operation(summary = "Buscar cupom por ID")
    public ResponseEntity<ApiResponseDTO<CouponResponseDTO>> findById(@PathVariable Long id) {
        CouponResponseDTO coupon = couponService.findById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(200, "Cupom encontrado com sucesso", coupon));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um cupom existente", responses = {
            @ApiResponse(responseCode = "200", description = "Cupom atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    public ResponseEntity<ApiResponseDTO<CouponResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody CouponUpdateDTO request) {
        CouponResponseDTO coupon = couponService.update(id, request);
        return ResponseEntity.ok(ApiResponseDTO.success(200, "Cupom atualizado com sucesso", coupon));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um cupom (Soft Delete)", responses = {
            @ApiResponse(responseCode = "200", description = "Cupom deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao deletar")
    })
    public ResponseEntity<ApiResponseDTO<?>> delete(@PathVariable Long id) {
        couponService.delete(id);
        return ResponseEntity.ok(ApiResponseDTO.success(200, "Cupom deletado com sucesso", null));
    }
}
