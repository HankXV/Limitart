package org.slingerxv.limitart.net.binary.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slingerxv.limitart.util.RandomUtil;

public class MessageMetaEntity extends MessageMeta {
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
	public MessageMetaBeanEntity bean = new MessageMetaBeanEntity();
	public byte[] byteArrayNull;
	public byte[] byteArray = new byte[Short.MAX_VALUE];
	public short[] shortArray = new short[Short.MAX_VALUE];
	public int[] intArray = new int[Short.MAX_VALUE];
	public long[] longArray = new long[Short.MAX_VALUE];
	public float[] floatArray = new float[Short.MAX_VALUE];
	public double[] doubleArray = new double[Short.MAX_VALUE];
	public char[] charArray = new char[Short.MAX_VALUE];
	public boolean[] booleanArray = new boolean[Short.MAX_VALUE];
	public Byte[] byteBoxArray = new Byte[Short.MAX_VALUE];
	public Short[] shortBoxArray = new Short[Short.MAX_VALUE];
	public Integer[] intBoxArray = new Integer[Short.MAX_VALUE];
	public Long[] longBoxArray = new Long[Short.MAX_VALUE];
	public Float[] floatBoxArray = new Float[Short.MAX_VALUE];
	public Double[] doubleBoxArray = new Double[Short.MAX_VALUE];
	public Character[] charBoxArray = new Character[Short.MAX_VALUE];
	public Boolean[] booleanBoxArray = new Boolean[Short.MAX_VALUE];
	public String[] stringArray = new String[Short.MAX_VALUE];
	public MessageMetaBeanEntity[] beanArray = new MessageMetaBeanEntity[Short.MAX_VALUE];
	public List<Byte> byteListNull;
	public List<Byte> byteList = new ArrayList<>();
	public List<Short> shortList = new ArrayList<>();
	public List<Integer> intList = new ArrayList<>();
	public List<Long> longList = new ArrayList<>();
	public List<Float> floatList = new ArrayList<>();
	public List<Double> doubleList = new ArrayList<>();
	public List<Character> charList = new ArrayList<>();
	public List<Boolean> boolList = new ArrayList<>();
	public List<String> strList = new ArrayList<>();
	public List<MessageMetaBeanEntity> beanList = new ArrayList<>();

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
		bean = new MessageMetaBeanEntity();
		bean.init();
		for (int i = 0; i < byteArray.length; ++i) {
			byteArray[i] = (byte) RandomUtil.randomInt(1, 1000);
		}
		for (int i = 0; i < shortArray.length; ++i) {
			shortArray[i] = (short) RandomUtil.randomInt(1, 1000);
		}
		for (int i = 0; i < intArray.length; ++i) {
			intArray[i] = RandomUtil.randomInt(1, 1000);
		}
		for (int i = 0; i < longArray.length; ++i) {
			longArray[i] = RandomUtil.randomLong(235, 123135234234l);
		}
		for (int i = 0; i < floatArray.length; ++i) {
			floatArray[i] = RandomUtil.randomFloat(0.001f, 10.562f);
		}
		for (int i = 0; i < doubleArray.length; ++i) {
			doubleArray[i] = RandomUtil.randomFloat(0.001f, 10.562f);
		}
		for (int i = 0; i < charArray.length; ++i) {
			charArray[i] = (char) RandomUtil.randomInt(1, 1000);
		}
		for (int i = 0; i < booleanArray.length; ++i) {
			booleanArray[i] = RandomUtil.randomOne() == 1;
		}
		for (int i = 0; i < stringArray.length; ++i) {
			stringArray[i] = RandomUtil.randomInt(1, 1000) + "234s";
		}
		for (int i = 0; i < beanArray.length; ++i) {
			MessageMetaBeanEntity messageMetaBeanEntity = new MessageMetaBeanEntity();
			messageMetaBeanEntity.init();
			beanArray[i] = messageMetaBeanEntity;
		}
		for (int i = 0; i < byteBoxArray.length; ++i) {
			byteBoxArray[i] = (byte) RandomUtil.randomInt(1, 1000);
		}
		for (int i = 0; i < shortBoxArray.length; ++i) {
			shortBoxArray[i] = (short) RandomUtil.randomInt(1, 1000);
		}
		for (int i = 0; i < intBoxArray.length; ++i) {
			intBoxArray[i] = RandomUtil.randomInt(1, 1000);
		}
		for (int i = 0; i < longBoxArray.length; ++i) {
			longBoxArray[i] = RandomUtil.randomLong(1, 1001231251240l);
		}
		for (int i = 0; i < floatBoxArray.length; ++i) {
			floatBoxArray[i] = RandomUtil.randomFloat(0.001f, 10.562f);
		}
		for (int i = 0; i < doubleBoxArray.length; ++i) {
			doubleBoxArray[i] = (double) RandomUtil.randomFloat(0.001f, 10.562f);
		}
		for (int i = 0; i < charBoxArray.length; ++i) {
			charBoxArray[i] = (char) RandomUtil.randomInt(1, 1000);
		}
		for (int i = 0; i < booleanBoxArray.length; ++i) {
			booleanBoxArray[i] = RandomUtil.randomOne() == 1;
		}
		for (int i = 0; i < stringArray.length; ++i) {
			stringArray[i] = RandomUtil.randomInt(1, 1000) + "dfg2" + i;
		}
		for (int i = 0; i < beanArray.length; ++i) {
			MessageMetaBeanEntity messageMetaBeanEntity = new MessageMetaBeanEntity();
			messageMetaBeanEntity.init();
			beanArray[i] = messageMetaBeanEntity;
		}

