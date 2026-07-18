package com.epiis.finalproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FinalprojectApplicationTests {

	@Test
	void testGetStatuses() {
		java.util.List<String> list = java.util.Arrays.stream(com.epiis.finalproject.staticdata.EnumAcademicPeriod.values())
				.map(com.epiis.finalproject.staticdata.EnumAcademicPeriod::toString)
				.collect(java.util.stream.Collectors.toList());
		System.out.println("STATUSES LIST: " + list);
	}

}
