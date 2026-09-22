package com.example.E_Commerce.repository;

import com.example.E_Commerce.model.Review;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByProductIdAndUserId(@NotNull Long productId, Long id);

   Page<Review> findByProductId(Long productId, Pageable pageable);
}
