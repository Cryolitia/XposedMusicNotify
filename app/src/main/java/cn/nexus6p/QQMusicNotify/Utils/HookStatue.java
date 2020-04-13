package cn.nexus6p.QQMusicNotify.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Keep
public class HookStatue {

    public static Statue getStatue(Context context) {
        if (fakeTaichi(context)) {
            if (isEnabled()) return Statue.taichi_magisk_active;
            else return Statue.taichi_active;
        }
        isInstall isInstall = new isInstall(context);
        GetMagiskModule getMagiskModule = new GetMagiskModule();
        int isExp = isExpModuleActive(context);
        if (isEnabled()) {
            if (edxp) return Statue.Edxp_Active;
            else if (isExp == TAICHI_ACTIVE) return Statue.taichi_magisk_active;
            else if (isInstall.isEdxpManagerInstall || getMagiskModule.edxpModule)
                return Statue.Edxp_Active;
            else if (isInstall.isXposedInstall) return Statue.xposed_active;
            else return Statue.xposed_active;
        } else {
            if (isExp == TAICHI_ACTIVE) {
                if (taichi_magisk() || getMagiskModule.taichiModule)
                    return Statue.taichi_magisk_active;
                else return Statue.taichi_active;
            } else if (isInstall.isEdxpManagerInstall || getMagiskModule.edxpModule)
                return Statue.Edxp_notActive;
            else if (isInstall.isXposedInstall) return Statue.xposed_notActive;
            else if (isExp == TAICHI_NOT_ACTIVE) {
                if (taichi_magisk() || getMagiskModule.taichiModule)
                    return Statue.taichi_magisk_notActive;
                else return Statue.taichi_notActive;
            } else return Statue.xposed_notActive;
        }
    }

    public static boolean isActive(Statue statue) {
        return !statue.name().contains("not");
    }

    public static String getStatueName(Statue statue) {
        switch (statue) {
            case xposed_notActive:
                return "Xposed 未激活";
            case xposed_active:
                return "Xposed 已激活";
            case Edxp_notActive:
                return "EdXposed 未激活";
            case Edxp_Active:
                return "EdXposed 已激活";
            case taichi_notActive:
                return "太极·阴 未激活";
            case taichi_active:
                return "太极·阴 已激活";
            case taichi_magisk_notActive:
                return "太极·阳 未激活";
            case taichi_magisk_active:
                return "太极·阳 已激活";
            default:
                throw new RuntimeException("unknown statue: " + statue.name());
        }
    }

    public static class isInstall {
        private isInstall() {
        }

        public isInstall(Context context) {
            PackageManager packageManager = context.getPackageManager();
            PackageInstallDetect pid = new PackageInstallDetect(packageManager);
            isXposedInstall = pid.isPackageInstall(xposed_installer_packageName);
            isEdxpManagerInstall = pid.isPackageInstall(edxposed_installer_packageName) || pid.isPackageInstall(edxposed_manager_packageName);

        }

        public boolean isEdxpManagerInstall = false;
        public boolean isXposedInstall = false;

        public static class PackageInstallDetect {
            private PackageManager packageManager;

            private PackageInstallDetect() {
            }

            public PackageInstallDetect(@NonNull PackageManager mPackageManager) {
                this.packageManager = mPackageManager;
            }

            public boolean isPackageInstall(@NonNull String packageName) {
                boolean isPackageInstalled = true;
                try {
                    packageManager.getPackageInfo(packageName, PackageManager.GET_GIDS);
                    isPackageInstalled = true;
                } catch (Exception e) {
                    //ignore
                    isPackageInstalled = false;
                }
                return isPackageInstalled;
            }
        }


        public final static String xposed_installer_packageName = "de.robv.android.xposed.installer";
        public final static String edxposed_installer_packageName = "com.solohsu.android.edxp.manager";
        public final static String edxposed_manager_packageName = "org.meowcat.edxposed.manager";

    }

    public enum Statue {
        Edxp_notActive, Edxp_Active,
        taichi_notActive, taichi_magisk_notActive, taichi_active, taichi_magisk_active,
        xposed_active, xposed_notActive
    }

    public static boolean edxp = new File("/system/framework/edxp.jar").exists();

    public static boolean isEnabled() {
        Log.d("XposedMusicNotify", "模块未激活");
        return false;
    }

    public static boolean fakeTaichi(Context context) {
        return GeneralUtils.getSharedPreferenceOnUI(context).getBoolean("fakeTaichi", false);
    }

    public static boolean taichi_magisk() {
        boolean taichi_magisk = false;
        try {
            taichi_magisk = "1".equals(System.getProperty("taichi_magisk"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taichi_magisk;
    }


    final public static int TAICHI_NOT_INSTALL = 0;
    final public static int TAICHI_NOT_ACTIVE = 1;
    final public static int TAICHI_ACTIVE = 2;

    @Target({ElementType.TYPE_PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TAICHI_NOT_INSTALL, TAICHI_NOT_ACTIVE, TAICHI_ACTIVE})
    public @interface Taichi_statue {
    }

    public static class GetMagiskModule {
        public boolean taichiModule = false;
        public boolean edxpModule = false;
        final public static String moduleLocate = "/data/adb/modules";

        public GetMagiskModule() {
            Shell.su("su");
            Shell.Result result = Shell.su("ls " + moduleLocate).exec();
            String resultString = result.getOut().toString();
            Log.d("getMagiskModule", resultString);
            if (resultString.contains("edxp")) edxpModule = true;
            if (resultString.contains("taichi")) taichiModule = true;
        }
    }

    @Taichi_statue
    public static int isExpModuleActive(Context context) {

        int isExp = TAICHI_NOT_INSTALL;
        if (context == null) {
            throw new IllegalArgumentException("context must not be null!!");
        }

        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://me.weishu.exposed.CP/");
            Bundle result = null;
            try {
                result = contentResolver.call(uri, "active", null, null);
            } catch (RuntimeException e) {
                // TaiChi is killed, try invoke
                try {
                    Intent intent = new Intent("me.weishu.exp.ACTION_ACTIVE");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Throwable e1) {
                    return TAICHI_NOT_INSTALL;
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null);
            }

            if (result == null) {
                return TAICHI_NOT_INSTALL;
            }
            isExp = !result.getBoolean("active", false) ? TAICHI_NOT_ACTIVE : TAICHI_ACTIVE;
        } catch (Throwable ignored) {
        }
        return isExp;
    }
}
