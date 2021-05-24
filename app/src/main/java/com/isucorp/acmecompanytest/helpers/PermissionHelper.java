package com.isucorp.acmecompanytest.helpers;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import com.isucorp.acmecompanytest.App;
import com.isucorp.acmecompanytest.Info;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

@Info("Created by Ivan Faez Cobo on 23/5/2021")
public class PermissionHelper
{
    /**
     * Check if the given permission is granted. It always returns true in android versions under the API 23.
     *
     * @param permission One of the permissions in the {@link android.Manifest.permission} class.
     * @return 'true' if the permission is granted.
     */
    public static boolean isGranted(String permission)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        return ContextCompat.checkSelfPermission(App.getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if the given permission is denied. It always returns false in android versions under the API 23.
     *
     * @param permission One of the permissions in the {@link android.Manifest.permission} class.
     * @return 'true' if the permission is denied.
     */
    public static boolean isDenied(String permission)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return false;

        return ContextCompat.checkSelfPermission(App.getContext(), permission) == PackageManager.PERMISSION_DENIED;
    }

    /**
     * Returns true if all the given permissions are granted. If one not granted we request the
     * permissions using the requestCode.
     * <p/>
     * The activity can override the {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * method as a callback and check if permission was granted or denied.
     *
     * @param activity    The activity.
     * @param permissions Array of the permissions in the {@link android.Manifest.permission} class.
     * @param requestCode The request code passed to the {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     */
    public static boolean checkForPermission(final Activity activity, final String[] permissions, final int requestCode)
    {
        for (String permission : permissions)
        {
            if (isDenied(permission))
            {
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
                return false;
            }
        }

        return true;
    }
}
