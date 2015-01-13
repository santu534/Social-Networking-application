package com.creative.cutebond.common;

import android.os.Environment;

public class Constants {

	public static final int CONTACT_PICKER = 600;

	public static final int CONTENT_INSTALL = 601;

	public static final int CONTENT_FSHARE = 602;
	
	public static final int ON_PLAY = 400;
	
	public static final int ON_PAUSE = 401;

	public static final String ITEM = "item.xml";
	public static final String VITEM = "vitem.xml";
	
	public static final String PTAG = "ptag.xml";

	private static String PATH = Environment.getExternalStorageDirectory()
			.toString();

	public static final String DWLPATH = PATH + "/cutebond/downloads/";

	public static final String CACHEPATH = PATH + "/cutebond/cache/";
	
	public static final String CACHEAUDIO = PATH + "/cutebond/audio/";
	
	public static final String CACHEPHOTO = PATH + "/cutebond/photo/";
	
	public static final String PREVIEW = PATH + "/cutebond/preview/";

	public static final String CACHEDATA = CACHEPATH + "data/";

	public static final String CACHETEMP = CACHEPATH + "temp/";

	public static final String CACHEIMAGE = CACHEPATH + "images/";

	public static final String CATEGORIES = "categories.xml";

	public static final String CATEGORIES_DEF = "categories_def.xml";
	
	public static final String V_L_MENU = "v_l_menu.txt";
	
	public static final String P_L_MENU = "p_l_menu.txt";
	
	public static final String PO_L_MENU = "po_l_menu.txt";
	
	public static final String MB_L_MENU = "mb_l_menu.txt";
	
	public static final String CNTRYLANG = "countryandlang.txt";
	
	public static final String MH_L_MENU = "mh_l_menu.txt";

}
