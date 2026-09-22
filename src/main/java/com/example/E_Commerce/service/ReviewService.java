package com.example.E_Commerce.service;

import com.example.E_Commerce.dto.review.ReviewRequest;
import com.example.E_Commerce.dto.review.ReviewResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(@Valid ReviewRequest request, Long id);

    ReviewResponse updateReview(Long id, @Valid ReviewRequest request, Long id1);

    void deleteReview(Long id, Long id1, boolean isAdmin);

    Page<ReviewResponse> getReviewsByProduct(Long productId, Pageable pageable);
}
