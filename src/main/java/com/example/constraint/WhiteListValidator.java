package com.example.constraint;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WhiteListValidator implements ConstraintValidator<WhiteListUrl, String> {
	@Override
	public void initialize(WhiteListUrl annotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		Map<String, Boolean> whiteList = new HashMap<>();
		whiteList.put("eric.com", Boolean.TRUE);
		whiteList.put("sean.com", Boolean.FALSE);

		if (whiteList.containsKey(value)) {
			return true;
		}

		int fromIndex = value.length()-1;
		int index;
		do {
			index = value.lastIndexOf('.', fromIndex);
			if (index != -1) {
				String domain = value.substring(index + 1);
				if (whiteList.containsKey(domain) && whiteList.get(domain) == Boolean.TRUE) {
					return true;
				}
			}
			fromIndex = index;
		} while (fromIndex != -1);
		return false;

	}

}
