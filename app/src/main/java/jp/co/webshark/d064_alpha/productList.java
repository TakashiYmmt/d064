package jp.co.webshark.d064_alpha;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.webshark.d064_alpha.customViews.FlickListener;
import jp.co.webshark.d064_alpha.customViews.HttpImageView;
import jp.co.webshark.d064_alpha.customViews.PagingListView;

public class productList extends Activity {

    private ArrayList<ProductData> productList;
    private int i = 0;
    private int nowIndex = 0;
    private UtilCommon common;
    //private ListView listView;
    private PagingListView listView;

    private LinearLayout scrollView;
    private String han_id;

    private AsyncPost productGetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        //listView = (ListView) findViewById(R.id.listView1);
        listView = (PagingListView) findViewById(R.id.listView1);
        //scrollView = (ScrollView) findViewById(R.id.scroll_body);
        scrollView = (LinearLayout) findViewById(R.id.scroll_body);
        //scrollView.

        common = (UtilCommon)getApplication();
        productList = new ArrayList<ProductData>();

        FacebookSdk.sdkInitialize(this);
        AccessToken.getCurrentAccessToken();
        // 販売店情報を取得
        ArrayList<String> userInfo = commonFunction.getUserInfo(getApplication().getBaseContext());
        if(userInfo != null && userInfo.size() == 2){
            han_id = userInfo.get(0);
            ((TextView)findViewById(R.id.txtName)).setText(userInfo.get(1)+" 様（ID："+userInfo.get(0)+"）");
        }

