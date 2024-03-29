package com.ccenglish.ccplayerlib.util;

public class ConfigUtil {

	// 配置同时下载个数
	public final static int DOWNLOADING_MAX = 2;

	// 配置API KEY
	public final static String API_KEY = "2bRDPcntNLDqZk9o3v8f3Uxdei8dpgZ8";

	// 配置帐户ID
	public final static String USERID = "325165FBF55C3838";

	// 配置下载文件路径
	public final static String DOWNLOAD_DIR = "CCDownload";
	
	// 配置视频回调地址
	public final static String NOTIFY_URL = "http://www.example.com";
	
	/** Fragment */
	
	public final static int MAIN_FRAGMENT_MAX_TAB_SIZE = 2;
	public final static int PLAY_TAB = 0;
	public final static int DOWNLOAD_TAB = 1;
	
	public final static int DOWNLOAD_FRAGMENT_MAX_TAB_SIZE = 2;
	public final static int DOWNLOADED = 0;
	public final static int DOWNLOADING = 1;
	
	/** Service Action */
	
	public final static String ACTION_DOWNLOADED = "demo.service.downloaded";
	public final static String ACTION_DOWNLOADING = "demo.service.downloading";
	
	/** Input Info ID */
	
	public final static int INPUT_INFO_TITLE_ID = 10000000;
	public final static int INPUT_EDIT_TITLE_ID = 10000010;
	
	public final static int INPUT_INFO_TAGS_ID = 10000001;
	public final static int INPUT_EDIT_TAGS_ID = 10000011;
	
	public final static int INPUT_INFO_DESC_ID = 10000002;
	public final static int INPUT_EDIT_DESC_ID = 10000012;
	
	/** spinner info id */
	public final static int SPINNER_MAIN_ID = 10000003;
	public final static int SPINNER_SUB_ID = 10000013;
	
	/** Download Group ID */
	public final static int DOWNLOADING_MENU_GROUP_ID = 20000000;
	public final static int DOWNLOADED_MENU_GROUP_ID = 20000001;

}
