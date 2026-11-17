package com.quanta.bom.shared;

public record ApiResponseOut<T>(T data, boolean success, String message) {
}
