package com.malgn.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class CustomResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;

    private CustomResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> CustomResponse<T> success(T data) {
        return new CustomResponse<>(true, data, null);
    }

    public static <T> CustomResponse<T> failure(String message) {
        return new CustomResponse<>(false, null, message);
    }

}
