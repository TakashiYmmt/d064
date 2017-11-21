package jp.co.webshark.d064_alpha.customViews;

import android.view.MotionEvent;
import android.view.View;

public class FlickListener implements View.OnTouchListener{

    /**
     * フリックイベントのリスナー
     */
    public interface Listener {
        void onFlickToLeft();
        void onFlickToRight();
        void onFlickToUp();
        void onFlickToDown();
    }

    /**
     * フリック判定時の遊び
     * 小さいほど判定が敏感になる
     */
    private static final float DEFAULT_PLAY = 100;

    private final Listener listener;
    private final float play;

    private float lastX;
    private float lastY;

    public FlickListener(Listener listener) {
        this(listener, DEFAULT_PLAY);
    }

    public FlickListener(Listener listener, float play) {
        this.listener = listener;
        this.play = play;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_UP:
                touchOff(event);
                break;
        }
        return true;
    }

    private void touchDown(MotionEvent event) {
        lastX = event.getX();
        lastY = event.getY();
    }

    private void touchOff(MotionEvent event) {
        final float currentX = event.getX();
        final float currentY = event.getY();

        // x -> y の順で判定しているので、斜めにフリックした場合はLeft,Rightのイベントの方が優先される
        // Up,Downを優先したい場合は、条件判定の順序を入れ替えること
        if ((currentX + play) < lastX) {
            listener.onFlickToLeft();
        } else if (lastX < (currentX - play)) {
            listener.onFlickToRight();
        } else if ((currentY + play) < lastY) {
            listener.onFlickToUp();
        } else if (lastY < (currentY - play)) {
            listener.onFlickToDown();
        }
    }
}