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

public class MainActivity extends FragmentActivity {

    public static final String TAG = "InfoFeed";

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

}
