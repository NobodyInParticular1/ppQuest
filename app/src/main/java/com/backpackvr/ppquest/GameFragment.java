package com.backpackvr.ppquest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {

    private static final String ARG_PARAM1 = "FRAGMENT3_MSG";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View buttonClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_game, container, false);
        String game_id = mParam1;
        String TAG = "GameFragment";
        g = Database.gameList.get(game_id);
        Log.i(TAG, "game_id: " + game_id);

        title = view.findViewById(R.id.gameTitle);
        title.setText(g.title);

        buttonClose = view.findViewById(R.id.buttonCloseGameFragment);
        buttonClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("onClick", "Close this");
                ((MainActivity)getActivity()).CloseGameFragment();
            }
        });

        BuildWebview(view);

        return view;
    }


    TextView title;
    private WebView webView;
    TextView downloadData;


    public void BuildWebview(View view) {

        webView = (WebView) view.findViewById(R.id.gameWebView);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(g.link);


        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                ((MainActivity)getActivity()).downloadThis(g.game_id, url, userAgent, contentDisposition, mimeType, contentLength);
            }
        });
    }

    private Game g;


}