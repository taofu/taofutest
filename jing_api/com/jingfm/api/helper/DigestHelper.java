package com.jingfm.api.helper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestHelper {
	      
    public static String md5ToHex(String val){  
        MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes());  
	        //byte[] m = md5.digest();//加密   
	        BigInteger hash = new BigInteger(1, md5.digest());
	        String result = hash.toString(16);
	        while(result.length() < 32) {
	        	result = "0" + result;
	        }
	        return result;
	        //return getString(m);  
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}  
        return null;
    }  
    
    public static void main(String[] argv){
    	System.out.println(DigestHelper.md5ToHex("1234"));	
    }
}
