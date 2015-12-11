package com.xubin.infofeed;

import java.util.ArrayList;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.widget.ListView;

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
    private static final String TAG = "WeiboHelper";
    private Activity activity;
    private ArrayList<RowStructure> rowList;
    private RowArrayAdapter<RowStructure> raa;

    private static WeiboHelper weiboHelper = null;

    private WeiboHelper(Activity activity){
        this.activity = activity;
    }

    public static WeiboHelper getInstance(Activity activity){
        if (null == weiboHelper){
            weiboHelper = new WeiboHelper(activity);
        }
        return weiboHelper;
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

                            ArrayList<String> al = new ArrayList<String>();

                            for (Status s : statuses.statusList){
                                rowList.add(new RowStructure(s.user.profile_image_url, s.user.name, s.created_at, s.text));
                                al.add(s.text);
                            }

                            if (null ==raa){
                                raa = new RowArrayAdapter<RowStructure>(activity, rowList);
                            } else{
                                raa.setData(rowList);
                                raa.notifyDataSetChanged();
                            }


                            ListView weibolv = (ListView)activity.findViewById(R.id.list);
                            //ArrayAdapter<RowStructure> weiboAdapte = new ArrayAdapter<RowStructure> (context, R.layout.list_row, rowList);

                            weibolv.setAdapter(new android.widget.ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, al));
                            //activity.removeView(weibolv);
                            //activity.setContentView(weibolv);

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

}
