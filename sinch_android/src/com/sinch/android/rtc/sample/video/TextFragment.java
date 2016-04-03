package com.sinch.android.rtc.sample.video;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RequestQueue requestQueue;
    TextView chatText;
    OnDataPass dataPass;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ////////////////////////////////////////////////////////////////////////////////////////////
    int counter;
    ////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TextFragment newInstance(String param1, String param2) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TextFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        counter =1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        chatText = (TextView) view.findViewById(R.id.remoteChat);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Timer timer= new Timer(true);
        TimerTask timerTask = new FetchChat();
        timer.schedule(timerTask, 0, Utilities.CHAT_FETCH_INTERVAL);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataPass =(OnDataPass)activity;
    }

    private class FetchChat extends TimerTask {

        @Override
        public void run() {
            StringRequest sr = new StringRequest(Utilities.TEXT_URL,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("app","The response is "+ response);
                    if (response.equals(Utilities.SWITCH_MODE_COMM))
                    {
                        dataPass.onDataPass(response);
                        Log.d("break", "called dataPass");
                    }
                    else
                        chatText.append(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("app", "error fetching chat");
                }
            });
            requestQueue.add(sr);
            counter++;
        }
    }

    public interface OnDataPass
    {
        public void onDataPass(String result);
    }
}
