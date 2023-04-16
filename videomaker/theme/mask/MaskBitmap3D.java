package infiapp.com.videomaker.theme.mask;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;

import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.R;

import java.lang.reflect.Array;
import java.util.Random;

public class MaskBitmap3D {
    public static float animatedFrame = 0.0f;
    static int animatedFrameCal = 0;
    static int origanalFrame = 0;
    static int totalFrame = 0;
    private static int averageHeight;
    private static int averageWidth;
    private static float axisX;
    private static float axisY;
    private static Bitmap[][] bitmaps;
    private static Camera camera = new Camera();
   static int direction = 0;
    static float f18f;
    private static Matrix matrix = new Matrix();
    static final Paint paint = new Paint();
    private static int partNumber = 8;
    static int[][] randRect;
    static Random random = new Random();
    static EFFECT rollMode;
    private static float rotateDegree;

    public enum EFFECT {
        Roll2D_TB("Roll2D_TB") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawRollWhole3D(bottom, top, new Canvas(mask), true);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Roll2D_BT("Roll2D_BT") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.setRotateDegree(MaskBitmap3D.animatedFrameCal - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawRollWhole3D(top, bottom, new Canvas(mask), true);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Roll2D_LR("Roll2D_LR") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawRollWhole3D(bottom, top, new Canvas(mask), true);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 0;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Roll2D_RL("Roll2D_RL") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.setRotateDegree(MaskBitmap3D.animatedFrameCal - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawRollWhole3D(top, bottom, new Canvas(mask), true);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 0;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Whole3D_TB("Whole3D_TB") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                Canvas canvas = new Canvas(mask);
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.drawRollWhole3D(bottom, top, canvas, false);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.rollMode = this;
            }
        },
        Whole3D_BT("Whole3D_BT") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.setRotateDegree(MaskBitmap3D.animatedFrameCal - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                Canvas canvas = new Canvas(mask);
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.drawRollWhole3D(top, bottom, canvas, false);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Whole3D_LR("Whole3D_LR") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                Canvas canvas = new Canvas(mask);
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.drawRollWhole3D(bottom, top, canvas, false);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 0;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Whole3D_RL("Whole3D_RL") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.setRotateDegree(MaskBitmap3D.animatedFrameCal - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                Canvas canvas = new Canvas(mask);
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.drawRollWhole3D(top, bottom, canvas, false);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.direction = 0;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        SepartConbine_TB("SepartConbine_TB") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawSepartConbine(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 4;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.initBitmaps(bottom, top, this);
            }
        },
        SepartConbine_BT("SepartConbine_BT") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.setRotateDegree(MaskBitmap3D.animatedFrameCal - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawSepartConbine(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 4;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.initBitmaps(top, bottom, this);
            }
        },
        SepartConbine_LR("SepartConbine_LR") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawSepartConbine(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 4;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.direction = 0;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.initBitmaps(bottom, top, this);
            }
        },
        SepartConbine_RL("SepartConbine_RL") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.setRotateDegree(MaskBitmap3D.animatedFrameCal - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawSepartConbine(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 4;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.direction = 0;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.initBitmaps(top, bottom, this);
            }
        },
        RollInTurn_TB("RollInTurn_TB") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawRollInTurn(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.initBitmaps(bottom, top, this);
            }
        },
        RollInTurn_BT("RollInTurn_BT") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.setRotateDegree(MaskBitmap3D.animatedFrameCal - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawRollInTurn(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.initBitmaps(top, bottom, this);
            }
        },
        RollInTurn_LR("RollInTurn_LR") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawRollInTurn(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 0;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.initBitmaps(bottom, top, this);
            }
        },
        RollInTurn_RL("RollInTurn_RL") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.setRotateDegree(MaskBitmap3D.animatedFrameCal - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawRollInTurn(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 0;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.initBitmaps(top, bottom, this);
            }
        },
        Jalousie_BT("Jalousie_BT") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.setRotateDegree(MaskBitmap3D.animatedFrameCal - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawJalousie(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.initBitmaps(top, bottom, this);

            }
        },
        Jalousie_LR("Jalousie_LR") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.drawJalousie(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 0;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
                MaskBitmap3D.initBitmaps(bottom, top, this);
            }
        },
        Pixel_effect("Pixel_effect") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.pixDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 0;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Bar("Bar") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.barDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Cross_Merge("Cross_Merge") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.barDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        }, Rect_Zoom_In("Rect_Zoom_In") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.barDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Rect_Zoom_Out("Rect_Zoom_Out") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.barDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Cross_Shutter_1("Cross_Shutter_1") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.barDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Row_Split("Row_Split") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.rowDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Col_Split("Col_Split") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.rowDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        }, Erase_Slide("Erase_Slide") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.eraseDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Erase("Erase") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.eraseDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        }, Flip_Page_Right("Flip_Page_Right") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.flipDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        }, Crossfade("Crossfade") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.crossDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Dip_to_Rani("Dip_to_Rani") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.dipDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        filter_color("filter_color") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.colorDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Curved_down("Curved_down") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.curvedDraw(bitmap, bitmap2, new Canvas(createBitmap), i);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        Tilt_Drift("Tilt_Drift") {
            public Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                MaskBitmap3D.setRotateDegree(i);
                Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                MaskBitmap3D.tiltDraw(bitmap, bitmap2, new Canvas(createBitmap), false);
                return createBitmap;
            }

            public void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                MaskBitmap3D.partNumber = 8;
                MaskBitmap3D.direction = 1;
                MaskBitmap3D.rollMode = this;
                MaskBitmap3D.camera = new Camera();
                MaskBitmap3D.matrix = new Matrix();
            }
        },
        ;

        String name;

        public abstract Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i);

        public abstract void initBitmaps(Bitmap bitmap, Bitmap bitmap2);

        private EFFECT(String name) {
            this.name = "";
            this.name = name;
        }
    }

    static {
        animatedFrame = 0.0f;
        animatedFrameCal = 0;
        origanalFrame = 0;
        totalFrame = 0;
        totalFrame = 30;
        origanalFrame = 8;
        animatedFrame = 22.0f;
        animatedFrameCal = (int) (animatedFrame - 1.0f);
        paint.setColor(-16777216);
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL_AND_STROKE);
    }

    public static void reintRect() {
        randRect = (int[][]) Array.newInstance(Integer.TYPE, new int[]{(int) animatedFrame, (int) animatedFrame});
        for (int i = 0; i < randRect.length; i++) {
            for (int j = 0; j < randRect[i].length; j++) {
                randRect[i][j] = 0;
            }
        }
    }


    private static void barDraw(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
        canvas.save();
        camera.save();
        camera.getMatrix(matrix);
        camera.restore();
        paint.setAlpha(255);
        if (i == 0) {
            canvas.drawBitmap(bitmap, matrix, paint);
        } else if (i == 21) {
            canvas.drawBitmap(bitmap2, matrix, paint);
        } else if (rollMode == EFFECT.Rect_Zoom_Out) {
            canvas.drawBitmap(bitmap, matrix, paint);
            Bitmap r0 = Bitmap.createScaledBitmap(((BitmapDrawable) MyApplication.getInstance().getResources().getDrawable(R.drawable.hinh_1)).getBitmap(), (int) ((((double) MyApplication.VIDEO_WIDTH) * 0.0476d) * ((double) i)), (int) ((((double) MyApplication.VIDEO_HEIGHT) * 0.0476d) * ((double) i)), false);
            Bitmap r1 = Bitmap.createBitmap(r0.getWidth(), r0.getHeight(), Config.ARGB_8888);
            Canvas r2 = new Canvas(r1);
            Paint r3 = new Paint(1);
            r3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            r2.drawBitmap(bitmap2, (float) ((r0.getWidth() / 2) - (MyApplication.VIDEO_WIDTH / 2)), (float) ((r0.getHeight() / 2) - (MyApplication.VIDEO_HEIGHT / 2)), null);
            r2.drawBitmap(r0, 0.0f, 0.0f, r3);
            r3.setXfermode(null);
            canvas.drawBitmap(r1, (float) ((MyApplication.VIDEO_WIDTH / 2) - (r1.getWidth() / 2)), (float) ((MyApplication.VIDEO_HEIGHT / 2) - (r1.getHeight() / 2)), new Paint());
        } else if (rollMode == EFFECT.Rect_Zoom_In) {
            canvas.drawBitmap(bitmap, matrix, paint);
            Bitmap r0 = Bitmap.createScaledBitmap(((BitmapDrawable) MyApplication.getInstance().getResources().getDrawable(R.drawable.hinh_1)).getBitmap(), (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.0476d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.0476d * ((double) i)))), false);
            Bitmap r1 = Bitmap.createBitmap(bitmap2.getWidth(), bitmap2.getHeight(), Config.ARGB_8888);
            Canvas r2 = new Canvas(r1);
            Paint r3 = new Paint(1);
            r3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            r2.drawBitmap(bitmap2, 0.0f, 0.0f, null);
            r2.drawBitmap(r0, (float) ((MyApplication.VIDEO_WIDTH / 2) - (r0.getWidth() / 2)), (float) ((MyApplication.VIDEO_HEIGHT / 2) - (r0.getHeight() / 2)), r3);
            r3.setXfermode(null);
            canvas.drawBitmap(r1, (float) ((MyApplication.VIDEO_WIDTH / 2) - (r1.getWidth() / 2)), (float) ((MyApplication.VIDEO_HEIGHT / 2) - (r1.getHeight() / 2)), new Paint());
        } else if (rollMode == EFFECT.Cross_Merge) {
            canvas.drawBitmap(bitmap2, matrix, paint);
            Bitmap r0 = Bitmap.createScaledBitmap(((BitmapDrawable) MyApplication.getInstance().getResources().getDrawable(R.drawable.hinh_1)).getBitmap(), (int) (((double) MyApplication.VIDEO_WIDTH) * ((0.025d * ((double) i)) + 0.1d)), (int) (((double) MyApplication.VIDEO_HEIGHT) * ((0.025d * ((double) i)) + 0.1d)), false);
            Bitmap r1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas r2 = new Canvas(r1);
            Paint r3 = new Paint(1);
            r3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            r2.drawBitmap(bitmap, 0.0f, 0.0f, null);
            r2.drawBitmap(r0, 0.0f, 0.0f, r3);
            r2.drawBitmap(r0, (float) (MyApplication.VIDEO_WIDTH - r0.getWidth()), 0.0f, r3);
            r2.drawBitmap(r0, 0.0f, (float) (MyApplication.VIDEO_HEIGHT - r0.getHeight()), r3);
            r2.drawBitmap(r0, (float) (MyApplication.VIDEO_WIDTH - r0.getWidth()), (float) (MyApplication.VIDEO_HEIGHT - r0.getHeight()), r3);
            r3.setXfermode(null);
            canvas.drawBitmap(r1, (float) ((MyApplication.VIDEO_WIDTH / 2) - (r1.getWidth() / 2)), (float) ((MyApplication.VIDEO_HEIGHT / 2) - (r1.getHeight() / 2)), new Paint());
        } else if (rollMode == EFFECT.Cross_Shutter_1) {
            int i2;
            int i3;
            canvas.drawBitmap(bitmap2, matrix, paint);
            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) MyApplication.getInstance().getResources().getDrawable(R.drawable.hinh_5)).getBitmap(), 180 - ((int) (((double) i) * 8.72d)), 180 - ((int) (((double) i) * 8.72d)), false);
            Bitmap[][] bitmapArr = (Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{24, 16});
            for (i2 = 0; i2 < 24; i2++) {
                for (i3 = 0; i3 < 16; i3++) {
                    Bitmap createBitmap = Bitmap.createBitmap(createScaledBitmap.getWidth(), createScaledBitmap.getHeight(), Config.ARGB_8888);
                    Canvas canvas2 = new Canvas(createBitmap);
                    Paint paint = new Paint(1);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas2.drawBitmap(createScaledBitmap, 0.0f, 0.0f, null);
                    canvas2.drawBitmap(bitmap, (float) (createScaledBitmap.getWidth() - ((i2 + 1) * 180)), (float) (createScaledBitmap.getHeight() - ((i3 + 1) * 180)), paint);
                    bitmapArr[i2][i3] = createBitmap;
                    paint.setXfermode(null);
                }
            }
            for (i2 = 0; i2 < 12; i2++) {
                for (i3 = 0; i3 < 8; i3++) {
                    canvas.drawBitmap(bitmapArr[i2][i3], (float) (((i2 + 1) * 180) - bitmapArr[i2][i3].getWidth()), (float) (((i3 + 1) * 180) - bitmapArr[i2][i3].getHeight()), new Paint());
                }
            }
        } else if (rollMode == EFFECT.Bar) {
            canvas.drawBitmap(bitmap2, matrix, paint);
            Bitmap r0 = Bitmap.createScaledBitmap(((BitmapDrawable) MyApplication.getInstance().getResources().getDrawable(R.drawable.hinh_5)).getBitmap(), MyApplication.VIDEO_WIDTH * 2, MyApplication.VIDEO_HEIGHT, false);
            Bitmap r1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas r2 = new Canvas(r1);
            Paint r3 = new Paint(1);
            r3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            r2.drawBitmap(bitmap, 0.0f, 0.0f, null);
            r2.drawBitmap(r0, (float) ((i * 100) - r0.getWidth()), 0.0f, r3);
            r3.setXfermode(null);
            canvas.drawBitmap(r1, 0.0f, 0.0f, new Paint());
        }
