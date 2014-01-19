package com.jingfm.api.fs;import java.io.BufferedReader;import java.io.BufferedWriter;import java.io.File;import java.io.FileInputStream;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import java.io.InputStreamReader;import java.io.OutputStreamWriter;import java.io.UnsupportedEncodingException;import java.util.HashMap;import java.util.Iterator;import java.util.Map;import java.util.Set;import android.os.Environment;import com.jingfm.api.context.AppContext;import com.jingfm.api.context.ClientContext;import com.jingfm.api.context.GuestClientContext;import com.jingfm.api.helper.MD5Helper;import com.jingfm.api.helper.StringHelper;public class LocalFileSystem {		private static final String FS_Root_Path = "jing";		private static final String FS_Data = "data";	//"/ETData"	//Environment.getExternalStorageDirectory().getPath()	private static final String User_Data_Mapping_File = Environment.getExternalStorageDirectory().getPath()+"/"+FS_Root_Path+"/"+FS_Data+"/umapping.txt";	//此 txt文件中只存一个appuser id	private static final String AppUser_Data_Mapping_File = Environment.getExternalStorageDirectory().getPath()+"/"+FS_Root_Path+"/"+FS_Data+"/appumapping.txt";	private static final String User_Data_Privicy_FilePath_Template = Environment.getExternalStorageDirectory().getPath()+"/"+FS_Root_Path+"/"+FS_Data+"/users/%s/privacy.txt";	//key:email value: lastlogin	private static Map<String,String> UserPathMapping = new HashMap<String,String>();	private static String lastLoginMail = null;		static{		loadUserPathMapping();	}		public static String getLastLoginMail(){		return lastLoginMail;	}	private static void loadUserPathMapping(){		File mappingfile = new File(User_Data_Mapping_File);		if(mappingfile.exists() && mappingfile.isFile()){			InputStream is = null;//CmbtParserFilterHelper.class.getResourceAsStream("/ETData/index/dic/badgedic/badges.dic");			try {				is = new FileInputStream(User_Data_Mapping_File);				BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));	            String line;	            //int index = 0;	            while ((line = br.readLine()) != null) {	            	try{		            	int index = line.lastIndexOf("=");		            	String key = line.substring(0, index);		            	String value = line.substring(index+1,line.length());		            	UserPathMapping.put(key, value);	            		if("true".equals(value)) {	            			lastLoginMail = key;	            		}		            	/*String[] array = value.split(",");		            	if(array.length == 2){//不等于2为非法数据，直接忽略		            		UserPathMapping.put(key, array[0]);		            		if("true".equals(array[0])) {		            			lastLoginMail = key;		            		}		            	}*/		            	//if(value.contains(','))		            	//insertWord(word.trim(),radicalword);	            	}catch(Exception ex){	            		ex.printStackTrace();	            		System.out.println(line);	            	}	            }	            br.close();			} catch (UnsupportedEncodingException e) {				e.printStackTrace();			} catch (IOException e) {				e.printStackTrace();			}finally{				if(is!=null)					try {						is.close();					} catch (IOException e) {						e.printStackTrace();					}			}		}	}			public static boolean canAppUserValidateLogin(){		File mappingfile = new File(AppUser_Data_Mapping_File);		if(mappingfile.exists() && mappingfile.isFile()){			InputStream is = null;//CmbtParserFilterHelper.class.getResourceAsStream("/ETData/index/dic/badgedic/badges.dic");			GuestClientContext context = new GuestClientContext();			try {				is = new FileInputStream(AppUser_Data_Mapping_File);				BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));	            String line;	            //int index = 0;	            while ((line = br.readLine()) != null) {	            	try{		            	int appUid = Integer.parseInt(line.trim());		            	context.setUid(appUid);		            	break;	            	}catch(Exception ex){	            		ex.printStackTrace();	            		System.out.println(line);	            	}	            }	            br.close();			} catch (UnsupportedEncodingException e) {				e.printStackTrace();			} catch (IOException e) {				e.printStackTrace();			}finally{				if(is!=null)					try {						is.close();					} catch (IOException e) {						e.printStackTrace();					}			}			boolean ret = context.validate();			if(ret){				AppContext.setGuestClientContext(context);			}			return ret;		}		return false;	}		public static boolean updateAppUserPrivacyFile(int appuid){		File file = new File(AppUser_Data_Mapping_File);		if (!file.exists()) {			file.getParentFile().mkdirs();			try {				file.createNewFile();			} catch (IOException e) {				e.printStackTrace();			}		}		FileOutputStream fos = null;		OutputStreamWriter osw = null;		BufferedWriter  bw = null;				try{			fos=new FileOutputStream(file);			osw=new OutputStreamWriter(fos, "UTF-8");			bw=new BufferedWriter(osw);			bw.write(String.valueOf(appuid)+"\n");		}catch(IOException ex){			ex.printStackTrace();			return false;		}finally{			try {				if(bw != null)					bw.close();				if(osw != null)					osw.close();				if(fos != null)					fos.close();							} catch (IOException e) {				e.printStackTrace();			}		}		return true;	}			public static boolean validateLocalEmailAndPwd(String email,String pwd){		String realUserPrivicy = String.format(User_Data_Privicy_FilePath_Template, email.hashCode());		Map<String,String> privacyMap = loadUserPrivacy(realUserPrivicy);		if(privacyMap == null || privacyMap.isEmpty()) return false;		if(!privacyMap.containsKey(ClientContext.JingPwdRequestParam))  return false;		String encoderPwd = privacyMap.get(ClientContext.JingPwdRequestParam);		if(encoderPwd != null && encoderPwd.equals(MD5Helper.md5(pwd)))			return true;		else 			return false;		/*File realUserPrivicyFile = new File(realUserPrivicy);		if(realUserPrivicyFile.exists() && realUserPrivicyFile.isFile()){			String encoderPwd = null;			InputStream is = null;			try {				is = new FileInputStream(realUserPrivicyFile);				BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));	            String line;	            while ((line = br.readLine()) != null) {	            	try{		            	int index = line.indexOf("=");		            	String key = line.substring(0, index);		            	String value = line.substring(index+1,line.length());		            	if(ClientContext.JingPwdRequestParam.equals(key)){		            		encoderPwd = value;		            		break;		            	}	            	}catch(Exception ex){	            		ex.printStackTrace();	            		System.out.println("error parser:"+line);	            	}	            }	            br.close();			} catch (UnsupportedEncodingException e) {				e.printStackTrace();			} catch (IOException e) {				e.printStackTrace();			}finally{				if(is!=null)					try {						is.close();					} catch (IOException e) {						e.printStackTrace();					}			}			if(encoderPwd != null && encoderPwd.equals(MD5Helper.md5(pwd)))				return true;			else 				return false;		}else{			return false;		}*/	}		public static boolean canUserRemoteValidateLogin(){		if(StringHelper.isNotEmpty(lastLoginMail) && !UserPathMapping.isEmpty()){			String lastLogin = UserPathMapping.get(lastLoginMail);			if(!"true".equals(lastLogin)) return false;			String realUserPrivicy = String.format(User_Data_Privicy_FilePath_Template, lastLoginMail.hashCode());			Map<String,String> privacyMap = loadUserPrivacy(realUserPrivicy);			if(privacyMap == null || privacyMap.isEmpty()) return false;			ClientContext clientContext = new ClientContext();						if(privacyMap.containsKey(ClientContext.JingUidRequestParam)){				String uidStr = privacyMap.get(ClientContext.JingUidRequestParam);				clientContext.setUid(Integer.parseInt(uidStr));			}			if(privacyMap.containsKey(ClientContext.JingATokenRequestParam)){				clientContext.setAtoken(privacyMap.get(ClientContext.JingATokenRequestParam));			}						if(privacyMap.containsKey(ClientContext.JingRTokenRequestParam)){				clientContext.setRtoken(privacyMap.get(ClientContext.JingRTokenRequestParam));			}			/*if(ClientContext.JingUidRequestParam.equals(key)){        	        	}        	if(ClientContext.JingATokenRequestParam.equals(key)){        		clientContext.setAtoken(value);        	}        	if(ClientContext.JingRTokenRequestParam.equals(key)){        		clientContext.setRtoken(value);        	}*//*			File realUserPrivicyFile = new File(realUserPrivicy);			ClientContext clientContext = new ClientContext();			if(realUserPrivicyFile.exists() && realUserPrivicyFile.isFile()){				InputStream is = null;//CmbtParserFilterHelper.class.getResourceAsStream("/ETData/index/dic/badgedic/badges.dic");				try {					is = new FileInputStream(realUserPrivicyFile);					BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));		            String line;		            while ((line = br.readLine()) != null) {		            	try{			            	int index = line.indexOf("=");			            	String key = line.substring(0, index);			            	String value = line.substring(index+1,line.length());			            	if(ClientContext.JingUidRequestParam.equals(key)){			            		clientContext.setUid(Integer.parseInt(value));			            	}			            	if(ClientContext.JingATokenRequestParam.equals(key)){			            		clientContext.setAtoken(value);			            	}			            	if(ClientContext.JingRTokenRequestParam.equals(key)){			            		clientContext.setRtoken(value);			            	}		            	}catch(Exception ex){		            		ex.printStackTrace();		            		System.out.println(line);		            	}		            }		            br.close();				} catch (UnsupportedEncodingException e) {					e.printStackTrace();				} catch (IOException e) {					e.printStackTrace();				}finally{					if(is!=null)						try {							is.close();						} catch (IOException e) {							e.printStackTrace();						}				}				boolean ret = clientContext.validate();				if(ret){					AppContext.setClientContext(clientContext);				}				return ret;			}else{				return false;			}*/		}		return false;			}		public static boolean updateUserRelationFiles(String _lastLoginMail,String _pwd){		if(StringHelper.isEmpty(_lastLoginMail)) return false;		lastLoginMail = _lastLoginMail;		UserPathMapping.put(_lastLoginMail, "true");		boolean updateD = updateDataMappingFile();		boolean updateU = updateUserPrivacyFile(_lastLoginMail,_pwd);		return updateD && updateU;	}		private static boolean updateUserPrivacyFile(String _lastLoginMail,String _pwd){		String realUserPrivicy = String.format(User_Data_Privicy_FilePath_Template, _lastLoginMail.hashCode());		File file = new File(realUserPrivicy);		if (!file.exists()) {			file.getParentFile().mkdirs();			try {				file.createNewFile();			} catch (IOException e) {				e.printStackTrace();			}		}		FileOutputStream fos = null;		OutputStreamWriter osw = null;		BufferedWriter  bw = null;				try{			fos=new FileOutputStream(file);			osw=new OutputStreamWriter(fos, "UTF-8");			bw=new BufferedWriter(osw);			StringBuilder sb = new StringBuilder();            sb.append(ClientContext.JingUidRequestParam).append('=').append(AppContext.getClientContext().getUid()).append("\n");            bw.write(sb.toString());                        sb.append(ClientContext.JingPwdRequestParam).append('=').append(MD5Helper.md5(_pwd)).append("\n");            bw.write(sb.toString());                        sb = new StringBuilder();            sb.append(ClientContext.JingATokenRequestParam).append('=').append(AppContext.getClientContext().getAtoken()).append("\n");            bw.write(sb.toString());            sb = new StringBuilder();            sb.append(ClientContext.JingRTokenRequestParam).append('=').append(AppContext.getClientContext().getRtoken()).append("\n");            bw.write(sb.toString());		}catch(IOException ex){			ex.printStackTrace();			return false;		}finally{			try {				if(bw != null)					bw.close();				if(osw != null)					osw.close();				if(fos != null)					fos.close();							} catch (IOException e) {				e.printStackTrace();			}		}		return true;	}		private static boolean updateDataMappingFile(){		//String realUserPrivicy = String.format(User_Data_Privicy_FilePath_Template, _lastLoginMail.hashCode());		File file = new File(User_Data_Mapping_File);		if (!file.exists()) {			file.getParentFile().mkdirs();			try {				file.createNewFile();			} catch (IOException e) {				e.printStackTrace();			}		}		FileOutputStream fos = null;		OutputStreamWriter osw = null;		BufferedWriter  bw = null;				try{			fos=new FileOutputStream(new File(User_Data_Mapping_File));			osw=new OutputStreamWriter(fos, "UTF-8");			bw=new BufferedWriter(osw);						Set<Map.Entry<String, String>> set = UserPathMapping.entrySet();	        for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {	            Map.Entry<String, String> entry = it.next();	            StringBuilder sb = new StringBuilder();	            sb.append(entry.getKey()).append('=');	            if(lastLoginMail.equals(entry.getKey())) {	            	sb.append("true");	            	UserPathMapping.put(entry.getKey(), "true");	            }	            else {	            	sb.append("false");	            	UserPathMapping.put(entry.getKey(), "false");	            }	            sb.append("\n");	            bw.write(sb.toString());	        }		    		}catch(IOException ex){			ex.printStackTrace();			return false;		}finally{			try {				if(bw != null)					bw.close();				if(osw != null)					osw.close();				if(fos != null)					fos.close();							} catch (IOException e) {				e.printStackTrace();			}					}    		return true;	}			private static Map<String,String> loadUserPrivacy(String privacyFilePath){		File realUserPrivicyFile = new File(privacyFilePath);		if(realUserPrivicyFile.exists() && realUserPrivicyFile.isFile()){			InputStream is = null;			Map<String,String> privacy = new HashMap<String,String>();			try {				is = new FileInputStream(realUserPrivicyFile);				BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));	            String line;	            while ((line = br.readLine()) != null) {	            	try{		            	int index = line.indexOf("=");		            	String key = line.substring(0, index);		            	String value = line.substring(index+1,line.length());		            	privacy.put(key, value);	            	}catch(Exception ex){	            		ex.printStackTrace();	            		System.out.println(line);	            	}	            }	            br.close();			} catch (UnsupportedEncodingException e) {				e.printStackTrace();			} catch (IOException e) {				e.printStackTrace();			}finally{				if(is!=null)					try {						is.close();					} catch (IOException e) {						e.printStackTrace();					}			}			return privacy;		}else return null;			}		/*public static boolean parserLocalUser(){		return false;	}*/}