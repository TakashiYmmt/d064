package jp.co.webshark.d064_alpha;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class d064_login extends Activity implements View.OnClickListener {


    private AsyncPost hanGetter;
    private InputMethodManager inputMethodManager;
    private LinearLayout mainLayout;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d064_login);

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnJoin).setOnClickListener(this);

        // 販売店情報が取得できたら即時破棄してメイン画面へ
        ArrayList<String> userInfo = commonFunction.getUserInfo(getApplication().getBaseContext());
        if(userInfo != null && userInfo.size() == 2){
            Intent intent = new Intent(d064_login.this,productList.class);
            startActivity(intent);
            finish();
        }
        ((TextView)findViewById(R.id.txtLink)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        //キーボードを閉じたいEditTextオブジェクト
        //editText = (EditText) findViewById(R.id.inputText);
        //画面全体のレイアウト
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        //キーボード表示を制御するためのオブジェクト
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    @Override
    public void onClick(View v) {

        if (v != null) {
            switch (v.getId()) {
                case R.id.btnLogin:
                    this.loginD064();
                    break;

                case R.id.btnJoin:
                    this.joinD064();
                    break;


                default:
                    break;
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        // コールバックの初期化
        this.setHanGetter();

    }

    // ログインチェック
    private void loginD064(){
        // API通信でログイン処理 販売店名とIDを取得しておく
        //Intent intent = new Intent(d064_login.this,productList.class);
        //startActivity(intent);
        //finish();
        loginAndGetHan();
    }

    // 電脳卸をブラウザで開く
    private void joinD064(){
        Uri uri = Uri.parse("https://secure.d-064.com/touroku/han_touroku_regist1.php?hid=243413");
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

    // APIコールバック定義
    private void setHanGetter(){
        // プロフィール取得用API通信のコールバック
        hanGetter = new AsyncPost(new AsyncCallback() {
            public void onPreExecute() {}
            public void onProgressUpdate(int progress) {}
            public void onCancelled() {}
            public void onPostExecute(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    ArrayList<String> list = new ArrayList<>();
                    String strResult = json.getString("result");
                    //strResult = "-1";

                    if(strResult.equals("1")){
                        commonFunction.setUserInfo(getApplication().getBaseContext(),json.getString("han_id"),json.getString("name"));
                        Intent intent = new Intent(d064_login.this,productList.class);
                        startActivity(intent);
                        finish();
                    }else if(strResult.equals("0")){
                        showToast(json.getString("message"));
                        setHanGetter();
                    }else if(strResult.equals("-1")){
                        String strMessage = json.getString("message");
                        alertMessage(strMessage);

                    }
                } catch (JSONException e) {
                    //e.printStackTrace();

                    String strMessage = "例外エラーが発生しました。アプリケーションは強制終了されます";
                    alertMessage(strMessage);
                }
            }
        });
    }

    private void alertMessage(String msg){
        new AlertDialog.Builder(d064_login.this)
                .setTitle("電脳卸")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // YES button pressed
                        System.exit(RESULT_OK);
                    }
                })
                .show();

    }

    private void loginAndGetHan(){
        String email = String.valueOf(((EditText) findViewById(R.id.inputMail)).getText());
        String pass = String.valueOf(((EditText) findViewById(R.id.inputPass)).getText());

        String strURL = "https://www.yhvh.jp/af_banner/d064_app.php?entity=login&id="+email+"&pass="+pass;
        HashMap<String,String> body = new HashMap<String,String>();

        body.put("entity", "login");

        // API通信のPOST処理
        hanGetter.setParams(strURL, body);
        hanGetter.execute();
    }

    public void pass_remind(View view){
        Uri uri = Uri.parse("https://secure.d-064.com/d064_password_reminder.php");
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
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
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
