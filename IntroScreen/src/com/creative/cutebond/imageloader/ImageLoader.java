package com.creative.cutebond.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.creative.cutebond.R;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	private boolean resize = false;
	private Context context = null;

	public ImageLoader(Context context, boolean save) {
		this.context = context;
		fileCache = new FileCache(context, save);
		executorService = Executors.newFixedThreadPool(5);

	}

	//final int stub_id = R.drawable.ic_launcher;
	final int stub_id = R.color.transparent;

	public void setResize(boolean resize) {
		this.resize = resize;
	}

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {

			if (resize)
				bitmap = setCustomImage(bitmap);

			imageView.setImageBitmap(bitmap);
		} else {
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}
	
	public void DisplayImage(String url, ImageView imageView, boolean rounded) {
		
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			if (resize)
				bitmap = setCustomImage(bitmap);
			imageView.setImageBitmap(bitmap);
		} else {
			queuePhoto(url, imageView, rounded);
			imageView.setImageResource(stub_id);
		}
	}
	
	public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
	    int targetWidth = 200;
	    int targetHeight = 200;
	    Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, 
	                        targetHeight,Bitmap.Config.ARGB_8888);

	    Canvas canvas = new Canvas(targetBitmap);
	    Path path = new Path();
	    path.addCircle(((float) targetWidth - 1) / 2,
	        ((float) targetHeight - 1) / 2,
	        (Math.min(((float) targetWidth), 
	        ((float) targetHeight)) / 2),
	        Path.Direction.CCW);

	    canvas.clipPath(path);
	    Bitmap sourceBitmap = scaleBitmapImage;
	    canvas.drawBitmap(sourceBitmap, 
	        new Rect(0, 0, sourceBitmap.getWidth(),
	        sourceBitmap.getHeight()), 
	        new Rect(0, 0, targetWidth, targetHeight), null);
	    return targetBitmap;
	}

	public void displayImageDrwable(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {

			Resources resources = context.getResources();
			Drawable drawable = new BitmapDrawable(resources, bitmap);

			imageView.setImageDrawable(drawable);
		} else {
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}
	
	private void queuePhoto(String url, ImageView imageView, boolean rounded) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		p.setRounded(rounded);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		try {
			Bitmap bitmap = null;
			// String link = url.replaceAll("%26", "&");
			URL imageUrl = new URL(url);
			// Log.e("Image url", url+"");
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);

			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);

			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private Bitmap decodeFile(File f) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			final int REQUIRED_SIZE = 200;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

		} catch (FileNotFoundException e) {
		}
		return null;
	}

	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public boolean rounded = false;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
		
		private void setRounded(boolean rounded) {
			this.rounded = rounded;						
		}
		
		private boolean isRounded() {
			return rounded;
		}

	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);

			if (resize)
				bmp = setCustomImage(bmp);
			
			if(photoToLoad.isRounded())
				bmp = getRoundedShape(bmp);

			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}
	
	public void adapterClear() {
		//clearThreads();
	}

	public void clearCache() {
		memoryCache.clear();
		clearThreads();
	}

	public void clearThreads() {
		
		if(executorService == null)
			return;
		
		if (!executorService.isShutdown()) {
			List<Runnable> list = executorService.shutdownNow();
			for (int i = 0; i < list.size(); i++) {
				BitmapDisplayer runnable = (BitmapDisplayer) list.get(i);
				imageViews.remove(runnable.photoToLoad.imageView);
			}
		}

		executorService.shutdown();
		executorService = null;

	}

	private Bitmap setCustomImage(Bitmap bitmap) {

		if (bitmap.getWidth() < 100) {
			return Bitmap.createScaledBitmap(bitmap, 200, 200, true);
		} else if (bitmap.getWidth() < 140) {
			return Bitmap.createScaledBitmap(bitmap, 150, 150, true);
		} else if (bitmap.getWidth() < 200) {
			return Bitmap.createScaledBitmap(bitmap, 200, 200, true);
		}
		return bitmap;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

}