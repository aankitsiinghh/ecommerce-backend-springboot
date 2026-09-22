package com.example.E_Commerce.service.impl;

import com.example.E_Commerce.dto.review.ReviewRequest;
import com.example.E_Commerce.dto.review.ReviewResponse;
import com.example.E_Commerce.exception.ReviewNotFoundException;
import com.example.E_Commerce.model.Product;
import com.example.E_Commerce.model.Review;
import com.example.E_Commerce.model.User;
import com.example.E_Commerce.repository.ProductRepository;
import com.example.E_Commerce.repository.ReviewRepository;
import com.example.E_Commerce.repository.UserRepository;
import com.example.E_Commerce.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, Long id) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ReviewNotFoundException("Product not found with " + request.getProductId()));

        User user =  userRepository.findById(id)
                .orElseThrow(()-> new ReviewNotFoundException("User not found with " + id));

        reviewRepository.findByProductIdAndUserId(request.getProductId(), id)
                .ifPresent(r -> {throw new IllegalStateException("You have already reviewed this product"); });

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest request, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        if (!review.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only update your own review");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReview(Long id, Long userId, boolean isAdmin) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        if (!isAdmin && !review.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only delete your own review");
        }

        reviewRepository.delete(review);

    }

    @Override
    public Page<ReviewResponse> getReviewsByProduct(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::mapToResponse);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .userId(review.getUser().getId())
                .username(review.getUser().getEmail())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
