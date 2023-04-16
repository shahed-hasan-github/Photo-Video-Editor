package infiapp.com.videomaker.model;

import android.text.TextUtils;

public class ImageData {
    public String folderName;
    public long id;
    public String imageAlbum;
    public int imageCount;
    public String imagePath;
    public String imageThumbnail;

    public ImageData() {
        this.imageCount = 0;

    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageAlbum() {
        return this.imageAlbum;
    }

    public void setImageAlbum(String imageAlbum) {
        this.imageAlbum = imageAlbum;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String toString() {
        if (TextUtils.isEmpty(this.imagePath)) {
            return super.toString();
        }
        return "ImageData { imagePath=" + this.imagePath + ",folderName=" + this.folderName + ",imageCount=" + this.imageCount + " }";
    }
}
