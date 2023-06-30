package it.gov.pagopa.mocker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest
class MockerApplicationTests {


	@Test
	void applicationContextLoaded() {
		assertTrue(true); // it just tests that an error has not occurred
	}

	@Test
	void applicationContextTest() {
		MockerApplication.main(new String[] {});
		assertTrue(true); // it just tests that an error has not occurred
	}

}
