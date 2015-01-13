package com.creative.cutebond.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.creative.cutebond.callbacks.IRequestCallback;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;

public class HTTPGetTask extends AsyncTask<String, Integer, Integer> {

	String requestUrl = null;
	HttpURLConnection conn = null;
	InputStream inputStream = null;
	IRequestCallback callback = null;
	String mimeType = null;
	Context context = null;
	private String downloadFileName = "";
	static Item item = null;

	boolean saveToFile = false;
	private int length = 0;
	static int RESCODE = 0;

	public HTTPGetTask(IRequestCallback callback) {
		this.callback = callback;
		this.context = (Context) callback;
	}

	public HTTPGetTask(IRequestCallback callback, Context context) {
		this.callback = callback;
		this.context = context;
	}

	public HTTPGetTask(IRequestCallback callback, String fName) {
		this.callback = callback;
		this.context = (Context) callback;
		this.downloadFileName = fName;
	}
	
	public HTTPGetTask(Context context, IRequestCallback callback,
			boolean savetofile, String downloadname) {
		this.context = context;
		this.callback = callback;
		this.saveToFile = savetofile;
		this.downloadFileName = downloadname;
	}

	public static void setHeaders(Item aItem) {
		if (item == null)
			item = aItem;
	}

	public static Item getHeader() {
		return item;
	}

