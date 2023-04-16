package infiapp.com.videomaker.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import infiapp.com.videomaker.R;


public class RatingDialog extends AppCompatDialog implements OnRatingBarChangeListener, OnClickListener {
    static final String SESSION_COUNT = "session_count";
    static final String SHOW_NEVER = "show_never";
    String myPrefs = "SwaRatingDialog";
    Builder builder;
    Context context;
    EditText etFeedback;
    LinearLayout feedbackButtons;
    ImageView ivIcon;
    RatingBar ratingBar;
    LinearLayout ratingButtons;
    int session;
    SharedPreferences sharedpreferences;
    float threshold;
    boolean thresholdPassed = true;
    TextView tvCancel;
    TextView tvFeedback;
    TextView tvNegative;
    TextView tvPositive;
    TextView tvSubmit;
    TextView tvTitle;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView((int) R.layout.dialog_rating);

        this.tvTitle =  findViewById(R.id.dialog_rating_title);
        this.tvNegative =  findViewById(R.id.dialog_rating_button_negative);
        this.tvPositive =  findViewById(R.id.dialog_rating_button_positive);
        this.tvFeedback =  findViewById(R.id.dialog_rating_feedback_title);
        this.tvSubmit = findViewById(R.id.dialog_rating_button_feedback_submit);
        this.tvCancel = findViewById(R.id.dialog_rating_button_feedback_cancel);
        this.ratingBar =  findViewById(R.id.dialog_rating_rating_bar);
        this.ivIcon =  findViewById(R.id.dialog_rating_icon);
        this.etFeedback =  findViewById(R.id.dialog_rating_feedback);
        this.ratingButtons =  findViewById(R.id.dialog_rating_buttons);
        this.feedbackButtons =  findViewById(R.id.dialog_rating_feedback_buttons);
        init();
    }


    public void init() {
        Context context11;
        this.tvTitle.setText(this.builder.title);
        this.tvFeedback.setText(this.builder.formTitle);
        this.tvSubmit.setText(this.builder.submitText);
        this.tvCancel.setText(this.builder.cancelText);
        this.etFeedback.setHint(this.builder.feedbackFormHint);
        TypedValue typedValue = new TypedValue();
        this.context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int i = typedValue.data;
        TextView textView = this.tvTitle;
        int s = this.builder.titleTextColor;
        int i2 = R.color.black;
        textView.setTextColor(s != 0 ? ContextCompat.getColor(this.context, this.builder.titleTextColor) : ContextCompat.getColor(this.context, R.color.black));
        this.tvPositive.setTextColor(this.builder.positiveTextColor != 0 ? ContextCompat.getColor(this.context, this.builder.positiveTextColor) : i);
        this.tvNegative.setTextColor(this.builder.negativeTextColor != 0 ? ContextCompat.getColor(this.context, this.builder.negativeTextColor) : ContextCompat.getColor(this.context, R.color.grey_500));
        textView = this.tvFeedback;
        if (this.builder.titleTextColor != 0) {
            context11 = this.context;
            i2 = this.builder.titleTextColor;
        } else {
            context11 = this.context;
        }
        textView.setTextColor(ContextCompat.getColor(context11, i2));
        textView = this.tvSubmit;
        if (this.builder.positiveTextColor != 0) {
            i = ContextCompat.getColor(this.context, this.builder.positiveTextColor);
        }
        textView.setTextColor(i);
        this.tvCancel.setTextColor(this.builder.negativeTextColor != 0 ? ContextCompat.getColor(this.context, this.builder.negativeTextColor) : ContextCompat.getColor(this.context, R.color.grey_500));
        if (this.builder.feedBackTextColor != 0) {
            this.etFeedback.setTextColor(ContextCompat.getColor(this.context, this.builder.feedBackTextColor));
        }
        if (this.builder.positiveBackgroundColor != 0) {
            this.tvPositive.setBackgroundResource(this.builder.positiveBackgroundColor);
            this.tvSubmit.setBackgroundResource(this.builder.positiveBackgroundColor);
        }
        if (this.builder.negativeBackgroundColor != 0) {
            this.tvNegative.setBackgroundResource(this.builder.negativeBackgroundColor);
            this.tvCancel.setBackgroundResource(this.builder.negativeBackgroundColor);
        }
        if (this.builder.ratingBarColor != 0) {
            if (VERSION.SDK_INT > 19) {
                LayerDrawable layerDrawable = (LayerDrawable) this.ratingBar.getProgressDrawable();
                layerDrawable.getDrawable(2).setColorFilter(ContextCompat.getColor(this.context, this.builder.ratingBarColor), Mode.SRC_ATOP);
                layerDrawable.getDrawable(1).setColorFilter(ContextCompat.getColor(this.context, this.builder.ratingBarColor), Mode.SRC_ATOP);
                layerDrawable.getDrawable(0).setColorFilter(ContextCompat.getColor(this.context, this.builder.ratingBarBackgroundColor != 0 ? this.builder.ratingBarBackgroundColor : R.color.grey_200), Mode.SRC_ATOP);
            } else {
                DrawableCompat.setTint(this.ratingBar.getProgressDrawable(), ContextCompat.getColor(this.context, this.builder.ratingBarColor));
            }
        }
        Drawable applicationIcon = this.context.getPackageManager().getApplicationIcon(this.context.getApplicationInfo());
        ImageView imageView = this.ivIcon;
        if (this.builder.drawable != null) {
            applicationIcon = this.builder.drawable;
        }
        imageView.setImageDrawable(applicationIcon);
        this.ratingBar.setOnRatingBarChangeListener(this);
        this.tvPositive.setOnClickListener(this);
        this.tvNegative.setOnClickListener(this);
        this.tvSubmit.setOnClickListener(this);
        this.tvCancel.setOnClickListener(this);
    }

    public void openForm() {
        this.tvFeedback.setVisibility(View.VISIBLE);
        this.etFeedback.setVisibility(View.VISIBLE);
        this.feedbackButtons.setVisibility(View.VISIBLE);
        this.ratingButtons.setVisibility(View.GONE);
        this.ivIcon.setVisibility(View.GONE);
        this.tvTitle.setVisibility(View.GONE);
        this.ratingBar.setVisibility(View.GONE);
    }

    public void openPlaystore(Context context) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
            new SessionManager(context).setBooleanData(SessionManager.prefAppRated, Boolean.valueOf(true));
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(context, "Couldn't find PlayStore on this device", Toast.LENGTH_SHORT).show();
        }
    }

    public void setRatingThresholdClearedListener() {
        this.builder.ratingThresholdClearedListener = new Builder.RatingThresholdClearedListener() {
            public void onThresholdCleared(RatingDialog swaNVRatingDialog, float f, boolean z) {
                swaNVRatingDialog = RatingDialog.this;
                swaNVRatingDialog.openPlaystore(swaNVRatingDialog.context);
                RatingDialog.this.dismiss();
            }
        };
    }

    public void setRatingThresholdFailedListener() {
        this.builder.ratingThresholdFailedListener = new Builder.RatingThresholdFailedListener() {
            public void onThresholdFailed(RatingDialog swaNVRatingDialog, float f, boolean z) {
                RatingDialog.this.openForm();
            }
        };
    }

    public void showNever() {
        this.sharedpreferences = this.context.getSharedPreferences(this.myPrefs, 0);
        Editor edit = this.sharedpreferences.edit();
        edit.putBoolean(SHOW_NEVER, true);
        edit.commit();
    }

    RatingDialog(Context context, Builder builder) {
        super(context);
        this.context = context;
        this.builder = builder;
        this.session = builder.session;
        this.threshold = builder.threshold;
    }

    private boolean checkIfSessionMatches(int i) {
        if (i == 1) {
            return true;
        }
        this.sharedpreferences = this.context.getSharedPreferences(this.myPrefs, 0);
        if (this.sharedpreferences.getBoolean(SHOW_NEVER, false)) {
            return false;
        }
        SharedPreferences sharedPreferences = this.sharedpreferences;
        String str = SESSION_COUNT;
        int i2 = sharedPreferences.getInt(str, 1);
        Editor edit;
        if (i == i2) {
            edit = this.sharedpreferences.edit();
            edit.putInt(str, 1);
            edit.commit();
            return true;
        } else if (i > i2) {
            i2++;
            edit = this.sharedpreferences.edit();
            edit.putInt(str, i2);
            edit.commit();
            return false;
        } else {
            edit = this.sharedpreferences.edit();
            edit.putInt(str, 2);
            edit.commit();
            return false;
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.dialog_rating_button_negative) {
            dismiss();
        } else if (view.getId() == R.id.dialog_rating_button_positive) {
            openPlaystore(this.context);
            dismiss();
        } else if (view.getId() == R.id.dialog_rating_button_feedback_submit) {
            String trim = this.etFeedback.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                this.etFeedback.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.shake));
                return;
            }
            if (this.builder.ratingDialogFormListener != null) {
                this.builder.ratingDialogFormListener.onFormSubmitted(trim);
            }
            dismiss();
            showNever();
        } else if (view.getId() == R.id.dialog_rating_button_feedback_cancel) {
            dismiss();
        }
    }



    @Override
    public void onRatingChanged(RatingBar ratingBar, float f, boolean z) {
        if (ratingBar.getRating() >= this.threshold) {
            this.thresholdPassed = true;
            if (this.builder.ratingThresholdClearedListener == null) {
                setRatingThresholdClearedListener();
            }
            this.builder.ratingThresholdClearedListener.onThresholdCleared(this, ratingBar.getRating(), this.thresholdPassed);
        } else {
            this.thresholdPassed = false;
            if (this.builder.ratingThresholdFailedListener == null) {
                setRatingThresholdFailedListener();
            }
            this.builder.ratingThresholdFailedListener.onThresholdFailed(this, ratingBar.getRating(), this.thresholdPassed);
        }
        if (this.builder.ratingDialogListener != null) {
            this.builder.ratingDialogListener.onRatingSelected(ratingBar.getRating(), this.thresholdPassed);
        }
        showNever();
    }

    public void show() {
        if (checkIfSessionMatches(this.session)) {
            super.show();
        }
    }

    static class Builder {
        final Context context;
        String cancelText;
        Drawable drawable;
        int feedBackTextColor;
        String feedbackFormHint;
        String formTitle;
        int negativeBackgroundColor;
        String negativeText;
        int negativeTextColor;
        String playstoreUrl;
        int positiveBackgroundColor;
        String positiveText;
        int positiveTextColor;
        int ratingBarBackgroundColor;
        int ratingBarColor;
        RatingDialogFormListener ratingDialogFormListener;
        RatingDialogListener ratingDialogListener;
        RatingThresholdClearedListener ratingThresholdClearedListener;
        RatingThresholdFailedListener ratingThresholdFailedListener;
        int session = 1;
        String submitText;
        float threshold = 1.0f;
        String title;
        int titleTextColor;

        Builder(Context context) {
            this.context = context;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("market://details?id=");
            stringBuilder.append(context.getPackageName());
            this.playstoreUrl = stringBuilder.toString();
            initText();
        }

        public void initText() {
            this.title = this.context.getString(R.string.rating_dialog_experience);
            this.positiveText = this.context.getString(R.string.rating_dialog_maybe_later);
            this.negativeText = this.context.getString(R.string.rating_dialog_never);
            this.formTitle = this.context.getString(R.string.rating_dialog_feedback_title);
            this.submitText = this.context.getString(R.string.rating_dialog_submit);
            this.cancelText = this.context.getString(R.string.rating_dialog_cancel);
            this.feedbackFormHint = this.context.getString(R.string.rating_dialog_suggestions);
        }

        RatingDialog build() {
            return new RatingDialog(this.context, this);
        }



        Builder formCancelText(String str) {
            this.cancelText = str;
            return this;
        }

        Builder formHint(String str) {
            this.feedbackFormHint = str;
            return this;
        }

        Builder formSubmitText(String str) {
            this.submitText = str;
            return this;
        }

        Builder formTitle(String str) {
            this.formTitle = str;
            return this;
        }

        Builder icon(Drawable drawable) {
            this.drawable = drawable;
            return this;
        }

        Builder negativeButtonBackgroundColor(int i) {
            this.negativeBackgroundColor = i;
            return this;
        }

        Builder negativeButtonText(String str) {
            this.negativeText = str;
            return this;
        }

        Builder negativeButtonTextColor(int i) {
            this.negativeTextColor = i;
            return this;
        }

        Builder onRatingBarFormSumbit(RatingDialogFormListener ratingDialogFormListener) {
            this.ratingDialogFormListener = ratingDialogFormListener;
            return this;
        }

        Builder onRatingChanged(RatingDialogListener ratingDialogListener) {
            this.ratingDialogListener = ratingDialogListener;
            return this;
        }

        Builder onThresholdCleared(RatingThresholdClearedListener ratingThresholdClearedListener) {
            this.ratingThresholdClearedListener = ratingThresholdClearedListener;
            return this;
        }

        Builder onThresholdFailed(RatingThresholdFailedListener ratingThresholdFailedListener) {
            this.ratingThresholdFailedListener = ratingThresholdFailedListener;
            return this;
        }

        Builder playstoreUrl(String str) {
            this.playstoreUrl = str;
            return this;
        }

        Builder positiveButtonBackgroundColor(int i) {
            this.positiveBackgroundColor = i;
            return this;
        }

        Builder positiveButtonText(String str) {
            this.positiveText = str;
            return this;
        }

        Builder positiveButtonTextColor(int i) {
            this.positiveTextColor = i;
            return this;
        }

        Builder ratingBarBackgroundColor(int i) {
            this.ratingBarBackgroundColor = i;
            return this;
        }

        Builder ratingBarColor(int i) {
            this.ratingBarColor = i;
            return this;
        }

        Builder session(int i) {
            this.session = i;
            return this;
        }

        Builder threshold(float f) {
            this.threshold = f;
            return this;
        }

        Builder title(String str) {
            this.title = str;
            return this;
        }

        Builder titleTextColor(int i) {
            this.titleTextColor = i;
            return this;
        }

        interface RatingDialogFormListener {
            public void onFormSubmitted(String str);
        }

        interface RatingDialogListener {
            public void onRatingSelected(float f, boolean z);
        }

        interface RatingThresholdClearedListener {
            public void onThresholdCleared(RatingDialog swaNVRatingDialog, float f, boolean z);
        }

        interface RatingThresholdFailedListener {
            public void onThresholdFailed(RatingDialog swaNVRatingDialog, float f, boolean z);
        }
    }
}
