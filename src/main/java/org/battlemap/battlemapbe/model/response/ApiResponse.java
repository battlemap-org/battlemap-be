package org.battlemap.battlemapbe.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private String result;  // "Success" / "Fail"
    private int status;
    private T success;
    private ErrorResponse error;

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String errorCode;
        private String message;
    }

    // 성공 응답
    public static <T> ApiResponse<T> success(T data, int status) {
        return ApiResponse.<T>builder()
                .result("Success")
                .status(status)
                .success(data)
                .error(null)
                .build();
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(String code, String message, int status) {
        return ApiResponse.<T>builder()
                .result("Fail")
                .status(status)
                .success(null)
                .error(new ErrorResponse(code, message))
                .build();
    }
}
