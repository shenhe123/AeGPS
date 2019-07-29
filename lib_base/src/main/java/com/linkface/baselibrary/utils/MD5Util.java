package com.linkface.baselibrary.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

/**
 * 统一Md5工具<br>
 * 
 * @author qiesai
 * @create 2016-3-8
 * 
 */
public class MD5Util {

	private static char[] Digit = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	private MD5Util() {
	}

	/**
	 * 根据给定的字节数组获取Md5后的字符<br>
	 * 输入的byte数组不允许为null
	 * 
	 * @param inputBytes
	 * @return
	 */
	public static String getMd5Sum(byte[] inputBytes) {
		if (inputBytes == null) {
			throw new IllegalArgumentException("Input Bytes Is Null");
		}

		// 获取标准的java Md5算法工具
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}
		digest.update(inputBytes);

		byte[] md5Sum = digest.digest();
		// 算法1
		// StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < 16; ++i) {
		// char[] ob = new char[] { Digit[md5sum[i] >> 4 & 15], Digit[md5sum[i]
		// & 15] };
		// String s = new String(ob);
		// sb.append(s);
		// }
		// 算法2
		int j = md5Sum.length;
		char[] finalValue = new char[j * 2];
		int k = 0;

		for (int i = 0; i < j; ++i) {
			byte encoded = md5Sum[i];
			finalValue[k++] = Digit[encoded >> 4 & 15];
			finalValue[k++] = Digit[encoded & 15];
		}

		return new String(finalValue);
	}

	/**
	 * 根据给定的字符串计算其Md5<br>
	 * 输入的字符串不允许为null<br>
	 * 
	 * @param inputStr
	 * @return
	 */
	public static String getMd5Sum(String inputStr) {
		// 不允许为null
		if (inputStr == null) {
			throw new IllegalArgumentException("Input String Is Null !");
		}
		// 获取字符�?
		byte[] inputStrByte = null;
		try {
			inputStrByte = inputStr.getBytes("UTF-8");
			return getMd5Sum(inputStrByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将key和要加密的字符串拼接后进行加密计<br>
	 * 
	 * @param inputStr
	 * @param key
	 * @return
	 */
	public static String getMd5Sum(String inputStr, String key) {
		String keyStr = inputStr + key;
		return MD5Util.getMd5Sum(keyStr);
	}

	/**
	 * 默认用|做分割进行加掩
	 * 
	 * @param params
	 * @param salt
	 * @return
	 */
	public static String getMd5Sum(Map<String, String> params, String salt) {
		return getMd5Sum(params, salt, "|");
	}

	public static String generateSignature(TreeMap<String, String> params) {
		StringBuilder valueStr = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			valueStr.append(entry.getKey());
			valueStr.append("=");
			valueStr.append(entry.getValue());
		}
		return MD5Util.getMd5Sum(valueStr.toString());
	}

	/**
	 * 将Map转换为|分割后加入value后计算Md5 md5(value1|value2|salt)
	 * 
	 * @param params
	 * @param key
	 * @return
	 */
	public static String getMd5Sum(Map<String, String> params, String salt, String decollator) {
		if (params == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		String value = null;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			value = entry.getValue();
			value = TextUtils.isEmpty(value) ? "" : value;
			sb.append(value);
			sb.append(decollator);
		}
		sb.append(salt);
		return MD5Util.getMd5Sum(sb.toString());
	}

	/**
	 * 验证给定的字符串和要站在的签名是否一致<br>
	 * 输入的字符串不允许为null
	 * 
	 * @param text
	 * @param sign
	 * @return
	 */
	public static boolean verify(String text, String sign) {
		String mysign = getMd5Sum(text);
		return mysign.equals(sign);
	}

	/**
	 * 获取文件的Md5值
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getMd5Sum(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		MessageDigest md5 = null;

		try {
			md5 = MessageDigest.getInstance("MD5");

			byte[] j = new byte[1024];

			while (true) {
				int fileLength = is.read(j);
				if (fileLength > 0) {
					md5.update(j, 0, fileLength);
				}

				if (fileLength == -1) {
					is.skip(0L);
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			is.close();
		}

		byte[] md5Sum = md5.digest();
		int len = md5Sum.length;
		char[] finalValue = new char[len * 2];
		int k = 0;

		for (int i = 0; i < len; ++i) {
			byte encoded = md5Sum[i];
			finalValue[k++] = Digit[encoded >> 4 & 15];
			finalValue[k++] = Digit[encoded & 15];
		}

		return new String(finalValue);
	}

	/**
	 * 将Map转换为|分割后加入key后计算Md5
	 * 
	 * @param params
	 * @param key
	 * @return
	 */
	public static String getMd5SumSign(Map<String, String> params, String key) {
		StringBuilder valueStr = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			valueStr.append(entry.getKey());
			valueStr.append("=");
			valueStr.append(entry.getValue() + "|");
		}
		valueStr.deleteCharAt(valueStr.length() - 1);
		valueStr.append(key);
		return MD5Util.getMd5Sum(valueStr.toString());
	}

	/**
	 * 将Map转换为|分割后加入key后计算Md5
	 * 
	 * @param params
	 * @param key
	 * @return
	 */
	public static String getMd5SumSign2(Map<String, String> params, String key) {
		StringBuilder valueStr = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			valueStr.append(entry.getValue());
			valueStr.append('|');
		}
		valueStr.append(key);
		return MD5Util.getMd5Sum(valueStr.toString());
	}

	public static String getMd5SumSignResult(Map<String, String> params, String key) {
		StringBuilder valueStr = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			valueStr.append(entry.getValue() + "|");
		}
		valueStr.append(key);
		return MD5Util.getMd5Sum(valueStr.toString());
	}

	public static String getMd5(String Str) {
		return MD5Util.getMd5Sum(Str);
	}
	
	public static void main(String[] args) {
		System.out.println(getMd5("123456"));
	}
}
