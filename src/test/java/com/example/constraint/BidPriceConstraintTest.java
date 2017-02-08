package com.example.constraint;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class BidPriceConstraintTest {
	private String[] badIds = { "-1.0", "11.0", "100"};
	private String[] goodIds = { "1.0", "0.0", "9.9", "10.0"};

	private static class TestClass {
		@BidPriceConstraint
		private BigDecimal price;

		TestClass(BigDecimal p) {
			this.price = p;
		}
	}


	public void testIds(String[] ids, int errors) {
		Arrays.asList(ids).stream().map(BigDecimal::new).map(TestClass::new)
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
