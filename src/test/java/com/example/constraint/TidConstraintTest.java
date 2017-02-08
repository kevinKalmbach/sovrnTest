package com.example.constraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

public class TidConstraintTest  {

	private String[] badIds = { null, "", "1"};

	private static class TestClass {
		@TidConstraint
		private String x;

		TestClass(String x) {
			this.x = x;
		}
	}


	public void testIds(Collection<String> ids, int errors) {
		ids.stream().map(TestClass::new)
				.forEach(o -> ValidationTestUtils.assertNumberOfViolationsOnObject(o, errors));
	}

	public void testIds(String[] ids, int errors) {
		testIds(Arrays.asList(ids),errors);
	}

	@Test
	public void testGoodIds() {
		List<String> goodIds = new ArrayList<>();
		for (int x=0;x < 10; x++) {
			goodIds.add(UUID.randomUUID().toString());
		}		
		testIds(goodIds, 0);
	}

	@Test
	public void testBadIds() {
		testIds(badIds, 1);
	}
	
	@Test
	public void testBadIds2() {
		String badId = UUID.randomUUID().toString();
		badId.replace('-', '!');
		List<String> badIds = new ArrayList<>();
		
		testIds(badIds, 1);
	}
}
