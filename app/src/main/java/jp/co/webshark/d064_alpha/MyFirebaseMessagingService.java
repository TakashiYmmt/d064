package jp.co.webshark.d064_alpha;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // プッシュメッセージのdataに含めた値を取得
        Map<String, String> data = remoteMessage.getData();
        String contentId = data.get("id");
        String contentType = data.get("type");

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
        Intent intent = new Intent(this, productList.class);
        //intent.putExtra(productList.ARG_ID, contentId);
        //intent.putExtra(productList.ARG_TYPE, contentType);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Notification表示
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        if(contentId == null){
            manager.notify(0, builder.build());
        }else{
            manager.notify(Integer.parseInt(contentId), builder.build());
        }
    }
}
