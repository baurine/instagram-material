package com.baurine.instamaterial.utils;

import android.util.Log;

public class DebugHelper {

    // 没有实际作用的函数，只用于帮助调试，
    // 需要观察某些局部变量的值但后面又没有可下断点的代码处，放置这个函数。
    public static void probe() {
        Log.e("probe", "probe");
    }
}
