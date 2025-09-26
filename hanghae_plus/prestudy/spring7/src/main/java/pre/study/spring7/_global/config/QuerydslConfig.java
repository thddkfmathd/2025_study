package pre.study.spring7._global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import jakarta.persistence.EntityManager;

// 사용여부 결정해야함
public class QuerydslConfig {
	@Autowired
    EntityManager em;

//	@Bean
//    public JPAQueryFactory jpaQueryFactory() {
//       return new JPAQueryFactory(em);
//    }
}
