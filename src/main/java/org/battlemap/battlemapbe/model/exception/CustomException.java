package org.battlemap.battlemapbe.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
// 모든 비즈니스 예외는 이 클래스를 상속받아 사용
public class CustomException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    // 생성자를 통해 에러 코드, 메시지, HTTP 상태 코드를 받음
    public CustomException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}