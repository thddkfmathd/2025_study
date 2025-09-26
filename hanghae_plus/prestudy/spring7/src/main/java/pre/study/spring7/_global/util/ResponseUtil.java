package pre.study.spring7._global.util;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUtil<T> {  // 해당 클래스 제네릭 사용 이유/ 장단점
	private boolean success;
    private String status; //    
    private int code; //    
    private String message;
    private T data;  
    private long timestamp;// Instant.now().toEpochMilli();

    public ResponseUtil(boolean result, String message, List<Map<String, Object>> data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }
    
    private static Map<String, Object> body(boolean success, String code, String message, Object data) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", success);
        m.put("code", code);
        m.put("message", message);
        m.put("data", data);
        m.put("ts", Instant.now().toEpochMilli());
        return m;
    }
    
    public static <T> ResponseUtil<T> success(T item) {
        return new ResponseUtil<>(true, "Success", item);
    }

    public static <T> ResponseUtil<T> success(String message, T item) {
        return new ResponseUtil<>(true, message, item);
    }

    public static <T> ResponseUtil<T> failure(String message) {
        return new ResponseUtil<>(false, message, null);
    }
    
    public static <T> ResponseUtil<T> datatables(List<Map<String, Object>> data) {
        return new ResponseUtil<>(data != null, "", data, data.size(), data.size());
    }
}
