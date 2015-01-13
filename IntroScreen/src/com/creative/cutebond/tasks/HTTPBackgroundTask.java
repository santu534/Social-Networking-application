package com.creative.cutebond.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.parsers.AccountsParser;
import com.creative.cutebond.parsers.ChatBlockedListingParser;
import com.creative.cutebond.parsers.ChatOthersListingParser;
import com.creative.cutebond.parsers.CntyNLangParser;
import com.creative.cutebond.parsers.CreateAlbumParser;
import com.creative.cutebond.parsers.DetailPageParser;
import com.creative.cutebond.parsers.InboxListingParser;
import com.creative.cutebond.parsers.LeftMenuParser;
import com.creative.cutebond.parsers.MicroBLogDetailPageParser;
import com.creative.cutebond.parsers.MicroblogListingParser;
import com.creative.cutebond.parsers.MyHubLeftMenuParser;
import com.creative.cutebond.parsers.MyHubListingParser;
import com.creative.cutebond.parsers.NotificationParser;
import com.creative.cutebond.parsers.PeopleLeftMenuParser;
import com.creative.cutebond.parsers.PeoplesListingParser;
import com.creative.cutebond.parsers.PhotoDetailPageParser;
import com.creative.cutebond.parsers.PhotoLeftMenuParser;
import com.creative.cutebond.parsers.PhotosListingParser;
import com.creative.cutebond.parsers.UserContactsListingParser;
import com.creative.cutebond.parsers.VideosListingParser;
import com.creative.cutebond.parsers.YVideosDetailParser;
import com.creative.cutebond.utils.Utils;

