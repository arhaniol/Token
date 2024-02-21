package pl.mik.token;

import android.os.CountDownTimer;

public class MyCounter extends CountDownTimer {

    int progress;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public MyCounter(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        progress = (int) (millisUntilFinished / 1000);
    }

    @Override
    public void onFinish() {

    }
}
