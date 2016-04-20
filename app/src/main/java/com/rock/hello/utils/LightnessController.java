package com.rock.hello.utils;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by Rock on 16/4/20.
 */
public class LightnessController {

    private static final String TAG = LightnessController.class.getSimpleName();

    /**
     * 开启亮度自动控制
     * @param act
     */
    public static void startAutoLigntness(Activity act){
        Settings.System.putInt(act.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 关闭亮度自动控制
     * @param act
     */
    public static void stopAutoLightness(Activity act){
        Settings.System.putInt(act.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 获取屏幕亮度值
     * @param act
     * @return
     */
    public static int getLightness(Activity act){
        return Settings.System.getInt(act.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,-1);
    }

    /**
     * 设置屏幕亮度值
     * @param act
     * @param light
     */
    public static void setLightness(Activity act,int light){
        try {
            Settings.System.putInt(act.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,light);
            WindowManager.LayoutParams lp = act.getWindow().getAttributes();
            lp.screenBrightness = light/ 255f;
            act.getWindow().setAttributes(lp);
        }catch (Exception e){
            Toast.makeText(act,"无法改变亮度",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 是否开启自动亮度控制
     * @param act
     * @return
     */
    public static boolean isAutoLightness(Activity act){
        boolean isAuto = false;
        try {
            isAuto = Settings.System.getInt(act.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return isAuto;
    }

    /**
     * 亮度增加
     * @param act
     * @param screenHeight
     * @param deltaY
     */
    public static void lightnessUp(Activity act,int screenHeight,float deltaY){
        float change = deltaY / screenHeight * 255 * 2;
        float destination = getLightness(act) - change;
        float min = Math.min(destination, 255);
        Log.e(TAG,"lightnessUp--" + min);
        setLightness(act, (int) min);
    }

    /**
     * 亮度降低
     * @param act
     * @param screenHeight
     * @param deltaY
     */
    public static void lightnessDown(Activity act,int screenHeight,float deltaY){
        float change = deltaY / screenHeight * 255 * 2;
        float destination = getLightness(act) - change;
        float max = Math.max(destination, 0);
        Log.e(TAG,"lightnessDown--" + max);
        setLightness(act, (int) max);
    }

}
