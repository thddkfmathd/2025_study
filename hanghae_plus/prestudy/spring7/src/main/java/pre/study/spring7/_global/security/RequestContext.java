package pre.study.spring7._global.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ThreadLocal 보관소: "현재 요청을 처리 중인 스레드" 전용 context
 * - set() : 필터에서 한 번 세팅 , get() : 어디서나 조회
 * - clear(): finally에서 반드시 제거 (스레드풀 재사용으로 인한 누수 방지)
 *    => userContext와 RequestContext구조 사용으로 테스트시 context정리 유의
 *    => *****스케줄러에는 적용하기 어려움
 *    => ***** context정리위한 테스트용 유틸메서드 만들어서 해결? /interface?/RequestContext의존최소화?
 * 
 */

public class RequestContext {
	private RequestContext() {} //
	private static final ThreadLocal<UserContext> CTX = ThreadLocal.withInitial(() -> null); //
	
	public static void set(UserContext c) { CTX.set(c); }
	public static void set(String userId, String userName, String role) { 
		CTX.set(new UserContext(userId, userName, role)); 
	}
	
	public static UserContext get() { return CTX.get(); }
	public static String getUserId() { return Optional.ofNullable(CTX.get()).map(UserContext::userId).orElse(null); }
    public static String getUserName() { return Optional.ofNullable(CTX.get()).map(UserContext::userName).orElse(null); }
    public static String getRole() { return Optional.ofNullable(CTX.get()).map(UserContext::role).orElse(null); }

    // 디버깅용
    public static Map<String, Object> asMap() {
        Map<String, Object> m = new HashMap<>();
        UserContext c = CTX.get();
        m.put("user_id", c != null ? c.userId() : null);
        m.put("user_name", c != null ? c.userName() : null);
        m.put("user_role", c != null ? c.role() : null);
        return m;
    }
    
    /* !!!!! 정리시점에 반드시 호출!!!!! */
    public static void clear() { CTX.remove(); }
}
