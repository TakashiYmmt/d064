package jp.co.webshark.d064_alpha;

import jp.co.webshark.d064_alpha.customViews.HttpImageView;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class twAuth extends Activity {

    private String mCallbackURL;
    private Twitter mTwitter;
    private RequestToken mRequestToken;
    private UtilCommon common;
    private String localImageURL;

    private InputMethodManager inputMethodManager;
    private RelativeLayout mainLayout;
    private EditText editText;
    private boolean withImage;
    private String pickupProduct;

    private AsyncPost accesChecker;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tw_auth);

        //キーボードを閉じたいEditTextオブジェクト
        editText = (EditText) findViewById(R.id.inputText);
        //画面全体のレイアウト
        mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
        //キーボード表示を制御するためのオブジェクト
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        common = (UtilCommon)getApplication();
        mCallbackURL = getString(R.string.twitter_callback_url);
        mTwitter = TwitterUtils.getTwitterInstance(this);
        withImage = true;

        // インテント引数を取得
        Intent intent = getIntent();
        pickupProduct = intent.getStringExtra("Pickup");
        if(pickupProduct != null){
            Log.d("intent=",pickupProduct);
        }

        startAuthorize();

        findViewById(R.id.action_tweet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
            }
        });
        if(common.getImageCache(common.getProduct().getProductImageUrl()) != null){
            HttpImageView imageView = (HttpImageView)findViewById(R.id.imageProduct);
            imageView.setImageBitmap(common.getImageCache(common.getProduct().getProductImageUrl()));
        }else{
            HttpImageView imageView = (HttpImageView)findViewById(R.id.imageProduct);
            imageView.setImageUrl(common.getProduct().getProductImageUrl(), 5000, getApplicationContext(), common);
        }

        EditText editView = (EditText)findViewById(R.id.inputText);
        editView.setText(common.getProduct().getSnsText1() + " " + common.getProduct().getSnsText2());

        // GoogleAnalytics
        Tracker t = ((UtilCommon)getApplication()).getTracker(UtilCommon.TrackerName.APP_TRACKER);
        t.setScreenName(this.getClass().getSimpleName());
        //t.send(new AppViewBuilder().build());
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onResume(){
        super.onResume();

        // コールバックの初期化
        this.setAccessChecker();
    }

    // APIコールバック定義
    private void setAccessChecker(){
        // プロフィール取得用API通信のコールバック
        accesChecker = new AsyncPost(new AsyncCallback() {
            public void onPreExecute() {}
            public void onProgressUpdate(int progress) {}
            public void onCancelled() {}
            public void onPostExecute(String result) {
            }
        });
    }

    private void putAccessPing(){

        // 販売店情報を取得
        String han_id = "0";
        ArrayList<String> userInfo = commonFunction.getUserInfo(getApplication().getBaseContext());
        if(userInfo != null && userInfo.size() == 2){
            han_id = userInfo.get(0);
        }

        // GoogleAnalyticsにカスタム情報を発信
        Tracker t = ((UtilCommon)getApplication()).getTracker(UtilCommon.TrackerName.APP_TRACKER);
        //t.setScreenName(this.getClass().getSimpleName());

        t.setScreenName("TW_H"+han_id);
        t.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(1, common.getProduct().getId()).build());

        t.setScreenName("TW_B"+common.getProduct().getId());
        t.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(2,han_id).build());

        if( common.getProduct().getId().equals(pickupProduct) ){
            t.setScreenName("TW_AD");
            t.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(2,han_id).setCustomDimension(1, common.getProduct().getId()).build());
        }

        GoogleAnalytics.getInstance(this).dispatchLocalHits();

        String strURL = "https://www.yhvh.jp/af_banner/d064_app.php?entity=cmp&sns=tw&bid="+common.getProduct().getId()+"&hid="+han_id;
        HashMap<String,String> body = new HashMap<String,String>();

        // API通信のPOST処理
        accesChecker.setParams(strURL, body);
        accesChecker.execute();
    }

    private void tweet() {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {

                if(!TwitterUtils.hasAccessToken(getBaseContext())) {
                    return false;
                }
                try {
                    StatusUpdate statusUpdate = new StatusUpdate(params[0]);
                    if(withImage){
                        statusUpdate.setMedia(new File(params[1]));
                        mTwitter.updateStatus(statusUpdate);
                    }else{
                        mTwitter.updateStatus(params[0]);
                    }
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    putAccessPing();
                    showToast("ツイートが完了しました！");
                    finish();
                } else {
                    showToast("ツイートに失敗しました。。。");
                    finish();
                }
            }
        };

        // 保存処理開始
        FileOutputStream fos = null;


        CheckBox checkUpImage = (CheckBox)findViewById(R.id.checkUpImage);
        withImage = checkUpImage.isChecked();
        try {
            //String dataDir = "/data/data/" +getApplicationContext().getPackageName() + "/files/";
            File file = new File(this.getApplicationContext().getFilesDir(), "tmp.jpg");
            fos = new FileOutputStream(file);
            //String dataDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            //fos = new FileOutputStream(new File(dataDir, "/tmp.jpg"));
            // jpegで保存
            common.getImageCache(common.getProduct().getProductImageUrl()).compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // 保存処理終了
            fos.close();
            localImageURL = file.getAbsolutePath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            withImage = false;
        } catch (IOException e) {
            e.printStackTrace();
            withImage = false;
        }

        EditText editView = (EditText)findViewById(R.id.inputText);
        //task.execute(String.valueOf(editView.getText())+" "+common.getProduct().getSnsText2(), localImageURL);
        task.execute(String.valueOf(editView.getText()), localImageURL);
    }

    /**
     * OAuth認証（厳密には認可）を開始します。
     *
     * @@param listener
     */
    private void startAuthorize() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if(!TwitterUtils.hasAccessToken(getBaseContext())){
                        mRequestToken = mTwitter.getOAuthRequestToken(mCallbackURL);
                        return mRequestToken.getAuthorizationURL();
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    // 失敗。。。
                }
            }
        };
        task.execute();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null
                || intent.getData() == null
                || !intent.getData().toString().startsWith(mCallbackURL)) {
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    // 認証成功！
                    showToast("認証成功！");
                    successOAuth(accessToken);
                } else {
                    // 認証失敗。。。
                    showToast("認証失敗。。。");
                }
            }
        };
        task.execute(verifier);
    }

    private void successOAuth(AccessToken accessToken) {
        TwitterUtils.storeAccessToken(this, accessToken);
        //Intent intent = new Intent(this, twAuth.class);
        //startActivity(intent);
        //finish();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * EditText編集時に背景をタップしたらキーボードを閉じるようにするタッチイベントの処理
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //背景にフォーカスを移す
        mainLayout.requestFocus();

        return false;
    }

    public void send_back(View view){
        finish();
    }

    public void toMypage(View view){
        Uri uri = Uri.parse("https://secure.d-064.com/af/index.php");
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

}