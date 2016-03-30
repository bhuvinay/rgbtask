package com.example.demo.rgptaskapp.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.rgptaskapp.R;
import com.example.demo.rgptaskapp.networks.ClientGenerator;
import com.example.demo.rgptaskapp.networks.RequestBuilder;
import com.example.demo.rgptaskapp.networks.ResponseListener;
import com.example.demo.rgptaskapp.utils.Constants;
import com.example.demo.rgptaskapp.utils.Utility;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RetrofitError;

/**
 * Created by vinaypratap on 30/3/16.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTextResult;
    private EditText mInputTimeInt;
    private Button mStartRequest, mEndRequest;
    private ProgressBar progressBar;
    private ArrayList<String> list = new ArrayList<>(Constants.MAX_SIZE);
    private StringBuilder stringBuilder;
    private SharedPreferences sharedPreferences;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_home);
        init();
    }

    private void init() {
        mTextResult = (TextView) findViewById(R.id.text_result);
        mInputTimeInt = (EditText) findViewById(R.id.input_timeinterval);
        mStartRequest = (Button) findViewById(R.id.start_request);
        mEndRequest = (Button) findViewById(R.id.end_request);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mStartRequest.setOnClickListener(this);
        mEndRequest.setOnClickListener(this);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String savedValue = sharedPreferences.getString(Constants.sharedPrefKey, null);
        if (Constants.DEBUG)
            Log.d(Constants.TAG, "savedValue-  " + savedValue);
        fillList(savedValue);

    }

    private void fillList(String savedValue) {
        if (savedValue != null) {
            String[] sArray = savedValue.split(",");
            for (String str : sArray) {
                list.add(str);
            }
            if (Constants.DEBUG)
                Log.d(Constants.TAG, "list-  " + list.toString());
        }
    }


    private void startRecurTimer(int timeInterVal) {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ResponseListener.cancelAll();  // cancell all previous requests
                RequestBuilder requestBuilder = ClientGenerator.createService(RequestBuilder.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                requestBuilder.getData(new ResponseListener<String>("myRequest") {
                    @Override
                    public void onSuccess(String s) {
                        mTextResult.setText("[" + s + "]");
                        progressBar.setVisibility(View.INVISIBLE);
                        addToList(s);  // add result to list
                    }

                    @Override
                    public void onError(RetrofitError error) {

                        if (error.isNetworkError()) {
                            if (error.getCause() instanceof ConnectException) {  // no connection
                                Toast.makeText(HomeActivity.this, R.string.toast_error_no_network, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                endReq();
                                if (list.size() != 0)
                                    mTextResult.setText(list.toString()); //show all 5 values from list or what ever we have
                                else
                                    mTextResult.setText(R.string.nothing_to_show);

                            } else if (error.getCause() instanceof SocketTimeoutException) { //timeout condition
                                //handle time out here
                                Toast.makeText(HomeActivity.this, R.string.toast_error_time_out, Toast.LENGTH_SHORT).show();
                                if (list.size() >= 3)
                                    mTextResult.setText(list.subList(0, 3).toString()); // Showing only past 3 numbers
                                else if (list.size() != 0)
                                    mTextResult.setText(list.toString()); // if numbers are less than 3 show what we have
                                else
                                    mTextResult.setText(R.string.nothing_to_show); // no number to show
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, R.string.toast_error, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                });
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, timeInterVal);
    }

    private void addToList(String s) {
        if (list.size() == Constants.MAX_SIZE)
            list.remove(Constants.MAX_SIZE - 1);
        list.add(0, s);
    }

    private void endReq() {
        if (timer != null)
            timer.cancel();
        ResponseListener.cancelAll();
        mInputTimeInt.setHint(R.string.input_time_interval);
    }

    @Override
    protected void onStop() {
        endReq();
        stringBuilder = new StringBuilder();
        for (String str : list) {
            stringBuilder.append(str);
            stringBuilder.append(",");
        }
        String sList = stringBuilder.toString();
        if (Constants.DEBUG)
            Log.d(Constants.TAG, "onStop: sList-  " + sList);
        saveToSharedPref(sList);
        super.onStop();
    }

    private void saveToSharedPref(String sList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.sharedPrefKey, sList);
        editor.commit();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.start_request:
                if (TextUtils.isEmpty(mInputTimeInt.getText().toString())) {
                    Toast.makeText(HomeActivity.this, R.string.toast_empty_field, Toast.LENGTH_SHORT).show();
                } else {
                    // if already running clean it and restart startrecurTimer with new value
                    endReq();
                    String timeIntVal = mInputTimeInt.getText().toString();
                    int timeIntervalMiliSec = Integer.valueOf(timeIntVal);
                    mInputTimeInt.setText(null);
                    Utility.hideKeyboard(view, HomeActivity.this);
                    if (timeIntervalMiliSec >= 1) {
                        startRecurTimer(timeIntervalMiliSec * 1000);
                        mInputTimeInt.setHint(R.string.task_running);
                    } else
                        Toast.makeText(HomeActivity.this, R.string.toast_input_one_or_more, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.end_request:
                endReq();
                mTextResult.setText(R.string.result_text);
                break;
        }

    }
}