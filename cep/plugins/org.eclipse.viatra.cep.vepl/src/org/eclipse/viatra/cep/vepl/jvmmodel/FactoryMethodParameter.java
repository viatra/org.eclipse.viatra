package org.eclipse.viatra.cep.vepl.jvmmodel;

public enum FactoryMethodParameter {
	EMPTY(""), NULL("null"), EVENTSOURCE("eventSource");

	private String parameterName;

	FactoryMethodParameter(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getLiteral() {
		return parameterName;
	}
}
