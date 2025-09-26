package pre.study.spring7._global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pre.study.spring7._global.util.ResponseUtil;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 예: IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseUtil<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
        		ResponseUtil.failure(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // 예: NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseUtil<Void>> handleNullPointerException(NullPointerException ex) {
        return new ResponseEntity<>(
        		ResponseUtil.failure("A null pointer error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    // 예상치 못한 예외 처리 -> Custom 된 Exception 처리
    @ExceptionHandler(CustomException.class)
	public <T> ResponseEntity<ResponseUtil<T>> handleCustomException(CustomException ex) {
    	return new ResponseEntity<>(
        		ResponseUtil.failure(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
	}
    // 기타 예상치 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseUtil<Void>> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(
        		ResponseUtil.failure(ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
