package com.xubin.infofeed;

import java.util.ArrayList;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.util.TypedValue;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import android.text.TextUtils;
import android.os.Bundle;

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

public class MainActivity extends ActionBarActivity {

    public static final String TAG = "InfoFeed";

    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;

    // private static boolean wifiConnected = false;
    // private static boolean mobileConnected = false;
    ConnStatusChangeReceiver receiver = new ConnStatusChangeReceiver();
    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ADD lISTERNER FOR FEED BUTTON */
        final Button feedButton = (Button)findViewById(R.id.feed_button);
        feedButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                weiboUpdate();
            }
        });

        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(receiver, filter);

        drawConnFragment();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submenu_weibo:
                mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
                mSsoHandler = new SsoHandler(MainActivity.this, mAuthInfo);

                mSsoHandler.authorize(new AuthListener());

                Toast.makeText(MainActivity.this, "Click weibo now", Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void drawConnFragment(){
        SimpleTextFragment connFragment = (SimpleTextFragment)getSupportFragmentManager().findFragmentById(R.id.test_conn_fragment);
        connFragment.setText(getNetworkConnectionType());
        connFragment.getTextView().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f);
    }

    protected void weiboUpdate(){
        Oauth2AccessToken wbAccessToken = AccessTokenKeeper.readWeiboAccessToken(MainActivity.this);

        String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(wbAccessToken.getExpiresTime()));
        Log.i(TAG, date);
        // 对statusAPI实例化
        StatusesAPI wbStatusesAPI = new StatusesAPI(MainActivity.this, Constants.APP_KEY, wbAccessToken);

        RequestListener mListener = new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    Log.i(TAG, response);
                    if (response.startsWith("{\"statuses\"")) {
                        // 调用 StatusList#parse 解析字符串成微博列表对象
                        StatusList statuses = StatusList.parse(response);
                        if (statuses != null && statuses.total_number > 0) {

                            ArrayList<String>  al = new ArrayList<String>();
                            for (Status s : statuses.statusList){
                                al.add(s.text);
                            }

                            ListView weibolv = (ListView)findViewById(R.id.list);
                            ArrayAdapter<String> weiboaa = new ArrayAdapter<String> (MainActivity.this, android.R.layout.simple_list_item_1, al);
                            weibolv.setAdapter(weiboaa);



                            Toast.makeText(MainActivity.this,
                                    "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else if (response.startsWith("{\"created_at\"")) {
                        // 调用 Status#parse 解析字符串成微博对象
                        Status status = Status.parse(response);
                        Toast.makeText(MainActivity.this,
                                "发送一送微博成功, id = " + status.id,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Log.e(TAG, e.getMessage());
                ErrorInfo info = ErrorInfo.parse(e.getMessage());
                Toast.makeText(MainActivity.this, info.toString(), Toast.LENGTH_LONG).show();
            }
        };

        //if (wbAccessToken != null && wbAccessToken.isSessionValid()){
        if (wbAccessToken != null){
            wbStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);
        }
    }

    @Override
	protected void onStart(){
		Log.i(TAG, "Entering onStart");
		super.onStart();
	}

	@Override
	protected void onResume(){
		Log.i(TAG, "Entering onResume");
		super.onResume();
	}

	@Override
	protected void onPause(){
		Log.i(TAG, "Entering onPause");
		super.onResume();
	}

	@Override
	protected void onStop(){
		Log.i(TAG, "Entering onStop");
		super.onResume();
	}

	@Override
	protected void onDestroy(){
		Log.i(TAG, "Entering onDestroy");
		super.onResume();
        this.unregisterReceiver(receiver);
	}

    protected String getNetworkConnectionType() {
        String connInfo = "Init State....So Strange if you see this line";
        ConnectivityManager connMgr =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI){
                connInfo = "WIFI";
            } else if (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                connInfo = activeInfo.getSubtypeName();
            }
        } else {
            connInfo = "No Connection Now";
        }
        return connInfo;
    }

    class ConnStatusChangeReceiver extends BroadcastReceiver {
        public static final String TAG = "ConnStatusChangeReceiver";

        @Override
        public void onReceive(Context context, Intent intent){
            drawConnFragment();
            Log.i(TAG, "Entering onReceive");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {

            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            String  phoneNum =  mAccessToken.getPhoneNum();


            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeWeiboAccessToken(MainActivity.this, mAccessToken);
                Toast.makeText(MainActivity.this, "Haha...", Toast.LENGTH_LONG).show();

            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                Toast.makeText(MainActivity.this, "code....", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this,
                    "CANCEL" , Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(MainActivity.this,
                     "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
    }

}
