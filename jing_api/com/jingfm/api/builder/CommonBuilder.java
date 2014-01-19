
package com.jingfm.api.builder;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jingfm.api.ResponseErrorCode;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.helper.JsonHelper;
import com.jingfm.api.model.ChooseAlikeDTO;
import com.jingfm.api.model.ListResult;

/**
 * @author Lukasz Wisniewski
 */
public class CommonBuilder{
	
/*	 public static final List EMPTY_LIST = new EmptyList<?>();
	 @SuppressWarnings("unchecked")
	 	public static final <T> List<T> emptyList() {
	    return (List<T>) EMPTY_LIST;
	      }
	
	 
	        private static class EmptyList<E>
	            extends AbstractList<E>
	           implements RandomAccess, Serializable {
	           private static final long serialVersionUID = 8842843931221139166L;
	    
	            public Iterator<E> iterator() {
	              return null;//emptyIterator();
	            }
	            public ListIterator<E> listIterator() {
	            return null;//emptyListIterator();
	            }
	    
	            public int size() {return 0;}
	            public boolean isEmpty() {return true;}
	    
	            public boolean contains(Object obj) {return false;}
	            public boolean containsAll(Collection<?> c) { return c.isEmpty(); }
	    
	           public Object[] toArray() { return new Object[0]; }
	    
	           public <T> T[] toArray(T[] a) {
	               if (a.length > 0)
	                    a[0] = null;
	                return a;
	           }
	    
	           public E get(int index) {
	                throw new IndexOutOfBoundsException("Index: "+index);
	            }
	   
	            public boolean equals(Object o) {
	               return (o instanceof List) && ((List<?>)o).isEmpty();
	            }
	   
	          public int hashCode() { return 1; }
	    
	           // Preserves singleton property
	           private Object readResolve() {
	               return EMPTY_LIST;
	            }
	        }*/
	 
	/*@SuppressWarnings("rawtypes")
	final static ResultResponse<ListResult> NULL_LISTRESULT_Response  = new ResultResponse<ListResult>(){
		
	};*/
	/*public static <T> T buildTResult(JSONObject jsonObject,Class<T> classz) throws JSONException {
		T t = null;
		try {
			t = classz.newInstance();
			Field[] fields = ResultResponse.class.getDeclaredFields();
			for(Field field:fields){
				System.out.println(field.getName());
			}
			@SuppressWarnings("rawtypes")
			Iterator keys = jsonObject.keys();
			while(keys.hasNext()){
				String key = String.class.cast(keys.next());
				try{
					Object value = jsonObject.get(key);
					//System.out.println("key:"+key+" value:"+value);
					if(StringHelper.isNotEmpty(String.valueOf(value))){
						ReflectionHelper.invokeSetterMethod(t, key, value);
					}
					//if(value != null && !"".equals(value))
						
				}catch(IllegalArgumentException e){
					e.printStackTrace();
				}
			}
			return t;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	/*public static <T> T buildResult(JSONObject jsonObject,Class<T> classz) throws JSONException {
		return null;
	}*/
	
