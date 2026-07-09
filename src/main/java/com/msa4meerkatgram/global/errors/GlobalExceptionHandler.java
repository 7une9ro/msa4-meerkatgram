package com.msa4meerkatgram.global.errors;

import com.msa4meerkatgram.global.errors.constant.CustomErrorCode;
import com.msa4meerkatgram.global.errors.custom.*;
import com.msa4meerkatgram.global.responses.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 공통 모듈화
    private ResponseEntity<BaseErrorResponse> globalErrorResponse(CustomErrorCode customErrorCode) {
        return ResponseEntity.status(customErrorCode.getHttpStatus())
                .body(BaseErrorResponse.from(customErrorCode.getCode(), customErrorCode.name()));
    }

    @ExceptionHandler(NotRegisteredException.class)
    public ResponseEntity<BaseErrorResponse> notRegisteredHandle(NotRegisteredException e) {
        log.debug(CustomErrorCode.NOT_REGISTERED_ERROR.name(), e);
        return this.globalErrorResponse(CustomErrorCode.NOT_REGISTERED_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseErrorResponse> authenticationHandle(AuthenticationException e) {
        log.debug(CustomErrorCode.UNAUTHENTICATED_ERROR.name(), e);
        return this.globalErrorResponse(CustomErrorCode.UNAUTHENTICATED_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseErrorResponse> accessDeniedHandle(AccessDeniedException e) {
        log.debug(CustomErrorCode.UNAUTHORIZED_ERROR.name(), e);
        return this.globalErrorResponse(CustomErrorCode.UNAUTHORIZED_ERROR);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<BaseErrorResponse> invalidTokenHandle(InvalidTokenException e) {
        log.debug(CustomErrorCode.INVALID_TOKEN_ERROR.name(), e);
        return this.globalErrorResponse(CustomErrorCode.INVALID_TOKEN_ERROR);
    }

    @ExceptionHandler(NotExistPostException.class)
    public ResponseEntity<BaseErrorResponse> notExistPostHandle(NotExistPostException e) {
        log.debug(CustomErrorCode.NOT_FOUND_DATA_ERROR.name(), e);
        return this.globalErrorResponse(CustomErrorCode.NOT_FOUND_DATA_ERROR);
    }

    @ExceptionHandler(DuplicatedRecordException.class)
    public ResponseEntity<BaseErrorResponse> duplicatedRecordHandle(DuplicatedRecordException e) {
        log.debug(CustomErrorCode.DUPLICATED_DATA_ERROR.name(), e);
        return this.globalErrorResponse(CustomErrorCode.DUPLICATED_DATA_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseErrorResponse> methodArgumentTypeMismatchHandle(MethodArgumentTypeMismatchException e) {
        log.debug(CustomErrorCode.INVALID_PARAMETER_ERROR.name(), String.format("%s : 필드를 확인해 주세요.", e.getName()));
        return this.globalErrorResponse(CustomErrorCode.INVALID_PARAMETER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseErrorResponse> methodArgumentNotValidHandle(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField, // 필드명
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "유효하지 않은 값입니다.",
                        (existing, replacement) -> existing // 중복 필드가 있을 경우 기존 값 유지
                ));

        log.debug(CustomErrorCode.INVALID_PARAMETER_ERROR.name(), errors);
        return this.globalErrorResponse(CustomErrorCode.INVALID_PARAMETER_ERROR);
    }

    @ExceptionHandler(FileManagedException.class)
    public ResponseEntity<BaseErrorResponse> fileManagedHandle(FileManagedException e) {
        log.debug(CustomErrorCode.FILE_MANAGED_ERROR.name(), e);
        return this.globalErrorResponse(CustomErrorCode.FILE_MANAGED_ERROR);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<BaseErrorResponse> sqlHandle(SQLException e) {
        log.error("DB 에러: ", e);
        return this.globalErrorResponse(CustomErrorCode.DB_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseErrorResponse> othersHandle(Exception e) {
        log.error("시스템 에러: ", e);
        return this.globalErrorResponse(CustomErrorCode.SYSTEM_ERROR);
    }
}
