package com.jingfm.api.helper;

public class StringHelper {
	
	public static final char httpPathGap='/';
	
	public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuilder(strLen)
            .append(Character.toTitleCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }
	
	public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !StringHelper.isEmpty(cs);
    }
    
    public static void main(String[] argv){
    	
    }
}
