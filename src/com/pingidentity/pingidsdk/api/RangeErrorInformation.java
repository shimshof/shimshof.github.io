package com.pingidentity.pingidsdk.api;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class RangeErrorInformation extends ErrorInformation{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double rangeMinimumValue;
	private double rangeMaximumValue;
	
	public RangeErrorInformation(double rangeMinimumValue, double rangeMaximumValue) {
		super(null);
		this.rangeMinimumValue = rangeMinimumValue;
		this.rangeMaximumValue = rangeMaximumValue;
	}

	public RangeErrorInformation() {
	}

	public double getRangeMinimumValue() {
		return rangeMinimumValue;
	}
	public void setRangeMinimumValue(double rangeMinimumValue) {
		this.rangeMinimumValue = rangeMinimumValue;
	}
	public double getRangeMaximumValue() {
		return rangeMaximumValue;
	}
	public void setRangeMaximumValue(double rangeMaximumValue) {
		this.rangeMaximumValue = rangeMaximumValue;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		RangeErrorInformation that = (RangeErrorInformation) o;

		if (Double.compare(that.rangeMinimumValue, rangeMinimumValue) != 0) return false;
		return Double.compare(that.rangeMaximumValue, rangeMaximumValue) == 0;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(rangeMinimumValue);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(rangeMaximumValue);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("RangeErrorInformation{");
		sb.append("rangeMinimumValue=").append(rangeMinimumValue);
		sb.append(", rangeMaximumValue=").append(rangeMaximumValue);
		sb.append('}');
		return sb.toString();
	}
}
