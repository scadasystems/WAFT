package org.lulzm.waft.mainFragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import org.lulzm.waft.R;

import java.util.Locale;
import java.util.Objects;

/*********************************************************
 *   $$\                  $$\             $$\      $$\
 *   $$ |                 $$ |            $$$\    $$$ |
 *   $$ |      $$\   $$\  $$ | $$$$$$$$\  $$$$\  $$$$ |
 *   $$ |      $$ |  $$ | $$ | \____$$  | $$ \$\$$ $$ |
 *   $$ |      $$ |  $$ | $$ |   $$$$ _/  $$  \$$  $$ |
 *   $$ |      $$ |  $$ | $$ |  $$  _/    $$ | $  /$$ |
 *   $$$$$$$$  \$$$$$$$ | $$ | $$$$$$$$\  $$ | \_/ $$ |
 *   \_______| \______/   \__| \________| \__|     \__|
 *
 * Project : WAFT
 * Created by Android Studio
 * Developer : Lulz_M
 * Date : 2019-05-03 003
 * Time : 오후 2:15
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 *********************************************************/
public class MainWebview extends Fragment {
    private WebView mWebView;

    // 진행바
    private ProgressDialog progressDialog;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_info, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String language = prefs.getString("language", "");
        Configuration config = new Configuration();
        config.setLocale(Locale.forLanguageTag(language));
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // progressbar
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loding_webview));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        if (mWebView != null) {
            mWebView.destroy();
        }

        // 다크모드 적용
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("change_theme", getActivity().MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }

        mWebView = view.findViewById(R.id.webview_info);
        // webview 자바 허용
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        // MainActivity.class 에서 bundle 로 보낸 데이터 받기.
        mWebView.addJavascriptInterface(new JavaScriptInterface(getActivity()), "android");
        String webUrl = getArguments().getString("webURL");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }
        });
        mWebView.loadUrl(webUrl);

        // WebView backButton
        mWebView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
                return true;
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
                return true;
            }
            return false;
        });
        return view;
    }

    // webview interface
    private class JavaScriptInterface {
        Context context;

        JavaScriptInterface(Context c) {
            context = c;
        }

        @JavascriptInterface
        public void changePage(String idxNum) {
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContent, new Fragment1()).commit();
        }
    }
}
