package com.jingfm.api;

public class ApiUrlDefine {
	public final static String API_URL_Prefix = "https://jing.fm/api/";
	public final static String Base_URL_Prefix = "https://jing.fm/";
//	public final static String API_URL_Prefix = "http://jing.fm/api/";
//	public final static String Base_URL_Prefix = "http://jing.fm/";
//	public final static String API_URL_Prefix = "http://192.168.100.193:8080/";
	public final static String API_Version = "v1";
//	public final static String API_Version = "msip_et_rest";
	
	public static class Guest{
		
	}
	
	public static class Normal{

		public static class User{
			public final static String Create 				= "/account/create";
			public final static String SessionCreate 		= "/sessions/create";
			public final static String SessionValidate 		= "/sessions/validates";
			public final static String CheckNick 			= "/account/check_nick";
			public final static String CheckEmail			= "/account/check_email";
			public final static String CheckPermalink 		= "/account/check_permalink";
			public final static String Frequent 			= "/account/frequent";
			
			public final static String VerifyInvitation 	= "/account/verify_invitation";
			public final static String RegisterCompleted 	= "/account/register_completed";
			public final static String ChangePwd 			= "/account/change_pwd";
			public final static String UpdateProfile 		= "/account/update_profile";
			public final static String FetchProfile 		= "/account/fetch_profile";
			
			public final static String FetchAvatar 			= "/account/fetch_avatar";
			
			public final static String followUser 			= "/account/follow_frd";
			public final static String blockUser 			= "/account/block_frd";
			
			public final static String AvatarUpload			= "/user/avatar/avatarupload";
			public final static String CoverUpload			= "/personal/coverupload";
			
			public final static String FetchUsersPlaytime			= "/account/fetch_users_pt";
			public final static String ForgotPassword       = "/account/forgot_password";
		}
		public static class UserPersonal{
			public final static String FetchPersonal 		= "/personal/fetch";
			public final static String FetchPersonalSettings 	=  "/personal/settings";
			/*public final static String SessionCreate 		= "/sessions/create";
			public final static String SessionValidate 		= "/sessions/validates";
			public final static String CheckNick 			= "/account/check_nick";
			public final static String CheckEmail			= "/account/check_email";
			public final static String CheckPermalink 		= "/account/check_permalink";
			public final static String Frequent 			= "/account/frequent";
			
			public final static String VerifyInvitation 	= "/account/verify_invitation";
			public final static String RegisterCompleted 	= "/account/register_completed";
			public final static String ChangePwd 			= "/account/change_pwd";
			public final static String UpdateProfile 		= "/account/update_profile";
			public final static String FetchProfile 		= "/account/fetch_profile";
			
			public final static String FetchAvatar 			= "/account/fetch_avatar";*/
			
		}
		public static class UserFriend{
			public final static String CheckFrdshp 			= "/account/check_frdshp";
			//获取可能认识的好友
			public final static String FetchMtknownFriends 	= "/account/fetch_mtknown_friends";
			public final static String PostNotknownFriends 	= "/account/post_notknown_friends";
			
			public final static String FetchFriends 		= "/account/fetch_friends";
			public final static String FetchFriendsOrder 	= "/account/fetch_friends_order";
			public final static String FetchOnlineFriends 	= "/account/fetch_online_friends";
			public final static String FetchBlockFriends 	= "/account/fetch_block_friends";
			
			//public final static String FetchOnlineFriendsCount = "/account/fetch_online_friends_count";
			
			//关注或取消关注
			public final static String FollowFrd 			= "/account/follow_frd";
			public final static String UnfollowFrd          = "/account/unfollow_frd";
			
			public final static String RemindFrd 			= "/account/remind_frd";
			
			public final static String FetchFollowings      = "/account/fetch_followings";
			public final static String FetchFolloweds      = "/account/fetch_followed";

			public final static String FetchUsersPt      = "/account/fetch_users_pt";
			
			public final static String FetchBanners      = "/banner/fetch_banners";
			public final static String FetchBannersDetail      = "/banner/fetch_banner_detail  ";
			
		}
		public static class Media{
			public final static String Post_SUrl 			= "/media/song/surl";
			public final static String Offline_Music 		= "/media/song/offline_music";
		}
		
		public static class Ticker{
			public final static String Fetch_Recents 	=  "/ticker/fetch_recents";
			public final static String Fetch_Love_Recents 	=  "/ticker/fetch_love_recents";
			public final static String Fetch_Personal_LoveTickers     = "/personal/lovetickers";
			
			public final static String Post_Cmbt 	=  "/ticker/post_cmbt";
		}
		
		public static class Chart{
			public final static String Fetch_Charts 	=  "/chart/fetch";
		}
		
		public static class Badge{
			public final static String Fetch_Badges 	=  "/badge/fetch_badges";
			public final static String Fetch_Badges_byfilter 	=  "/badge/fetch_badges_byfilter";
		}
		
		public static class Config{
			public final static String Fetch_Android 	=  "/config/android";
		}
		
		public static class Chat{
            public final static String Fetch_Offline_message	=  "/message/fetch_offline_messages";
			public final static String Fetch_Chatctt 	=  "/chat/fetch_chatctt";
			public final static String Fetch_Personal_Sysmessage 	=  "/chat/fetch_personal_sysmessage";
			public final static String Post_Share_Track 	=  "/chat/post_share_track";
			public final static String Fetch_Chatsession 	=  "/chat/fetch_chatsessions_by_frds";
		}
		
