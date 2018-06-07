package tk.ubublik.spotifydownloader.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class Appearance {

    public static void setFullscreenFlags(Activity activity){
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void setLayoutUnderStatusBar(Activity activity, @IdRes int layout){
        View view = activity.findViewById(layout);
        if (!(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) throw new IllegalArgumentException("Layout doesn't support margins");
        ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).topMargin = getStatusBarHeight(activity);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
