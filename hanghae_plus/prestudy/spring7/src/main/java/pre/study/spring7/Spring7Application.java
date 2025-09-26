package pre.study.spring7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan 
//@ConfigurationProperties 사용위함 (+ @ConfigurationPropertiesScan) : yml에서 외부설정값으로 가져옴
public class Spring7Application {

	public static void main(String[] args) {
		SpringApplication.run(Spring7Application.class, args);
	}

}
