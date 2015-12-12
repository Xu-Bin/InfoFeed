package com.xubin.infofeed;

import com.xubin.infofeed.database.IconOpenHelper;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.widget.ListView;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.database.sqlite.SQLiteDatabase;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.net.RequestListener;

import android.app.Activity;

public class WeiboHelper {

    /* TODO:
     0. add image into rows;
     1. sort according to timestamp
     2. swift cache structure
    */
    private static final String TAG = "WeiboHelper";
    private Activity activity;
    //private SQLiteDatabase db = (new IconOpenHelper(activity)).getWritableDatabase();
    private ArrayList<RowStructure> rowList;
    private RowArrayAdapter<RowStructure> raa;

    private static WeiboHelper weiboHelper = null;

    private WeiboHelper(Activity activity){
        this.activity = activity;
    }

    public static WeiboHelper getInstance(Activity activity){
        return new WeiboHelper(activity);
    }

    protected void weiboUpdate(){
        Oauth2AccessToken wbAccessToken = AccessTokenKeeper.readWeiboAccessToken(activity);

        String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(wbAccessToken.getExpiresTime()));

        // 对statusAPI实例化
        StatusesAPI wbStatusesAPI = new StatusesAPI(activity, Constants.APP_KEY, wbAccessToken);

        RequestListener mListener = new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    Log.i(TAG, response);
                    if (response.startsWith("{\"statuses\"")) {
                        // 调用 StatusList#parse 解析字符串成微博列表对象
                        StatusList statuses = StatusList.parse(response);
                        if (statuses != null && statuses.total_number > 0) {

                            if (null == rowList){
                                rowList = new ArrayList<RowStructure>();
                            }

                            for (Status s : statuses.statusList){
                                rowList.add(new RowStructure(getIcon(s.user.profile_image_url), s.user.name, s.created_at, s.text));
                            }

                            if (null ==raa){
                                raa = new RowArrayAdapter<RowStructure>(activity, rowList);
                            } else{
                                raa.addAll(rowList);
                            }

                            ListView weibolv = (ListView)activity.findViewById(R.id.list);
                            weibolv.setAdapter(raa);


                            Toast.makeText(activity,
                                    "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else if (response.startsWith("{\"created_at\"")) {
                        // 调用 Status#parse 解析字符串成微博对象
                        Status status = Status.parse(response);
                        Toast.makeText(activity,
                                "发送一送微博成功, id = " + status.id,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Log.e(TAG, e.getMessage());
                ErrorInfo info = ErrorInfo.parse(e.getMessage());
                Toast.makeText(activity, info.toString(), Toast.LENGTH_LONG).show();
            }
        };

        //if (wbAccessToken != null && wbAccessToken.isSessionValid()){
        if (wbAccessToken != null){
            wbStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);
        }
    }

    protected Bitmap getIcon(String icon_url){
        Bitmap bm = null;
        AsyncTask<String, Integer, Bitmap> mtask = new DownloadImageTask().execute(icon_url);
        try {
            bm = mtask.get(2000, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            Log.i(TAG, e.getMessage());
        }

        return bm;
    }
}
