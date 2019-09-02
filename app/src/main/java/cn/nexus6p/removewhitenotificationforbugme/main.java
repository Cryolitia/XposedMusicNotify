package cn.nexus6p.removewhitenotificationforbugme;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class main {

    private static ArrayList<String> nameStrings = new ArrayList<>();
    private final static String minHeight = "notification_min_height_legacy";
    private final static int MINHEIGHT = 133;
    static {
        nameStrings.add("notification_background_custom_padding_left");
        nameStrings.add("notification_background_custom_padding_top");
        nameStrings.add("notification_background_custom_padding_right");
        nameStrings.add("notification_background_custom_padding_bottom");
        nameStrings.add("notification_custom_view_margin_end");
        nameStrings.add("notification_custom_view_margin_start");
        nameStrings.add("notification_custom_view_corner_radius");
        nameStrings.add("notification_row_extra_padding");
    }

    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        /*if (!resparam.packageName.equals("com.android.systemui")) return;
        final int leftID = resparam.res.getIdentifier("notification_background_custom_padding_left","dimen","com.android.systemui");
        final int topID = resparam.res.getIdentifier("notification_background_custom_padding_top","dimen","com.android.systemui");
        final int rightID = resparam.res.getIdentifier("notification_background_custom_padding_right","dimen","com.android.systemui");
        final int bottomID = resparam.res.getIdentifier("notification_background_custom_padding_bottom","dimen","com.android.systemui");
        resparam.res.setReplacement(leftID,0);
        resparam.res.setReplacement(topID,0);
        resparam.res.setReplacement(rightID,0);
        resparam.res.setReplacement(bottomID,0);*/
        for (String nameString : nameStrings) {
            try {
                final int ID = resparam.res.getIdentifier(nameString, "dimen", "com.android.systemui");
                resparam.res.setReplacement(ID, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.systemui")) return;
        final ClassLoader classLoader = lpparam.classLoader;
        XposedHelpers.findAndHookMethod(Application.class.getName(), classLoader, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context context = (Context) param.args[0];
                /*final int leftID = context.getResources().getIdentifier("notification_background_custom_padding_left","dimen","com.android.systemui");
                final int topID = context.getResources().getIdentifier("notification_background_custom_padding_top","dimen","com.android.systemui");
                final int rightID = context.getResources().getIdentifier("notification_background_custom_padding_right","dimen","com.android.systemui");
                final int bottomID = context.getResources().getIdentifier("notification_background_custom_padding_bottom","dimen","com.android.systemui");*/
                final ArrayList<Integer> IDs = new ArrayList<>();
                for (String nameString : nameStrings) {
                    try {
                        IDs.add(context.getResources().getIdentifier(nameString, "dimen", "com.android.systemui"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                XposedHelpers.findAndHookMethod(Resources.class, "getDimensionPixelSize", int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        int resID = (int) param.args[0];
                        for (Integer ID : IDs) {
                            if (resID == ID) {
                                param.setResult(0);
                                return;
                            }
                        }
                    }
                });
            }
        });
    }
}
