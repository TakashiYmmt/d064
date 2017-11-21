package jp.co.webshark.d064_alpha.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import jp.co.webshark.d064_alpha.UtilCommon;

/**
 * Created by takashi on 2015/07/23.
 */
public class HttpImageView extends ImageView {
    private ImageLoadTask task = null;
    private Bitmap cacheBitmap = null;
    private String pid = null;
    UtilCommon common;

    public void setImageUrl(String server_url,int limitSize, Context context,UtilCommon common) {

        this.common = common;

        // キャッシュがあればそれを表示しておしまい
        cacheBitmap = common.getImageCache(server_url);

        if( cacheBitmap != null ){
            setImageBitmap(cacheBitmap);
            return;
        }

        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }

        // 表示クリア。必要であればロード中画像をセット
        setImageURI(null);

        // 画像のロード実行
        task = new ImageLoadTask();
        task.server_url = server_url;
        task.limitSize = limitSize;
        task.context = context;

        //task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        task.execute();
    }

    public void setImageUrl(String server_url,int limitSize, Context context, String cachePath) {

        if( cacheBitmap != null ){
            setImageBitmap(cacheBitmap);
            return;
        }

        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }

        // 表示クリア。必要であればロード中画像をセット
        setImageURI(null);

        // 画像のロード実行
        task = new ImageLoadTask();
        task.server_url = server_url;
        task.limitSize = limitSize;
        task.context = context;
        pid = cachePath;

        //task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        task.execute();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // taskが実行中であれば止める
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }
    }

    public HttpImageView(Context context) {
        super(context);
    }

    public HttpImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HttpImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 画像を取得するAsyncTask
    private class ImageLoadTask extends AsyncTask<Void, Void, Uri>  {

        private String server_url = null;
        private Context context = null;
        private int limitSize = 0;
        Bitmap bitmap = null;
        boolean drawCircle = false;

        @Override
        protected Uri doInBackground(Void... params) {

            URL url = null;
            InputStream istream;
            HttpURLConnection con = null;

            try {
                url = new URL(server_url);

                con = (HttpURLConnection) url.openConnection();
                con.setUseCaches(true);
                con.setRequestMethod("GET");
                con.setReadTimeout(500000);
                con.setConnectTimeout(50000);
                con.connect();

                //istream = url.openStream();
                istream = con.getInputStream();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPurgeable = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;

                bitmap = BitmapFactory.decodeStream(istream,null,options);
                //common.setBitmap(bitmap);
                common.setImagesCache(server_url,bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Uri result) {
            //setImageURI(result);
            //ずれはキャッシュしたローカルファイルを参照したいけど、今はクラス内にバイナリを持って扱う
            setImageBitmap(bitmap);
            bitmap = null;
        }
    }

}
