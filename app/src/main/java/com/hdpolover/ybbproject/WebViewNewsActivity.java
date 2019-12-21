package com.hdpolover.ybbproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WebViewNewsActivity extends AppCompatActivity {

    WebView webView;
    String myUid;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_news);

        //action bar and its propertoes
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("News Detail");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        checkUserStatus();

        Intent intent = getIntent();
        String articleLink = intent.getStringExtra("link");

        progressDialog = new ProgressDialog(WebViewNewsActivity.this);
        progressDialog.setMessage("Loading news...");
        progressDialog.show();

        webView = findViewById(R.id.newsWebview);
        webView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(articleLink);
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in
            myUid = user.getUid();
        } else {
            //user not signed in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }
    }
}
