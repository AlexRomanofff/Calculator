package com.calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.IContextProvider;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.validation.validator.PatternValidator;

public class Calculator extends WebPage{	
	
	private static final long serialVersionUID = 2L;

	private final String ZERO = "0";
	private final String ERROR = "err";	
	private final String MINUS = "-";
	
	private Form<?> form;
	private TextField<String> screenValue; 
	private Operation operation;
	private String firstValue;
	private String secondValue;	
	private boolean isPointed;
	private boolean isSigned;
	private boolean isAdded;	
	
	public Calculator() {	
				
		form = new Form<String>("form");
		
		add(new Label("lbl", new Model<String>("Calculator")));
		screenValue = new TextField<String>("value",  new Model<String>(ZERO));
		screenValue.setOutputMarkupId(true);
		form.add(screenValue);	
	
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
					target.add(screenValue);
				}
			});
			
		}
	}

	
	private void initSignsButtons() {

		for (final Operation op: Operation.values()) {

			form.add(new AjaxButton(op.toString()) {

				public void onSubmit(AjaxRequestTarget target, Form form) {

					setMathOperation(op);
					target.add(screenValue);
				}

				private void setMathOperation(final Operation op) {
					String num = screenValue.getInput();
					if (num.equals(ERROR)) {
						setDefault();
						setValueToZero();
						num = ZERO;				
					}

					getMathOperand(op, num);
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
					String textValue = screenValue.getInput();
					textValue = setPlusMinus(textValue);					
					setValueOnScreen(textValue);
					target.add(screenValue);					
				}
			}
		});			
	}

	private void initBackspace() {
		
		form.add(new AjaxButton("backspace") {
			
			public void onSubmit(AjaxRequestTarget target, Form form) {
				if (isAdded) {				
				
					String textValue = screenValue.getInput();
					textValue = deleteOneDigit(textValue);
					setValueOnScreen(textValue);				
					target.add(screenValue);
				}				
			}
		});
	}

	private void initClear() {
		
		form.add(new AjaxButton("clear") {
			
			public void onSubmit(AjaxRequestTarget target, Form form) {
				setDefault();
				setValueToZero();
				target.add(screenValue);
			}
		});
	}
	
	private void initEqual() {

		form.add(new AjaxButton("equal") {
			public void onSubmit(AjaxRequestTarget target, Form form) {
				
				if (isSigned) {
					secondValue = screenValue.getInput();
					isSigned = false;
					getResult();
					setDefault();		
					target.add(screenValue);
				}
			}
		});
	}
	
	private void initPoint() {
		
		form.add(new AjaxButton("point") {
			public void onSubmit(AjaxRequestTarget target, Form form) {
				
				if(!isPointed) {
				setValueOnScreen(screenValue.getInput()+".");
				isPointed = true;
				target.add(screenValue);
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
		
		String value = this.screenValue.getInput();
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
		
		this.screenValue.setModelObject(value);
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

	