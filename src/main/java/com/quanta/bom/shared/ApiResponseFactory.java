package com.quanta.bom.shared;

public final class ApiResponseFactory {

    private ApiResponseFactory() {
    }

    public static <T> ApiResponseOut<T> ok(T data) {
        return new ApiResponseOut<>(data, true, null);
    }

    public static ApiResponseOut<Void> error(String message) {
        return new ApiResponseOut<>(null, false, message);
    }
}
