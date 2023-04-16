package infiapp.com.videomaker.theme.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import infiapp.com.videomaker.BuildConfig;
import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.R;

import static infiapp.com.videomaker.BuildConfig.APPLICATION_ID;

public class FileUtils {

    public static final String HIDDENDIRECTORYNAME = ".MyGalaryLock/";
    public static final String HIDDENDIRECTORYNAMEIMAGE = "Image/";
    public static final String HIDDENDIRECTORYNAMETHUMBIMAGE = ".thumb/Image/";
    public static final String HIDDENDIRECTORYNAMETHUMBVIDEO = ".thumb/Video/";
    public static final String HIDDENDIRECTORYNAMEVIDEO = "Video/";
    static long mDeleteFileCount = 0;
    public static String filepath1 = "/data/user/0/" + BuildConfig.APPLICATION_ID;
    //    public static String filepath1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    public static String filepath = "/storage/emulated/0";
    public static File mSdCard = new File(filepath1);
    //    public static File appDirectory = new File(mSdCard, "PhotoSlideShow");
    public static File appDirectory = new File(mSdCard, MyApplication.appName);
    public static final File TEMP_DIRECTORY = new File(appDirectory, ".temp");
    public static final File TEMP_DIRECTORY_AUDIO = new File(appDirectory, ".temp_audio");
    public static final File TEMP_IMG_DIRECTORY = new File(appDirectory, ".temp_image");
    public static final File TEMP_VID_DIRECTORY = new File(TEMP_DIRECTORY, ".temp_vid");
    static final String FFMPEGFILENAME = "ffmpeg";
    public static final File frameFile = new File(appDirectory, ".frame.png");
    private static File[] mStorageList;
    static final String RAWEXTERNALSTORAGE = System.getenv("EXTERNAL_STORAGE");
    static String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
    static String unlockDirectoryNameImage = "GalaryLock/Image/";
    static String unlockDirectoryNameVideo = "GalaryLock/Video/";

    static {
        if (!TEMP_IMG_DIRECTORY.exists()) {
            TEMP_IMG_DIRECTORY.mkdirs();
        }
        if (!TEMP_DIRECTORY.exists()) {
            TEMP_DIRECTORY.mkdirs();
        }
        if (!TEMP_VID_DIRECTORY.exists()) {
            TEMP_VID_DIRECTORY.mkdirs();
        }
    }

    public FileUtils() {
        mDeleteFileCount = 0;
    }


    public static void addImageTovideo(File file) {
        appendLog(getVideoDirectory(), String.format("file '%s'", file.getAbsolutePath()));
    }

    public static void addVideoToConcat(int n) {
        appendLog(new File(getVideoDirectory(), "video.txt"), String.format("file '%s'", getVideoFile(n).getAbsolutePath()));
    }

