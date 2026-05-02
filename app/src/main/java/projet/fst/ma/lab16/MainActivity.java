package projet.fst.ma.lab16;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView tvTemps;
    private Button btnStart, btnStop;
    private ChronometreService chronometreService;
    private boolean isBound = false;

    // Handler pour mettre à jour l'UI toutes les secondes
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isBound && chronometreService != null) {
                tvTemps.setText(chronometreService.getFormattedTime());
            }
            handler.postDelayed(this, 1000);
        }
    };

    // Connexion au service (Bound Service)
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChronometreService.LocalBinder binder = (ChronometreService.LocalBinder) service;
            chronometreService = binder.getService();
            isBound = true;
            // Démarrer la mise à jour de l'UI dès que le service est connecté
            handler.post(updateTimeRunnable);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            chronometreService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTemps = findViewById(R.id.tvTemps);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        // Demander la permission de notification pour Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demarrerService();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arreterService();
            }
        });
    }

    private void demarrerService() {
        Intent intent = new Intent(this, ChronometreService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void arreterService() {
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        handler.removeCallbacks(updateTimeRunnable);

        Intent intent = new Intent(this, ChronometreService.class);
        intent.setAction("STOP");
        // On utilise startService pour envoyer l'action "STOP"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        
        tvTemps.setText("00:00");
    }

    @Override
    protected void onDestroy() {
        if (isBound) {
            unbindService(connection);
        }
        handler.removeCallbacks(updateTimeRunnable);
        super.onDestroy();
    }
}
