package projet.fst.ma.lab16;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChronometreService extends Service {

    private static final String TAG = "ChronometreService";
    private final IBinder binder = new LocalBinder();
    
    private int secondes = 0;
    private boolean isRunning = false;
    private ScheduledExecutorService executor;
    private static final int NOTIFICATION_ID = 1001;
    private NotificationManager notificationManager;

    public class LocalBinder extends Binder {
        public ChronometreService getService() {
            return ChronometreService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        creerNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = (intent != null) ? intent.getAction() : null;
        Log.d(TAG, "onStartCommand action: " + action);

        if ("STOP".equals(action)) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (!isRunning) {
            isRunning = true;
            startForeground(NOTIFICATION_ID, creerNotification());
            demarrerChronometre();
        }
        return START_STICKY;
    }

    private void demarrerChronometre() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                secondes++;
                updateNotification();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void creerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "chrono_channel",
                    "Chronomètre Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification creerNotification() {
        return new NotificationCompat.Builder(this, "chrono_channel")
                .setContentTitle("Chronomètre en cours")
                .setContentText("Temps : " + getFormattedTime())
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void updateNotification() {
        notificationManager.notify(NOTIFICATION_ID, creerNotification());
    }

    public String getFormattedTime() {
        int minutes = secondes / 60;
        int secondesRest = secondes % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, secondesRest);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return binder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        isRunning = false;
        if (executor != null) {
            executor.shutdown();
        }
        stopForeground(true);
        super.onDestroy();
    }
}
