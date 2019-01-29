package it.bz.beacon.adminapp.eventbus;

import android.os.Handler;
import android.os.Looper;

public class PubSub {
    private static PubSub instance;
    private MainThreadBus mBus;

    public static MainThreadBus getInstance() {
        if (instance == null) {
            instance = new PubSub();
        }
        return instance.getBus();
    }

    public PubSub() {
        this.mBus = new MainThreadBus();
    }

    private MainThreadBus getBus() {
        return mBus;
    }

    public class MainThreadBus extends com.squareup.otto.Bus {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainThreadBus.super.post(event);
                    }
                });
            }
        }
    }
}
