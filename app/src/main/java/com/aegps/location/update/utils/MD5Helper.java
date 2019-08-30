package com.aegps.location.update.utils;

import android.text.TextUtils;

import com.aegps.location.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Md5 签名(小写的字符串)
 * 1.对字符串的签名
 * 2.对Map的签名(key值的升序排列,value以排序之后value1:values2:value3签名)
 * 3.对文件的签名
 * */
public class MD5Helper {
	public static final String COMMON_KEY = "aaa_scrt";
	public static final String COMMON_VALUE = "kWw0ryRC0F5rkbrk";
	private static byte[] hex = new byte[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	/** 对字符串的签名 */
	public static String getMd5(String rawString) {
		String md5String = null;
		
		try{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(rawString.getBytes());
			md5String = convertToHexString(md5.digest());
		}catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		if (null != md5String) {
			return md5String.toLowerCase();
		}
		
		return md5String;
	}

	/** 对Map(Key值的升序排列)的签名 */
	public static String getMd5(Map<String, String> params) {
		Map<String, String> descMap = sortMapByKey(params);
		String paramStr = paramsToString(descMap, false);
		String md5Sign = getMd5(paramStr);
		return md5Sign;
	}

	/** 对文件的签名 */
	public static String getMd5(File file) {
		FileInputStream fis = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			fis = new FileInputStream(file);
			byte[] buffer = new byte[2048];
			int length = -1;
			while ((length = fis.read(buffer)) != -1) {
				md.update(buffer, 0, length);
			}
			byte[] b = md.digest();
			return convertToHexString(b);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static String convertToHexString(byte[] digests) {
		byte[] md5String = new byte[digests.length * 2];
		
		int index = 0;
		for (byte digest : digests)
		{
			md5String[index] 		= hex[(digest >> 4) & 0x0F];
			md5String[index + 1] 	= hex[digest &0x0F];
			index += 2;
		}
		
		return new String(md5String);
	}

	private static String paramsToString(Map<String, String> params, boolean isEncodeValue) {
		if (params != null && params.size() > 0) {
			String paramsEncoding = "UTF-8";
			StringBuilder encodedParams = new StringBuilder();
			try {
				int index = 0;
				for (Map.Entry<String, String> entry : params.entrySet()) {
					encodedParams.append(isEncodeValue ? URLEncoder.encode(TextUtils.isEmpty(entry.getValue()) ? "" : entry.getValue(), paramsEncoding) : entry.getValue());

					index++;
					if (index < params.size()) {
						encodedParams.append(':');
					}
				}
				return encodedParams.toString();
			} catch (UnsupportedEncodingException uee) {
				throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
			}
		}
		return null;
	}

	private static String getMd5(Map<String, String> params, String spli) {
		Map<String, String> descMap = sortMapByKey(params);
		String paramStr = paramsToString(descMap, spli,false);
		LogUtil.e(paramStr);
		String md5Sign = getMd5(paramStr);
		return md5Sign;
	}


	private static String paramsToString(Map<String, String> params, String C, boolean isEncodeValue) {
		if (params != null && params.size() > 0) {
			String paramsEncoding = "UTF-8";
			StringBuilder encodedParams = new StringBuilder();
			try {
				int index = 0;
				for (Map.Entry<String, String> entry : params.entrySet()) {
					encodedParams.append(isEncodeValue ? URLEncoder.encode(TextUtils.isEmpty(entry.getValue()) ? "" : entry.getValue(), paramsEncoding) : entry.getValue());

					index++;
					if (index < params.size()) {
						encodedParams.append(C);
					}
				}
				return encodedParams.toString();
			} catch (UnsupportedEncodingException uee) {
				throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
			}
		}
		return null;
	}


	public static Map<String, String> sortMapByKey(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());
		sortMap.putAll(map);
		return sortMap;
	}

	private static class MapKeyComparator implements Comparator<String> {
		@Override
		public int compare(String str1, String str2) {
			return str1.compareTo(str2);
		}
	}
}
