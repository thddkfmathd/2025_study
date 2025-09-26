package pre.study.spring7._global.security;

/**
 * 요청단위로 user data 보관 컨텍스트 dto
 * 불변데이터예상, record 사용
 * 어떤 레이어 내에서든 RequestContext로 간단 조회
 *  
 * */

public record UserContext(String userId, String userName, String role) {
}
