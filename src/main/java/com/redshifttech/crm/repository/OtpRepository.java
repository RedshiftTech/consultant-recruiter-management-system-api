package com.redshifttech.crm.repository;

import com.redshifttech.crm.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findTopByEmailAndOtpAndUsedFalseOrderByIdDesc(
            String email,
            String otp
    );
    List<Otp> findByEmailAndUsedFalse(String email);
}
