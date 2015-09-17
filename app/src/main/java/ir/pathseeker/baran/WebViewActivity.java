package ir.pathseeker.baran;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


public class WebViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        loadwebview();
    }

    @Override
    protected void onDestroy() {
        myProgress.destroyDrawingCache();
        myProgress = null;
        progressBar.dismiss();
        super.onDestroy();
        Log.i(TAG, "On Destroy .....");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private WebView browser;
    private ProgressBar myProgress;
    private ProgressDialog progressBar;
    String TAG = "Baaraan";

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }



    public void loadwebview(){

        if(isNetworkConnected() == false) {
            Toast.makeText(getBaseContext(), " دسترسی به اینترنت ممکن نیست! لطفا برسی نمایید ", Toast.LENGTH_LONG).show();
        }

        progressBar = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressBar.setMessage("در حال بارگذاری ...");
        progressBar.hide();

        myProgress = (ProgressBar)findViewById(R.id.appprogressBar);
        myProgress.setVisibility(View.GONE);

        Log.w("Filmjoo log", "loadwebview before");

        browser = (WebView) findViewById(R.id.filmjoowebView);
        browser.getSettings().setJavaScriptEnabled(true);

        browser.loadUrl("http://projectbaran.ir/");
        browser.setWebViewClient(new MyWebViewClient());
        browser.setWebChromeClient(new MyWebViewChromeClient());

        Log.w("Filmjoo log", "loadwebview after");

    }

    private class MyWebViewChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
                WebViewActivity.this.setValue(newProgress);
                super.onProgressChanged(view, newProgress);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("Filmjoo", "Processing webview url click...");
            Log.w("Filmjoo", Uri.parse(url).getHost());
            if (Uri.parse(url).getHost().equals("projectbaran.ir") || Uri.parse(url).getHost().equals("list.filmjoo.com")) {
                // This is my web site, so do not override; let my WebView load the page
                progressBar.show();
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.hide();
            Log.i("Filmjoo", "Finished loading URL: " +url);
            if(myProgress != null) {
                myProgress.setVisibility(View.INVISIBLE);
            }
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.show();
            myProgress.setVisibility(View.VISIBLE);
            WebViewActivity.this.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            browser.loadUrl("file:///android_asset/loading.html");
        }

    }

    public void setValue(int progress) {
        if(myProgress != null) {
            myProgress.setProgress(progress);
        }
    }


}
