package com.backpackvr.ppquest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowserFragment extends Fragment {

    private static final String ARG_PARAM1 = "FRAGMENT4_MSG";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public BrowserFragment() {
        // Required empty public constructor
    }

    public static BrowserFragment newInstance(String param1, String param2) {
        BrowserFragment fragment = new BrowserFragment();
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

    View buttonBack;
    View buttonReload;
    View buttonForward;
    View buttonEnter;

    EditText urlBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_browser, container, false);
        String game_id = mParam1;
        String TAG = "GameFragment";
        Log.e(TAG, "game_id: " + game_id);

        buttonBack = view.findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (webView.canGoBack()) {
                    webView.goBack();
                    urlBar.setText(webView.getUrl());
                }
            }
        });

        buttonForward= view.findViewById(R.id.buttonForward);
        buttonForward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (webView.canGoForward()) {
                    webView.goForward();
                    urlBar.setText(webView.getUrl());
                }
            }
        });

        buttonReload= view.findViewById(R.id.buttonReload);
        buttonReload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                webView.reload();
                urlBar.setText(webView.getUrl());
            }
        });


        buttonEnter = view.findViewById(R.id.buttonEnter);
        urlBar = (EditText)view.findViewById(R.id.urlBar);
        buttonEnter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String url = urlBar.getText().toString();
                LoadUrl(url);
            }
        });

        urlBar.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String url = urlBar.getText().toString();
                    LoadUrl(url);
                    return true;
                }
                return false;
            }
        });

        BuildWebview(view);

        return view;
    }

    private WebView webView;
    TextView downloadData;

    public void LoadUrl(String url) {
        Log.i("onClick", "new url: " + url);
        if (!url.startsWith("https://")) {
            try {
                url = "https://duckduckgo.com/?q=" + URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                ((MainActivity)getActivity()).debugText.setText("error encoding url");
            }
        }
        webView.loadUrl(url);
        urlBar.setText(webView.getUrl());
    }
    public void BuildWebview(View view) {

        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());


        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

        String startUrl = "https://itch.io/login";
        webView.loadUrl(startUrl);
        urlBar.setText(startUrl);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                ((MainActivity)getActivity()).downloadThis("", url, userAgent, contentDisposition, mimeType, contentLength);
            }
        });
    }


}