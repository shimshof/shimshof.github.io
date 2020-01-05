package com.pingidentity.pingidsdk.api;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PatternErrorInformation extends ErrorInformation{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String allowedPattern;
	
	public String getAllowedPattern() {
		return allowedPattern;
	}

	public void setAllowedPattern(String allowedPattern) {
		this.allowedPattern = allowedPattern;
	}

	public PatternErrorInformation(String allowedPattern) {
		super(null);
		this.allowedPattern = allowedPattern;
	}

	public PatternErrorInformation() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		PatternErrorInformation that = (PatternErrorInformation) o;

		return allowedPattern != null ? allowedPattern.equals(that.allowedPattern) : that.allowedPattern == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (allowedPattern != null ? allowedPattern.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PatternErrorInformation{");
		sb.append("allowedPattern='").append(allowedPattern).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
