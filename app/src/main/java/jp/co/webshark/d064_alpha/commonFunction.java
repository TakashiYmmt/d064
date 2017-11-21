package jp.co.webshark.d064_alpha;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * Created by takashi on 2015/06/18.
 */
public class commonFunction extends Application {

    public static void sleep(long millis) throws InterruptedException{
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
    public static void setUserInfo(Context context, String userID, String userName){

        OutputStream out;
        try {
            out = context.openFileOutput("user.info", MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            //上書きする
            writer.println(userID);
            writer.println(userName);
            writer.close();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }
    public static ArrayList<String> getUserInfo(Context context){

        InputStream in;
        String lineBuffer;

        try {
            in = context.openFileInput("user.info");

            File f1  = new File("user.info");
            if( f1.exists() ){
                return null;

            }
            BufferedReader reader= new BufferedReader(new InputStreamReader(in,"UTF-8"));
            ArrayList<String> result = new ArrayList<String>();
            while( (lineBuffer = reader.readLine()) != null ){
                try {
                    result.add(lineBuffer);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return result;
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            //e.printStackTrace();
            return null;
        }
    }

    public static void createBitmapCache(Context context, Bitmap bitmap, String fileName){

        // 保存処理開始
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);

            // jpegで保存
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // 保存処理終了
            fos.close();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }
    /**
     * dpからpixelへの変換
     * @param dp
     * @param context
     * @return float pixel
     */
    public static float convertDp2Px(float dp, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }
}