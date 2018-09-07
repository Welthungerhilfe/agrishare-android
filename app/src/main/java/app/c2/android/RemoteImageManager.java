package app.c2.android;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

public class RemoteImageManager
{
	private static RemoteImageManager mInstance = null;
	
	private ArrayList<RemoteImageObject> images;
	private Boolean isRunning = false;
	private fetchBitmap task;
	private RemoteImageObject currentImage;	
	private HashMap<String, SoftReference<Bitmap>> cache=new HashMap<String, SoftReference<Bitmap>>();
	
	public static RemoteImageManager getInstance()
    {
    	if (mInstance == null)
    		mInstance = new RemoteImageManager();
    	return mInstance;
    }
    
    private RemoteImageManager()
    { 
    	images = new ArrayList<RemoteImageObject>();
    }	
    
    public RemoteImageObject addImage(ImageView image, String filepath, RelativeLayout loader, TextView failed_to_load)
    {
    	// load from cache
    	if (cache.containsKey(filepath))
    	{   		
    		SoftReference<Bitmap> bmp = cache.get(filepath);
    		if (bmp.get() != null)
    		{
    			image.setImageBitmap(bmp.get());
				loader.setVisibility(View.GONE);
    			return new RemoteImageObject(image, filepath, loader, failed_to_load);
    		}    		
    	}    	
    	
    	// remove items from queue previously set to load this image
    	for(int j = 0; j < images.size();)
    	{
			if(images.get(j).view == image)
                images.remove(j);
            else
                ++j;
        }
		// add to queue
    	RemoteImageObject obj = new RemoteImageObject(image, filepath, loader, failed_to_load);
    	images.add(obj);

		if (!isRunning)
    		fetchNext();
    	
    	return obj;
    }
    
    public void clear()
    {
    	if (task != null && task.getStatus() != Status.FINISHED)
    		task.cancel(true);
    	
    	isRunning = false;
    	images.clear();
    	cache.clear();
	}
    
    private void fetchNext()
    {
		// lock
    	if (isRunning || images.size() == 0) 
    		return;
    	isRunning = true;
    	
    	// get the next image
    	currentImage = images.get(0);
    	
    	if (cache.containsKey(currentImage.filepath))
    	{
			// load from cache
    		SoftReference<Bitmap> bmp = cache.get(currentImage.filepath);
    		if (bmp.get() != null)
    		{
    			currentImage.view.setImageBitmap(bmp.get());

	    		images.remove(0);
	    		currentImage = null; 	
	    		isRunning = false;
	    		fetchNext();
    		}
    		else
    		{
    			cache.remove(currentImage.filepath);
    			task = new fetchBitmap();
    	    	task.execute(currentImage.filepath);
    		}
    		
    		return;
    	}
    	else if (currentImage.aborted)
    	{
			// download aborted
    		images.remove(0);
    		currentImage = null;	
    		isRunning = false;
    		fetchNext();
    	}
    	else
    	{
			// start new download
	    	task = new fetchBitmap();
	    	task.execute(currentImage.filepath);
    	}
    }
    
    private class fetchBitmap extends AsyncTask<String, Void, Bitmap>
    {
        protected Bitmap doInBackground(String... urls)
        {
        	if (isCancelled())
        		return null;
        	
            Bitmap bmp = BitmapUtils.getBitmapFromURL(urls[0]);
            return bmp;
        }
        
        protected void onPostExecute(Bitmap bmp)
        {	
        	// cache
        	cache.put(currentImage.filepath, new SoftReference<Bitmap>(bmp));
        	
        	// show image
        	if (!currentImage.aborted) {
					currentImage.view.setImageBitmap(bmp);
					currentImage.failed_to_load.setVisibility(View.GONE);
			}

			//if (bmp == null)
			//	currentImage.failed_to_load.setVisibility(View.VISIBLE);

			currentImage.loader.setVisibility(View.GONE);
        	
        	// clean up
        	currentImage = null;
        	images.remove(0);
        	
        	// get next image
        	isRunning = false;
        	fetchNext();
        }
    }   
    
    public class RemoteImageObject
    {
    	public ImageView view;
    	public String filepath;
    	public Boolean aborted = false;
		public RelativeLayout loader;
		TextView failed_to_load;
    	
    	public RemoteImageObject(ImageView view, String filepath, RelativeLayout loader, TextView failed_to_load)
    	{
    		this.view = view;
    		this.filepath = filepath;
			this.loader = loader;
			this.failed_to_load = failed_to_load;
    	}
    	
    	public void cancel()
    	{
    		aborted = true;
    	}
    }
}
