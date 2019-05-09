package org.lulzm.waft.MainFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_info, container, false);

        if (mWebView != null) {
            mWebView.destroy();
        }

        mWebView = view.findViewById(R.id.webview_info);
        // webview 자바 허용
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.addJavascriptInterface(new JavaScriptInterface(getActivity()), "android");

        // MainActivity.class 에서 bundle 로 보낸 데이터 받기.
        String webUrl = getArguments().getString("webURL");
        mWebView.loadUrl(webUrl);

        // WebView backButton
        mWebView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction()!=KeyEvent.ACTION_DOWN)
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
