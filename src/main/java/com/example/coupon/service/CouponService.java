package com.example.coupon.service;

import com.example.coupon.domain.model.Coupon;
import com.example.coupon.domain.repository.CouponRepository;
import com.example.coupon.dto.request.CouponRequestDTO;
import com.example.coupon.dto.request.CouponUpdateDTO;
import com.example.coupon.dto.response.CouponResponseDTO;
import com.example.coupon.exception.BusinessException;
import com.example.coupon.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {


    private final CouponRepository couponRepository;

    /**
     * Cria um novo cupom. 
     * @Transactional garante que se algo der errado, nada sera salvo no banco (Atomicidade).
     */
    @Transactional
    public CouponResponseDTO create(CouponRequestDTO request) {
        log.info("Iniciando criacao de cupom: {}", request.getCode());
        
        validateUniqueness(request.getCode());

        Coupon coupon = Coupon.create(
                request.getCode(),
                request.getDiscountValue(),
                request.getExpirationDate()
        );

        // Geracao de ID manual para MongoDB (Simulando auto-incremento)
        coupon.setId(System.currentTimeMillis());

        coupon = couponRepository.save(coupon);
        log.info("Cupom {} criado com sucesso no MongoDB", coupon.getCode());
        
        return CouponResponseDTO.fromEntity(coupon);
    }

    @Transactional(readOnly = true)
    public List<CouponResponseDTO> findAll() {
        return couponRepository.findAllByDeletedFalse().stream()
                .map(CouponResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CouponResponseDTO findById(Long id) {
        return couponRepository.findByIdAndDeletedFalse(id)
                .map(CouponResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Cupom nao encontrado com o ID: " + id));
    }

    @Transactional
    public CouponResponseDTO update(Long id, CouponUpdateDTO request) {
        log.info("Atualizando dados do cupom ID: {}", id);
        Coupon coupon = couponRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupom nao encontrado para atualizacao"));

        String sanitizedNewCode = Coupon.sanitizeCode(request.getCode());
        if (!coupon.getCode().equals(sanitizedNewCode)) {
            validateUniqueness(request.getCode());
        }

        coupon.update(request.getCode(), request.getDiscountValue(), request.getExpirationDate());
        
        return CouponResponseDTO.fromEntity(couponRepository.save(coupon));
    }

    @Transactional
    public void delete(Long id) {
        log.info("Solicitando soft delete para o ID: {}", id);
        Coupon coupon = couponRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nao e possivel deletar um cupom inexistente"));
        
        // No MongoDB implementamos o soft delete manualmente chamando o metodo de exclusao logica
        coupon.delete();
        couponRepository.save(coupon);
        log.info("Soft delete realizado com sucesso para ID: {}", id);
    }

    private void validateUniqueness(String code) {
        String sanitized = Coupon.sanitizeCode(code);
        if (couponRepository.findByCodeAndDeletedFalse(sanitized).isPresent()) {
            throw new BusinessException("Ja existe um cupom ativo com o codigo: " + sanitized);
        }
    }
}
