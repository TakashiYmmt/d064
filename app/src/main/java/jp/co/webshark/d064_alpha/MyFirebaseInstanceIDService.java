package jp.co.webshark.d064_alpha;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // 自前サーバーへのRegistrationId送信処理を実装
        //sendRegistrationToServer(refreshedToken);
        //Log.v(" ",refreshedToken);
        Log.d("refreshedToken=",String.valueOf(refreshedToken));
    }
}