		public static class Setting{
			public final static String Post_Settings 	=  "/setting/post_settings";
		}
		
		public static class Music{
			
			//public final static String Fetch_Music_Hates 	=  "/music/fetch_hates";
			//public final static String Fetch_Music_Favorites = "/music/fetch_favorites";
			
			public final static String Fetch_Music_Hates 	=  "/music/fetch_hate";
			public final static String Fetch_Music_Favorites = "/music/fetch_fav";
			
			public final static String Fetch_TrackInfos 	= "/handset/music/fetch_track_infos";
			public final static String Fetch_TrackRelatedInfos 	= "/music/fetch_related_infos";
			//歌曲被哪些好友喜欢请求
			public final static String Fetch_Music_Frdlvd 	=  "/handset/music/fetch_frdlvd";
			
			public final static String Fetch_Validate_Favorites = "/music/validate_favorites";
			
			public final static String Post_LoveMusic 		= "/music/post_love_song";
			public final static String Post_HateMusic 		= "/music/post_hate_song";
			public final static String Post_HeardMusic 		= "/music/post_heard_song";
			public final static String Post_HalfHeardMusic 	= "/music/post_half";
			public final static String Post_NextMusic 		= "/music/post_next";
		}
		public static class Click{
			public final static String Post_ChannelData 	= "/click/channel/post";
			public final static String Post_PlayingData 	= "/click/playdata/post";
			public final static String Post_Playduration 	= "/click/playduration/post";
			public final static String Post_Heartbeat = "/click/heartbeat/post";
			public final static String Post_Favs		 		= "/ticker/post_favs";
		}
		public static class Recommend{
			public final static String Fetch_Recommend 	= "/app/rcmnd_app";
		}
		public static class Auto{
			public final static String Fetch_Casual_Cmbt 	= "/badge/fetch_casual_cmbt";
			public final static String Fetch_Pop 			= "/badge/fetch_pop";
			public final static String Fetch_Auto 			= "/search/ling/auto";
			public final static String Fetch_Ntlg 			= "/app/fetch_natural";
			public final static String Fetch_Ntlg_Auto 		= "/search/ling/ntlg_auto";
			public final static String Fetch_Ntlg_Sauto 		= "/search/ling/ntlg_sauto";
			
		}
		public static class Search{
			public final static String Fetch_Pls 			= "/search/jing/fetch_pls";
			public final static String Fetch_Nick 			= "/search/fetch_nick";
			public final static String Fetch_Frd_Nick 			= "/search/fetch_frd_nick";
		}
		public static class Push{
			public final static String Fetch_Push 			= "/click/push/fetch";
		}
		public static class OAuth{
			public final static String Authorize 			= "/oauth/proxyauthorize";
			public final static String Fetch_Friends 			= "/oauth/fetch_friends";
			public final static String Post_ShareMusic 			= "/oauth/music/share";
			public final static String Post_Association 			= "/oauth/association";
			public final static String Post_Bind 			= "/oauth/bind";
			public final static String Post_RemoveBind 			= "/oauth/remove_bind";
			public final static String Fetch_Identifies 			= "/oauth/fetch_identifies";
			public final static String Post_Auto_Create 			= "/oauth/auto_create";
			public final static String Post_Invite_Friend 			= "/oauth/invite_friend";
			public final static String Post_ShareCmbt               = "/oauth/music/sharecmbt";
		}
		public static class Favorites{
			public final static String Post_FavoritesCmbt			= "/favorites/post_favorites_cmbt";
			public final static String Remove_FavoritesCmbt 			= "/favorites/remove_favorites_cmbt";
			public final static String Fetch_FavoritesCmbt 			= "/favorites/fetch_favorites_cmbt";
		}
		
		public static class Guest{
			public final static String Create 					= "/app/account/create";
			public final static String SessionCreate 			= "/app/sessions/create";
			public final static String SessionValidate 			= "/app/sessions/validates";
			
			public final static String Frequent 			= "/visit/account/frequent";
			public final static String FetchPersonal 		= "/visit/personal/fetch";
			public final static String Fetch_Pls 			= "/visit/search/jing/fetch_pls";
			public final static String Fetch_Auto 			= "/visit/search/ling/auto";
			public final static String Fetch_Pop 			= "/visit/badge/fetch_pop";
			public final static String Fetch_Ntlg 			= "/visit/app/fetch_natural";
			public final static String Fetch_Ntlg_Auto 		= "/visit/search/jing/ntlg_auto";
			
			public final static String Fetch_Badges 	=  "/visit/badge/fetch_badges";
			
		}
		
		public static class Device{
			public final static String Post_Register 					= "/device/register";
			public final static String Post_Destory 			= "/device/destory";
		}
		
		public static class Feedback{
			public final static String Post_Error 					= "feedbacks/post_error";
		}
	}
	
	public static String getApiUrl(String Fetch_Pls){
		return API_URL_Prefix.concat(API_Version).concat(Fetch_Pls);
	}
	
	public static String getBaseUrl(String fetch_pls){
		return Base_URL_Prefix.concat(fetch_pls);
	}
	
}
