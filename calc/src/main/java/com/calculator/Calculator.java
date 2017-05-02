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
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.validation.validator.PatternValidator;

public class Calculator extends WebPage{	
	
	private static final long serialVersionUID = 2L;	
	private final String MINUS = "-";
	private final String ZERO = "0";
	private TextField<String> value; 
	
	private Form<?> form = null;
	private String operation;	
	private String firstValue;
	private String secondValue;	
	private boolean isPointed;
	private boolean isSigned;
	private boolean isAdded;	
	
	public Calculator() {		 
				
		form = new Form<String>("form");
		add(new Label("lbl", new Model<String>("Calculator")));
		add(new Label("lab", new Model<String>("To start the application press \"C\" button ")));
		
		firstValue = ZERO;
		secondValue = ZERO;
		value = new TextField<String>("value",  new Model<String>(ZERO));
		
		form.add(value);
		initAllButtons();
		add(form);	
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
							
					setValueOnScreen(setValuesForNumbers(s));					
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
					target.add(value, "value");
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
				
					setValueOnScreen(textValue);
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
					setValueOnScreen(textValue);
				
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
				setValueOnScreen(value.getInput()+".");
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
		setValueOnScreen(result);
		isPointed = true;
		isAdded = false;
	}	
				
	private void setValueToZero() {
		setValueOnScreen(ZERO);
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

	private void setValueOnScreen(String value) {
		
		this.value.setModelObject(value);
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

	