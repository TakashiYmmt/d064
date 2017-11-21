package jp.co.webshark.d064_alpha;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.HashMap;

/**
 * グローバル変数を扱うクラス
 * Created by sample on 2016/11/18.
 */
public class UtilCommon extends Application {

    private static final String TAG = "UtilCommon";
    private ProductData productData;
    private Bitmap bitmap;
    private HashMap<String,Bitmap> imagesCache;

    /**
     * アプリケーションの起動時に呼び出される
     */
    @Override
    public void onCreate() {
        super.onCreate();
        productData = null;
        imagesCache = new HashMap<>();
    }

    /**
     * アプリケーション終了時に呼び出される
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        productData = null;
    }

    public void setProduct(ProductData product) {
        productData = product;
    }

    public ProductData getProduct() {
        return productData;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public void clearBitmap() {
        this.bitmap = null;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setImagesCache(String url, Bitmap bitmap){
        imagesCache.put(url,bitmap);
    }
    public Bitmap getImageCache(String url){
        return imagesCache.get(url);
    }

    private static final String PROPERTY_ID = "UA-108801142-1";
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
/*
    // GoogleAnalytics
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
*/
}