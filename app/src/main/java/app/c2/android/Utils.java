package app.c2.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.agrishare.MyApplication;
import app.agrishare.R;


public class Utils
{
	public static DateTimeFormatter JSON_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");


	public static String cleanFileName(String fileName)
	{
		String filename = "";
		filename = fileName.replace("/data/", "");
        filename = filename.replace(".png", "");
        filename = filename.replace(".jpg", "");
		String pattern = "[^a-z]";
		return filename.toLowerCase().replaceAll(pattern, "_");
	}

    public static String cleanFileName2(String fileName)
    {
        String filename = "";
        //filename = fileName.replace("/data/", "");
        String filename2 = fileName.replace( "http://seedcocdn.end.ai/data/", "");
        String filename3 = filename2.replace("/", "");
		String filename4 = filename3.replace("-Zoom", "");
       // filename = filename.replace(".png", "");
       // filename = filename.replace(".jpg", "");
        //String pattern = "[^a-z]";
        return filename4;
    }

	public static int getRawId(Context context, String filename)
	{
		String filename_without_extension = filename.split("\\.")[0];
		return context.getResources().getIdentifier(filename_without_extension, "raw", context.getPackageName());
	}

	public static int getRawId(Context context, String filename, int defaultId)
	{
		String thumb_id = Utils.cleanFileName(filename);
		int res_id = context.getResources().getIdentifier(thumb_id, "raw", context.getPackageName());
		if (res_id == 0)
			return defaultId;
		else
			return res_id;
	}

	public static Bitmap getBitmapFromView(ViewGroup v)
	{
		LayoutParams lp =
				new LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
		
		v.setLayoutParams(lp);
		
		int ms =  MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		v.measure(ms, ms);
		
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		
		Bitmap b = Bitmap.createBitmap(
				v.getMeasuredWidth(),
				v.getMeasuredHeight(),
				Bitmap.Config.ARGB_8888);
		
		Canvas c = new Canvas(b);
		v.draw(c);
		return b;
	}

	public static String formatDateAsString(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
	}

	public static String formatDateAsFriendlyString(Date date) {
		//return new SimpleDateFormat("d MMMM yyyy").format(date);
		//return new SimpleDateFormat("dd/MM/yy").format(date);
		return new SimpleDateFormat("dd MMM yyyy").format(date);
	}

	public static String formatDateAsDayString(Date date) {
		//return new SimpleDateFormat("d MMMM yyyy").format(date);
		//return new SimpleDateFormat("dd/MM/yy").format(date);
		return new SimpleDateFormat("EEEE").format(date);
	}

	public static String formatDateAsFriendlyTime(Date date) {
		//return new SimpleDateFormat("d MMMM yyyy").format(date);
		//return new SimpleDateFormat("dd/MM/yy").format(date);
		return new SimpleDateFormat("h:mm aa").format(date);
	}

	public static String formatDateAsFriendlyMonthString(Date date) {
		//return new SimpleDateFormat("d MMMM yyyy").format(date);
		//return new SimpleDateFormat("dd/MM/yy").format(date);
		return new SimpleDateFormat("MMM").format(date);
	}

	public static String formatDateAsFriendlyDateString(Date date) {
		//return new SimpleDateFormat("d MMMM yyyy").format(date);
		//return new SimpleDateFormat("dd/MM/yy").format(date);
		return new SimpleDateFormat("dd MMMM yyyy HH:mm").format(date);
	}

	public static String formatDateAsFriendlyTimeString(Date date) {
		//return new SimpleDateFormat("d MMMM yyyy").format(date);
		//return new SimpleDateFormat("dd/MM/yy").format(date);
		return new SimpleDateFormat("HH:mm").format(date);
	}

