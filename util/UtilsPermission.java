package infiapp.com.videomaker.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Iterator;

import infiapp.com.videomaker.R;


public class UtilsPermission {
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 7889;
    public static ArrayList<Permission> sPermissions;
    private static Activity appContext;

    public UtilsPermission(Activity context) {

        appContext = context;
    }

    @TargetApi(23)
    public static void checkPermissionsGranted() {
        createPermissionsCheckListIfNeed();
        String[] packageNameNotGrantedArray = getPackageNameNotGrantedArray(sPermissions);
        if (packageNameNotGrantedArray == null || packageNameNotGrantedArray.length <= 0) {
            return;
        }
        String string = appContext.getString(R.string.permission_guide);
        Iterator it = sPermissions.iterator();
        while (it.hasNext()) {
            Permission permission = (Permission) it.next();
            if (!permission.isGranted) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(string);
                stringBuilder.append("\n    ");
                stringBuilder.append(permission.displayName);
                string = stringBuilder.toString();
            }
        }
        appContext.requestPermissions(getPackageNameNotGrantedArray(sPermissions), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

    }

    @TargetApi(23)
    public static void createPermissionsCheckListIfNeed() {

        ArrayList arrayList = sPermissions;
        if (arrayList == null || arrayList.size() == 0) {
            sPermissions = new ArrayList();
            sPermissions.add(new Permission("android.permission.WRITE_EXTERNAL_STORAGE", appContext.getString(R.string.permission_write_ex_storage)));
            sPermissions.add(new Permission("android.permission.CAMERA", appContext.getString(R.string.permission_Camera)));
            sPermissions.add(new Permission("android.permission.READ_PHONE_STATE", appContext.getString(R.string.permission_Camera)));
        }
        Iterator it = sPermissions.iterator();
        while (it.hasNext()) {
            Permission permission = (Permission) it.next();
            permission.isGranted = appContext.checkSelfPermission(permission.permissionName) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static String[] getPackageNameNotGrantedArray(ArrayList<Permission> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Permission permission = (Permission) it.next();
            if (!permission.isGranted) {
                arrayList2.add(permission.permissionName);
            }
        }
        return (String[]) arrayList2.toArray(new String[arrayList2.size()]);
    }


    public static class Permission {
        public String displayName;
        public boolean isGranted;
        String permissionName;

        public Permission(String str, String str2) {
            this.permissionName = str;
            this.displayName = str2;
        }

        public Permission(String str, String str2, boolean z) {
            this.permissionName = str;
            this.displayName = str2;
            this.isGranted = z;
        }
    }
}