        i = 0;
        this.loadProduct(i);
        this.listView.setPagingListener(new PagingListView.OnPagingListener() {
            @Override
            public void onScrollStart(int _nowPage) {
                //ここにスクロールが開始したときの処理を書く
            }

            @Override
            public void onScrollFinish(int _nowPage) {
                //ここにスクロールが終了したときの処理を書く
                //common.setProduct(productList.get(_nowPage));
                //listView.setNowPage(listView.getFirstVisiblePosition());
                //listView.setSelection(_nowPage);
                if(_nowPage == 0){
                    nowIndex = nowIndex-1;
                }else if(_nowPage == 1){
                    nowIndex = nowIndex+1;
                }else if(_nowPage == 2){
                    nowIndex = nowIndex+1;
                }
                Log.d("nowIndex=",String.valueOf(nowIndex));
                Log.d("nowPage=",String.valueOf(_nowPage));
                drawProductList(nowIndex);
            }

            @Override
            public void onNextListLoad(int _nowPage) {
                //ここにまだ読み込んでいない画象を追加したい時に処理を書く
            }
        });

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
        this.setProductGetter();
        if(productList.size() == 0){
            getProductList();
        }

    }

    private void loadProduct(int index){

        if(productList != null){
            if(productList.size() > index){

                common.setProduct(productList.get(index));
                common.setBitmap(null);

            }
        }
    }

    private void setProductList(String result){


        try {
            JSONObject json = new JSONObject(result);
            String strResult = json.getString("result");

            if(strResult.equals("1")){
                productList.clear();
                try{
                    JSONArray jArray = json.getJSONArray("arrAfPosterData");
                    setProductGetter();

                    for( int i = 0 ; i < jArray.length() ; i++ ){
                        ProductData pData = new ProductData();
                        JSONObject row = (JSONObject) jArray.get(i);
                        pData.setProductName(row.getString("name"));
                        pData.setProductImageUrl(row.getString("image"));
                        pData.setProductLinkUrl(row.getString("url"));
                        pData.setSnsText1(row.getString("text"));
                        pData.setSnsText2(row.getString("redirect_url"));//+"&img=93002501&app=1"
                        pData.setReward(row.getString("reward"));
                        pData.setId(row.getString("id"));

                        productList.add(pData);
                    }

                    common.setProduct(productList.get(0));
                    common.clearBitmap();
                }catch (JSONException e) {
                    e.printStackTrace();
                }



            }else{
                setProductGetter();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void twAuth(View view){
        common.setProduct(productList.get(nowIndex));

        Intent intent = new Intent(productList.this,twAuth.class);
        startActivity(intent);
    }

    public void toMypage(View view){
        Uri uri = Uri.parse("https://secure.d-064.com/af/index.php");
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

    public void fbAuth(View view){
        common.setProduct(productList.get(nowIndex));

        Intent intent = new Intent(productList.this,fbShare.class);
        startActivity(intent);
    }
    public void tempMessage(View view){
        common.setProduct(productList.get(nowIndex));

        Uri uri = Uri.parse(common.getProduct().getProductLinkUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

    // APIコールバック定義
    private void setProductGetter(){
        // プロフィール取得用API通信のコールバック
        productGetter = new AsyncPost(new AsyncCallback() {
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
                        setProductList(result);
                        nowIndex = 0;
                        drawProductList(nowIndex);
                        setProductGetter();
                    }else if(strResult.equals("0")){
                        showToast(json.getString("message"));
                        setProductGetter();
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
        new AlertDialog.Builder(productList.this)
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

    private void getProductList(){

        String strURL = "https://www.yhvh.jp/af_banner/d064_app.php?entity=product_list&hid="+han_id;
        HashMap<String,String> body = new HashMap<String,String>();

        // API通信のPOST処理
        productGetter.setParams(strURL, body);
        productGetter.execute();
    }

    private void drawProductList(int center){

        if(center < 0){
            center = 0;
            nowIndex = center;
        }else if(center >= productList.size()){
            center = productList.size()-1;
            nowIndex = center;
        }

        ArrayList<ProductData> minList = new ArrayList<>();
        int position = 0;
        if(center <= 0){
            minList.add(productList.get(0));
            minList.add(productList.get(1));
            position = 0;
        }else if(productList.size()-1 > center){
            minList.add(productList.get(center-1));
            minList.add(productList.get(center));
            minList.add(productList.get(center+1));
            position = 1;
        }else if(productList.size()-1 == center){
            minList.add(productList.get(center-1));
            minList.add(productList.get(center));
            position = 1;
        }else{
            return;
        }

        //listView.setTotalPage(productList.size());
        //listView.setNowPage(0);

        listView.setTotalPage(minList.size());
        listView.setNowPage(position);

        FriendsAdapter adapter = new FriendsAdapter(productList.this);

        //adapter.setProductList(productList);
        adapter.setProductList(minList);
        listView.setAdapter(adapter);

        listView.setSelection(position);
        Log.d("select=", String.valueOf(position));
        Log.d("total=", String.valueOf(listView.getCount()));

        adapter.notifyDataSetChanged();

        this.setProductGetter();

    }

    private class FriendsAdapter extends BaseAdapter {

        Context context;
        LayoutInflater layoutInflater = null;
        ArrayList<ProductData> productList;

        public FriendsAdapter(Context context){
            this.context = context;
            this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setProductList(ArrayList<ProductData> productList) {
            this.productList = productList;
        }

        @Override
        public int getCount() {
            return productList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.product_list_cell,parent,false);
            ProductData product = productList.get(position);
            ((TextView)convertView.findViewById(R.id.txtProduct)).setText(product.getProductName());
            //((TextView)convertView.findViewById(R.id.txtReward)).setText(Html.fromHtml("成果報酬： &lt;font color=#00BBEE&gt;" + product.getReward() + "&lt;/font&gt;"));
            String tmpReward = product.getReward();
            tmpReward = tmpReward.replaceAll("商品購入金額の", "");
            if(tmpReward.equals(product.getReward())){
                ((TextView)convertView.findViewById(R.id.txtRewardBody)).setText(product.getReward());
            }else{
                ((TextView)convertView.findViewById(R.id.txtReward)).setText("成果報酬： 商品購入金額の");
                ((TextView)convertView.findViewById(R.id.txtRewardBody)).setText(tmpReward);
            }
            HttpImageView productImage = (HttpImageView) convertView.findViewById(R.id.imageProduct);
            productImage.setImageUrl(product.getProductImageUrl(),5000, parent.getContext(),common);

            return convertView;
        }
    }
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
