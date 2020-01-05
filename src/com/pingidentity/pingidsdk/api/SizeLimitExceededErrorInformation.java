package com.pingidentity.pingidsdk.api;

public class SizeLimitExceededErrorInformation extends ErrorInformation{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double maxValue;

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public SizeLimitExceededErrorInformation(double maxValue) {
		super();
		this.maxValue = maxValue;
	}

	public SizeLimitExceededErrorInformation() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		SizeLimitExceededErrorInformation that = (SizeLimitExceededErrorInformation) o;

		return Double.compare(that.maxValue, maxValue) == 0;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(maxValue);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("SizeLimitExceededErrorInformation{");
		sb.append("maxValue=").append(maxValue);
		sb.append('}');
		return sb.toString();
	}
}
