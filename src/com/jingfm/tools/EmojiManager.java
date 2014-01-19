package com.jingfm.tools;

import java.util.HashMap;

import com.jingfm.R;

public class EmojiManager {
	public static EmojiManager instance;
	public static EmojiManager getInstance(){
		if (instance == null) {
			instance = new EmojiManager();
		}
		return instance;
	}
	
	public int EMOJI_COUNT = 54;
	private EmojiData[] emojiDataArray = new EmojiData[EMOJI_COUNT];
	private HashMap<String, EmojiData> emojiMap = new HashMap<String, EmojiData>();
	private EmojiManager() {
		emojiDataArray[0] = new EmojiData(R.drawable.emoji_01, "tq","調情");
		emojiDataArray[1] = new EmojiData(R.drawable.emoji_02, "zyj","眨眼睛");
		emojiDataArray[2] = new EmojiData(R.drawable.emoji_03, "tst","吐舌頭");
		emojiDataArray[3] = new EmojiData(R.drawable.emoji_04, "smm","色咪咪");
		emojiDataArray[4] = new EmojiData(R.drawable.emoji_05, "h","汗");
		emojiDataArray[5] = new EmojiData(R.drawable.emoji_06, "sa","示愛");
		emojiDataArray[6] = new EmojiData(R.drawable.emoji_07, "kq","哭泣");
		emojiDataArray[7] = new EmojiData(R.drawable.emoji_08, "sq","生氣");
		emojiDataArray[8] = new EmojiData(R.drawable.emoji_09, "gg","尷尬");
		emojiDataArray[9] = new EmojiData(R.drawable.emoji_10, "k","酷");
		emojiDataArray[10] = new EmojiData(R.drawable.emoji_11, "zgl","做鬼臉");
		emojiDataArray[11] = new EmojiData(R.drawable.emoji_12, "kbs","摳鼻屎");
		emojiDataArray[12] = new EmojiData(R.drawable.emoji_13, "sj","睡覺");
		emojiDataArray[13] = new EmojiData(R.drawable.emoji_14, "yx","淫笑");
		emojiDataArray[14] = new EmojiData(R.drawable.emoji_15, "lm","來嘛");
		emojiDataArray[15] = new EmojiData(R.drawable.emoji_16, "bs","鄙視");
		emojiDataArray[16] = new EmojiData(R.drawable.emoji_17, "sb","生病");
		emojiDataArray[17] = new EmojiData(R.drawable.emoji_18, "bm","便秘");
		emojiDataArray[18] = new EmojiData(R.drawable.emoji_19, "fd","奮鬥");
		emojiDataArray[19] = new EmojiData(R.drawable.emoji_20, "hx","害羞");
		emojiDataArray[20] = new EmojiData(R.drawable.emoji_21, "zgws","崢哥完事");
		emojiDataArray[21] = new EmojiData(R.drawable.emoji_22, "lshd","劉順喝多");
		emojiDataArray[22] = new EmojiData(R.drawable.emoji_23, "tc","貪財");
		emojiDataArray[23] = new EmojiData(R.drawable.emoji_24, "sh","使壞");
		emojiDataArray[24] = new EmojiData(R.drawable.emoji_25, "wsbqf","吳雙被欺負");
		emojiDataArray[25] = new EmojiData(R.drawable.emoji_26, "jy","驚訝");
		emojiDataArray[26] = new EmojiData(R.drawable.emoji_27, "kh","口活");
		emojiDataArray[27] = new EmojiData(R.drawable.emoji_28, "mny","木乃伊");
		emojiDataArray[28] = new EmojiData(R.drawable.emoji_29, "tzz","兔子嘴");
		emojiDataArray[29] = new EmojiData(R.drawable.emoji_30, "ggyx","陰險");
		
		emojiDataArray[30] = new EmojiData(R.drawable.emov2_0, "emov2_0","");
		emojiDataArray[31] = new EmojiData(R.drawable.emov2_1, "emov2_1","");
		emojiDataArray[32] = new EmojiData(R.drawable.emov2_2, "emov2_2","");
		emojiDataArray[33] = new EmojiData(R.drawable.emov2_3, "emov2_3","");
		emojiDataArray[34] = new EmojiData(R.drawable.emov2_4, "emov2_4","");
		emojiDataArray[35] = new EmojiData(R.drawable.emov2_5, "emov2_5","");
		emojiDataArray[36] = new EmojiData(R.drawable.emov2_6, "emov2_6","");
		emojiDataArray[37] = new EmojiData(R.drawable.emov2_7, "emov2_7","");
		emojiDataArray[38] = new EmojiData(R.drawable.emov2_8, "emov2_8","");
		emojiDataArray[39] = new EmojiData(R.drawable.emov2_9, "emov2_9","");
		emojiDataArray[40] = new EmojiData(R.drawable.emov2_10, "emov2_10","");
		emojiDataArray[41] = new EmojiData(R.drawable.emov2_11, "emov2_11","");
		emojiDataArray[42] = new EmojiData(R.drawable.emov2_12, "emov2_12","");
		emojiDataArray[43] = new EmojiData(R.drawable.emov2_13, "emov2_13","");
		emojiDataArray[44] = new EmojiData(R.drawable.emov2_14, "emov2_14","");
		emojiDataArray[45] = new EmojiData(R.drawable.emov2_15, "emov2_15","");
		emojiDataArray[46] = new EmojiData(R.drawable.emov2_16, "emov2_16","");
		emojiDataArray[47] = new EmojiData(R.drawable.emov2_17, "emov2_17","");
		emojiDataArray[48] = new EmojiData(R.drawable.emov2_18, "emov2_18","");
		emojiDataArray[49] = new EmojiData(R.drawable.emov2_19, "emov2_19","");
		emojiDataArray[50] = new EmojiData(R.drawable.emov2_20, "emov2_20","");
		emojiDataArray[51] = new EmojiData(R.drawable.emov2_21, "emov2_21","");
		emojiDataArray[52] = new EmojiData(R.drawable.emov2_22, "emov2_22","");
		emojiDataArray[53] = new EmojiData(R.drawable.emov2_23, "emov2_23","");
		
		for (int i = 0; i < emojiDataArray.length; i++) {
			emojiMap.put(emojiDataArray[i].content, emojiDataArray[i]);
		}
	}
	
	public EmojiData[] getEmojiDataArray() {
		return emojiDataArray;
	}
	
	public class EmojiData{
		public int rid;
		public String content;
		public String description;
		public EmojiData(int rid,String content,String description) {
			this.rid = rid;
			this.content = content;
			this.description = description;
		}
	}

	public static boolean isEmoji(String ctt) {
		if (ctt != null 
				&& ctt.length() > 2
				&& ctt.charAt(0) == 007
				&& ctt.charAt(ctt.length()-1) == 007) {
			return true;
		}
		return false;
	}
	
	public EmojiData getEmoji(String ctt) {
		String key = ctt.substring(1, ctt.length()-1);
		return emojiMap.get(key);
	}
}
