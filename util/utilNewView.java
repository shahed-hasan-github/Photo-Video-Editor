package infiapp.com.videomaker.util;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class utilNewView extends AppCompatImageView {
    public utilNewView(Context context) {
        super(context);
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight());
    }

    public utilNewView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public utilNewView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}
