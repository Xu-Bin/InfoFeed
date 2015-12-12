package com.xubin.infofeed;

import java.net.URL;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {

    protected Bitmap doInBackground(String... urls) {
        //Map<String, Bitmap> iconmap = new HashTable<String, Bitmap>();
        Bitmap mIcon = null;


        try {
            java.io.InputStream in = new java.net.URL(urls[0]).openStream();
            mIcon = BitmapFactory.decodeStream(in);
            //iconmap.put(urls[i], mIcon)
        }catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        //publishProgress((int) ((i / (float) count) * 100));
        // Escape early if cancel() is called
        //if (isCancelled()) break;

        return mIcon;
    }


    // protected void onProgressUpdate(Integer... progress) {
    //     setProgressPercent(progress[0]);
    // }

    // protected void onPostExecute(Bitmap) {
    //     //showDialog("Downloaded " + result + " bytes");
    // }

}