//        else if (rollMode == EFFECT.TEST) {
//            canvas.drawBitmap(bitmap2, matrix, paint);
//            RectF rectF = new RectF(-90.0f, -210.0f, 810.0f, 690.0f);
//            Bitmap createBitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
//            Canvas canvas3 = new Canvas(createBitmap2);
//            Paint paint2 = new Paint(1);
//            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//            canvas3.drawBitmap(bitmap, 0.0f, 0.0f, null);
//            canvas3.drawArc(rectF, 270.0f, (float) (17.1d * ((double) i)), true, paint2);
//            paint2.setXfermode(null);
//            canvas.drawBitmap(createBitmap2, 0.0f, 0.0f, new Paint());
//        }
    }

    private static void rowDraw(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
        canvas.save();
        camera.save();
        camera.getMatrix(matrix);
        camera.restore();
        paint.setAlpha(255);
        if (i == 0) {
            canvas.drawBitmap(bitmap, matrix, paint);
        } else if (i == 21) {
            canvas.drawBitmap(bitmap2, matrix, paint);
        } else {
            canvas.drawBitmap(bitmap2, matrix, paint);
            if (rollMode == EFFECT.Col_Split) {
                canvas.drawBitmap(getPartBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * ((0.0238d * ((double) i)) + 0.5d)), 0, new Rect((int) (((double) MyApplication.VIDEO_WIDTH) * ((0.0238d * ((double) i)) + 0.5d)), 0, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT)), (float) ((int) (((double) MyApplication.VIDEO_WIDTH) * ((0.0238d * ((double) i)) + 0.5d))), 0.0f, paint);
                canvas.drawBitmap(getPartBitmap(bitmap, 0, 0, new Rect(0, 0, (int) (((double) MyApplication.VIDEO_WIDTH) * ((-0.0238d * ((double) i)) + 0.5d)), MyApplication.VIDEO_HEIGHT)), 0.0f, 0.0f, paint);
            } else if (rollMode == EFFECT.Row_Split) {
                Rect rect = new Rect(0, (int) (((double) MyApplication.VIDEO_HEIGHT) * ((0.0238d * ((double) i)) + 0.5d)), MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT);
                canvas.drawBitmap(getPartBitmap(bitmap, 0, (int) (((double) MyApplication.VIDEO_HEIGHT) * ((0.0238d * ((double) i)) + 0.5d)), rect), 0.0f, (float) ((int) (((double) MyApplication.VIDEO_HEIGHT) * ((0.0238d * ((double) i)) + 0.5d))), paint);
                canvas.drawBitmap(getPartBitmap(bitmap, 0, 0, new Rect(0, 0, MyApplication.VIDEO_WIDTH, (int) (((double) MyApplication.VIDEO_HEIGHT) * ((-0.0238d * ((double) i)) + 0.5d)))), 0.0f, 0.0f, paint);
            }
        }
    }

    private static void flipDraw(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
        canvas.save();
        camera.save();
        camera.getMatrix(matrix);
        camera.restore();
        paint.setAlpha(255);
        Drawable drawable = MyApplication.getInstance().getResources().getDrawable(R.drawable.left);
        Drawable drawable2 = MyApplication.getInstance().getResources().getDrawable(R.drawable.right);
        paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_0), PorterDuff.Mode.LIGHTEN));
        if (i == 0) {
            canvas.drawBitmap(bitmap, matrix, paint);
        } else if (i == 21) {
            canvas.drawBitmap(bitmap2, matrix, paint);
        } else if (i == 1) {
            canvas.drawBitmap(bitmap, matrix, paint);
            canvas.drawBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) drawable).getBitmap(), 30, MyApplication.VIDEO_WIDTH, false), 0.0f, 0.0f, paint);
        } else {
            Matrix matrix = new Matrix();
            matrix.postScale(-1.0f, 1.0f, (float) (MyApplication.VIDEO_HEIGHT / 2), 0.0f);
            Rect rect = new Rect(0, 0, (int) (((double) (MyApplication.VIDEO_HEIGHT * (i - 1))) * 0.05d), MyApplication.VIDEO_WIDTH);
            Rect rect2 = new Rect((int) (((double) (MyApplication.VIDEO_HEIGHT * (i - 1))) * 0.05d), 0, MyApplication.VIDEO_HEIGHT, MyApplication.VIDEO_WIDTH);
            Bitmap a = getPartBitmap(Bitmap.createBitmap(bitmap, 0, 0, MyApplication.VIDEO_HEIGHT, MyApplication.VIDEO_WIDTH, matrix, false), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (((double) (i - 1)) * 0.05d))), 0, rect);
            Bitmap a2 = getPartBitmap(bitmap, (int) (((double) (MyApplication.VIDEO_HEIGHT * (i - 1))) * 0.05d), 0, rect2);
            canvas.drawBitmap(bitmap2, matrix, paint);
            canvas.drawBitmap(a2, (float) ((int) (((double) (MyApplication.VIDEO_HEIGHT * (i - 1))) * 0.05d)), 0.0f, paint);
            canvas.drawBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) drawable2).getBitmap(), 30, MyApplication.VIDEO_WIDTH, false), (float) (((int) (((double) (MyApplication.VIDEO_HEIGHT * (i - 1))) * 0.05d)) - 30), 0.0f, paint);
            canvas.drawBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) drawable).getBitmap(), 30, MyApplication.VIDEO_WIDTH, false), (float) (((int) (((double) (MyApplication.VIDEO_HEIGHT * (i - 1))) * 0.05d)) * 2), 0.0f, paint);
            paint.setAlpha(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
            paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_8), PorterDuff.Mode.LIGHTEN));
            canvas.drawBitmap(a, (float) ((int) (((double) (MyApplication.VIDEO_HEIGHT * (i - 1))) * 0.05d)), 0.0f, paint);
            paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_0), PorterDuff.Mode.LIGHTEN));
        }
        canvas.restore();
    }

    private static void crossDraw(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
        canvas.save();
        camera.save();
        camera.getMatrix(matrix);
        camera.restore();
        if (i == 21) {
            paint.setAlpha(255);
            canvas.drawBitmap(bitmap2, matrix, paint);
        } else {
            paint.setAlpha(255 - ((int) (((float) (i + 1)) * 11.0f)));
            canvas.drawBitmap(bitmap, matrix, paint);
            paint.setAlpha(((int) (11.0f * ((float) (i + 1)))) - 1);
            canvas.drawBitmap(bitmap2, matrix, paint);
            paint.setAlpha(255);
        }
        canvas.restore();
    }

    private static void tiltDraw(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, boolean z) {
        canvas.save();
        if (direction == 1) {
            camera.save();
            if (z) {
                camera.rotateX(0.0f);
            } else {
                camera.rotateX(-rotateDegree);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) ((-MyApplication.VIDEO_WIDTH) / 2), 0.0f);
            matrix.postTranslate((float) (MyApplication.VIDEO_WIDTH / 2), axisY);
            if (axisY < ((float) MyApplication.VIDEO_HEIGHT)) {
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((float) MyApplication.VIDEO_WIDTH) * ((((float) MyApplication.VIDEO_HEIGHT) - axisY) / ((float) MyApplication.VIDEO_HEIGHT))), (int) (((float) MyApplication.VIDEO_HEIGHT) - axisY), false), matrix, paint);
            } else {
                canvas.drawBitmap(bitmap, matrix, paint);
            }
            camera.save();
            if (z) {
                camera.rotateX(0.0f);
            } else {
                camera.rotateX(90.0f - rotateDegree);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) ((-MyApplication.VIDEO_WIDTH) / 2), (float) (-MyApplication.VIDEO_HEIGHT));
            matrix.postTranslate((float) (MyApplication.VIDEO_WIDTH / 2), axisY);
            if (axisY > 0.0f) {
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((float) MyApplication.VIDEO_WIDTH) * (axisY / ((float) MyApplication.VIDEO_HEIGHT))), (int) axisY, false), matrix, paint);
            } else {
                canvas.drawBitmap(bitmap2, matrix, paint);
            }
        } else {
            camera.save();
            if (z) {
                camera.rotateY(0.0f);
            } else {
                camera.rotateY(rotateDegree);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(0.0f, (float) ((-MyApplication.VIDEO_HEIGHT) / 2));
            matrix.postTranslate(axisX, (float) (MyApplication.VIDEO_HEIGHT / 2));
            canvas.drawBitmap(bitmap, matrix, paint);
            camera.save();
            if (z) {
                camera.rotateY(0.0f);
            } else {
                camera.rotateY(rotateDegree - 90.0f);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) (-MyApplication.VIDEO_WIDTH), (float) ((-MyApplication.VIDEO_HEIGHT) / 2));
            matrix.postTranslate(axisX, (float) (MyApplication.VIDEO_HEIGHT / 2));
            canvas.drawBitmap(bitmap2, matrix, paint);
        }
        canvas.restore();
    }

    private static void curvedDraw(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
        canvas.save();
        camera.save();
        camera.getMatrix(matrix);
        camera.restore();
        switch (i) {
            case 0:
                paint.setAlpha(255);
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 1:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.04d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.04d * ((double) i)))), false), (float) (((((double) MyApplication.VIDEO_WIDTH) * 0.04d) * ((double) i)) / 2.0d), (float) ((((double) MyApplication.VIDEO_HEIGHT) * 0.04d) * ((double) i)), paint);
                break;
            case 2:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.04d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.04d * ((double) i)))), false), (float) (((((double) MyApplication.VIDEO_WIDTH) * 0.04d) * ((double) i)) / 2.0d), (float) ((((double) MyApplication.VIDEO_HEIGHT) * 0.04d) * ((double) i)), paint);
                break;
            case 3:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.04d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.04d * ((double) i)))), false), (float) (((((double) MyApplication.VIDEO_WIDTH) * 0.04d) * ((double) i)) / 2.0d), (float) ((((double) MyApplication.VIDEO_HEIGHT) * 0.04d) * ((double) i)), paint);
                break;
            case 4:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.04d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.04d * ((double) i)))), false), (float) (((((double) MyApplication.VIDEO_WIDTH) * 0.04d) * ((double) i)) / 2.0d), (float) ((((double) MyApplication.VIDEO_HEIGHT) * 0.04d) * ((double) i)), paint);
                break;
            case 5:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.04d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.04d * ((double) i)))), false), (float) (((((double) MyApplication.VIDEO_WIDTH) * 0.04d) * ((double) i)) / 2.0d), (float) ((((double) MyApplication.VIDEO_HEIGHT) * 0.04d) * ((double) i)), paint);
                break;
            case 6:
                paint.setAlpha(131);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) ((((double) MyApplication.VIDEO_WIDTH) * 0.04d) * ((double) i)), (int) ((((double) MyApplication.VIDEO_HEIGHT) * 0.04d) * ((double) i)), false), (float) ((((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.04d * ((double) i)))) / 2.0d), (float) ((int) (((double) MyApplication.VIDEO_HEIGHT) * 0.2d)), paint);
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.05d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.05d * ((double) i)))), false), (float) (((((double) MyApplication.VIDEO_WIDTH) * 0.05d) * ((double) i)) / 2.0d), (float) ((((double) MyApplication.VIDEO_HEIGHT) * 0.05d) * ((double) i)), paint);
                break;
            case 7:
                paint.setAlpha(162);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) ((((double) MyApplication.VIDEO_WIDTH) * 0.04d) * ((double) i)), (int) ((((double) MyApplication.VIDEO_HEIGHT) * 0.04d) * ((double) i)), false), (float) ((((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.04d * ((double) i)))) / 2.0d), (float) ((int) (((double) MyApplication.VIDEO_HEIGHT) * 0.16d)), paint);
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.05d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.05d * ((double) i)))), false), (float) (((((double) MyApplication.VIDEO_WIDTH) * 0.05d) * ((double) i)) / 2.0d), (float) ((((double) MyApplication.VIDEO_HEIGHT) * 0.05d) * ((double) i)), paint);
                break;
            case 8:
                paint.setAlpha(193);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) ((((double) MyApplication.VIDEO_WIDTH) * 0.04d) * ((double) i)), (int) ((((double) MyApplication.VIDEO_HEIGHT) * 0.04d) * ((double) i)), false), (float) ((((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.04d * ((double) i)))) / 2.0d), (float) ((int) (((double) MyApplication.VIDEO_HEIGHT) * 0.12d)), paint);
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.05d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.05d * ((double) i)))), false), (float) (((((double) MyApplication.VIDEO_WIDTH) * 0.05d) * ((double) i)) / 2.0d), (float) ((((double) MyApplication.VIDEO_HEIGHT) * 0.05d) * ((double) i)), paint);
                break;
            case 9:
                paint.setAlpha(224);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) ((((double) MyApplication.VIDEO_WIDTH) * 0.04d) * ((double) i)), (int) ((((double) MyApplication.VIDEO_HEIGHT) * 0.04d) * ((double) i)), false), (float) ((((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.04d * ((double) i)))) / 2.0d), (float) ((int) (((double) MyApplication.VIDEO_HEIGHT) * 0.08d)), paint);
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.05d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.05d * ((double) i)))), false), (float) (((((double) MyApplication.VIDEO_WIDTH) * 0.05d) * ((double) i)) / 2.0d), (float) ((((double) MyApplication.VIDEO_HEIGHT) * 0.05d) * ((double) i)), paint);
                break;
            case 10:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) ((((double) MyApplication.VIDEO_WIDTH) * 0.04d) * ((double) i)), (int) ((((double) MyApplication.VIDEO_HEIGHT) * 0.04d) * ((double) i)), false), (float) ((((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.04d * ((double) i)))) / 2.0d), (float) ((int) (((double) MyApplication.VIDEO_HEIGHT) * 0.04d)), paint);
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * (1.0d - (0.05d * ((double) i)))), (int) (((double) MyApplication.VIDEO_HEIGHT) * (1.0d - (0.05d * ((double) i)))), false), (float) (((((double) MyApplication.VIDEO_WIDTH) * 0.05d) * ((double) i)) / 2.0d), (float) ((((double) MyApplication.VIDEO_HEIGHT) * 0.05d) * ((double) i)), paint);
                break;
            case 11:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.45d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.45d), false), (float) (((double) MyApplication.VIDEO_WIDTH) * 0.275d), (float) (((double) MyApplication.VIDEO_HEIGHT) * 0.55d), paint);
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.45d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.45d), false), (float) (((double) MyApplication.VIDEO_WIDTH) * 0.275d), 0.0f, paint);
                break;
            case 12:
                paint.setAlpha(224);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.42d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.42d), false), (float) (((double) MyApplication.VIDEO_WIDTH) * 0.29d), (float) (((double) MyApplication.VIDEO_HEIGHT) * 0.48d), paint);
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.52d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.52d), false), (float) (((double) MyApplication.VIDEO_WIDTH) * 0.24d), 0.0f, paint);
                break;
            case 13:
                paint.setAlpha(193);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.4d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.4d), false), (float) (((double) MyApplication.VIDEO_WIDTH) * 0.3d), (float) (((double) MyApplication.VIDEO_HEIGHT) * 0.45d), paint);
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.59d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.59d), false), (float) (((double) MyApplication.VIDEO_WIDTH) * 0.205d), 0.0f, paint);
                break;
            case 14:
                paint.setAlpha(162);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.38d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.38d), false), (float) (((double) MyApplication.VIDEO_WIDTH) * 0.31d), (float) (((double) MyApplication.VIDEO_HEIGHT) * 0.42d), paint);
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.66d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.66d), false), (float) (((double) MyApplication.VIDEO_WIDTH) * 0.17d), 0.0f, paint);
                break;
            case 15:
                paint.setAlpha(131);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.35d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.35d), false), (float) (((double) MyApplication.VIDEO_WIDTH) * 0.325d), (float) (((double) MyApplication.VIDEO_HEIGHT) * 0.4d), paint);
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.73d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.73d), false), (float) (((double) MyApplication.VIDEO_WIDTH) * 0.135d), 0.0f, paint);
                break;
            case 16:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.8d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.8d), false), (float) ((((double) MyApplication.VIDEO_WIDTH) * 0.2d) / 2.0d), 0.0f, paint);
                break;
            case 17:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.84d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.84d), false), (float) ((((double) MyApplication.VIDEO_WIDTH) * 0.16d) / 2.0d), 0.0f, paint);
                break;
            case 18:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.88d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.88d), false), (float) ((((double) MyApplication.VIDEO_WIDTH) * 0.12d) / 2.0d), 0.0f, paint);
                break;
            case 19:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.92d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.92d), false), (float) ((((double) MyApplication.VIDEO_WIDTH) * 0.08d) / 2.0d), 0.0f, paint);
                break;
            case 20:
                paint.setAlpha(255);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) (((double) MyApplication.VIDEO_WIDTH) * 0.96d), (int) (((double) MyApplication.VIDEO_HEIGHT) * 0.96d), false), (float) ((((double) MyApplication.VIDEO_WIDTH) * 0.04d) / 2.0d), 0.0f, paint);
                break;
            case 21:
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
        }
        canvas.restore();
    }

    private static void colorDraw(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
        canvas.save();
        camera.save();
        camera.getMatrix(matrix);
        camera.restore();
        switch (i) {
            case 0:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_0), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 1:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_1), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 2:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_2), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 3:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_3), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 4:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_4), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 5:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_5), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 6:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_6), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 7:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_7), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 8:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_8), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 9:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_9), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 10:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_10), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 11:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_9), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 12:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_8), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 13:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_7), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 14:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_6), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 15:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_5), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 16:
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(MyApplication.getInstance(), R.color.filter_4), PorterDuff.Mode.LIGHTEN));
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 17:
                paint.setColorFilter(null);
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 18:
                paint.setColorFilter(null);
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 19:
                paint.setColorFilter(null);
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 20:
                paint.setColorFilter(null);
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 21:
                paint.setColorFilter(null);
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
        }
        canvas.restore();
    }

    private static void dipDraw(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
        canvas.save();
        camera.save();
        camera.getMatrix(matrix);
        camera.restore();
        switch (i) {
            case 0:
                paint.setAlpha(255);
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case 1:
                paint.setAlpha(230);
                canvas.drawBitmap(bitmap, matrix, paint);
                canvas.drawARGB(20, 30, 24, 60);
                break;
            case 2:
                paint.setAlpha(205);
                canvas.drawBitmap(bitmap, matrix, paint);
                canvas.drawARGB(40, 30, 24, 60);
                break;
            case 3:
                paint.setAlpha(180);
                canvas.drawBitmap(bitmap, matrix, paint);
                canvas.drawARGB(60, 30, 24, 60);
                break;
            case 4:
                paint.setAlpha(155);
                canvas.drawBitmap(bitmap, matrix, paint);
                canvas.drawARGB(80, 30, 24, 60);
                break;
            case 5:
                paint.setAlpha(130);
                canvas.drawBitmap(bitmap, matrix, paint);
                canvas.drawARGB(100, 30, 24, 60);
                break;
            case 6:
                paint.setAlpha(105);
                canvas.drawBitmap(bitmap, matrix, paint);
                canvas.drawARGB(120, 30, 24, 60);
                break;
            case 7:
                paint.setAlpha(55);
                canvas.drawBitmap(bitmap, matrix, paint);
                canvas.drawARGB(140, 30, 24, 60);
                break;
            case 8:
                paint.setAlpha(55);
                canvas.drawBitmap(bitmap, matrix, paint);
                canvas.drawARGB(160, 30, 24, 60);
                break;
            case 9:
                paint.setAlpha(30);
                canvas.drawBitmap(bitmap, matrix, paint);
                canvas.drawARGB(180, 30, 24, 60);
                break;
            case 10:
                canvas.drawARGB(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 30, 24, 60);
                break;
            case 11:
                paint.setAlpha(25);
                canvas.drawBitmap(bitmap2, matrix, paint);
                canvas.drawARGB(180, 30, 24, 60);
                break;
            case 12:
                paint.setAlpha(50);
                canvas.drawBitmap(bitmap2, matrix, paint);
                canvas.drawARGB(160, 30, 24, 60);
                break;
            case 13:
                paint.setAlpha(75);
                canvas.drawBitmap(bitmap2, matrix, paint);
                canvas.drawARGB(140, 30, 24, 60);
                break;
            case 14:
                paint.setAlpha(100);
                canvas.drawBitmap(bitmap2, matrix, paint);
                canvas.drawARGB(120, 30, 24, 60);
                break;
            case 15:
                paint.setAlpha(125);
                canvas.drawBitmap(bitmap2, matrix, paint);
                canvas.drawARGB(100, 30, 24, 60);
                break;
            case 16:
                paint.setAlpha(150);
                canvas.drawBitmap(bitmap2, matrix, paint);
                canvas.drawARGB(80, 30, 24, 60);
                break;
            case 17:
                paint.setAlpha(175);
                canvas.drawBitmap(bitmap2, matrix, paint);
                canvas.drawARGB(60, 30, 24, 60);
                break;
            case 18:
                paint.setAlpha(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 19:
                paint.setAlpha(220);
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 20:
                paint.setAlpha(240);
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
            case 21:
                paint.setAlpha(255);
                canvas.drawBitmap(bitmap2, matrix, paint);
                break;
        }
        canvas.restore();
    }

    private static void pixDraw(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
        canvas.save();
        camera.save();
        camera.getMatrix(matrix);
        camera.restore();
        paint.setAlpha(255);
        if (i == 0) {
            canvas.drawBitmap(bitmap, matrix, paint);
        } else if (i == 21) {
            canvas.drawBitmap(bitmap2, matrix, paint);
        } else if (i <= 0 || i > 10) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(Bitmap.createScaledBitmap(bitmap2, MyApplication.VIDEO_WIDTH / ((21 - i) * 2), MyApplication.VIDEO_WIDTH / ((21 - i) * 2), false), MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_WIDTH, false), matrix, paint);
        } else {
            canvas.drawBitmap(Bitmap.createScaledBitmap(Bitmap.createScaledBitmap(bitmap, MyApplication.VIDEO_WIDTH / (i * 2), MyApplication.VIDEO_WIDTH / (i * 2), false), MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_WIDTH, false), matrix, paint);
        }
        canvas.restore();
    }

    private static void eraseDraw(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
        canvas.save();
        camera.save();
        camera.getMatrix(matrix);
        camera.restore();
        paint.setAlpha(255);
        if (i == 0) {
            canvas.drawBitmap(bitmap, matrix, paint);
        } else if (i == 21) {
            canvas.drawBitmap(bitmap2, matrix, paint);
        } else {
            canvas.drawBitmap(bitmap2, matrix, paint);
            if (((int) ((((double) MyApplication.VIDEO_WIDTH) * 0.05d) * ((double) i))) < MyApplication.VIDEO_WIDTH) {
                canvas.drawBitmap(getPartBitmap(bitmap, (int) ((((double) MyApplication.VIDEO_WIDTH) * 0.05d) * ((double) i)), 0, new Rect((int) ((((double) MyApplication.VIDEO_WIDTH) * 0.05d) * ((double) i)), 0, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT)), (float) ((int) ((((double) MyApplication.VIDEO_WIDTH) * 0.05d) * ((double) i))), 0.0f, paint);
            }
            if (rollMode == EFFECT.Erase) {
                Bitmap a;
                if (((int) (((double) (MyApplication.VIDEO_WIDTH * i)) * 0.05d)) - (MyApplication.VIDEO_WIDTH / 8) < 0) {
                    a = getPartBitmap(bitmap, 0, 0, new Rect(0, 0, (int) (((double) (MyApplication.VIDEO_WIDTH * i)) * 0.05d), MyApplication.VIDEO_HEIGHT));
                    paint.setShader(new ComposeShader(new LinearGradient(0.0f, 0.0f, (float) a.getWidth(), 0.0f, ViewCompat.MEASURED_SIZE_MASK, -1, Shader.TileMode.CLAMP), new BitmapShader(a, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_IN));
                    canvas.drawRect(0.0f, 0.0f, (float) a.getWidth(), (float) a.getHeight(), paint);
                } else {
                    a = getPartBitmap(bitmap, ((int) (((double) (MyApplication.VIDEO_WIDTH * i)) * 0.05d)) - (MyApplication.VIDEO_WIDTH / 8), 0, new Rect(((int) (((double) (MyApplication.VIDEO_WIDTH * i)) * 0.05d)) - (MyApplication.VIDEO_WIDTH / 8), 0, (int) (((double) (MyApplication.VIDEO_WIDTH * i)) * 0.05d), MyApplication.VIDEO_HEIGHT));
                    paint.setShader(new ComposeShader(new LinearGradient(0.0f, 0.0f, (float) (MyApplication.VIDEO_WIDTH / 8), 0.0f, ViewCompat.MEASURED_SIZE_MASK, -1, Shader.TileMode.CLAMP), new BitmapShader(a, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_IN));
                    Bitmap createBitmap = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH / 8, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                    new Canvas(createBitmap).drawRect(0.0f, 0.0f, (float) a.getWidth(), (float) a.getHeight(), paint);
                    paint.setShader(null);
                    canvas.drawBitmap(createBitmap, (float) (((int) (((double) (MyApplication.VIDEO_WIDTH * i)) * 0.05d)) - (MyApplication.VIDEO_WIDTH / 8)), 0.0f, paint);
                }
            }
        }
        canvas.restore();
    }

    public static void setRotateDegree(int factor) {
        int i = 90;
        if (rollMode == EFFECT.RollInTurn_BT || rollMode == EFFECT.RollInTurn_LR || rollMode == EFFECT.RollInTurn_RL || rollMode == EFFECT.RollInTurn_TB) {
            rotateDegree = (((((float) (partNumber - 1)) * 30.0f) + 90.0f) * ((float) factor)) / ((float) animatedFrameCal);
        } else if (rollMode == EFFECT.Jalousie_BT || rollMode == EFFECT.Jalousie_LR) {
            rotateDegree = (180.0f * ((float) factor)) / ((float) animatedFrameCal);
        } else {
            rotateDegree = (((float) factor) * 90.0f) / ((float) animatedFrameCal);
        }
        if (direction == 1) {
            float f = rotateDegree;
            if (rollMode == EFFECT.Jalousie_BT || rollMode == EFFECT.Jalousie_LR) {
                i = 180;
            }
            axisY = (f / ((float) i)) * ((float) MyApplication.VIDEO_HEIGHT);
            return;
        }
        f18f = rotateDegree;
        if (rollMode == EFFECT.Jalousie_BT || rollMode == EFFECT.Jalousie_LR) {
            i = 180;
        }
        axisX = (f18f / ((float) i)) * ((float) MyApplication.VIDEO_WIDTH);
    }

    public static void initBitmaps(Bitmap bottom, Bitmap top, EFFECT effect) {
        rollMode = effect;
        if (MyApplication.VIDEO_HEIGHT > 0 || MyApplication.VIDEO_WIDTH > 0) {
            bitmaps = (Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{2, partNumber});
            averageWidth = MyApplication.VIDEO_WIDTH / partNumber;
            averageHeight = MyApplication.VIDEO_HEIGHT / partNumber;
            int i = 0;
            while (i < 2) {
                for (int j = 0; j < partNumber; j++) {
                    Bitmap partBitmap;
                    Rect rect;
                    Bitmap bitmap;
                    if (rollMode == EFFECT.Jalousie_BT || rollMode == EFFECT.Jalousie_LR) {
                        if (direction == 1) {
                            rect = new Rect(0, averageHeight * j, MyApplication.VIDEO_WIDTH, (j + 1) * averageHeight);
                            if (i == 0) {
                                bitmap = bottom;
                            } else {
                                bitmap = top;
                            }
                            partBitmap = getPartBitmap(bitmap, 0, averageHeight * j, rect);
                        } else {
                            rect = new Rect(averageWidth * j, 0, (j + 1) * averageWidth, MyApplication.VIDEO_HEIGHT);
                            if (i == 0) {
                                bitmap = bottom;
                            } else {
                                bitmap = top;
                            }
                            partBitmap = getPartBitmap(bitmap, averageWidth * j, 0, rect);
                        }
                    } else if (direction == 1) {
                        rect = new Rect(averageWidth * j, 0, (j + 1) * averageWidth, MyApplication.VIDEO_HEIGHT);
                        if (i == 0) {
                            bitmap = bottom;
                        } else {
                            bitmap = top;
                        }
                        partBitmap = getPartBitmap(bitmap, averageWidth * j, 0, rect);
                    } else {
                        partBitmap = getPartBitmap(i == 0 ? bottom : top, 0, averageHeight * j, new Rect(0, averageHeight * j, MyApplication.VIDEO_WIDTH, (j + 1) * averageHeight));
                    }
                    bitmaps[i][j] = partBitmap;
                }
                i++;
            }
        }
    }

    private static Bitmap getPartBitmap(Bitmap bitmap, int x, int y, Rect rect) {
        return Bitmap.createBitmap(bitmap, x, y, rect.width(), rect.height());
    }

    private static void drawRollWhole3D(Bitmap bottom, Bitmap top, Canvas canvas, boolean draw2D) {
        Bitmap currWholeBitmap = bottom;
        Bitmap nextWholeBitmap = top;
        canvas.save();
        if (direction == 1) {
            camera.save();
            if (draw2D) {
                camera.rotateX(0.0f);
            } else {
                camera.rotateX(-rotateDegree);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) ((-MyApplication.VIDEO_WIDTH) / 2), 0.0f);
            matrix.postTranslate((float) (MyApplication.VIDEO_WIDTH / 2), axisY);
            canvas.drawBitmap(currWholeBitmap, matrix, paint);
            camera.save();
            if (draw2D) {
                camera.rotateX(0.0f);
            } else {
                camera.rotateX(90.0f - rotateDegree);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) ((-MyApplication.VIDEO_WIDTH) / 2), (float) (-MyApplication.VIDEO_HEIGHT));
            matrix.postTranslate((float) (MyApplication.VIDEO_WIDTH / 2), axisY);
            canvas.drawBitmap(nextWholeBitmap, matrix, paint);
        } else {
            camera.save();
            if (draw2D) {
                camera.rotateY(0.0f);
            } else {
                camera.rotateY(rotateDegree);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(0.0f, (float) ((-MyApplication.VIDEO_HEIGHT) / 2));
            matrix.postTranslate(axisX, (float) (MyApplication.VIDEO_HEIGHT / 2));
            canvas.drawBitmap(currWholeBitmap, matrix, paint);
            camera.save();
            if (draw2D) {
                camera.rotateY(0.0f);
            } else {
                camera.rotateY(rotateDegree - 90.0f);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) (-MyApplication.VIDEO_WIDTH), (float) ((-MyApplication.VIDEO_HEIGHT) / 2));
            matrix.postTranslate(axisX, (float) (MyApplication.VIDEO_HEIGHT / 2));
            canvas.drawBitmap(nextWholeBitmap, matrix, paint);
        }
        canvas.restore();
    }

    private static void drawSepartConbine(Canvas canvas) {
        for (int i = 0; i < partNumber; i++) {
            Bitmap currBitmap = bitmaps[0][i];
            Bitmap nextBitmap = bitmaps[1][i];
            canvas.save();
            if (direction == 1) {
                camera.save();
                camera.rotateX(-rotateDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) ((-currBitmap.getWidth()) / 2), 0.0f);
                matrix.postTranslate((float) ((currBitmap.getWidth() / 2) + (averageWidth * i)), axisY);
                canvas.drawBitmap(currBitmap, matrix, paint);
                camera.save();
                camera.rotateX(90.0f - rotateDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) ((-nextBitmap.getWidth()) / 2), (float) (-nextBitmap.getHeight()));
                matrix.postTranslate((float) ((nextBitmap.getWidth() / 2) + (averageWidth * i)), axisY);
                canvas.drawBitmap(nextBitmap, matrix, paint);
            } else {
                camera.save();
                camera.rotateY(rotateDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate(0.0f, (float) ((-currBitmap.getHeight()) / 2));
                matrix.postTranslate(axisX, (float) ((currBitmap.getHeight() / 2) + (averageHeight * i)));
                canvas.drawBitmap(currBitmap, matrix, paint);
                camera.save();
                camera.rotateY(rotateDegree - 90.0f);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-nextBitmap.getWidth()), (float) ((-nextBitmap.getHeight()) / 2));
                matrix.postTranslate(axisX, (float) ((nextBitmap.getHeight() / 2) + (averageHeight * i)));
                canvas.drawBitmap(nextBitmap, matrix, paint);
            }
            canvas.restore();
        }
    }

    private static void drawRollInTurn(Canvas canvas) {
        for (int i = 0; i < partNumber; i++) {
            Bitmap currBitmap = bitmaps[0][i];
            Bitmap nextBitmap = bitmaps[1][i];
            float tDegree = rotateDegree - ((float) (i * 30));
            if (tDegree < 0.0f) {
                tDegree = 0.0f;
            }
            if (tDegree > 90.0f) {
                tDegree = 90.0f;
            }
            canvas.save();
            if (direction == 1) {
                float tAxisY = (tDegree / 90.0f) * ((float) MyApplication.VIDEO_HEIGHT);
                if (tAxisY > ((float) MyApplication.VIDEO_HEIGHT)) {
                    tAxisY = (float) MyApplication.VIDEO_HEIGHT;
                }
                if (tAxisY < 0.0f) {
                    tAxisY = 0.0f;
                }
                camera.save();
                camera.rotateX(-tDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-currBitmap.getWidth()), 0.0f);
                matrix.postTranslate((float) (currBitmap.getWidth() + (averageWidth * i)), tAxisY);
                canvas.drawBitmap(currBitmap, matrix, paint);
                camera.save();
                camera.rotateX(90.0f - tDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-nextBitmap.getWidth()), (float) (-nextBitmap.getHeight()));
                matrix.postTranslate((float) (nextBitmap.getWidth() + (averageWidth * i)), tAxisY);
                canvas.drawBitmap(nextBitmap, matrix, paint);
            } else {
                float tAxisX = (tDegree / 90.0f) * ((float) MyApplication.VIDEO_WIDTH);
                if (tAxisX > ((float) MyApplication.VIDEO_WIDTH)) {
                    tAxisX = (float) MyApplication.VIDEO_WIDTH;
                }
                if (tAxisX < 0.0f) {
                    tAxisX = 0.0f;
                }
                camera.save();
                camera.rotateY(tDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate(0.0f, (float) ((-currBitmap.getHeight()) / 2));
                matrix.postTranslate(tAxisX, (float) ((currBitmap.getHeight() / 2) + (averageHeight * i)));
                canvas.drawBitmap(currBitmap, matrix, paint);
                camera.save();
                camera.rotateY(tDegree - 90.0f);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-nextBitmap.getWidth()), (float) ((-nextBitmap.getHeight()) / 2));
                matrix.postTranslate(tAxisX, (float) ((nextBitmap.getHeight() / 2) + (averageHeight * i)));
                canvas.drawBitmap(nextBitmap, matrix, paint);
            }
            canvas.restore();
        }
    }

    private static void drawJalousie(Canvas canvas) {
        for (int i = 0; i < partNumber; i++) {
            Bitmap currBitmap = bitmaps[0][i];
            Bitmap nextBitmap = bitmaps[1][i];
            canvas.save();
            if (direction == 1) {
                if (rotateDegree < 90.0f) {
                    camera.save();
                    camera.rotateX(rotateDegree);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((float) ((-currBitmap.getWidth()) / 2), (float) ((-currBitmap.getHeight()) / 2));
                    matrix.postTranslate((float) (currBitmap.getWidth() / 2), (float) ((currBitmap.getHeight() / 2) + (averageHeight * i)));
                    canvas.drawBitmap(currBitmap, matrix, paint);
                } else {
                    camera.save();
                    camera.rotateX(180.0f - rotateDegree);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((float) ((-nextBitmap.getWidth()) / 2), (float) ((-nextBitmap.getHeight()) / 2));
                    matrix.postTranslate((float) (nextBitmap.getWidth() / 2), (float) ((nextBitmap.getHeight() / 2) + (averageHeight * i)));
                    canvas.drawBitmap(nextBitmap, matrix, paint);
                }
            } else if (rotateDegree < 90.0f) {
                camera.save();
                camera.rotateY(rotateDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) ((-currBitmap.getWidth()) / 2), (float) ((-currBitmap.getHeight()) / 2));
                matrix.postTranslate((float) ((currBitmap.getWidth() / 2) + (averageWidth * i)), (float) (currBitmap.getHeight() / 2));
                canvas.drawBitmap(currBitmap, matrix, paint);
            } else {
                camera.save();
                camera.rotateY(180.0f - rotateDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) ((-nextBitmap.getWidth()) / 2), (float) ((-nextBitmap.getHeight()) / 2));
                matrix.postTranslate((float) ((nextBitmap.getWidth() / 2) + (averageWidth * i)), (float) (nextBitmap.getHeight() / 2));
                canvas.drawBitmap(nextBitmap, matrix, paint);
            }
            canvas.restore();
        }
    }
}
