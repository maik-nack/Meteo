package com.maiknack.meteo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.maiknack.meteo.pref.PrefActivity;

import static com.maiknack.meteo.Utils.sleepMillisec;

public class MainActivity extends AppCompatActivity {

    TcpClient mTcpClient;
    ConnectTask mConTask;
    TextView mTextResult;
    SharedPreferences mPref;
    Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextResult = (TextView) findViewById(R.id.text_view_result);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mTcpClient = new TcpClient();
        mSnackbar =
                Snackbar.make(findViewById(R.id.coordinator),
                        getString(R.string.connection_fail),
                        Snackbar.LENGTH_INDEFINITE);
        View vSnackbar = mSnackbar.getView();
        vSnackbar.setBackgroundColor(getResources().getColor(R.color.colorRed));
        TextView tv =
                (TextView) vSnackbar.findViewById(com.google.android.material.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTcpClient != null) {
            String address = mPref.getString(getString(R.string.address_key), "");
            String port = mPref.getString(getString(R.string.port_key), "0");
            mTcpClient.setServerSettings(address, Integer.parseInt(port));
            mConTask = new ConnectTask();
            mConTask.execute();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTcpClient != null) {
            mTcpClient.stopClient();
            mConTask.cancel(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, "Preferences");
        mi.setIntent(new Intent(this, PrefActivity.class));
        return super.onCreateOptionsMenu(menu);
    }

    class ConnectTask extends AsyncTask<Short, Short, TcpClient> {

        @Override
        protected TcpClient doInBackground(Short... message) {

            mTcpClient.setListener(new TcpClient.TCPListener() {
                @Override
                public void messageReceived(Short... message) {
                    publishProgress(message);
                }

                @Override
                public void connectionFailed() {
                    if (!mSnackbar.isShown()) mSnackbar.show();
                }

                @Override
                public void connectionSuccessful() {
                    if (mSnackbar.isShown()) mSnackbar.dismiss();
                }
            });

            while (!isCancelled() && !mTcpClient.run()) {
                sleepMillisec(5000);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Short... values) {
            super.onProgressUpdate(values);
            double temp = values[0] / 100.0, press = values[1] / 10.0;
            mTextResult.setText(getString(R.string.result, temp, press));
        }
    }

}
