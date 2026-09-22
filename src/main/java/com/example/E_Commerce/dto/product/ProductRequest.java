package com.example.E_Commerce.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @Size(max = 20, message = "Product name cannot be empty!")
    @NotBlank
    private String name;
    @Size(max = 200, message = "Product description cannot be empty!")
    private String description;

    @NotNull(message = "price cannot be empty")
    private BigDecimal price;

    @NotNull(message = "price cannot be empty")
    @Min(value = 0, message = "price cannot be negative!")
    private Integer stock;
    private String imageUrl;
    @NotNull(message = "Category ID id required!")
    private Long categoryId;
}
