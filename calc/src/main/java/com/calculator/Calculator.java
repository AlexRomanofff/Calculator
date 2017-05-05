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

	private final String ZERO = "0";
	private final String ERROR = "err";	
	private final String MINUS = "-";
	
	private FunctionOfCalculator function;
	
	private Form<?> form;
	private TextField<String> value; 
	private Operation operation;
	private String firstValue;
	private String secondValue;	
	private boolean isPointed;
	private boolean isSigned;
	private boolean isAdded;	
	
	public Calculator() {		 
				
		form = new Form<String>("form");
		function = new FunctionOfCalculator();
		add(new Label("lbl", new Model<String>("Calculator")));
		add(new Label("lab", new Model<String>("To start the application press \"C\" button ")));				
		value = new TextField<String>("value",  new Model<String>(ZERO));		
		form.add(value);		
		setDefault();
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

		for (final Operation op: Operation.values()) {

			form.add(new AjaxButton(op.toString()) {

				public void onSubmit(AjaxRequestTarget target, Form form) {

					String num = value.getInput();
					if (num.equals(ERROR)) {
						setDefault();
						setValueToZero();
						num = ZERO;
						target.add(value);
					}

					getMathOperand(op, num);
					target.add(value, "value");
				}

				private void getMathOperand(Operation op, String num) {
					
					if (!isSigned) {
						isPointed = false;
						isAdded = false;
						isSigned = true;
						firstValue = num;
					} else {
						secondValue = num;
						getResult();
					}
					operation = op;
				}

			});
		}
	}
	
	private void initPlusMinus() {

		form.add(new AjaxButton("plusMinus") {

			public void onSubmit(AjaxRequestTarget target, Form form) {
				
				if (isAdded) {
					String textValue = value.getInput();
					textValue = setPlusMinus(textValue);
				
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
					textValue = deleteOneDigit(textValue);
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

		BigDecimal evaluation = operation.eval(firstValue, secondValue);
		String result = null;
		
		if (evaluation == null) {
			result = ERROR;
		} else {
			result = operation.eval(firstValue, secondValue).stripTrailingZeros().toPlainString();			
			this.firstValue = result;
			isPointed = true;
			isAdded = false;
		}
		setValueOnScreen(result);
	}
	
	private void setValueToZero() {
		setValueOnScreen(ZERO);
	}
	
	private String setValuesForNumbers(String s) {
		
		String value = this.value.getInput();
		if (isAdded) {
			if (value.equals(ZERO)) {
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
	
	private String deleteOneDigit(String value) {
		
		if(value.length()>1) {
		value = value.substring(0, value.length() - 1);
		} else if (value.length()==1) value = ZERO;
		return value;
	}
	
	private String setPlusMinus(String value) {
		
		if (value.startsWith(MINUS)) {
			value = value.substring(1);
		} else {
			value = MINUS + value;
		}
		return value;
	}
	
}

	