	//JSONObject result
	public static <T> ListResult<T> buildListResult(JSONObject jsonObject,Class<T> classz) throws JSONException {
		ListResult<T> result = new ListResult<T>();
		
		if(!jsonObject.isNull("total")){
			result.setTotal(jsonObject.getInt("total"));
		}
		if(!jsonObject.isNull("st")){
			result.setSt(jsonObject.getInt("st"));
		}
		
		if(!jsonObject.isNull("ps")){
			result.setPs(jsonObject.getInt("ps"));
		}
		
		if(!jsonObject.isNull("moodids")){
			result.setMoodids(jsonObject.getString("moodids"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		if(!jsonObject.isNull("moods")){
			JSONArray array = jsonObject.getJSONArray("moods");
			if (array.length() > 0) {
				List<String> moods = new ArrayList<String>();
				for (int i=0; i < array.length(); i++) {
					moods.add(array.getString(i));
				}
				result.setMoods(moods);
			}
			
			//result.setMoodids(jsonObject.getString("moodids"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		
		if(!jsonObject.isNull("fid")){
			result.setFid(jsonObject.getString("fid"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		
		if(!jsonObject.isNull("cmbt")){
			result.setCmbt(jsonObject.getString("cmbt"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		
		if(!jsonObject.isNull("index")){
			result.setIndex(jsonObject.getInt("index"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		
		if(!jsonObject.isNull("m")){
			result.setM(jsonObject.getInt("m"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		
		if(!jsonObject.isNull("hint")){
			result.setHint(jsonObject.getString("hint"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		
		if(!jsonObject.isNull("choose")){
			result.setChoose(jsonObject.getBoolean("choose"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		if(!jsonObject.isNull("an")){
			result.setAn(jsonObject.getString("an"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		if(!jsonObject.isNull("tn")){
			result.setTn(jsonObject.getString("tn"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		if(!jsonObject.isNull("tid")){
			result.setTid(jsonObject.getInt("tid"));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		if(result.isChoose()){
			JSONArray choosearray = jsonObject.getJSONArray("items");
			result.setChooseitems(JsonHelper.getListDTO(choosearray, ChooseAlikeDTO.class));
			//return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}else{
			JSONArray array = jsonObject.getJSONArray("items");
			result.setItems(JsonHelper.getListDTO(array, classz));
		}
		return result;
	}
	
	
	public static <T> List<T> buildSimpleListResult(JSONArray array,Class<T> classz) throws JSONException {
		//JSONArray array = jsonObject.getJSONArray("items");
		/*List<T> ts = new ArrayList<T>();
		if (arrray.length() > 0) {
			for (int i=0; i < arrray.length(); i++) {
				ts.add(buildTResult(arrray.getJSONObject(i),classz));
			}
		}*/
		/*result.setItems(JsonHelper.getListDTO(array, classz));
		return result;*/
		return JsonHelper.getListDTO(array, classz);
	}
	
	/*public static <T> ListResult<T> buildPlsListResult(JSONObject jsonObject,Class<T> classz) throws JSONException {
		ListResult<T> result = new ListResult<T>();
		result.setTotal(jsonObject.getInt("total"));
		result.setSt(jsonObject.getInt("st"));
		result.setPs(jsonObject.getInt("ps"));
		
		JSONArray array = jsonObject.getJSONArray("items");
		result.setItems(JsonHelper.getListDTO(array, classz));
		return result;
	}*/
	
	
	
	public static <T> ResultResponse<T> buildSimpleResultSuccess(JSONObject jsonObject) throws JSONException {
		ResultResponse<T> Success_Response  = new ResultResponse<T>(){};
		if(jsonObject == null || jsonObject.length() == 0){
			//Success_Response.setCode(ResponseErrorCode.REQUEST_NULL_ERROR.code());
			Success_Response.setCodemsg("操作成功");
		}else{
			//Success_Response.setCode(jsonObject.getString("msg"));
			Success_Response.setCodemsg(jsonObject.getString("msg"));
		}
		Success_Response.setSuccess(true);
		return Success_Response;
	}
	
	public static String ResultError_JSON_Template = "{\"code\":\"%s\",\"codemsg\":\"%s\",\"msg\":\"操作失败\",\"stack\":\"\",\"success\":false}";
	
	public static String builderJsonResultError(int errorCode){
		ResponseErrorCode responseErrorCode = ResponseErrorCode.getByCode(String.valueOf(errorCode));
		return String.format(ResultError_JSON_Template, responseErrorCode.code(),responseErrorCode.i18n());
	}
	
	public static <T> ResultResponse<T> buildResultError(){
		ResultResponse<T> Error_Response  = new ResultResponse<T>(){};
		Error_Response.setCode(ResponseErrorCode.REQUEST_NULL_ERROR.code());
		Error_Response.setCodemsg(ResponseErrorCode.REQUEST_NULL_ERROR.i18n());
		Error_Response.setSuccess(false);
		return Error_Response;
	}
	
	public static <T> ResultResponse<T> buildResultError(int errorCode){
		ResponseErrorCode responseErrorCode = ResponseErrorCode.getByCode(String.valueOf(errorCode));
		
		ResultResponse<T> Error_Response  = new ResultResponse<T>(){};
		Error_Response.setCode(responseErrorCode.code());
		Error_Response.setCodemsg(responseErrorCode.i18n());
		Error_Response.setSuccess(false);
		return Error_Response;
	}
	
	public static <T> ResultResponse<T> buildResultError(JSONObject jsonObject) throws JSONException {
		ResultResponse<T> Error_Response  = new ResultResponse<T>(){};
		if(jsonObject == null || jsonObject.length() == 0){
			Error_Response.setCode(ResponseErrorCode.REQUEST_NULL_ERROR.code());
			Error_Response.setCodemsg(ResponseErrorCode.REQUEST_NULL_ERROR.i18n());
		}else{
			Error_Response.setCode(jsonObject.getString("code"));
			Error_Response.setCodemsg(jsonObject.getString("codemsg"));
		}
		Error_Response.setSuccess(false);
		return Error_Response;
	}
	
	public static <T> ResultResponse<List<T>> buildSimpleListResultError(JSONObject jsonObject) throws JSONException {
		ResultResponse<List<T>> Error_Response  = new ResultResponse<List<T>>(){};
		if(jsonObject == null || jsonObject.length() == 0){
			Error_Response.setCode(ResponseErrorCode.REQUEST_NULL_ERROR.code());
			Error_Response.setCodemsg(ResponseErrorCode.REQUEST_NULL_ERROR.i18n());
		}else{
			Error_Response.setCode(jsonObject.getString("code"));
			Error_Response.setCodemsg(jsonObject.getString("codemsg"));
		}
		Error_Response.setSuccess(false);
		return Error_Response;
	}
	
	public static <T> ResultResponse<ListResult<T>> buildListResultError(JSONObject jsonObject) throws JSONException {
		ResultResponse<ListResult<T>> Error_Response  = new ResultResponse<ListResult<T>>(){};
		if(jsonObject == null || jsonObject.length() == 0){
			Error_Response.setCode(ResponseErrorCode.REQUEST_NULL_ERROR.code());
			Error_Response.setCodemsg(ResponseErrorCode.REQUEST_NULL_ERROR.i18n());
		}else{
			if(!jsonObject.isNull("code")){
				Error_Response.setCode(jsonObject.getString("code"));
			}
			Error_Response.setCodemsg(jsonObject.getString("codemsg"));
		}
		Error_Response.setSuccess(false);
		return Error_Response;
	}
	
	public static <T> ResultResponse<T> buildSingleResultError(JSONObject jsonObject) throws JSONException {
		ResultResponse<T> Error_Response  = new ResultResponse<T>(){};
		if(jsonObject == null || jsonObject.length() == 0){
			Error_Response.setCode(ResponseErrorCode.REQUEST_NULL_ERROR.code());
			Error_Response.setCodemsg(ResponseErrorCode.REQUEST_NULL_ERROR.i18n());
		}else{
			Error_Response.setCode(jsonObject.getString("code"));
			Error_Response.setCodemsg(jsonObject.getString("codemsg"));
		}
		Error_Response.setSuccess(false);
		return Error_Response;
	}
}
