package com.example.constraint;

import org.junit.Test;

import com.google.common.primitives.Ints;

public class UserIdConstraintTest {

	private int[] badIds = { -1,0};
	private int[] goodIds = {1,2,3,4, 1000000};

	private static class TestClass {
		@UserIdConstraint
		private int x;

		TestClass(int p) {
			this.x = p;
		}
		
	}

	public void testIds(int[] ids, int errors) {
		Ints.asList(ids).stream().map(TestClass::new)
				.forEach(o -> ValidationTestUtils.assertNumberOfViolationsOnObject(o, errors));
	}

	@Test
	public void testGoodIds() {
		testIds(goodIds, 0);
	}

	@Test
	public void testBadIds() {
		testIds(badIds, 1);
	}
}
