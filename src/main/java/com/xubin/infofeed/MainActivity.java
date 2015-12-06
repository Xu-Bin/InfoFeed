package com.xubin.infofeed;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.support.v7.app.ActionBarActivity;

import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;

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

    public class ConnStatusChangeReceiver extends BroadcastReceiver {
        public static final String TAG = "ConnStatusChangeReceiver";

        @Override
        public void onReceive(Context context, Intent intent){
            drawConnFragment();
            Log.i(TAG, "Entering onReceive");
        }
    }


    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //     super.onActivityResult(requestCode, resultCode, data);
    //
    //     // SSO 授权回调
    //     // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
    //     if (mSsoHandler != null) {
    //         mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    //     }
    //
    // }


    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            String  phoneNum =  mAccessToken.getPhoneNum();


            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                // updateTokenView(false);
                Toast.makeText(MainActivity.this, "Haha...", Toast.LENGTH_LONG).show();
                // // 保存 Token 到 SharedPreferences
                // AccessTokenKeeper.writeAccessToken(WBAuthActivity.this, mAccessToken);
                // Toast.makeText(WBAuthActivity.this,
                //         R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                // String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                // if (!TextUtils.isEmpty(code)) {
                //     message = message + "\nObtained the code: " + code;
                // }
                Toast.makeText(MainActivity.this, "code....", Toast.LENGTH_LONG).show();
                Log.i(TAG, "code = " + code);
            }
        }

        @Override
        public void onCancel() {
            // Toast.makeText(WBAuthActivity.this,
            //        R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
             Toast.makeText(MainActivity.this,
                     "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
