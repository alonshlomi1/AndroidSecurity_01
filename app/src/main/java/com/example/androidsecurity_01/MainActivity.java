package com.example.androidsecurity_01;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    private MaterialTextView main_MTV_password;
    private EditText main_ET_password;
    private MaterialButton main_MTB_submit;
    private MaterialButton main_MTB_hint;
    private MaterialButton main_MTB_hint2;
    private MaterialTextView main_MTV_hint;
    private MaterialTextView main_MTV_hint2;
    private MaterialTextView main_MTV_status;
    private int battery;
    private String wifiName;
    private boolean isDarkmodeOn = false;
    private BatteryManager batteryManager;

    private WifiManager wifiManager;

    private int currentNightMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViews();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            // Permissions are already granted
            initViews();
        }

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the functionality
                initViews();
            } else {
                // Permission denied, handle accordingly
                handlePermissionDenied();
            }
        }
    }

    private void handlePermissionDenied() {
        main_MTV_password.setVisibility(View.GONE);
        main_ET_password.setVisibility(View.GONE);
        main_MTB_submit.setVisibility(View.GONE);
        main_MTB_hint.setVisibility(View.GONE);
        main_MTB_hint2.setVisibility(View.GONE);
        main_MTV_hint.setVisibility(View.GONE);
        main_MTV_hint2.setVisibility(View.GONE);
        main_MTV_status.setText("Have To Allow Persice Location Permissions");
        main_MTV_status.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        main_MTB_submit.setOnClickListener(v -> submit());
        main_MTB_hint.setOnClickListener(v -> hintClicked());
        main_MTB_hint2.setOnClickListener(v -> hint2Clicked());
        main_MTV_hint.setVisibility(View.GONE);
        main_MTV_hint2.setVisibility(View.GONE);
        main_MTB_hint2.setVisibility(View.GONE);
        main_MTV_status.setVisibility(View.GONE);
        batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private void submit() {
        getBattery();
        getWifinName();
        getDarkmodeStatus();
        checkValid();
    }



    private void getBattery() {
        battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    private void getWifinName() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        wifiName = wifiInfo.getSSID();
        if (wifiName.startsWith("\"") && wifiName.endsWith("\"")) {
            wifiName = wifiName.substring(1, wifiName.length() - 1);
        }
    }

    private void getDarkmodeStatus() {

        currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(currentNightMode == 32)
            isDarkmodeOn = true;
    }

    private void checkValid() {

        String password = battery +""+ wifiName.substring(0, Math.min(wifiName.length(), 2));
        String enteredPassword = main_ET_password.getText().toString();

        Log.d("######", password +" "+ enteredPassword+"  " );
        if(enteredPassword.equals(password) && isDarkmodeOn )
            valid();
//        else
//            vibrate();

    }

    private void valid() {
        main_MTV_password.setVisibility(View.GONE);
        main_ET_password.setVisibility(View.GONE);
        main_MTB_submit.setVisibility(View.GONE);
        main_MTB_hint.setVisibility(View.GONE);
        main_MTB_hint2.setVisibility(View.GONE);
        main_MTV_hint.setVisibility(View.GONE);
        main_MTV_hint2.setVisibility(View.GONE);
        main_MTV_status.setText("Welcome");
        main_MTV_status.setVisibility(View.VISIBLE);
    }

    private void hint2Clicked() {
        main_MTB_hint2.setVisibility(View.GONE);
        main_MTV_hint2.setText("turn on your Dark mode");
        main_MTV_hint2.setVisibility(View.VISIBLE);

    }

    private void hintClicked() {
        main_MTV_hint.setVisibility(View.VISIBLE);
        main_MTV_hint.setText("The Password is your battery life + Two First Letters in the wifi name");
        main_MTB_hint.setVisibility(View.GONE);
        main_MTB_hint2.setVisibility(View.VISIBLE);

    }

    private void findViews() {
        main_MTV_password = findViewById(R.id.main_MTV_password);
        main_ET_password = findViewById(R.id.main_ET_password);
        main_MTB_submit = findViewById(R.id.main_MTB_submit);
        main_MTB_hint = findViewById(R.id.main_MTB_hint);
        main_MTB_hint2 = findViewById(R.id.main_MTB_hint2);
        main_MTV_hint = findViewById(R.id.main_MTV_hint);
        main_MTV_hint2 = findViewById(R.id.main_MTV_hint2);
        main_MTV_status = findViewById(R.id.main_MTV_status);
    }
}