		for (int i = 0; i < byteList.size(); ++i) {
			byteList.add((byte) RandomUtil.randomInt(1, 1000));
		}
		for (int i = 0; i < shortList.size(); ++i) {
			shortList.add((short) RandomUtil.randomInt(1, 1000));
		}
		for (int i = 0; i < intList.size(); ++i) {
			intList.add(RandomUtil.randomInt(1, 1000));
		}
		for (int i = 0; i < longList.size(); ++i) {
			longList.add(RandomUtil.randomLong(1234, 346345345234l));
		}
		for (int i = 0; i < floatList.size(); ++i) {
			floatList.add(RandomUtil.randomFloat(0.001f, 10.562f));
		}
		for (int i = 0; i < doubleList.size(); ++i) {
			doubleList.add((double) RandomUtil.randomFloat(0.001f, 10.562f));
		}
		for (int i = 0; i < charList.size(); ++i) {
			charList.add((char) RandomUtil.randomInt(1, 1000));
		}
		for (int i = 0; i < boolList.size(); ++i) {
			boolList.add(RandomUtil.randomOne() == 1);
		}
		for (int i = 0; i < strList.size(); ++i) {
			strList.add(i + "23423dg");
		}
		for (int i = 0; i < beanList.size(); ++i) {
			MessageMetaBeanEntity messageMetaBeanEntity = new MessageMetaBeanEntity();
			messageMetaBeanEntity.init();
			beanList.add(messageMetaBeanEntity);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bean == null) ? 0 : bean.hashCode());
		result = prime * result + Arrays.hashCode(beanArray);
		result = prime * result + ((beanList == null) ? 0 : beanList.hashCode());
		result = prime * result + ((boolBoxVal == null) ? 0 : boolBoxVal.hashCode());
		result = prime * result + ((boolList == null) ? 0 : boolList.hashCode());
		result = prime * result + (boolVal ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(booleanArray);
		result = prime * result + Arrays.hashCode(booleanBoxArray);
		result = prime * result + Arrays.hashCode(byteArray);
		result = prime * result + Arrays.hashCode(byteArrayNull);
		result = prime * result + Arrays.hashCode(byteBoxArray);
		result = prime * result + ((byteBoxVal == null) ? 0 : byteBoxVal.hashCode());
		result = prime * result + ((byteList == null) ? 0 : byteList.hashCode());
		result = prime * result + ((byteListNull == null) ? 0 : byteListNull.hashCode());
		result = prime * result + byteVal;
		result = prime * result + Arrays.hashCode(charArray);
		result = prime * result + Arrays.hashCode(charBoxArray);
		result = prime * result + ((charBoxVal == null) ? 0 : charBoxVal.hashCode());
		result = prime * result + ((charList == null) ? 0 : charList.hashCode());
		result = prime * result + charVal;
		result = prime * result + Arrays.hashCode(doubleArray);
		result = prime * result + Arrays.hashCode(doubleBoxArray);
		result = prime * result + ((doubleBoxVal == null) ? 0 : doubleBoxVal.hashCode());
		result = prime * result + ((doubleList == null) ? 0 : doubleList.hashCode());
		long temp;
		temp = Double.doubleToLongBits(doubleVal);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(floatArray);
		result = prime * result + Arrays.hashCode(floatBoxArray);
		result = prime * result + ((floatBoxVal == null) ? 0 : floatBoxVal.hashCode());
		result = prime * result + ((floatList == null) ? 0 : floatList.hashCode());
		result = prime * result + Float.floatToIntBits(floatVal);
		result = prime * result + Arrays.hashCode(intArray);
		result = prime * result + Arrays.hashCode(intBoxArray);
		result = prime * result + ((intBoxVal == null) ? 0 : intBoxVal.hashCode());
		result = prime * result + ((intList == null) ? 0 : intList.hashCode());
		result = prime * result + intVal;
		result = prime * result + Arrays.hashCode(longArray);
		result = prime * result + Arrays.hashCode(longBoxArray);
		result = prime * result + ((longBoxVal == null) ? 0 : longBoxVal.hashCode());
		result = prime * result + ((longList == null) ? 0 : longList.hashCode());
		result = prime * result + (int) (longVal ^ (longVal >>> 32));
		result = prime * result + Arrays.hashCode(shortArray);
		result = prime * result + Arrays.hashCode(shortBoxArray);
		result = prime * result + ((shortBoxVal == null) ? 0 : shortBoxVal.hashCode());
		result = prime * result + ((shortList == null) ? 0 : shortList.hashCode());
		result = prime * result + shortVal;
		result = prime * result + ((strList == null) ? 0 : strList.hashCode());
		result = prime * result + ((strVal == null) ? 0 : strVal.hashCode());
		result = prime * result + ((strValNull == null) ? 0 : strValNull.hashCode());
		result = prime * result + Arrays.hashCode(stringArray);
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
		MessageMetaEntity other = (MessageMetaEntity) obj;
		if (bean == null) {
			if (other.bean != null)
				return false;
		} else if (!bean.equals(other.bean))
			return false;
		if (!Arrays.equals(beanArray, other.beanArray))
			return false;
		if (beanList == null) {
			if (other.beanList != null)
				return false;
		} else if (!beanList.equals(other.beanList))
			return false;
		if (boolBoxVal == null) {
			if (other.boolBoxVal != null)
				return false;
		} else if (!boolBoxVal.equals(other.boolBoxVal))
			return false;
		if (boolList == null) {
			if (other.boolList != null)
				return false;
		} else if (!boolList.equals(other.boolList))
			return false;
		if (boolVal != other.boolVal)
			return false;
		if (!Arrays.equals(booleanArray, other.booleanArray))
			return false;
		if (!Arrays.equals(booleanBoxArray, other.booleanBoxArray))
			return false;
		if (!Arrays.equals(byteArray, other.byteArray))
			return false;
		if (!Arrays.equals(byteArrayNull, other.byteArrayNull))
			return false;
		if (!Arrays.equals(byteBoxArray, other.byteBoxArray))
			return false;
		if (byteBoxVal == null) {
			if (other.byteBoxVal != null)
				return false;
		} else if (!byteBoxVal.equals(other.byteBoxVal))
			return false;
		if (byteList == null) {
			if (other.byteList != null)
				return false;
		} else if (!byteList.equals(other.byteList))
			return false;
		if (byteListNull == null) {
			if (other.byteListNull != null)
				return false;
		} else if (!byteListNull.equals(other.byteListNull))
			return false;
		if (byteVal != other.byteVal)
			return false;
		if (!Arrays.equals(charArray, other.charArray))
			return false;
		if (!Arrays.equals(charBoxArray, other.charBoxArray))
			return false;
		if (charBoxVal == null) {
			if (other.charBoxVal != null)
				return false;
		} else if (!charBoxVal.equals(other.charBoxVal))
			return false;
		if (charList == null) {
			if (other.charList != null)
				return false;
		} else if (!charList.equals(other.charList))
			return false;
		if (charVal != other.charVal)
			return false;
		if (!Arrays.equals(doubleArray, other.doubleArray))
			return false;
		if (!Arrays.equals(doubleBoxArray, other.doubleBoxArray))
			return false;
		if (doubleBoxVal == null) {
			if (other.doubleBoxVal != null)
				return false;
		} else if (!doubleBoxVal.equals(other.doubleBoxVal))
			return false;
		if (doubleList == null) {
			if (other.doubleList != null)
				return false;
		} else if (!doubleList.equals(other.doubleList))
			return false;
		if (Double.doubleToLongBits(doubleVal) != Double.doubleToLongBits(other.doubleVal))
			return false;
		if (!Arrays.equals(floatArray, other.floatArray))
			return false;
		if (!Arrays.equals(floatBoxArray, other.floatBoxArray))
			return false;
		if (floatBoxVal == null) {
			if (other.floatBoxVal != null)
				return false;
		} else if (!floatBoxVal.equals(other.floatBoxVal))
			return false;
		if (floatList == null) {
			if (other.floatList != null)
				return false;
		} else if (!floatList.equals(other.floatList))
			return false;
		if (Float.floatToIntBits(floatVal) != Float.floatToIntBits(other.floatVal))
			return false;
		if (!Arrays.equals(intArray, other.intArray))
			return false;
		if (!Arrays.equals(intBoxArray, other.intBoxArray))
			return false;
		if (intBoxVal == null) {
			if (other.intBoxVal != null)
				return false;
		} else if (!intBoxVal.equals(other.intBoxVal))
			return false;
		if (intList == null) {
			if (other.intList != null)
				return false;
		} else if (!intList.equals(other.intList))
			return false;
		if (intVal != other.intVal)
			return false;
		if (!Arrays.equals(longArray, other.longArray))
			return false;
		if (!Arrays.equals(longBoxArray, other.longBoxArray))
			return false;
		if (longBoxVal == null) {
			if (other.longBoxVal != null)
				return false;
		} else if (!longBoxVal.equals(other.longBoxVal))
			return false;
		if (longList == null) {
			if (other.longList != null)
				return false;
		} else if (!longList.equals(other.longList))
			return false;
		if (longVal != other.longVal)
			return false;
		if (!Arrays.equals(shortArray, other.shortArray))
			return false;
		if (!Arrays.equals(shortBoxArray, other.shortBoxArray))
			return false;
		if (shortBoxVal == null) {
			if (other.shortBoxVal != null)
				return false;
		} else if (!shortBoxVal.equals(other.shortBoxVal))
			return false;
		if (shortList == null) {
			if (other.shortList != null)
				return false;
		} else if (!shortList.equals(other.shortList))
			return false;
		if (shortVal != other.shortVal)
			return false;
		if (strList == null) {
			if (other.strList != null)
				return false;
		} else if (!strList.equals(other.strList))
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
		if (!Arrays.equals(stringArray, other.stringArray))
			return false;
		return true;
	}

}