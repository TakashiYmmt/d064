package jp.co.webshark.d064_alpha;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static java.security.AccessController.getContext;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        UtilCommon common = (UtilCommon)getApplication();
        UtilCommon.AppStatus appStatus = common.get(common.getApplicationContext()).getAppStatus();
        boolean isFore = common.get(common.getApplicationContext()).isForeground();
        if(common.get(common.getApplicationContext()).isForeground()){
            Log.d("isFore=","FORE");
        }else{
            Log.d("isFore=","BACK");
        }

        // プッシュメッセージのdataに含めた値を取得
        Map<String, String> data = remoteMessage.getData();
        String contentPickup = "";
        if(data != null){
            contentPickup = data.get("pickup");
            if(contentPickup != null){
                //commonFunction.setPickupInfo(getApplication().getBaseContext(),contentPickup);
                Log.d("pickup=",contentPickup);
            }
        }

        // Notificationを生成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.icon);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS);
        builder.setAutoCancel(true);

        // タップ時に呼ばれるIntentを生成
        Intent intent = new Intent(this, d064_login.class);
        intent.putExtra("pickup", contentPickup);
        //intent.putExtra(productList.ARG_TYPE, contentType);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Notification表示
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(0, builder.build());
    }
}
