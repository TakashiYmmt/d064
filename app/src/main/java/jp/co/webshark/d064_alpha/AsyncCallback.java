package jp.co.webshark.d064_alpha;

/**
 * Created by takashi on 2017/10/03.
 */
public interface AsyncCallback {
        void onPreExecute();
        void onPostExecute(String result);
        void onProgressUpdate(int progress);
        void onCancelled();
}
