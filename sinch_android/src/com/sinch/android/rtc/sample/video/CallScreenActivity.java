package com.sinch.android.rtc.sample.video;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;
import com.squareup.picasso.Picasso;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends BaseActivity implements TextFragment.OnDataPass {

    static final String TAG = CallScreenActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;

    private String mCallId;
    private long mCallStart = 0;
    private boolean mAddedListener = false;
    private boolean mVideoViewsAdded = false;
    private boolean mCalling = false;
    private Context mContext;
    private MediaRecorder mediaRecorder;
    private String outputFile = null;

    ImageView view ;
    RequestQueue rq;
    Timer timer;
    FrameLayout mFragment_container;
    FragmentManager manager;

    @Override
    public void onDataPass(String result) {
        if (result.equals(Utilities.SWITCH_MODE_COMM))
        {
            FragmentTransaction transaction = manager.beginTransaction();
            Log.d("break", "Reached this line");
            transaction.replace(R.id.fragment_container, new ImageFragment());
            transaction.commit();

        }
    }




    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(CALL_START_TIME, mCallStart);
        savedInstanceState.putBoolean(ADDED_LISTENER, mAddedListener);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCallStart = savedInstanceState.getLong(CALL_START_TIME);
        mAddedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);

        mAudioPlayer = new AudioPlayer(this);
        mFragment_container =(FrameLayout)findViewById(R.id.fragment_container);
        manager = getFragmentManager();
        mediaRecorder = new MediaRecorder();
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);

        Button endCallButton = (Button) findViewById(R.id.hangupButton);



        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        if (savedInstanceState == null) {
            mCallStart = System.currentTimeMillis();
        }
        mContext = getApplicationContext();
        rq = Volley.newRequestQueue(this);

        timer = new Timer(true);

    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            if (!mAddedListener) {
                call.addCallListener(new SinchCallListener());
                mAddedListener = true;
                Log.d("app", "Service connected");

            } else {
                Log.e(TAG, "Started with invalid callId, aborting.");
                finish();
            }
            updateUI();
        }
    }

    private void updateUI() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {

            if (call.getState() == CallState.ESTABLISHED) {
                addVideoViews();
              }
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        //mDurationTask.cancel();
//        mTimer.cancel();
        timer.cancel();
        removeVideoViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        //mTimer = new Timer();
        //mDurationTask = new UpdateCallDurationTask();
        //mTimer.schedule(mDurationTask, 0, 500);
        view = (ImageView) findViewById(R.id.remoteVideo);
        updateUI();
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void addVideoViews() {
        if (mVideoViewsAdded || getSinchServiceInterface() == null) {
            return; //early
        }

        final VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.addView(vc.getLocalView());
            localView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vc.toggleCaptureDevicePosition();
                    mVideoViewsAdded = true;
                }
            });


        }
    }

    private void removeVideoViews() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }

    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            //mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            mCallStart = System.currentTimeMillis();
            Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());

            if (mFragment_container != null)
            {
                TextFragment textFragment = new TextFragment();
                textFragment.setArguments(getIntent().getExtras());
                manager.beginTransaction().add(R.id.fragment_container,textFragment).commit();
            }



        }



        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(TAG, "Video track added");
            addVideoViews();
        }
    }


}
