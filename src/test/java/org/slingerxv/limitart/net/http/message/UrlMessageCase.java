package org.slingerxv.limitart.net.http.message;

import org.slingerxv.limitart.net.http.constant.QueryMethod;

public class UrlMessageCase extends UrlMessage {
	public byte byteVal;
	public short shortVal;
	public int intVal;
	public long longVal;
	public float floatVal;
	public double doubleVal;
	public char charVal;
	public boolean boolVal;
	public Byte byteBoxVal;
	public Short shortBoxVal;
	public Integer intBoxVal;
	public Long longBoxVal;
	public Float floatBoxVal;
	public Double doubleBoxVal;
	public Character charBoxVal;
	public Boolean boolBoxVal;
	public String strVal;
	public String strValNull;

	public void init() {
		byteVal = 1;
		shortVal = 2;
		intVal = 3;
		longVal = 4l;
		floatVal = 5.02F;
		doubleVal = 234.2342D;
		charVal = 'C';
		boolVal = true;
		byteBoxVal = -1;
		shortBoxVal = -2;
		intBoxVal = -3;
		longBoxVal = -4l;
		floatBoxVal = -42.62F;
		doubleBoxVal = -235.26782D;
		charBoxVal = '/';
		boolBoxVal = false;
		strVal = "23lfc92lsajngf0cls;;2's2w09\'23;sdpo12123";
	}

	@Override
	public String getUrl() {
		return "/test";
	}

	@Override
	public QueryMethod getMethod() {
		return QueryMethod.POST;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((boolBoxVal == null) ? 0 : boolBoxVal.hashCode());
		result = prime * result + (boolVal ? 1231 : 1237);
		result = prime * result + ((byteBoxVal == null) ? 0 : byteBoxVal.hashCode());
		result = prime * result + byteVal;
		result = prime * result + ((charBoxVal == null) ? 0 : charBoxVal.hashCode());
		result = prime * result + charVal;
		result = prime * result + ((doubleBoxVal == null) ? 0 : doubleBoxVal.hashCode());
		long temp;
		temp = Double.doubleToLongBits(doubleVal);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((floatBoxVal == null) ? 0 : floatBoxVal.hashCode());
		result = prime * result + Float.floatToIntBits(floatVal);
		result = prime * result + ((intBoxVal == null) ? 0 : intBoxVal.hashCode());
		result = prime * result + intVal;
		result = prime * result + ((longBoxVal == null) ? 0 : longBoxVal.hashCode());
		result = prime * result + (int) (longVal ^ (longVal >>> 32));
		result = prime * result + ((shortBoxVal == null) ? 0 : shortBoxVal.hashCode());
		result = prime * result + shortVal;
		result = prime * result + ((strVal == null) ? 0 : strVal.hashCode());
		result = prime * result + ((strValNull == null) ? 0 : strValNull.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UrlMessageCase other = (UrlMessageCase) obj;
		if (boolBoxVal == null) {
			if (other.boolBoxVal != null)
				return false;
		} else if (!boolBoxVal.equals(other.boolBoxVal))
			return false;
		if (boolVal != other.boolVal)
			return false;
		if (byteBoxVal == null) {
			if (other.byteBoxVal != null)
				return false;
		} else if (!byteBoxVal.equals(other.byteBoxVal))
			return false;
		if (byteVal != other.byteVal)
			return false;
		if (charBoxVal == null) {
			if (other.charBoxVal != null)
				return false;
		} else if (!charBoxVal.equals(other.charBoxVal))
			return false;
		if (charVal != other.charVal)
			return false;
		if (doubleBoxVal == null) {
			if (other.doubleBoxVal != null)
				return false;
		} else if (!doubleBoxVal.equals(other.doubleBoxVal))
			return false;
		if (Double.doubleToLongBits(doubleVal) != Double.doubleToLongBits(other.doubleVal))
			return false;
		if (floatBoxVal == null) {
			if (other.floatBoxVal != null)
				return false;
		} else if (!floatBoxVal.equals(other.floatBoxVal))
			return false;
		if (Float.floatToIntBits(floatVal) != Float.floatToIntBits(other.floatVal))
			return false;
		if (intBoxVal == null) {
			if (other.intBoxVal != null)
				return false;
		} else if (!intBoxVal.equals(other.intBoxVal))
			return false;
		if (intVal != other.intVal)
			return false;
		if (longBoxVal == null) {
			if (other.longBoxVal != null)
				return false;
		} else if (!longBoxVal.equals(other.longBoxVal))
			return false;
		if (longVal != other.longVal)
			return false;
		if (shortBoxVal == null) {
			if (other.shortBoxVal != null)
				return false;
		} else if (!shortBoxVal.equals(other.shortBoxVal))
			return false;
		if (shortVal != other.shortVal)
			return false;
		if (strVal == null) {
			if (other.strVal != null)
				return false;
		} else if (!strVal.equals(other.strVal))
			return false;
		if (strValNull == null) {
			if (other.strValNull != null)
				return false;
		} else if (!strValNull.equals(other.strValNull))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UrlMessageCase [byteVal=" + byteVal + ", shortVal=" + shortVal + ", intVal=" + intVal + ", longVal="
				+ longVal + ", floatVal=" + floatVal + ", doubleVal=" + doubleVal + ", charVal=" + charVal
				+ ", boolVal=" + boolVal + ", byteBoxVal=" + byteBoxVal + ", shortBoxVal=" + shortBoxVal
				+ ", intBoxVal=" + intBoxVal + ", longBoxVal=" + longBoxVal + ", floatBoxVal=" + floatBoxVal
				+ ", doubleBoxVal=" + doubleBoxVal + ", charBoxVal=" + charBoxVal + ", boolBoxVal=" + boolBoxVal
				+ ", strVal=" + strVal + ", strValNull=" + strValNull + "]";
	}

}
