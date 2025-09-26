package pre.study.spring7;

import org.springframework.boot.SpringApplication;

public class TestSpring7Application {

	public static void main(String[] args) {
		SpringApplication.from(Spring7Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
