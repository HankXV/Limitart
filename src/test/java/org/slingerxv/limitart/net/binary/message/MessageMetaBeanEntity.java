package org.slingerxv.limitart.net.binary.message;

public class MessageMetaBeanEntity extends MessageMeta {
	public byte byteVal;
	public short shortVal;
	public int intVal;
	public long longVal;
	public float floatVal;
	public double doubleVal;
	public char charVal;
	public boolean boolVal;
	public String strVal;

	public void init() {
		byteVal = 13;
		shortVal = 2674;
		intVal = 1216523;
		longVal = 2352376l;
		floatVal = 323623.234f;
		doubleVal = 563476.236d;
		charVal = '-';
		boolVal = true;
		strVal = "34534fgjh4r5\'23;sdpo12123";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (boolVal ? 1231 : 1237);
		result = prime * result + byteVal;
		result = prime * result + charVal;
		long temp;
		temp = Double.doubleToLongBits(doubleVal);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Float.floatToIntBits(floatVal);
		result = prime * result + intVal;
		result = prime * result + (int) (longVal ^ (longVal >>> 32));
		result = prime * result + shortVal;
		result = prime * result + ((strVal == null) ? 0 : strVal.hashCode());
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
		MessageMetaBeanEntity other = (MessageMetaBeanEntity) obj;
		if (boolVal != other.boolVal)
			return false;
		if (byteVal != other.byteVal)
			return false;
		if (charVal != other.charVal)
			return false;
		if (Double.doubleToLongBits(doubleVal) != Double.doubleToLongBits(other.doubleVal))
			return false;
		if (Float.floatToIntBits(floatVal) != Float.floatToIntBits(other.floatVal))
			return false;
		if (intVal != other.intVal)
			return false;
		if (longVal != other.longVal)
			return false;
		if (shortVal != other.shortVal)
			return false;
		if (strVal == null) {
			if (other.strVal != null)
				return false;
		} else if (!strVal.equals(other.strVal))
			return false;
		return true;
	}

}
