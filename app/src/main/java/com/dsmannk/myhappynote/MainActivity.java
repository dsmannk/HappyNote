package com.dsmannk.myhappynote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    FirebaseRemoteConfig remoteConfig;
    long newAppVersion = 0;
    long toolbarImgCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        checkVersion(task.isSuccessful());

                        if (task.isSuccessful()) {
                            Log.e("new_app_version", " = " + remoteConfig.getLong("new_app_version"));
                            Log.e("toolbar_img_count", " = " + remoteConfig.getLong("toolbar_img_count"));
                        }
                    }
                });
    }

    private void checkVersion(boolean successful) {
        if(successful) {
            newAppVersion = remoteConfig.getLong("new_app_version");
            toolbarImgCount = remoteConfig.getLong("toolbar_img_count");

            try {
                PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);

                long appVersion;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    appVersion = pi.getLongVersionCode();
                } else {
                    appVersion = pi.versionCode;
                }

                if(appVersion < newAppVersion) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("???????????? ??????.");
                    builder.setMessage("??????????????? ?????????????????????.\n???????????? ?????????.")
                            .setCancelable(false).setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=com.dsmannk.myhappynote"));
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "???????????? ?????? ?????????", Toast.LENGTH_SHORT);
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}