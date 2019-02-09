package ru.siamoil.siamservice.pvtcalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


public class PlotFragment extends Fragment {

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plot_fragment, container, false);
        setHasOptionsMenu(true);
        initView(view);
        return view;
    }

    public void updateWebView(String xJsonSurf, String yJsonSurf, String zJsonSurf, double xPoint, double yPoint, double zPoint) {
        String script = "javascript:plot('" + xJsonSurf + "'" +
                ",'" + yJsonSurf + "'" +
                ",'" + zJsonSurf + "'" +
                ",'" + String.valueOf(xPoint) + "'" +
                ",'" + String.valueOf(yPoint) + "'" +
                ",'" + String.valueOf(zPoint) + "')";
        webView.loadUrl(script);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView(View view) {
        WebView.setWebContentsDebuggingEnabled(true);
        webView = view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/webview.html");
    }
}