	public static String formatDateAsStringDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public static Date formatStringAsDate(String date_string) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date_string);
		//	return new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(date_string);
		} catch (ParseException ex) {
			Log.d("Parse Exception", ex.toString());
			return new Date();
		}
	}

	public static Date formatStringAsDate3(String date_string) {
		try {
			return new SimpleDateFormat("dd MMMM yyyy HH:mm").parse(date_string);
		} catch (ParseException ex) {
			Log.d("Parse Exception", ex.toString());
			//return new Date();
			return formatStringAsDate4(date_string);
		}
	}

	public static Date formatStringAsDate4(String date_string) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date_string);
		} catch (ParseException ex) {
			Log.d("Parse Exception", ex.toString());
			return new Date();
		}
	}

	public static Date formatStringAsDateMonth(String date_string) {
		try {
			//Log.d("CHAT TIME 2", ""+new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(date_string));
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date_string);
			//	return new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(date_string);
		} catch (ParseException ex) {
			Log.d("Parse Exception", ex.toString());
			return new Date();
		}
	}

	public static Date formatStringAsDate2(String date_string) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(date_string);
		} catch (ParseException ex) {
			return new Date();
		}
	}

	public static String makeFriendlyDateString(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		return Utils.formatDateAsFriendlyDateString(date);
	}

	public static String convertDate(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		long milliseconds = date.getTime();
		long seconds = milliseconds / 1000;

		if (seconds < 60)
			return "Just now";

		long minutes = seconds / 60;
		if (minutes < 60)
			return minutes + " minutes ago";

		long hours = minutes / 60;
		if (hours < 24)
			return hours + " hours ago";

		/*long days = hours / 24;
		if (days < 7)
			return days + " days ago";*/

		return Utils.formatDateAsFriendlyString(date);
	}

	public static String daysLeft(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		Date now = new Date();
		long milliseconds = date.getTime() - now.getTime();
		long seconds = milliseconds / 1000;
		Log.d("SECONDS LEFT", "" + seconds);

		if (seconds >= 0) {

			if (seconds < 60) {
				if (seconds == 1)
					return "1 second left";
				else
					return seconds + "seconds left";
			}

			long minutes = seconds / 60;
			if (minutes < 60) {
				if (minutes == 1)
					return "1 minute left";
				else
					return minutes + " minutes left";
			}

			long hours = minutes / 60;
			if (hours < 24) {
				if (hours == 1)
					return "1 hour left";
				else
					return hours + " hours left";
			}

			long days = hours / 24;
			if (days < 10000) {
				if (days == 1)
					return "1 day left";
				else
					return days + " days left";
			}

			return Utils.formatDateAsFriendlyString(date);
		}
		else {
			return "Ended on " + Utils.formatDateAsFriendlyString(date);
		}
	}

	public static String convertDateToFriendly(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		long milliseconds = date.getTime();

		Calendar inputTime = Calendar.getInstance();
		inputTime.setTimeInMillis(milliseconds);

		Calendar now = Calendar.getInstance();

		Calendar yesterdayCalendar = Calendar.getInstance();
		yesterdayCalendar.add(Calendar.DATE, -1);

		if (now.get(Calendar.DATE) == inputTime.get(Calendar.DATE) ) {
			return "Today";
		} else if (yesterdayCalendar.get(Calendar.DATE) == inputTime.get(Calendar.DATE)){
			return "Yesterday";
		}

		String formatted_date_string = Utils.formatDateAsFriendlyString(date);
		return formatted_date_string.replace("&nbsp;", " ");

	}

	public String getFormattedDate(Context context, long smsTimeInMilis) {
		Calendar smsTime = Calendar.getInstance();
		smsTime.setTimeInMillis(smsTimeInMilis);

		Calendar now = Calendar.getInstance();

		final String timeFormatString = "h:mm aa";
		final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
		final long HOURS = 60 * 60 * 60;
		if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
			return "Today " + DateFormat.format(timeFormatString, smsTime);
		} else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
			return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
		} else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
			return DateFormat.format(dateTimeFormatString, smsTime).toString();
		} else {
			return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
		}
	}

	public static void hideKeyboard(Activity act) {
		hideKeyboard(act, act.getCurrentFocus());
	}

	public static void hideKeyboard(Context context, View view) {
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static String timeAgo(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		long milliseconds = new Date().getTime() - date.getTime();
		long seconds = milliseconds / 1000;

		if (seconds < 60)
			return "Just now";

		long minutes = seconds / 60;
		if (minutes == 1)
			return "1 minute ago";
		if (minutes < 60)
			return minutes + " minutes ago";

		long hours = minutes / 60;
		if (hours == 1)
			return "1 hour ago";
		if (hours < 24)
			return hours + " hours ago";

		long days = hours / 24;
		if (days == 1)
			return "1 day ago";
		if (days < 7)
			return days + " days ago";

		return Utils.formatDateAsFriendlyString(date);
	}

	public static String timeAgoClean(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		long milliseconds = new Date().getTime() - date.getTime();
		long seconds = milliseconds / 1000;

		if (seconds < 60)
			return "Just now";

		long minutes = seconds / 60;
		if (minutes == 1)
			return "1min";
		if (minutes < 60)
			return minutes + "mins";

		long hours = minutes / 60;
		if (hours == 1)
			return "1h";
		if (hours < 24)
			return hours + "h";

		long days = hours / 24;
		if (days == 1)
			return "1d";
		if (days < 7)
			return days + "d";

		return Utils.formatDateAsFriendlyString(date);
	}

	public static String convertTime(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		return Utils.formatDateAsFriendlyTime(date);
	}

	public static String convertToFriendlyTime(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		return Utils.formatDateAsFriendlyTimeString(date);
	}

	public static String convertToJustMonth(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		return Utils.formatDateAsFriendlyMonthString(date);
	}

	public static String convertToJustDate(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		return Utils.formatDateAsFriendlyDateString(date);
	}

	public static String convertToJustTime(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		return Utils.formatDateAsFriendlyTimeString(date);
	}

	public static String stripOutTime(String raw_date)
	{
		Date date = Utils.formatStringAsDate(raw_date);
		return Utils.formatDateAsStringDate(date);
	}

	public static void displayImage(Context context, ImageView imageView, String url, String name, String type){
		File cacheDir = context.getCacheDir();
		Boolean found = false;
	/*	if (cacheDir.exists()) {
			for (File f : cacheDir.listFiles()) {
				//perform here your operation
				if (f.getName().equals(name + type)){
					Log.d("FOUND!", f.getName());
					found = true;
					//Picasso.with(context).load(f.getAbsolutePath()).into(imageView);
					Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
					imageView.setImageBitmap(myBitmap);
					break;
				}
			}
		}		*/

		File file = new File(cacheDir.getAbsolutePath() + "/" + name + type);
		if (file.exists()){
			Log.d("FOUND!", file.getName());
			found = true;
			Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			imageView.setImageBitmap(myBitmap);
		}

		if (!found){
			AssetManager mg = context.getResources().getAssets();
			InputStream is = null;
			try {
				is = mg.open("images/" + name + type);
				//File exists so do something with it
				Picasso.get().load("file:///android_asset/images/"  + name + type).into(imageView);
			} catch (IOException ex) {
				//file does not exist

				//load remotely
				Picasso.get().load(url).into(imageView);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException ex){
						Log.d("IOException", ex.getMessage());
					}
				}
			}
		}
	}

	public static void displayImageButDontSearchCacheDir(Context context, ImageView imageView, String url, String name, String type){
		AssetManager mg = context.getResources().getAssets();
		InputStream is = null;
		try {
			is = mg.open("images/" + name + type);
			//File exists so do something with it
			Picasso.get().load("file:///android_asset/images/"  + name + type).into(imageView);
		} catch (IOException ex) {
			//file does not exist try .jpg

			try {
				is = mg.open("images/" + name + ".jpg");
				//File exists so do something with it
				Picasso.get().load("file:///android_asset/images/"  + name + ".jpg").into(imageView);
			} catch (IOException exc) {
				//file does not exist

				//load remotely
				Picasso.get().load(url).into(imageView);
			}

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex){
					Log.d("IOException", ex.getMessage());
				}
			}
		}
	}

	public static int dpToPx(float dp, Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int px = (int) Math.round(dp * (displayMetrics.ydpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	public static String getMimeType(Uri uri, Context context) {
		String mimeType = null;
		if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
			ContentResolver cr = context.getContentResolver();
			mimeType = cr.getType(uri);
		} else {
			String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
					.toString());
			mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
					fileExtension.toLowerCase());
		}
		return mimeType;
	}

	public static Spanned formatText(String name, String message, String date, Context context){
		Spanned html_title = Html.fromHtml("<b>" + name + "</b>" + "<font color=\"" + getMessageHtmlColor(context) + "\"> " + message+ "</font><font color=\"" + getDateHtmlColor(context) + "\"><b> "+ date + "</b></font>");
		return html_title;
	}

	public static String getMessageHtmlColor(Context context){
		int orange = context.getResources().getColor(R.color.row_title_grey);
		String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(orange), Color.green(orange), Color.blue(orange))));
		return htmlColor;
	}

	public static  String getDateHtmlColor(Context context){
		int orange = context.getResources().getColor(R.color.row_title_grey);
		String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(orange), Color.green(orange), Color.blue(orange))));
		return htmlColor;
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

		Matrix matrix = new Matrix();
		switch (orientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				return bitmap;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				matrix.setScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.setRotate(180);
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				matrix.setRotate(180);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				matrix.setRotate(90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.setRotate(90);
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				matrix.setRotate(-90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.setRotate(-90);
				break;
			default:
				return bitmap;
		}
		try {
			Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bitmap.recycle();
			return bmRotated;
		}
		catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void colorHashtags(TextView textView){
		SpannableString hashText = new SpannableString(textView.getText().toString());
		Matcher matcher = Pattern.compile("#([A-Za-z0-9_-]+)").matcher(hashText);
		while (matcher.find()) {
			hashText.setSpan(new ForegroundColorSpan(Color.BLACK), matcher.start(), matcher.end(), 0);
		}
		textView.setText(hashText);
	}

	public static boolean isScreenDiagonalInchesGreaterThan(double valueInInches, Activity activity){
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		float yInches= metrics.heightPixels/metrics.ydpi;
		float xInches= metrics.widthPixels/metrics.xdpi;
		double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
		Log.d("DIAGONAL INCHES", "" + diagonalInches + " ");
		if (diagonalInches>=valueInInches){
			//  device bigger
			return true;
		}else{
			// smaller device
			return false;
		}
	}

	public static int convertDPtoPx(int dp_value, Context context){
		final float scale = context.getResources().getDisplayMetrics().density;
		int value_in_px = (int) (dp_value * scale + 0.5f);
		return value_in_px;
	}

	public static int pixelsToSp(int px, Context context) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return  px/((int) scaledDensity);
	}

	public static void shareOtherUserPost(String media_type, ImageView imageView, String caption, String username, String media_thumb, long postId,  Context context){
		if(media_type.equals(".jpeg") || media_type.equals(".jpg") || media_type.equals(".JPEG")
				|| media_type.equals(".JPG") || media_type.equals(".png") || media_type.equals(".PNG")) {
			Uri bmpUri = getLocalBitmapUri(imageView);
			if (bmpUri != null) {
				// Construct a ShareIntent with link to image
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_TEXT,  username + ": \"" + caption  + "\"\n" + "Sent via Keepnet Social App. Get the app from www.c2.co.zw");
				shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
				shareIntent.setType("image/*");
				// Launch sharing dialog for image
				context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
			} else {
				// ...sharing failed, handle error
				Log.d("Sharing image failed", "Bitmap is null");
			}
		}
		else {
			Uri bmpUri = getLocalBitmapURiFromRemoteURL(media_thumb);
			if (bmpUri != null) {
				// Construct a ShareIntent with link to image
				Intent shareVideoIntent = new Intent();
				shareVideoIntent.setAction(Intent.ACTION_SEND);
				shareVideoIntent.putExtra(Intent.EXTRA_TEXT, username + ": \"" + caption  + "\" " + "http://keepnet.co/appredirect?r=keepnetsocial://post?id=" + postId + "\n"  + "Sent via Keepnet Social App. Get the app from www.c2.co.zw");
				shareVideoIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
				shareVideoIntent.setType("image/*");
				// Launch sharing dialog for image
				context.startActivity(Intent.createChooser(shareVideoIntent, "Share Post"));
			} else {
				// ...sharing failed, handle error
				Log.d("Sharing image failed", "Bitmap is null");
			}
		}

	}

	public static void share(String media_type, ImageView imageView, String caption, String media_thumb, long postId, Context context){
		if(media_type.equals(".jpeg") || media_type.equals(".jpg") || media_type.equals(".JPEG")
				|| media_type.equals(".JPG") || media_type.equals(".png") || media_type.equals(".PNG")) {
			Uri bmpUri = getLocalBitmapUri(imageView);
			if (bmpUri != null) {
				// Construct a ShareIntent with link to image
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_TEXT, caption  + "\n" + "Sent via Econet Marathon Diary App. Get the app from www.c2.co.zw");
				shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
				shareIntent.setType("image/*");
				// Launch sharing dialog for image
				context.startActivity(Intent.createChooser(shareIntent, "Share Post"));
			} else {
				// ...sharing failed, handle error
				Log.d("Sharing image failed", "Bitmap is null");
			}
		}
		else {
			Uri bmpUri = getLocalBitmapURiFromRemoteURL(media_thumb);
			if (bmpUri != null) {
				// Construct a ShareIntent with link to image
				Intent shareVideoIntent = new Intent();
				shareVideoIntent.setAction(Intent.ACTION_SEND);
				shareVideoIntent.putExtra(Intent.EXTRA_TEXT, caption + " http://marathon.econetapps.com/appredirect?r=ecorunnerpost://post?id=" + postId + "\n" + "Sent via Econet Marathon Diary App. Get the app from www.c2.co.zw");
				shareVideoIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
				shareVideoIntent.setType("image/*");
				// Launch sharing dialog for image
				context.startActivity(Intent.createChooser(shareVideoIntent, "Share Post"));
			} else {
				// ...sharing failed, handle error
				Log.d("Sharing image failed", "Bitmap is null");
			}
		}
	}

	public static Uri getLocalBitmapUri(ImageView imageView) {
		// Extract Bitmap from ImageView drawable
		Drawable drawable = imageView.getDrawable();
		Bitmap bmp = null;
		if (drawable instanceof BitmapDrawable){
			bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		} else {
			return null;
		}
		// Store image to default external storage directory
		Uri bmpUri = null;
		try {
			File file =  new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
			bmpUri = Uri.fromFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmpUri;
	}

	public static Uri getLocalBitmapURiFromRemoteURL(String url){
		Bitmap bmp = getBitmapFromURL(url);
		// Store image to default external storage directory
		Uri bmpUri = null;
		try {
			File file =  new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
			bmpUri = Uri.fromFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmpUri;
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			// Log exception
			return null;
		}
	}


	public static float getScreenWidth(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		float pxWidth = outMetrics.widthPixels;
		return pxWidth;
	}

	public static void stripUnderlines(TextView textView) {
		Spannable s = new SpannableString(textView.getText());
		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
		for (URLSpan span: spans) {
			int start = s.getSpanStart(span);
			int end = s.getSpanEnd(span);
			s.removeSpan(span);
			span = new URLSpanNoUnderline(span.getURL());
			s.setSpan(span, start, end, 0);
		}
		textView.setText(s);
	}

	public static void prepareHashtag(TextView textView) {
		Pattern wikiWordMatcher = Pattern.compile("#([A-Za-z0-9]+)");
		String wikiViewURL = "keepnetsocial://tag?text=";
		Linkify.addLinks(textView, wikiWordMatcher, wikiViewURL);
		textView.setLinkTextColor(Color.parseColor("#000000"));
		textView.setTypeface(MyApplication.typeFace);
		stripUnderlines(textView);
	}

	public static void prepareClickableUsername(TextView textView, String username) {
		Pattern wikiWordMatcher = Pattern.compile("^(" + username + ")\\s");
		String wikiViewURL = "keepnetsocial://user?username=";
		Linkify.addLinks(textView, wikiWordMatcher, wikiViewURL);
		textView.setLinkTextColor(Color.parseColor("#1867ae"));
		stripUnderlines(textView);
	}

	public static void prepareClickableFullname(TextView textView, String username, String fullname) {
		Pattern wikiWordMatcher = Pattern.compile("^(" + fullname + ")\\s");
		String wikiViewURL = "keepnetsocial://user?username=";
		Linkify.addLinks(textView, wikiWordMatcher, wikiViewURL);
		textView.setLinkTextColor(Color.parseColor("#1867ae"));
		stripUnderlines(textView);
	}

	public static long stringDateTimeToMillis(String raw_date){
		Date date = Utils.formatStringAsDate(raw_date);
		long milliseconds = date.getTime();
		return milliseconds;
	}

	public static void setRowPadding(int position, int size, RelativeLayout parent_container, Context context){
		int padding = convertDPtoPx(16, context);
		int row_spacing = convertDPtoPx(4, context);
		int extra_padding = convertDPtoPx(16, context);
		if(position == 0){
			parent_container.setPadding(padding, extra_padding, padding, row_spacing);
		}
		else if(position == size - 1){
			parent_container.setPadding(padding, row_spacing, padding, extra_padding);
		}
		else {
			parent_container.setPadding(padding, row_spacing, padding, row_spacing);
		}
	}

	public static String getDateTimeNowAsString(Context context){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String stringDateTimeNow = df.format(c.getTime());
		return stringDateTimeNow;
	}

	public static String getCalendarDateAsString(Context context, Calendar c){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		String stringDateToday = dateFormat.format(calendar.getTime());

		SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
		String stringDate = df.format(c.getTime());

		if (stringDate.equals(stringDateToday))
			return "TODAY";
		else
			return stringDate;
	}

	public static String getCalendarAsStringDateTime(Context context, Calendar c){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String stringDateTimeNow = df.format(c.getTime());
		return stringDateTimeNow;
	}

	public static String getThumbPath(String photo){
		try {
			JSONObject jsonObject = new JSONObject(photo);
			return jsonObject.optString("Thumb");
		} catch (JSONException ex){
			Log.d("Photo JSON Exception: ", ex.getMessage());
			return "";
		}
	}

	public static String getFirstThumbPath(String photo){
		try {
			JSONArray jsonArray = new JSONArray(photo);
			if (jsonArray.length() > 0) {
				return jsonArray.getJSONObject(0).optString("Thumb");
			}
			else
				return "";
		} catch (JSONException ex){
			Log.d("Photo JSON Exception: ", ex.getMessage());
			return "";
		}
	}

	public static boolean isVideo(String mediaObject){
		try {
			JSONObject jsonObject = new JSONObject(mediaObject);
			String type = jsonObject.optString("Type");
			if (type.equals(".mp4") || type.equals(".mov"))
				return true;
			else
				return false;
		} catch (JSONException ex){
			return false;
		}
	}

	public static String getZoomPath(String photo){
		try {
			JSONObject jsonObject = new JSONObject(photo);
			return jsonObject.optString("FilePath");
		} catch (JSONException ex){
			Log.d("Photo JSON Exception: ", ex.getMessage());
			return "";
		}
	}

	public static String getValue(String objectString, String keyfield){
		try {
			JSONObject jsonObject = new JSONObject(objectString);
			return jsonObject.optString(keyfield);
		} catch (JSONException ex){
			Log.d("Value JSON Exception: ", ex.getMessage());
			return "";
		}
	}

	public static double getDoubleValue(String userObject, String keyfield){
		try {
			JSONObject jsonObject = new JSONObject(userObject);
			return jsonObject.optDouble(keyfield);
		} catch (JSONException ex){
			Log.d("Value JSON Exception: ", ex.getMessage());
			return 0.0;
		}
	}

	public static long getLongValue(String userObject, String keyfield){
		try {
			JSONObject jsonObject = new JSONObject(userObject);
			return jsonObject.optLong(keyfield);
		} catch (JSONException ex){
			Log.d("Value JSON Exception: ", ex.getMessage());
			return 0;
		}
	}

	public static JSONObject getJSONObject(String userObject, String keyfield){
		try {
			JSONObject jsonObject = new JSONObject(userObject);
			return jsonObject.optJSONObject(keyfield);
		} catch (JSONException ex){
			Log.d("getJSONObject Excep: ", ex.getMessage());
			return null;
		}
	}

	public static double getRatio(String photo){
		try {
			JSONObject jsonObject = new JSONObject(photo);
			return jsonObject.optDouble("Ratio");
		} catch (JSONException ex){
			Log.d("Photo JSON Exception: ", ex.getMessage());
			return 0;
		}
	}

	public static void openURL(String url, Context context){
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			url = "http://" + url;
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(browserIntent);
	}

	public static String getUnit(int value){
		switch (value) {
			case 0:
				return "None";
			case 1:
				return "KG";
			case 2:
				return "LB";
			case 3:
				return "CM";
			case 4:
				return "MM";
			case 5:
				return "IN";
			default:
				return "None";
		}
	}

	public static boolean isListViewScrolledAllTheWayDown(ListView listView){
		if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() -1 &&
				listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight())
		{
			//It is scrolled all the way down here
			return true;
		}
		else
			return false;
	}

	public static boolean isListViewScrolledAllTheWayUp(ListView listView){
		if (listView != null) {
			if (listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() >= 0) {
				//It is scrolled all the way up here
				return true;
			} else
				return false;
		}
		else
			return false;
	}

	/**** Method for Setting the Height of the ListView dynamically.
	 **** Hack to fix the issue of not showing all the items of the ListView
	 **** when placed inside a ScrollView  ****/
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

}
