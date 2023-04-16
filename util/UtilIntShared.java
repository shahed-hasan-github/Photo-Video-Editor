package infiapp.com.videomaker.util;

public enum UtilIntShared {
    ADS(1);
    
    int mDefaultValue;

    UtilIntShared(int i) {
        this.mDefaultValue = i;
    }

    public int getDefaultValue() {
        return this.mDefaultValue;
    }

    public String getName() {
        return name();
    }
}
