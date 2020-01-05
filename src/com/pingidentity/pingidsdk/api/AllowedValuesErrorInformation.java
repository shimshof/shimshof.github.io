package com.pingidentity.pingidsdk.api;

import java.util.List;

public class AllowedValuesErrorInformation extends ErrorInformation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> allowedValues;

	public List<String> getAllowedValues() {
		return allowedValues;
	}

	public void setAllowedValues(List<String> allowedValues) {
		this.allowedValues = allowedValues;
	}

	public AllowedValuesErrorInformation(List<String> allowedValues) {
		super();
		this.allowedValues = allowedValues;
	}

	public AllowedValuesErrorInformation() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		AllowedValuesErrorInformation that = (AllowedValuesErrorInformation) o;

		return allowedValues != null ? allowedValues.equals(that.allowedValues) : that.allowedValues == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (allowedValues != null ? allowedValues.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AllowedValuesErrorInformation{");
		sb.append("allowedValues=").append(allowedValues);
		sb.append('}');
		return sb.toString();
	}
}
