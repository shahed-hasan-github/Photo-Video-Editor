package infiapp.com.videomaker.theme.mask;

import infiapp.com.videomaker.R;

import java.util.ArrayList;

import infiapp.com.videomaker.theme.mask.MaskBitmap3D.EFFECT;

public enum AllTheme {
    Mixer("Mixer") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_20;
        }

        public int getThemeMusic() {
            return R.raw.song_1;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Pixel_effect);
            arrayList.add(EFFECT.Erase_Slide);
            arrayList.add(EFFECT.Erase);
            arrayList.add(EFFECT.Crossfade);
            arrayList.add(EFFECT.Dip_to_Rani);
            arrayList.add(EFFECT.filter_color);
            arrayList.add(EFFECT.Rect_Zoom_Out);
            arrayList.add(EFFECT.Row_Split);
            arrayList.add(EFFECT.Col_Split);
            arrayList.add(EFFECT.Cross_Merge);
            arrayList.add(EFFECT.Cross_Shutter_1);
            arrayList.add(EFFECT.Flip_Page_Right);
            arrayList.add(EFFECT.Curved_down);
            arrayList.add(EFFECT.Tilt_Drift);
            return arrayList;
        }
    }, Crossfade("Crossfade") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_30;
        }

        public int getThemeMusic() {
            return R.raw.song_1;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Crossfade);
            arrayList.add(EFFECT.filter_color);
            arrayList.add(EFFECT.Dip_to_Rani);
            return arrayList;
        }
    }, Erase_Slide("Erase_Slide") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_28;
        }

        public int getThemeMusic() {
            return R.raw.song_2;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Erase_Slide);
            arrayList.add(EFFECT.Erase);
            return arrayList;
        }
    },Curved_down("Curved_down") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_05;
        }

        public int getThemeMusic() {
            return R.raw.song_2;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Curved_down);
            arrayList.add(EFFECT.Tilt_Drift);
            return arrayList;
        }
    }, Shine("Shine") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_01;
        }

        public int getThemeMusic() {
            return R.raw.song_3;
        }

        public ArrayList<MaskBitmap3D.EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Whole3D_BT);
            arrayList.add(EFFECT.Whole3D_TB);
            arrayList.add(EFFECT.Whole3D_LR);
            arrayList.add(EFFECT.Whole3D_RL);
            arrayList.add(EFFECT.SepartConbine_BT);
            arrayList.add(EFFECT.SepartConbine_TB);
            arrayList.add(EFFECT.SepartConbine_LR);
            arrayList.add(EFFECT.SepartConbine_RL);
            arrayList.add(EFFECT.RollInTurn_BT);
            arrayList.add(EFFECT.RollInTurn_TB);
            arrayList.add(EFFECT.RollInTurn_LR);
            arrayList.add(EFFECT.RollInTurn_RL);
            arrayList.add(EFFECT.Jalousie_BT);
            arrayList.add(EFFECT.Jalousie_LR);
            arrayList.add(EFFECT.Roll2D_BT);
            arrayList.add(EFFECT.Roll2D_TB);
            arrayList.add(EFFECT.Roll2D_LR);
            arrayList.add(EFFECT.Roll2D_RL);
            return arrayList;
        }
    },
//    Bar("Bar") {
//        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
//            return null;
//        }
//
//        public int getThemeDrawable() {
//            return R.drawable.t_22;
//        }
//
//        public int getThemeMusic() {
//            return R.raw.song_2;
//        }
//
//        public ArrayList<EFFECT> getTheme() {
//            ArrayList<EFFECT> arrayList = new ArrayList();
//            arrayList.add(EFFECT.Bar);
//            return arrayList;
//        }
//    },

    Cross_Merge("Cross_Merge") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_22;
        }

        public int getThemeMusic() {
            return R.raw.song_3;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Cross_Merge);
            return arrayList;
        }
    },
    Pixel_effect("Pixel_effect") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_20;
        }

        public int getThemeMusic() {
            return R.raw.song_1;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Pixel_effect);
            return arrayList;
        }
    }

    , Cross_Shutter_1("Cross_Shutter_1") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_27;
        }

        public int getThemeMusic() {
            return R.raw.song_3;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Cross_Shutter_1);
            return arrayList;
        }
    }

    , Row_Split("Row_Split") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_26;
        }

        public int getThemeMusic() {
            return R.raw.song_1;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Row_Split);
            arrayList.add(EFFECT.Col_Split);
            return arrayList;
        }
    }


    , Flip_Page_Right("Flip_Page_Right") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_29;
        }

        public int getThemeMusic() {
            return R.raw.song_3;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Flip_Page_Right);
            return arrayList;
        }
    },
    Jalousie_Down_Up("Jalousie Down Up") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_02;
        }

        public int getThemeMusic() {
            return R.raw.song_2;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Jalousie_BT);
            arrayList.add(EFFECT.Jalousie_LR);
            return arrayList;
        }
    },

    Whole3D_Down_Up("Whole3D Down Up") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_04;
        }

        public int getThemeMusic() {
            return R.raw.song_3;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Whole3D_BT);
            arrayList.add(EFFECT.Whole3D_TB);
            arrayList.add(EFFECT.Whole3D_LR);
            arrayList.add(EFFECT.Whole3D_RL);
            return arrayList;
        }
    },

    SepartConbine_Down_Up("SepartConbine Down Up") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_08;
        }

        public int getThemeMusic() {
            return R.raw.song_1;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.SepartConbine_BT);
            arrayList.add(EFFECT.SepartConbine_TB);
            arrayList.add(EFFECT.SepartConbine_LR);
            arrayList.add(EFFECT.SepartConbine_RL);
            return arrayList;
        }
    },

    RollInTurn_Down_Up("RollInTurn Down Up") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_12;
        }

        public int getThemeMusic() {
            return R.raw.song_3;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.RollInTurn_BT);
            arrayList.add(EFFECT.RollInTurn_TB);
            arrayList.add(EFFECT.RollInTurn_LR);
            arrayList.add(EFFECT.RollInTurn_RL);
            return arrayList;
        }
    },

    Roll2D_Down_Up("Roll2D Down Up") {
        public ArrayList<EFFECT> getTheme(ArrayList<EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.t_16;
        }

        public int getThemeMusic() {
            return R.raw.song_2;
        }

        public ArrayList<EFFECT> getTheme() {
            ArrayList<EFFECT> arrayList = new ArrayList();
            arrayList.add(EFFECT.Roll2D_BT);
            arrayList.add(EFFECT.Roll2D_TB);
            arrayList.add(EFFECT.Roll2D_LR);
            arrayList.add(EFFECT.Roll2D_RL);
            return arrayList;
        }
    },;


    String name;

    public abstract ArrayList<EFFECT> getTheme();

    public abstract int getThemeDrawable();

    public abstract int getThemeMusic();

    AllTheme(String str) {
        this.name = "";
        this.name = str;
    }
}