	@Override
	protected void onCancelled() {
		callback.onRequestCancelled("You have cancelled request to server");
		try {
			if (conn != null)
				conn.disconnect();
			conn = null;

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

		try {

			if (!isNetworkAvailable()) {
				return 5;
			}

			requestUrl = params[0];
			requestUrl = urlEncode(requestUrl);

			// Log.e("requestUrl : ", requestUrl);

			conn = getHTTPConnection(requestUrl);

			if (conn == null) {
				return 1;
			}

			mimeType = conn.getContentType();

			String type = mimeType;

			inputStream = conn.getInputStream();

			length = conn.getContentLength();

			if (length == -1) {
				String leng = conn.getHeaderField("content-filesize");
				if (leng != null)
					length = Integer.parseInt(leng);
			}

			length = length / 1024;

			if (type != null && saveToFile)
				if (type.contains("video/")
						|| type.contains("image/")
						|| type.contains("audio/")
						|| type.contains("application/vnd.android.package-archive")
						|| type.contains("videotone/"))
					saveFile(mimeType, length);

			return 0;

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
			return 7;
		}

	}

	protected void onPostExecute(Integer result) {

		try {

			if (result != 0) {
				if (result == 20)

					if (result == 3)
						callback.onRequestFailed("Server not reachable.!");
				if (result == 2)
					callback.onRequestFailed("Server connection timeout.!");
				if (result == 6)
					callback.onRequestFailed("Server not reachable.!!");
				if (result == 4)
					callback.onRequestFailed("Invalid URL");
				if (result == 1)
					callback.onRequestFailed("Invalid server response : "
							+ RESCODE);
				if (result == 5)
					callback.onRequestFailed("No Internet, Please check your network settings");
				if (result == 7)
					callback.onRequestFailed("Server not reachable.!!!");
				if (result == 8)
					callback.onRequestFailed("Error, while downloading the content. Please download again");

				if (conn != null) {
					conn.disconnect();
					conn = null;
				}

				File file = new File(Constants.DWLPATH + downloadFileName);
				if (file.exists()) {
					file.delete();
				}

				return;
			}

			if (!saveToFile && inputStream != null) {
				callback.onRequestComplete(inputStream, mimeType);
				return;
			} else if (saveToFile && mimeType != null) {
				callback.onRequestComplete(requestUrl + "###"
						+ downloadFileName, mimeType);
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

	@Override
	protected void onProgressUpdate(Integer... values) {
		callback.onRequestProgress(values[0], length);
	}

	/**
	 * 
	 * @param url
	 *            - URL to establish connection
	 * @return HttpURLConnection
	 * @throws Exception
	 *             if URL Malformed or SocketTimeout or Any other Exception.
	 */
	@SuppressWarnings("rawtypes")
	static public HttpURLConnection getHTTPConnection(String url)
			throws Exception {
		HttpURLConnection _conn = null;
		URL serverAddress = null;
		URL hostAddress = null;
		int socketExepCt = 0;
		int ExepCt = 0;
		int numRedirect = 0;

		hostAddress = new URL(url);
		String host = "http://" + hostAddress.getHost() + "/";

		for (int i = 0; i <= 5; i++) {

			try {

				Log.e("request url :::", url + "");

				serverAddress = new URL(url);

				_conn = (HttpURLConnection) serverAddress.openConnection();
				if (_conn != null) {
					_conn.setRequestMethod("GET");
					_conn.setDoOutput(false);
					_conn.setReadTimeout(30000);
					_conn.setConnectTimeout(10000);
					_conn.setInstanceFollowRedirects(false);

					if (item != null) {
						Hashtable requestProperty = item.getAllAttributes();
						Enumeration keys = requestProperty.keys();
						while (keys.hasMoreElements()) {
							String key = keys.nextElement().toString();
							String value = requestProperty.get(key).toString();
							_conn.setRequestProperty(key, value);

						}
					}

					RESCODE = 0;
					_conn.connect();

					RESCODE = _conn.getResponseCode();
					if (RESCODE == HttpURLConnection.HTTP_OK) {
						return _conn;
					} else if (RESCODE == HttpURLConnection.HTTP_MOVED_TEMP
							|| RESCODE == HttpURLConnection.HTTP_MOVED_PERM) {
						if (numRedirect > 15) {
							_conn.disconnect();
							_conn = null;
							break;
						}

						numRedirect++;
						i = 0;
						url = _conn.getHeaderField("Location");
						if (!url.startsWith("http")) {
							url = host + url;
						}
						// Log.e("redirection url : ", url);
						_conn.disconnect();
						_conn = null;
						continue;

					} else {
						_conn.disconnect();
						_conn = null;
					}
				}
			}

			catch (MalformedURLException me) {
				me.printStackTrace();
				throw me;
			}

			catch (SocketTimeoutException se) {
				se.printStackTrace();
				_conn = null;
				if (i >= 5)
					throw se;
			}

			catch (SocketException s) {
				s.printStackTrace();
				if (socketExepCt > 10) {
					_conn = null;
					throw s;
				}
				socketExepCt++;
				i = 0;
				continue;
			}

			catch (Exception e) {
				e.printStackTrace();

				if (ExepCt > 10) {
					_conn = null;
					throw e;
				}
				ExepCt++;
				i = 0;
				continue;

			}
		}
		return null;
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

	/**
	 * urlEncode -
	 * 
	 * @param String
	 * @return String
	 */
	public static String urlEncode(String sUrl) {
		int i = 0;
		String urlOK = "";
		while (i < sUrl.length()) {
			if (sUrl.charAt(i) == ' ') {
				urlOK = urlOK + "%20";
			} else {
				urlOK = urlOK + sUrl.charAt(i);
			}
			i++;
		}
		return (urlOK);
	}

	/**
	 * saveFile -
	 * 
	 * @param String
	 * @param int
	 * @throws Exception
	 */
	private void saveFile(String aMineType, int length) throws Exception {
		byte[] buffer = new byte[1024];
		int bytesRead = 0, totalRead = 0;

		File f = new File(Constants.DWLPATH);
		if (!f.exists()) {
			f.mkdirs();
		}
		try {

			downloadFileName = downloadFileName.replaceAll("[^a-zA-Z0-9_]+", "");

			downloadFileName = downloadFileName + "."
					+ getFileExtension(aMineType);

			FileOutputStream outStream = new FileOutputStream(Constants.DWLPATH
					+ downloadFileName);

			while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >= 0) {

				totalRead += bytesRead;

				int totalReadInKB = totalRead / 1024;

				publishProgress(totalReadInKB);

				outStream.write(buffer, 0, bytesRead);

			}

			outStream.close();
			buffer = null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * getFileExtension - This method return file extenion based on mime type.
	 * 
	 * @param String
	 * @return String
	 */
	private String getFileExtension(String aType) {
		String type = "";
		type = aType;// image/gif;charset=ISO-8859-1
		if (type.contains("videotone/"))
			return type.substring(10, type.length());
		if (type.contains("video/") || type.contains("audio/")
				|| type.contains("image/")) {
			type = type.substring(6, type.length());
			if (type.contains(";"))
				type = type.substring(0, type.indexOf(";"));
			return type;
		}

		else if (type.contains("application/vnd.android.package-archive"))
			return "apk";

		return type;
	}

}