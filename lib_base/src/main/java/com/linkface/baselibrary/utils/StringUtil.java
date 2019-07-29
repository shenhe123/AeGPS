package com.linkface.baselibrary.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 字符串转换成十六进制字符串
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     * 十六进制转换字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * bytes转换成十六进制字符串
     */
    public static String byte2HexStr(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            // if (n<b.length-1) hs=hs+":";
        }
        return hs.toUpperCase();
    }

    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        byte b1 = Byte.decode("0x" + src1).byteValue();
        byte ret = (byte) (b0 | b1);
        return ret;
    }

    /**
     * bytes转换成十六进制字符串
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }

    /**
     * String的字符串转换成unicode的String
     */
    public static String stringToUnicode(String strText) throws Exception {
        char c;
        String strRet = "";
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128) {
                strRet += "\\u" + strHex;
            } else {
                // 低位在前面补00
                strRet += "\\u00" + strHex;
            }
        }
        return strRet;
    }

    /**
     * unicode的String转换成String的字符串
     */
    public static String unicodeToString(String hex) {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++) {
            String s = hex.substring(i * 6, (i + 1) * 6);
            // 高位需要补上00再转
            String s1 = s.substring(2, 4) + "00";
            // 低位直接转
            String s2 = s.substring(4);
            // 将16进制的string转为int
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // 将int转换为字符
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    /**
     * gbk的String转换成utf-8的字符串
     */
    public static String gbk2utf8(String gbk) {
        String utf8 = "";
        try {
            utf8 = new String(gbk.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return utf8;
    }

    /**
     * 检查字符串不为空
     *
     * @param str
     * @return
     */
    public static boolean notNull(String str) {
        return str != null && !"".equals(str) && !"null".equals(str);
    }

    /**
     * 检查字符串为空
     *
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return str == null || "".equals(str) || "null".equals(str);
    }

    /**
     * 判断字符串是否为null或全为空格
     *
     * @param s 待校验字符串
     * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
     */
    public static boolean isSpace(String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 判断str是否为空，或者等于指定的option， 否则都返回缺省值
     *
     * @param str
     * @param option
     * @param defaultValue
     * @return
     */
    public static String parseNull(String str, String option, String defaultValue) {
        if (notNull(str)) {
            if (notNull(option) && str.equals(option)) {
                return defaultValue;
            } else {
                return str;
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * 判断是否在6-16位
     *
     * @param str
     * @return
     */
    public static boolean pswLength(String str) {
        return str.length() > 5 && str.length() < 17;
    }

    // 不能全位相同的数字和字母
    public static boolean equalStr(String numOrStr) {
        boolean flag = true;
        char str = numOrStr.charAt(0);
        for (int i = 0; i < numOrStr.length(); i++) {
            if (str != numOrStr.charAt(i)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    // 判断是否连续的字母和数字
    public static boolean isOrderNumeric(String numOrStr) {
        boolean flag = true;// 如果全是连续数字返回true
        boolean isNumeric = true;// 如果全是数字返回true
        /*
		 * for (int i = 0; i < numOrStr.length(); i++) { if
		 * (!Character.isDigit(numOrStr.charAt(i))) { isNumeric = false; break;
		 * } }
		 */

        char[] chars = numOrStr.toCharArray();
        int[] asciiArray = new int[chars.length];

        for (int i = 0; i < chars.length; i++) {
            asciiArray[i] = (int) (chars[i]);
        }
        if (true) {// 如果全是数字则执行是否连续数字判断
            for (int i = 0; i < numOrStr.length(); i++) {
                if (i > 0) {// 判断如123456 ,abcdef
                    int num = asciiArray[i];
                    int num_ = asciiArray[i - 1] + 1;
                    if (num != num_) {
                        flag = false;
                        break;
                    }
                }
            }
        } else {
            flag = false;
        }
        return flag;
    }

    // 判断是否连续的字母和数字--递减（如：987654、876543）连续数字返回true
    public static boolean isOrderNumeric_(String numOrStr) {
        boolean flag = true;// 如果全是连续数字返回true
        boolean isNumeric = true;// 如果全是数字返回true
		/*
		 * for (int i = 0; i < numOrStr.length(); i++) { if
		 * (!Character.isDigit(numOrStr.charAt(i))) { isNumeric = false; break;
		 * } }
		 */
        char[] chars = numOrStr.toCharArray();
        int[] asciiArray = new int[chars.length];

        for (int i = 0; i < chars.length; i++) {
            asciiArray[i] = (int) (chars[i]);
        }
        if (isNumeric) {// 如果全是数字则执行是否连续数字判断
            for (int i = 0; i < asciiArray.length; i++) {
                if (i > 0) {// 判断如654321

                    int num = asciiArray[i];
                    int num_ = asciiArray[i - 1] - 1;
                    if (num != num_) {
                        flag = false;
                        break;
                    }
                }
            }
        } else {
            flag = false;
        }
        return flag;
    }

    // 判断密码是否为password弱密码
    public static boolean isSimplePsw(String str) {
        Pattern pattern = Pattern.compile("password");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 检测String是否全是中文
     *
     * @param name
     * @return
     */

    public static boolean checkNameChese(String name) {

        boolean res = true;

        char[] cTemp = name.toCharArray();

        for (int i = 0; i < name.length(); i++) {

            if (!isChinese(cTemp[i])) {

                res = false;

                break;

            }

        }

        return res;

    }

    /**
     * 不包括中文字符
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {

        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A;

    }

    /**
     *      * 金额显示格式转换，每隔三位加逗号
     *      * @param long
     *     
     */
    public static StringBuffer amountChange(long l) {
        StringBuffer str = new StringBuffer(String.valueOf(l)); //将d转化为String类型
        for (int i = str.length() - 3; i > 0; i = i - 3) { //找到小数点的位置，从小数点从右往左遍历
            str.insert(i, ','); //每隔三位插入一个逗号
        }
        return str.append(".00");
    }

}
