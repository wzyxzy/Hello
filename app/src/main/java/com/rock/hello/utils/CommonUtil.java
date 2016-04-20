package com.rock.hello.utils;

import android.text.format.DateFormat;

/**
 * Created by Rock on 16/4/20.
 */
public class CommonUtil {

    /**
     *  毫秒转化为mm:ss
     * @param millsTime
     * @return
     */
    public static CharSequence formatTime(int millsTime){
        return DateFormat.format("mm:ss",millsTime);
    }

}