public class HTTPBackgroundTask extends AsyncTask<String, Integer, Integer>
		implements IItemHandler {

	private HttpURLConnection conn = null;

	private InputStream inputStream = null;

	private IItemHandler handler = null;

	private Context context = null;

	private int length = 0;

	static Item item = null;

	private int parserType = 0;

	private int requestId = 0;

	private boolean state = false;

	private String errorMsg = "";

	private boolean fromRaw = false;

	private String fileName = "";

	private int id = 0;

	private Object obj = null;

	private int cacheType = 0;

	public HTTPBackgroundTask(IItemHandler callback, Context context,
			int parserType, int requestId) {
		this.handler = callback;
		this.context = context;
		this.parserType = parserType;
		this.requestId = requestId;
	}

	public void setRawAttri(boolean fromRaw, String fileName, int id) {
		this.fromRaw = fromRaw;
		this.fileName = fileName;
		this.id = id;
	}

	public void setCacheType(int cache) {
		cacheType = cache;
	}

	public static void setHeaders(Item aItem) {
		if (item == null)
			item = aItem;
	}

	@Override
	protected void onCancelled() {
		// handler.onError("You have cancelled request to server", requestId);
		try {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onCancelled();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		String requestUrl = "";
		try {

			requestUrl = params[0];
			requestUrl = Utils.urlEncode(requestUrl);

			if (fromRaw) {
				getFromRaw(fileName, id, requestUrl);
			} else {

				if (!isNetworkAvailable()) {
					return 5;
				}

				// Log.e("requestUrl : ", requestUrl);

				conn = HTTPGetTask.getHTTPConnection(requestUrl);

				if (conn == null) {
					return 1;
				}

				length = conn.getContentLength();
				length = length / 1024;
				inputStream = conn.getInputStream();

				parseData(parserType);
			}
			if (state) {
				if (cacheType != 0 && obj != null) {
					
				}
				return 0;
			} else
				return 10;

		} catch (MalformedURLException me) {
			me.printStackTrace();
			return 4;
		}

		catch (ConnectException e) {
			e.printStackTrace();
			return 3;
		}

		catch (SocketException se) {
			se.printStackTrace();
			return 6;
		}

		catch (SocketTimeoutException stex) {
			stex.printStackTrace();
			return 2;
		}

		catch (Exception ex) {
			ex.printStackTrace();
			return 1;
		}
	}

	@Override
	protected void onPostExecute(Integer result) {

		try {

			if (result != 0) {
				if (result == 3)
					handler.onError("Server not reachable.!", requestId);
				if (result == 2)
					handler.onError("Server connection timeout.!", requestId);
				if (result == 6)
					handler.onError("Server not reachable.!!", requestId);
				if (result == 4)
					handler.onError("Invalid URL", requestId);
				if (result == 1)
					handler.onError("Connection Error", requestId);
				if (result == 5)
					handler.onError(
							"No Internet, Please check your network settings",
							requestId);
				if (result == 10)
					handler.onError(errorMsg, requestId);
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
				return;
			}

			if (obj != null) {
				handler.onFinish(obj, requestId);
				return;
			}
			return;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}

	/**
	 * checkConnectivity - Checks Whether Internet connection is available or
	 * not.
	 */
	private boolean isNetworkAvailable() {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}

		NetworkInfo net = manager.getActiveNetworkInfo();
		if (net != null) {
			if (net.isConnected()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	/*public void onFinish(Vector<Item> results, int currentPage, int totalPages,
			int requestType) {
		state = true;
		this.requestId = requestType;
	}*/

	public void onError(String errorCode, int requestType) {
		state = false;
		errorMsg = errorCode;
		this.requestId = requestType;
	}

	@Override
	public void onFinish(Object results, int requestType) {
		state = true;
		this.requestId = requestType;
		this.obj = results;
	}

	private void getFromRaw(String path, int id, String requestUrl) {
		FileInputStream fileInputStream = null;
		try {
			File iFile = context.getFileStreamPath(path);
			if (iFile.exists()) {
				
				state = true;
				return;
			} else {
				inputStream = context.getResources().openRawResource(id);
			}
			iFile = null;

			parseData(parserType);

		} catch (Exception e) {
			e.printStackTrace();
			try {
				inputStream = context.getResources().openRawResource(id);
				parseData(parserType);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				inputStream = null;

				if (fileInputStream != null)
					fileInputStream.close();
				fileInputStream = null;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void parseData(int parserType) throws Exception {
		try {
			switch (parserType) {
			
			case 1:
				CntyNLangParser cntynlang = new CntyNLangParser(this, requestId);
				cntynlang.parseXmlData(inputStream);
				break;

			case 2:
				VideosListingParser clParser = new VideosListingParser(this, requestId);
				clParser.parseXmlData(inputStream);
				/*CommonParser common = new CommonParser(this, requestId);
				common.parseXmlData(inputStream);*/
				break;
				
			case 3:
				LeftMenuParser lmp = new LeftMenuParser(this, requestId);
				lmp.parseXmlData(inputStream);
				break;
				
			case 4:
				PhotosListingParser plp = new PhotosListingParser(this, requestId);
				plp.parseXmlData(inputStream);
				break;
				
			case 5:
				PeoplesListingParser polp = new PeoplesListingParser(this, requestId);
				polp.parseXmlData(inputStream);
				break;
				
			case 6:
				PhotoLeftMenuParser phlmp = new PhotoLeftMenuParser(this, requestId);
				phlmp.parseXmlData(inputStream);				
				break;
				
			case 7:
				DetailPageParser dpp = new DetailPageParser(this, requestId);
				dpp.parseXmlData(inputStream);				
				break;
				
			case 8:
				MicroblogListingParser mlp = new MicroblogListingParser(this, requestId);
				mlp.parseXmlData(inputStream);				
				break;
				
			case 9:
				PhotoDetailPageParser pdpp = new PhotoDetailPageParser(this, requestId);
				pdpp.parseXmlData(inputStream);				
				break;
				
			case 10:
				PeopleLeftMenuParser plmp = new PeopleLeftMenuParser(this, requestId);
				plmp.parseXmlData(inputStream);				
				break;
				
			case 11:
				MyHubListingParser mhlp = new MyHubListingParser(this, requestId);
				mhlp.parseXmlData(inputStream);				
				break;
				
			case 12:
				AccountsParser ap = new AccountsParser(this, requestId);
				ap.parseXmlData(inputStream);				
				break;
				
			case 13:
				CreateAlbumParser cap = new CreateAlbumParser(this, requestId);
				cap.parseXmlData(inputStream);				
				break;
				
			case 14:
				UserContactsListingParser uclp = new UserContactsListingParser(this, requestId);
				uclp.parseXmlData(inputStream);				
				break;
				
			case 15:
				YVideosDetailParser yvdp = new YVideosDetailParser(this, requestId);
				yvdp.parseXmlData(inputStream);				
				break;
				
			case 16:
				MicroBLogDetailPageParser mbdpp = new MicroBLogDetailPageParser(this, requestId);
				mbdpp.parseXmlData(inputStream);				
				break;
				
			case 17:
				MyHubLeftMenuParser mhlmp = new MyHubLeftMenuParser(this, requestId);
				mhlmp.parseXmlData(inputStream);				
				break;
				
			case 18: //Chat inbox listing
				InboxListingParser ilp = new InboxListingParser(this, requestId);
				ilp.parseXmlData(inputStream);				
				break;
								
			case 19: //chat others listing
				ChatOthersListingParser colp = new ChatOthersListingParser(this, requestId);
				colp.parseXmlData(inputStream);
				break;
				
			case 20: //chat others listing
				ChatBlockedListingParser cblp = new ChatBlockedListingParser(this, requestId);
				cblp.parseXmlData(inputStream);
				break;
				
			case 21: 
				NotificationParser np = new NotificationParser(this, requestId);
				np.parseXmlData(inputStream);
				break;
				
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	}
