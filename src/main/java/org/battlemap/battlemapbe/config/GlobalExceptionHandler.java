package org.battlemap.battlemapbe.config;

import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

// 모든 컨트롤러에서 발생하는 예외를 JSON 응답으로 처리
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 사용자 정의 비즈니스 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException ex) {

        int status = ex.getHttpStatus().value();

        // CustomException의 HttpStatus와 ErrorCode를 사용하여 ApiResponse 생성
        ApiResponse<?> errorResponse = ApiResponse.error(
                ex.getErrorCode(),
                ex.getMessage(),
                status
        );

        // CustomException HTTP 상태 코드 반환
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    // 서버 오류 500 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        int status = httpStatus.value();

        ApiResponse<?> errorResponse = ApiResponse.error(
                "SERVER_500",
                "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
                status
        );

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    // 404 오류 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFoundException(NoHandlerFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("ROUTE_404", "요청하신 경로를 찾을 수 없습니다.", 404));
    }
}