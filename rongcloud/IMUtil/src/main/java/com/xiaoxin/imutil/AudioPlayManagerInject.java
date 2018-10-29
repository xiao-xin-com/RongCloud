package com.xiaoxin.imutil;

import android.hardware.SensorEvent;
import android.util.Log;

import io.rong.imkit.manager.AudioPlayManager;

public class AudioPlayManagerInject {
    private static final String TAG = "AudioPlayManagerInject";

    private static class MZAudioPlayManager extends AudioPlayManager {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values[0] != 0) {
                event.values[0] = event.sensor.getMaximumRange();
            }
            super.onSensorChanged(event);
        }

        public static AudioPlayManager getInstance() {
            return SingletonHolder.sInstance;
        }

        static class SingletonHolder {
            static final MZAudioPlayManager sInstance = new MZAudioPlayManager();

            private SingletonHolder() {

            }
        }
    }

    public static void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                inject();
            }
        }).start();
    }

    private static void inject() {
        try {
            Util.setField("io.rong.imkit.manager.AudioPlayManager$SingletonHolder",
                    "sInstance", null, MZAudioPlayManager.getInstance());
        } catch (Exception e) {
            Log.e(TAG, "init error", e);
        }
    }
}
