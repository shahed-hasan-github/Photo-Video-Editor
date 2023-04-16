package infiapp.com.videomaker.theme.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build;
import android.os.Build.VERSION;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.core.view.MotionEventCompat;

import infiapp.com.videomaker.MyApplication;

import java.lang.reflect.Array;

public class FastBlur {

    private FastBlur() {
        //dosomething
    }

    public static Bitmap blurBitmap(Bitmap bitmap, int n, Context context) {
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        RenderScript create = RenderScript.create(context);
        ScriptIntrinsicBlur create2 = null;
        if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        }
        Allocation fromBitmap = Allocation.createFromBitmap(create, bitmap);
        Allocation fromBitmap2 = Allocation.createFromBitmap(create, bitmap2);
        if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            create2.setRadius((float) n);
            create2.setInput(fromBitmap);
            create2.forEach(fromBitmap2);
        }
        fromBitmap2.copyTo(bitmap2);
        create.destroy();
        return bitmap2;
    }


    public static Bitmap doBlur(Bitmap copy, int n, boolean b) {
        Bitmap bitmap = copy;
        if (VERSION.SDK_INT >= 17) {
            return blurBitmap(bitmap, n, MyApplication.getInstance());
        }
        if (b) {
            copy = bitmap;
        } else {
            copy = bitmap.copy(copy.getConfig(), true);
        }
        if (n < 1) {
            return null;
        }
        int width = copy.getWidth();
        int height = copy.getHeight();
        int n2 = width * height;
        int[] array = new int[n2];
        copy.getPixels(array, 0, width, 0, 0, width, height);
        int n3 = width - 1;
        int n4 = height - 1;
        int n5 = (n + n) + 1;
        int[] array2 = new int[n2];
        int[] array3 = new int[n2];
        int[] array4 = new int[n2];
        int[] array5 = new int[Math.max(width, height)];
        int n6 = (n5 + 1) >> 1;
        int n7 = n6 * n6;
        int n8 = n7 * 256;
        int[] array6 = new int[n8];
        for (int i = 0; i < n8; i++) {
            array6[i] = i / n7;
        }
        int[][] array7 = (int[][]) Array.newInstance(Integer.TYPE, new int[]{n5, 3});
        int n9 = n + 1;
        int n10 = 0;
        int n11 = 0;
        int n12 = n4;
        Bitmap bitmap2 = copy;
        for (int j = 0; j < height; j++) {
            int n13 = 0;
            int n14 = 0;
            int n15 = 0;
            int n16 = 0;
            int n17 = 0;
            int n18 = 0;
            int n19 = 0;
            int n20 = 0;
            int n21 = 0;
            for (int k = -n; k <= n; k++) {
                int n22 = array[Math.min(n3, Math.max(k, 0)) + n10];
                int[] array8 = array7[k + n];
                array8[0] = (16711680 & n22) >> 16;
                array8[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & n22) >> 8;
                array8[2] = n22 & 255;
                int n23 = n9 - Math.abs(k);
                n13 += array8[0] * n23;
                n14 += array8[1] * n23;
                n15 += array8[2] * n23;
                if (k > 0) {
                    n16 += array8[0];
                    n17 += array8[1];
                    n18 += array8[2];
                } else {
                    n19 += array8[0];
                    n20 += array8[1];
                    n21 += array8[2];
                }
            }
            int n25 = n17;
            int n26 = n16;
            int n27 = n;
            for (int l = 0; l < width; l++) {
                array2[n10] = array6[n13];
                array3[n10] = array6[n14];
                array4[n10] = array6[n15];
                int[] array9 = array7[((n27 - n) + n5) % n5];
                int n28 = array9[0];
                int n29 = array9[1];
                int n30 = array9[2];
                if (j == 0) {
                    array5[l] = Math.min((l + n) + 1, n3);
                }
                int n31 = array[array5[l] + n11];
                array9[0] = (16711680 & n31) >> 16;
                array9[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & n31) >> 8;
                array9[2] = n31 & 255;
                int n32 = n26 + array9[0];
                int n33 = n25 + array9[1];
                int n34 = n18 + array9[2];
                n13 = (n13 - n19) + n32;
                n14 = (n14 - n20) + n33;
                n15 = (n15 - n21) + n34;
                n27 = (n27 + 1) % n5;
                int[] array10 = array7[n27 % n5];
                n19 = (n19 - n28) + array10[0];
                n20 = (n20 - n29) + array10[1];
                n21 = (n21 - n30) + array10[2];
                n26 = n32 - array10[0];
                n25 = n33 - array10[1];
                n18 = n34 - array10[2];
                n10++;
            }
            n11 += width;
        }
        int n35 = height;
        int n37 = n12;
        int[] array11 = array5;
        for (int n38 = 0; n38 < width; n38++) {
            int n39 = -n;
            int n40 = n39 * width;
            int n41 = 0;
            int n42 = 0;
            int n43 = 0;
            int n44 = 0;
            int n45 = 0;
            int n46 = 0;
            int n47 = 0;
            int n48 = 0;
            int n49 = 0;
            while (n39 <= n) {
                int n50 = Math.max(0, n40) + n38;
                int[] array12 = array7[n39 + n];
                array12[0] = array2[n50];
                array12[1] = array3[n50];
                array12[2] = array4[n50];
                int n51 = n9 - Math.abs(n39);
                int n52 = n41 + (array2[n50] * n51);
                n42 += array3[n50] * n51;
                n43 += array4[n50] * n51;
                if (n39 > 0) {
                    n44 += array12[0];
                    n45 += array12[1];
                    n46 += array12[2];
                } else {
                    n47 += array12[0];
                    n48 += array12[1];
                    n49 += array12[2];
                }
                int n53 = n40;
                if (n39 < n37) {
                    n53 = n40 + width;
                }
                n39++;
                n40 = n53;
                n41 = n52;
            }
            int n55 = n45;
            int n58 = n;
            int n59 = n44;
            int n60 = n43;
            int n61 = n42;
            int n62 = n46;
            int n63 = n41;
            int n64 = n38;
            for (int n65 = 0; n65 < n35; n65++) {
                array[n64] = (((array[n64] & -16777216) | (array6[n63] << 16)) | (array6[n61] << 8)) | array6[n60];
                int[] array13 = array7[((n58 - n) + n5) % n5];
                int n66 = array13[0];
                int n67 = array13[1];
                int n68 = array13[2];
                if (n38 == 0) {
                    array11[n65] = Math.min(n65 + n9, n37) * width;
                }
                int n69 = array11[n65] + n38;
                array13[0] = array2[n69];
                array13[1] = array3[n69];
                array13[2] = array4[n69];
                int n70 = n59 + array13[0];
                int n71 = n55 + array13[1];
                int n72 = n62 + array13[2];
                n63 = (n63 - n47) + n70;
                n61 = (n61 - n48) + n71;
                n60 = (n60 - n49) + n72;
                n58 = (n58 + 1) % n5;
                int[] array14 = array7[n58];
                n47 = (n47 - n66) + array14[0];
                n48 = (n48 - n67) + array14[1];
                n49 = (n49 - n68) + array14[2];
                n59 = n70 - array14[0];
                n55 = n71 - array14[1];
                n62 = n72 - array14[2];
                n64 += width;
            }
        }
        bitmap2.setPixels(array, 0, width, 0, 0, width, n35);
        return bitmap2;
    }
}
