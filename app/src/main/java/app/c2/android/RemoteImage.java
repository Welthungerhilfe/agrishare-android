package app.c2.android;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class RemoteImage 
{
	private ImageView image;
	private fetchBitmap task;
	private String filepath;
	
	public RemoteImage(ImageView image, String filepath)
	{
		this.image = image;
		this.filepath = filepath;		
    }
	
	public void fetch()
	{
		task = new fetchBitmap();
		task.execute(filepath);
	}
	
	public void cancel()
	{
		task.cancel(true);
		image.setImageBitmap(null);
	}

    private class fetchBitmap extends AsyncTask<String, Void, Bitmap>
    {
        protected Bitmap doInBackground(String... urls)
        {
            Bitmap bmp = BitmapUtils.getBitmapFromURL(urls[0]);
            return bmp;
        }
        protected void onPostExecute(Bitmap bmp)
        {	
            image.setImageBitmap(bmp);
        }
    }    
}
