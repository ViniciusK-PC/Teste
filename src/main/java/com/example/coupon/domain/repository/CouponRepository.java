package com.example.coupon.domain.repository;

import com.example.coupon.domain.model.Coupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends MongoRepository<Coupon, Long> {
    
    Optional<Coupon> findByCodeAndDeletedFalse(String code);
    
    List<Coupon> findAllByDeletedFalse();
    
    Optional<Coupon> findByIdAndDeletedFalse(Long id);
}
