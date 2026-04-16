package com.example.coupon.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private T data;
    private Object errors;

    public static <T> ApiResponseDTO<T> success(int status, String message, T data) {
        return ApiResponseDTO.<T>builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static ApiResponseDTO<?> error(int status, String message, Object errors) {
        return ApiResponseDTO.builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }
}
