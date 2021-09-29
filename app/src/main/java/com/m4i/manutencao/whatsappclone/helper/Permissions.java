//
// Author: Bruno Lima
// Company: M4I
// 13/08/2021 at 16:19
//

package com.m4i.manutencao.whatsappclone.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissions {

    public static boolean validatePermissions(String[] neededPermissions, Activity activity, int requestCode) {

        if (Build.VERSION.SDK_INT >= 23) {

            List<String> extraPermissionsNeeded = new ArrayList<>();

            for (String permission : neededPermissions) {
                Boolean hasPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if (!hasPermission) {
                    extraPermissionsNeeded.add(permission);
                }
            }

            //If no extra permissions is needed we do not need to add anything
            if (extraPermissionsNeeded.isEmpty()) {
                return true;
            }

            //If we need to add permissions to the app
            String[] newPermissions = new String[extraPermissionsNeeded.size()];
            extraPermissionsNeeded.toArray(newPermissions);
            ActivityCompat.requestPermissions(activity, neededPermissions, requestCode);
        }
        return true;
    }
}
