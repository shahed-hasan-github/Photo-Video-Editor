package infiapp.com.videomaker.song;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Random;

public class SeekTest {
    public static final String PREF_SEEK_TEST_DATE = "seek_test_date";
    public static final String PREF_SEEK_TEST_RESULT = "seek_test_result";
    private static byte[] silenceMp3Fame;
    public static long after;
    public static long before;

    private SeekTest() {
        //dosomething
    }

    static class C13151 implements OnCompletionListener {
        C13151() {
        }

        public synchronized void onCompletion(MediaPlayer arg0) {
            SeekTest.after = System.currentTimeMillis();
        }
    }

    public static boolean canSeekAccurately(SharedPreferences prefs) {
        Editor prefsEditor;

        boolean result = prefs.getBoolean(PREF_SEEK_TEST_RESULT, false);
        long testDate = prefs.getLong(PREF_SEEK_TEST_DATE, 0);
        long now = new Date().getTime();
        if (now - testDate < 604800000) {

            return result;
        }
        String filename = "/sdcard/silence" + new Random().nextLong() + ".mp3";
        File file = new File(filename);
        boolean ok = false;
        try {
        } catch (Exception e) {
            ok = true;
        }
        if (ok) {

            try {
                file.createNewFile();
                try {
                    int i;
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    for (i = 0; i < 80; i++) {
                        fileOutputStream.write(silenceMp3Fame, 0, silenceMp3Fame.length);
                    }
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(new FileInputStream(filename).getFD(), (long) (silenceMp3Fame.length * 70), (long) (silenceMp3Fame.length * 10));
                    player.prepare();
                    before = 0;
                    after = 0;
                    player.setOnCompletionListener(new C13151());
                    player.start();
                    i = 0;
                    while (i < 200 && before == 0) {
                        try {
                            if (player.getCurrentPosition() > 0) {
                                before = System.currentTimeMillis();
                            }
                            Thread.sleep(10);
                            i++;
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            try {
                                file.delete();
                            } catch (Exception e3) {
                                //dosomething
                            }
                            prefsEditor = prefs.edit();
                            prefsEditor.putLong(PREF_SEEK_TEST_DATE, now);
                            prefsEditor.putBoolean(PREF_SEEK_TEST_RESULT, result);
                            prefsEditor.commit();
                            return false;
                        }
                    }
                    if (before == 0) {
                        try {
                            file.delete();
                        } catch (Exception e4) {
                            //dosomething
                        }
                        prefsEditor = prefs.edit();
                        prefsEditor.putLong(PREF_SEEK_TEST_DATE, now);
                        prefsEditor.putBoolean(PREF_SEEK_TEST_RESULT, result);
                        prefsEditor.commit();
                        return false;
                    }
                    for (i = 0; i < 300 && after == 0; i++) {
                        Thread.sleep(10);
                    }
                    if (after <= before || after >= before + 2000) {
                    } else {
                        result = true;
                    }
                    prefsEditor = prefs.edit();
                    prefsEditor.putLong(PREF_SEEK_TEST_DATE, now);
                    prefsEditor.putBoolean(PREF_SEEK_TEST_RESULT, result);
                    prefsEditor.commit();
                    try {
                        file.delete();
                    } catch (Exception e5) {
                        //sosomething
                    }
                    return result;
                } catch (Exception e6) {
                    try {
                        file.delete();
                    } catch (Exception e7) {
                        //dosomething
                    }
                    return false;
                }
            } catch (Exception e8) {
                return false;
            }
        }
        return false;
    }

    static {
        byte[] bArr = new byte[104];
        bArr[0] = (byte) -1;
        bArr[1] = (byte) -5;
        bArr[2] = (byte) 16;
        bArr[3] = (byte) -60;
        bArr[5] = (byte) 3;
        bArr[6] = (byte) -127;
        bArr[7] = (byte) -12;
        bArr[8] = (byte) 1;
        bArr[9] = (byte) 38;
        bArr[10] = (byte) 96;
        bArr[12] = (byte) 64;
        bArr[13] = (byte) 32;
        bArr[14] = (byte) 89;
        bArr[15] = Byte.MIN_VALUE;
        bArr[16] = (byte) 35;
        bArr[17] = (byte) 72;
        bArr[19] = (byte) 9;
        bArr[20] = (byte) 116;
        bArr[22] = (byte) 1;
        bArr[23] = (byte) 18;
        bArr[24] = (byte) 3;
        bArr[25] = (byte) -1;
        bArr[26] = (byte) -1;
        bArr[27] = (byte) -1;
        bArr[28] = (byte) -1;
        bArr[29] = (byte) -2;
        bArr[30] = (byte) -97;
        bArr[31] = (byte) 99;
        bArr[32] = (byte) -65;
        bArr[33] = (byte) -47;
        bArr[34] = (byte) 122;
        bArr[35] = (byte) 63;
        bArr[36] = (byte) 93;
        bArr[37] = (byte) 1;
        bArr[38] = (byte) -1;
        bArr[39] = (byte) -1;
        bArr[40] = (byte) -1;
        bArr[41] = (byte) -1;
        bArr[42] = (byte) -2;
        bArr[43] = (byte) -115;
        bArr[44] = (byte) -83;
        bArr[45] = (byte) 108;
        bArr[46] = (byte) 49;
        bArr[47] = (byte) 66;
        bArr[48] = (byte) -61;
        bArr[49] = (byte) 2;
        bArr[50] = (byte) -57;
        bArr[51] = (byte) 12;
        bArr[52] = (byte) 9;
        bArr[53] = (byte) -122;
        bArr[54] = (byte) -125;
        bArr[55] = (byte) -88;
        bArr[56] = (byte) 122;
        bArr[57] = (byte) 58;
        bArr[58] = (byte) 104;
        bArr[59] = (byte) 76;
        bArr[60] = (byte) 65;
        bArr[61] = (byte) 77;
        bArr[62] = (byte) 69;
        bArr[63] = (byte) 51;
        bArr[64] = (byte) 46;
        bArr[65] = (byte) 57;
        bArr[66] = (byte) 56;
        bArr[67] = (byte) 46;
        bArr[68] = (byte) 50;
        silenceMp3Fame = bArr;
    }
}
