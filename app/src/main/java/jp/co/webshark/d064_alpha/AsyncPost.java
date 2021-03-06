package jp.co.webshark.d064_alpha;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by takashi on 2017/10/03.
 */
public class AsyncPost extends AsyncTask<Void, Void, String> {

    private AsyncCallback _asyncCallback = null;
    private ArrayList<NameValuePair> nameValuePairs;
    private String apiUrl;
    private String uploadFileName;
    private String uploadFilePath;
    private byte[] imageData;

    public AsyncPost(AsyncCallback asyncCallback) {
        this._asyncCallback = asyncCallback;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this._asyncCallback.onPreExecute();
    }

    public void setParams(String url, HashMap<String, String> paramsMap) {

        apiUrl = url;
        nameValuePairs = new ArrayList<NameValuePair>();

        for (HashMap.Entry<String, String> e : paramsMap.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(e.getKey(), e.getValue()));
        }
        uploadFileName = "";
        uploadFilePath = "";
    }

    public void setParams(String url, HashMap<String, String> paramsMap, String fileName, byte[] image) {

        apiUrl = url;
        nameValuePairs = new ArrayList<NameValuePair>();

        for (HashMap.Entry<String, String> e : paramsMap.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(e.getKey(), e.getValue()));
        }

        uploadFileName = fileName;
        imageData = image;
    }

    @Override
    protected String doInBackground(Void... params) {

        String returnString = "";

        Log.d("AsyncPost ：", " doInBackground（） ");
        try {
            //post
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(apiUrl);

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
            returnString = EntityUtils.toString(response.getEntity());
            Log.d("upload result NEW", returnString);


        } catch (Exception e) {
            //Log.d("upload catch error result NEW", e.getMessage().toString());
        }
        uploadFilePath = "";
        uploadFileName = "";
        return returnString;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        this._asyncCallback.onPostExecute(result);
    }
}
