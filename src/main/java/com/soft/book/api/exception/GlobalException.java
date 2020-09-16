package com.soft.book.api.exception;

import com.soft.book.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalException {

    private static final Logger logger = LoggerFactory.getLogger(GlobalException.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handle(Exception e) {
        logger.error("Exception", e);
        if (e instanceof AuthException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.fail(e.getMessage(), new Object()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.fail("请求服务器错误"));
    }

}
