package com.calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

public class Calculator extends WebPage{	
	
	private static final long serialVersionUID = 2L;	
	private final String MINUS = "-";
	private final String ZERO = "0";
	private final TextField<String> value = new TextField<String>("value",  Model.of(""));
	
	private Form<?> form = null;
	private String operation;	
	private String firstValue;
	private String secondValue;	
	private boolean isPointed;
	private boolean isSigned;
	private boolean isAdded;	
	
	public Calculator() {
		
		firstValue = ZERO;
		secondValue = ZERO;
		
		form = new Form<String>("form");
		add(new Label("lbl", new Model<String>("Calculator")));

		setValueToZero();
		form.add(value);
		initAllButtons();
		add(form);
		setResponsePage(getPage());
		
	}

	private void initAllButtons() {
		initDigitButtons();
		initSignsButtons();
		initPoint();
		initEqual();
		initClear();
		initBackspace();
		initPlusMinus();
	}
	
	private void initDigitButtons() {

		for (int i = 0; i <= 9; i++) {
			final String s = "" + i;
			form.add(new AjaxButton(s) {
				public void onSubmit(AjaxRequestTarget target, Form form) {
					value.setModelObject(setValuesForNumbers(s));
					target.add(value, "value");
				}
			});
		}
	}

	private void initSignsButtons() {

		List<String> signs = new ArrayList<>();
		signs.add("plus");
		signs.add("minus");
		signs.add("division");
		signs.add("multiply");

		for (final String sign : signs) {

			form.add(new AjaxButton(sign) {

				public void onSubmit(AjaxRequestTarget target, Form form) {

					String num = value.getInput();
					if (num.equals("err")) {
						setDefault();
						setValueToZero();
						target.add(value);
					}

					getMathOperand(sign, num);
					target.add(value);
				}

				private void getMathOperand(final String sign, String num) {
					
					if (!isSigned) {
						operation = sign;
						isPointed = false;
						isAdded = false;
						isSigned = true;
						firstValue = num;
					} else {
						secondValue = num;
						getResult();
						operation = sign;
					}
				}

			});
		}
	}

	private void initPlusMinus() {

		form.add(new AjaxButton("plusMinus") {

			public void onSubmit(AjaxRequestTarget target, Form form) {
				
				if (isAdded) {
					String textValue = value.getInput();
					if (textValue.startsWith(MINUS)) {
						textValue = textValue.substring(1);
					} else {
						textValue = MINUS + textValue;
					}
				
					value.setModelObject(textValue);
					target.add(value);
					
				}
			}
		});			
	}

	private void initBackspace() {
		
		form.add(new AjaxButton("backspace") {
			
			public void onSubmit(AjaxRequestTarget target, Form form) {
				if (isAdded) {
				
				
					String textValue = value.getInput();
					if(textValue.length()>1) {
					textValue = textValue.substring(0, textValue.length() - 1);
					} else if (textValue.length()==1) textValue = ZERO;
					value.setModelObject(textValue);
				
					target.add(value);
				}
				
			}
		});
	}

	private void initClear() {
		
		form.add(new Button("clear") {
			
			public void onSubmit() {
				setDefault();
				setValueToZero();
			}
		});
	}
	
	private void initEqual() {

		form.add(new AjaxButton("equal") {
			public void onSubmit(AjaxRequestTarget target, Form form) {
				
				if (isSigned) {
					secondValue = value.getInput();
					isSigned = false;
					getResult();
					setDefault();
					target.add(value);
				}
			}
		});
	}
	
	private void initPoint() {
		
		form.add(new AjaxButton("point") {
			public void onSubmit(AjaxRequestTarget target, Form form) {
				
				if(!isPointed) {
				value.setModelObject(value.getInput()+".");
				isPointed = true;
				target.add(value);
				}
			}
		});
	}
	
	private void setDefault() {
		
		firstValue = ZERO;
		secondValue = ZERO;
		isPointed = false;
		isSigned = false;
		isAdded = false;
	}	
	
	private void getResult() {
		String result = mathOperation(firstValue, secondValue);
		value.setModelObject(result);
		isPointed = true;
		isAdded = false;
	}	
				
	private void setValueToZero() {
		value.setDefaultModelObject(ZERO);
	}
	
	private String setValuesForNumbers(String s) {
		
		String value = this.value.getInput();
		if (isAdded) {
			if (value.equals("0")) {
				value = s;
			} else {
				value += s;
			}
		} else {
			value = s;
			isAdded = true;
		}
		return value;
	}

	private String mathOperation(String first, String second) {

		BigDecimal result = new BigDecimal("0");
		BigDecimal firstValue = new BigDecimal(first);
		BigDecimal secondValue = new BigDecimal(second);
		
		switch (operation) {

		case ("plus"):
			result = firstValue.add(secondValue);
			break;
			
		case ("minus"):
			result = firstValue.subtract(secondValue);
			break;
			
		case ("division"):
			if (secondValue.equals(result)) {
				setDefault();
				return "err";
			}
			result = firstValue.divide(secondValue, 6, BigDecimal.ROUND_HALF_UP);			
			break;
			
		case ("multiply"):
			result = firstValue.multiply(secondValue);
			break;
		}
		
		this.firstValue = result.stripTrailingZeros().toPlainString();

		return this.firstValue;
	}
}

	