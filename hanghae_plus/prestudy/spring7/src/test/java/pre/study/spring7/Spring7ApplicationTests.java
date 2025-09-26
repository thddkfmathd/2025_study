package pre.study.spring7;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class Spring7ApplicationTests {

	@Test
	void contextLoads() {
	}

}
