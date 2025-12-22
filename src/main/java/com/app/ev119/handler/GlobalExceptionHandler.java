package com.app.ev119.handler;

import com.app.ev119.domain.dto.ApiResponseDTO;
import com.app.ev119.exception.FirstAidException;
import com.app.ev119.exception.MemberException;
import com.app.ev119.exception.MyPageException;
import jakarta.security.auth.message.AuthException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler({
            RedisConnectionFailureException.class,
            RedisSystemException.class
    })
    public ResponseEntity<ApiResponseDTO<Object>> handleRedisDown(Exception e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDTO.of("현재 인증/세션 서비스(REDIS)가 불안정합니다. 잠시 후 다시 시도해주세요."));
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ApiResponseDTO> handleException(IllegalAccessException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseDTO.of(e.getMessage()));
    }

    @ExceptionHandler(FirstAidException.class)
    public ResponseEntity<ApiResponseDTO> handleMyTestException(FirstAidException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseDTO.of(e.getMessage()));
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleMemberException(MemberException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseDTO.of(e.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleAuthException(AuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDTO.of(e.getMessage()));
    }

    @ExceptionHandler(MyPageException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleMyPageException(MyPageException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseDTO.of(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleException(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseDTO.of(e.getMessage()));
    }
}
