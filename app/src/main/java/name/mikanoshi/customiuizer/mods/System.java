package name.mikanoshi.customiuizer.mods;

import android.view.View;
import android.view.ViewGroup;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class System {
  public static void ExpandNotificationsHook(XC_LoadPackage.LoadPackageParam paramLoadPackageParam) { XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.StatusBar", paramLoadPackageParam.classLoader, "updateRowStates", new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param1MethodHookParam) throws Throwable {
              ViewGroup viewGroup = (ViewGroup)XposedHelpers.getObjectField(param1MethodHookParam.thisObject, "mStackScroller");
              for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
                View view = viewGroup.getChildAt(i);
                if (view != null && view.getClass().getSimpleName().equalsIgnoreCase("ExpandableNotificationRow"))
                  try {
                    XposedHelpers.getObjectField(XposedHelpers.callMethod(view, "getEntry"), "notification");
                    XposedHelpers.callMethod(view, "setSystemExpanded", true);
                  } finally {
                    view = null;
                  }
              }
            }
          }); }
}


/* Location:              C:\Users\Neuron\Documents\Tencent Files\[数据删除]\FileRecv\MobileFile\classes.jar!\name\mikanoshi\customiuizer\mods\System.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */