package jp.co.webshark.d064_alpha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jp.co.webshark.d064_alpha.customViews.HttpImageView;
import twitter4j.auth.RequestToken;

public class fbShare extends Activity {

    private CallbackManager callbackManager;
    private String mCallbackURL;
    private RequestToken mRequestToken;
    private UtilCommon common;
    private String localImageURL;
    private String shareURL;

    private InputMethodManager inputMethodManager;
    private RelativeLayout mainLayout;
    private EditText editText;
    ShareDialog shareDialog;
    private AsyncPost accesChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_share);

        //キーボードを閉じたいEditTextオブジェクト
        editText = (EditText) findViewById(R.id.inputText);
        //画面全体のレイアウト
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        //キーボード表示を制御するためのオブジェクト
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        common = (UtilCommon)getApplication();
        mCallbackURL = getString(R.string.twitter_callback_url);
        callbackManager = CallbackManager.Factory.create();

        //2.facebook利用する時のInitialize
        FacebookSdk.sdkInitialize(this);

        if(common.getImageCache(common.getProduct().getProductImageUrl()) != null){
            HttpImageView imageView = (HttpImageView)findViewById(R.id.imageProduct);
            imageView.setImageBitmap(common.getImageCache(common.getProduct().getProductImageUrl()));
        }else{
            HttpImageView imageView = (HttpImageView)findViewById(R.id.imageProduct);
            imageView.setImageUrl(common.getProduct().getProductImageUrl(), 5000, getApplicationContext(), common);
        }

        EditText editView = (EditText)findViewById(R.id.inputText);
        editView.setText(common.getProduct().getSnsText1());
        shareURL = common.getProduct().getSnsText2();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken != null){
            setShareCallback();
            send_share(null);
        }else{
            //startAuthorize();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));    //profileとemailの情報を取得

            //3.コールバック登録
            callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            //3.1 ログイン成功
                            showToast("認証成功！");
                            setShareCallback();
                        }

                        @Override
                        public void onCancel() {
                            //3.2 ログインキャンセル
                            showToast("認証失敗。。。");;
                            setShareCallback();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            //3.3 ログイン失敗
                            showToast("認証失敗。。。");;
                            setShareCallback();
                        }
                    });
        }

        findViewById(R.id.action_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tweet();
            }
        });

       /*-- /facebook SDK --*/

        findViewById(R.id.action_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse(shareURL)).build();
                shareDialog.show(content);
            }
        });

        /*
        Button fbButton = (Button)findViewById(R.id.action_share);
        int btnHeignt = (int)commonFunction.convertDp2Px(40,getBaseContext());
        Drawable leftDrawableFb = getResources().getDrawable(R.drawable.fb_icon);
        leftDrawableFb.setBounds(btnHeignt/4, 0, btnHeignt, btnHeignt);
        fbButton.setCompoundDrawables(leftDrawableFb, null, null, null);
        fbButton.refreshDrawableState();
        */
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
        String strURL = "https://www.yhvh.jp/af_banner/d064_app.php?entity=cmp&sns=fb&bid="+common.getProduct().getId()+"&hid="+han_id;
        HashMap<String,String> body = new HashMap<String,String>();

        // API通信のPOST処理
        accesChecker.setParams(strURL, body);
        accesChecker.execute();
    }

    private void setShareCallback(){

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                putAccessPing();
                showToast("シェアが完了しました！");
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onError(FacebookException error) {
                showToast("シェアに失敗しました。。。");
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //4.コールバック設定（Activity戻ってきた時にコールバックを呼ぶ）
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    public void send_share(View view){
        ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse(shareURL)).build();
        shareDialog.show(content);
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
