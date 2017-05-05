package com.calculator;

import java.math.BigDecimal;

public enum Operation {

	plus {
		@Override
		BigDecimal eval(String a, String b) {
			return convertToBigDecimal(a).add(convertToBigDecimal(b));
		}
	},

	minus {
		@Override
		BigDecimal eval(String a, String b) {
			return convertToBigDecimal(a).subtract(convertToBigDecimal(b));
		}
	},

	multiply {
		@Override
		BigDecimal eval(String a, String b) {
			return convertToBigDecimal(a).multiply(convertToBigDecimal(b));
		}
	},

	divide {
		@Override
		BigDecimal eval(String a, String b) {
			if (b.equals("0"))
				return null;
			return convertToBigDecimal(a).divide(convertToBigDecimal(b), 6, BigDecimal.ROUND_HALF_UP);
		}
	};

	abstract BigDecimal eval(String a, String b);

	private static BigDecimal convertToBigDecimal(String a) {
		return new BigDecimal(a);
	}
}