    public static void appendLog(File file, String s) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
                bufferedWriter.append(s);
                bufferedWriter.newLine();
                bufferedWriter.close();
            }
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }


    public static void copyFile(File file, File file2) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(file2)) {
                byte[] array = new byte[1024];
                while (true) {
                    int read = fileInputStream.read(array);
                    if (read <= 0) {
                        fileInputStream.close();
                        fileOutputStream.close();
                        return;
                    }
                    fileOutputStream.write(array, 0, read);
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        boolean delete = false;
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File file2 : listFiles) {
                        mDeleteFileCount += file2.length();
                        deleteFile(file2);
                    }
                }
                mDeleteFileCount += file.length();
                return file.delete();
            }
            mDeleteFileCount += file.length();
            delete = file.delete();
        }
        return delete;
    }

    public static boolean deleteFile(String s) {
        return deleteFile(new File(s));
    }

    public static void deleteTempDir() {
        final File[] listFiles = TEMP_DIRECTORY.listFiles();
        int length = listFiles.length;
        for (int i = 0; i < length; i++) {
            final int finalI = i;
            new Thread(() -> FileUtils.deleteFile(listFiles[finalI])).start();
        }
    }

    public static boolean deleteThemeDir(String s) {
        return deleteFile(getImageDirectory(s));
    }

    public static String genrateFileId(String s) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(System.currentTimeMillis());
        String string = sb.toString();
        if (s.endsWith(".jpg")) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(string);
            sb2.append("song_1");
            return sb2.toString();
        } else if (s.endsWith(".JPG")) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(string);
            sb3.append("song_2");
            return sb3.toString();
        } else if (s.endsWith(".png")) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(string);
            sb4.append("song_3");
            return sb4.toString();
        } else if (s.endsWith(".PNG")) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append(string);
            sb5.append("song_4");
            return sb5.toString();
        } else if (s.endsWith(".jpeg")) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append(string);
            sb6.append("song_5");
            return sb6.toString();
        } else if (s.endsWith(".JPEG")) {
            StringBuilder sb7 = new StringBuilder();
            sb7.append(string);
            sb7.append("_6");
            return sb7.toString();
        } else if (s.endsWith(".mp4")) {
            StringBuilder sb8 = new StringBuilder();
            sb8.append(string);
            sb8.append("_7");
            return sb8.toString();
        } else if (s.endsWith(".3gp")) {
            StringBuilder sb9 = new StringBuilder();
            sb9.append(string);
            sb9.append("_8");
            return sb9.toString();
        } else if (s.endsWith(".flv")) {
            StringBuilder sb10 = new StringBuilder();
            sb10.append(string);
            sb10.append("_9");
            return sb10.toString();
        } else if (s.endsWith(".m4v")) {
            StringBuilder sb11 = new StringBuilder();
            sb11.append(string);
            sb11.append("_10");
            return sb11.toString();
        } else if (s.endsWith(".avi")) {
            StringBuilder sb12 = new StringBuilder();
            sb12.append(string);
            sb12.append("_11");
            return sb12.toString();
        } else if (s.endsWith(".wmv")) {
            StringBuilder sb13 = new StringBuilder();
            sb13.append(string);
            sb13.append("_12");
            return sb13.toString();
        } else if (s.endsWith(".mpeg")) {
            StringBuilder sb14 = new StringBuilder();
            sb14.append(string);
            sb14.append("_13");
            return sb14.toString();
        } else if (s.endsWith(".VOB")) {
            StringBuilder sb15 = new StringBuilder();
            sb15.append(string);
            sb15.append("_14");
            return sb15.toString();
        } else if (s.endsWith(".MOV")) {
            StringBuilder sb16 = new StringBuilder();
            sb16.append(string);
            sb16.append("_15");
            return sb16.toString();
        } else if (s.endsWith(".MPEG4")) {
            StringBuilder sb17 = new StringBuilder();
            sb17.append(string);
            sb17.append("_16");
            return sb17.toString();
        } else if (s.endsWith(".DivX")) {
            StringBuilder sb18 = new StringBuilder();
            sb18.append(string);
            sb18.append("_17");
            return sb18.toString();
        } else if (!s.endsWith(".mkv")) {
            return string;
        } else {
            StringBuilder sb19 = new StringBuilder();
            sb19.append(string);
            sb19.append("_18");
            return sb19.toString();
        }
    }

    public static ArrayList<File> getCacheDirectory(String s) {
        ArrayList<File> list = new ArrayList();
        for (File file : getStorages()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Android/data/");
            sb.append(s);
            sb.append("/cache");
            File file2 = new File(file, sb.toString());
            if (file2.exists()) {
                list.add(file2);
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Android/data/");
            sb2.append(s);
            sb2.append("/Cache");
            File file3 = new File(file, sb2.toString());
            if (file3.exists()) {
                list.add(file3);
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Android/data/");
            sb3.append(s);
            sb3.append("/.cache");
            File file4 = new File(file, sb3.toString());
            if (file4.exists()) {
                list.add(file4);
            }
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Android/data/");
            sb4.append(s);
            sb4.append("/.Cache");
            File file5 = new File(file, sb4.toString());
            if (file5.exists()) {
                list.add(file5);
            }
        }
        return list;
    }

    public static long getDirectorySize(File file) {
        long j = 0;
        if (file != null) {
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file2 : listFiles) {
                    long directorySize;
                    if (file2.isDirectory()) {
                        directorySize = j + getDirectorySize(file2);
                    } else {
                        directorySize = j + file2.length();
                    }
                    j = directorySize;
                }
            }
        }
        return j;
    }

    @SuppressLint({"DefaultLocale"})
    public static String getDuration(long n) {
        if (n < 1000) {
            return String.format("%02d:%02d", Integer.valueOf(0), Integer.valueOf(0));
        }
        long n2 = n / 1000;
        n = n2 / 3600;
        long n3 = 3600 * n;
        long n5 = n2 - ((60 * ((n2 - n3) / 60)) + n3);
        if (n == 0) {
            return String.format("%02d:%02d", n3, Long.valueOf(n5));
        }
        return String.format("%02d:%02d:%02d", n, Long.valueOf(n3), Long.valueOf(n5));
    }

    public static String getFFmpeg(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(getFilesDirectory(context).getAbsolutePath());
        sb.append(File.separator);
        sb.append(FFMPEGFILENAME);
        return sb.toString();
    }

    static String getFFmpeg(Context context, Map<String, String> map) {
        String str = "";
        if (map != null) {
            for (Entry<String, String> entry : map.entrySet()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(entry.getKey());
                stringBuilder.append("=");
                stringBuilder.append(entry.getValue());
                stringBuilder.append(" ");
                str = stringBuilder.toString();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(getFFmpeg(context));
        return sb.toString();
    }


    static File getFilesDirectory(Context context) {
        return context.getFilesDir();
    }

    public static File getHiddenAppDirectory(File file) {
        File file2 = new File(file, HIDDENDIRECTORYNAME);
        if (file2.exists()) {
            file2.mkdirs();
        }
        return file2;
    }

    public static File getHiddenImageAppDirectory(File file) {
        File file2 = new File(getHiddenAppDirectory(file), HIDDENDIRECTORYNAMEIMAGE);
        if (file2.exists()) {
            file2.mkdirs();
        }
        return file2;
    }

    public static File getHiddenImageDirectory(File file, String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(".MyGalaryLock/Image/");
        sb.append(s);
        return new File(file, sb.toString());
    }

    public static File getHiddenVideoAppDirectory(File file) {
        File file2 = new File(getHiddenAppDirectory(file), HIDDENDIRECTORYNAMEVIDEO);
        if (file2.exists()) {
            file2.mkdirs();
        }
        return file2;
    }

    public static File getHiddenVideoDirectory(File file, String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(".MyGalaryLock/Video/");
        sb.append(s);
        return new File(file, sb.toString());
    }

    public static File getImageDirectory(int n) {
        File file = new File(TEMP_DIRECTORY, String.format("IMG_%03d", Integer.valueOf(n)));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getImageDirectory(String s) {
        File file = new File(TEMP_DIRECTORY, s);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getImageDirectory(String s, int n) {
        File file = new File(getImageDirectory(s), String.format("IMG_%03d", Integer.valueOf(n)));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getMoveFolderpath(File file, String s) {
        File parentFile = file.getParentFile().getParentFile();
        StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("/");
        sb.append(file.getName());
        return new File(parentFile, sb.toString());
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File getOutputImageFile() {
        if (!appDirectory.exists() && !appDirectory.mkdirs()) {
            return null;
        }
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File appDirectory1 = appDirectory;
        StringBuilder sb = new StringBuilder();
        sb.append("IMG_");
        sb.append(format);
        sb.append(".jpg");
        return new File(appDirectory1, sb.toString());
    }

    public static Bitmap getPicFromBytes(byte[] array) {
        if (array == null) {
            return null;
        }
        int round;
        Options bitmapFactoryOptions = new Options();
        bitmapFactoryOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(array, 0, array.length, bitmapFactoryOptions);
        if (bitmapFactoryOptions.outWidth >= bitmapFactoryOptions.outHeight ? bitmapFactoryOptions.outWidth >= 1024 : bitmapFactoryOptions.outHeight >= 1024) {
            round = Math.round(((float) bitmapFactoryOptions.outWidth) / 1024.0f);
        } else {
            round = 1;
        }
        Options bitmapFactoryOptions2 = new Options();
        bitmapFactoryOptions2.inSampleSize = round;
        return BitmapFactory.decodeByteArray(array, 0, array.length, bitmapFactoryOptions2).copy(Config.RGB_565, true);
    }

    public static File getRestoreImageDirectory(File file, String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(unlockDirectoryNameImage);
        sb.append(s);
        return new File(file, sb.toString());
    }

    public static File getRestoreVideoDirectory(File file, String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(unlockDirectoryNameVideo);
        sb.append(s);
        return new File(file, sb.toString());
    }

    public static File[] getStorages() {
        if (mStorageList != null) {
            return mStorageList;
        }
        return getStorge();
    }

    private static File[] getStorge() {
        ArrayList<File> list = new ArrayList();
        if (RAWEXTERNALSTORAGE != null) {
            list.add(new File(RAWEXTERNALSTORAGE));
        } else if (mSdCard != null) {
            list.add(mSdCard);
        }
        if (rawSecondaryStoragesStr != null) {
            list.add(new File(rawSecondaryStoragesStr));
        }
        mStorageList = new File[list.size()];
        for (int i = 0; i < list.size(); i++) {
            mStorageList[i] = list.get(i);
        }
        return mStorageList;
    }

    public static File getThumbnailDirectory(File file, int n) {
        if (n == 0) {
            file = new File(getHiddenAppDirectory(file), HIDDENDIRECTORYNAMETHUMBVIDEO);
        } else {
            file = new File(getHiddenAppDirectory(file), HIDDENDIRECTORYNAMETHUMBIMAGE);
        }
        if (file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getVideoDirectory() {
        if (!TEMP_VID_DIRECTORY.exists()) {
            TEMP_VID_DIRECTORY.mkdirs();
        }
        return TEMP_VID_DIRECTORY;
    }

    public static File getVideoFile(int n) {
        if (!TEMP_VID_DIRECTORY.exists()) {
            TEMP_VID_DIRECTORY.mkdirs();
        }
        return new File(TEMP_VID_DIRECTORY, String.format("vid_%03d.mp4", new Object[]{Integer.valueOf(n)}));
    }


    public static void moveFile(File file, File file2) throws IOException {
        if (file.exists() && !file.renameTo(file2)) {
            if (!file2.getParentFile().exists()) {
                file2.getParentFile().mkdirs();
            }
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(file2)) {
                    byte[] array = new byte[1024];
                    while (true) {
                        int read = fileInputStream.read(array);
                        if (read <= 0) {
                            break;
                        }
                        fileOutputStream.write(array, 0, read);
                    }
                    fileInputStream.close();
                    fileOutputStream.close();
                }
            }
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void moveFile(String s, String s2) throws IOException {
        if (!new File(s).renameTo(new File(s2))) {
            File file = new File(s);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileInputStream fileInputStream = new FileInputStream(s)) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(s2)) {
                    byte[] array = new byte[1024];
                    while (true) {
                        int read = fileInputStream.read(array);
                        if (read <= 0) {
                            break;
                        }
                        fileOutputStream.write(array, 0, read);
                    }
                    fileInputStream.close();
                    fileOutputStream.close();
                }
            }
            File file2 = new File(s);
            if (file2.exists()) {
                file2.delete();
            }
        }
    }


    public static char[] readPatternData(Context context) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(context.getFilesDir());
            sb.append("/pattern");
            if (!new File(sb.toString()).exists()) {
                return null;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(context.getFilesDir());
            sb2.append("/pattern");
            char[] array;
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(sb2.toString()))) {
                array = (char[]) objectInputStream.readObject();
                objectInputStream.close();
            }
            return array;
        } catch (Exception e) {
            return null;
        }
    }
}
