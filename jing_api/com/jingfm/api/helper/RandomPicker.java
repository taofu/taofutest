 package com.jingfm.api.helper;
 
 import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
 
 public class RandomPicker
 {
   public static <T> T pick(Set<T> set)
   {
     if (set.size() == 0) return null;
 
//     double v = set.size() * Math.random();
//     int count = 0;
//     for (Iterator<T> iter = set.iterator(); iter.hasNext(); ) { 
//    	 T t = iter.next();
//    	 ++count;
//    	 if ((count < v) || (count >= set.size())) {
//    		 return t;
//    	 }
//     }
//     return null;
     int size = set.size();
     int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
     int i = 0;
     for(T t : set)
     {
         if (i == item)
             return t;
         i++;
     }
     return null;
   }
 
   public static <K, V> K pick(Map<K, V> map) {
     if (map.size() == 0) return null;
     double v = map.size() * Math.random();
     int count = 0;
     for (Iterator<K> iter = map.keySet().iterator(); iter.hasNext(); ) { 
    	 K k = iter.next();
    	 ++count;
    	 if ((count < v) || (count >= map.size())) {
    		 return k;
    	 }
     }
     return null;
   }
 
   public static <T> T pick(List<T> list) {
     if (list.size() == 0) return null;
 
     double v = list.size() * Math.random();
     return list.get((int)v);
   }
 
   public static <T> T pick(T[] arr) {
	 if (arr==null) return null;  
     if (arr.length == 0) return null;
     if (arr.length == 1) return arr[0];
     double v = arr.length * Math.random();
     return arr[(int)v];
   }
 
/*   public static <T> Set<T> multiPick(Set<T> set, int amount)
   {
     if (set.size() == 0) return Collections.emptySet();
 
     Set<T> ret = new HashSet<T>(amount);
     for (int i = 0; i < amount; ++i) {
       ret.add(pick(set));
     }
     return ret;
   }*/
 
   public static <K, V> Set<K> multiPick(Map<K, V> map, int amount) {
     if (map.size() == 0) return Collections.emptySet();
 
     Set<K> ret = new HashSet<K>(amount);
     for (int i = 0; i < amount; ++i) {
       ret.add(pick(map));
     }
     return ret;
   }
 
   public static <T> List<T> multiPick(List<T> list, int amount) {
     if (list.size() == 0) return Collections.emptyList();
 
     List<T> ret = new ArrayList<T>(amount);
     for (int i = 0; i < amount; ++i) {
       ret.add(pick(list));
     }
 
     return ret;
   }

   public static <T> Set<T> multiPick(Set<T> set, int amount) {
		if (set.isEmpty())
			return Collections.emptySet();
		if (set.size() <= amount)
			return set;
		Set<T> cloneSet = null;
		try{
			cloneSet = new HashSet<T>(set);
			Set<T> ret = new HashSet<T>(amount);
			//T[] t = ret.toArray(new T[0]);
			//int itercount = 0;
			while(ret.size()<amount){
				T t = pick(cloneSet);
				if(ret.add(t)){
					//itercount++;
					cloneSet.remove(t);
				}
			}
			return ret;
		}finally{
			if(cloneSet != null){
				cloneSet.clear();
				cloneSet = null;
			}
		}
		
	}
   	public static <T> List<T> multiPick(List<T> list, int amount, boolean norepeat) {
		if (list.size() == 0)
			return Collections.emptyList();
		if(amount >= list.size()){
			Collections.shuffle(list);
			return list;
		}
		List<T> list_clone = null;
		try{
			list_clone = new ArrayList<T>(list);
			List<T> ret = new ArrayList<T>(amount);
			for(int i=0;i<amount;i++){
				T t = pick(list_clone);
				if(norepeat){
					if(!ret.contains(t)){
						ret.add(t);
						list_clone.remove(t);
					}
				}else ret.add(t);
			}
			return ret;
		}finally{
			if(list_clone != null){
				list_clone.clear();
				list_clone = null;
			}
		}
		
		/*int itercount = 0;
		while(ret.size()<amount){
			T t = pick(list_clone);
			if(norepeat){
				if(!ret.contains(t)){
					ret.add(t);
					list_clone.remove(t);
				}
			}else ret.add(t);
			itercount++;
		}*/
		//System.out.println(itercount);
		
	}
	/*public static <T> List<T> multiPick(List<T> list, int amount, boolean norepeat) {
		if (list.size() == 0)
			return Collections.emptyList();
		if(amount >= list.size()){
			Collections.shuffle(list);
			return list;
		}
		List<T> ret = new ArrayList<T>(amount);
		int itercount = 0;
		while(ret.size()<amount){
			T t = pick(list);
			if(norepeat){
				if(!ret.contains(t)){
					ret.add(t);
				}
			}else ret.add(t);
			itercount++;
		}
		//System.out.println(itercount);
		return ret;
	}*/
	
	public static void main(String[] argv){
		/*List<String> list =new ArrayList<String>();
		list.add("123456");
		list.add("123457");
		List<String>ret = multiPick(list,3,true);
		System.out.println(ret);
		
		for(int i=0;i<100;i++){
			System.out.println(10*Math.random());
		}*/
		
		for(int i=0;i<1000;i++){
			System.out.println(randBoolean());
		}
	}
   @SuppressWarnings("unchecked")
   public static <T> List<T> multiPick(T[] arr, int amount) {
	   	if (arr.length == 0) return Collections.EMPTY_LIST;
		 List<T> ret = new ArrayList<T>(amount);
		 for (int i = 0; i < amount; ++i) {
		   ret.add(pick(arr));
		 }
		 return ret;
   }
 
   /*public static <T> List<T> multiPick(T[] arr, int amount) {
	   if (arr.length == 0) return Collections.EMPTY_LIST;
	     List<T> ret = new ArrayList<T>(amount);
	     for (int i = 0; i < amount; ++i) {
	       ret.add(pick(arr));
	     }
	     return ret;
   }*/
/*   public static <T> Set<T> multiPick(T[] arr, int amount) {
     if (arr.length == 0) return Collections.emptySet();
 
     Set<T> ret = new HashSet<T>(amount);
     //for (int i = 0; i < amount; ++i) {
    //	 ret.add(pick(arr));
     //}
     while (ret.size() < amount) {
       ret.add(pick(arr));
     }
     return ret;
   }*/
   // static String[] arrayStr = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	public static <T> String randString(T[] arr, int amount){
		List<T> sets = multiPick(arr,amount);
		StringBuilder sb = new StringBuilder();
		for(T t:sets){
			sb.append(t.toString());
		}
		return sb.toString();
	}
	public static <T> String randString(T[] arr, int amount,String split){
		List<T> sets = multiPick(arr,amount);
		StringBuilder sb = new StringBuilder();
		int index = 0;
		int size = sets.size();
		for(T t:sets){
			sb.append(t.toString());
			if(index <(size-1))
				sb.append(split);
			index ++;
		}
		return sb.toString();
	}
	
	public static boolean randBoolean(){
		int end = 9;
		int start = 0;
		int delta = end - start;
	    if (delta < 0) delta = 0 - delta;
	    int ret =  (int)(delta * Math.random() + start);
	    return ret > 5;
	}
 }
