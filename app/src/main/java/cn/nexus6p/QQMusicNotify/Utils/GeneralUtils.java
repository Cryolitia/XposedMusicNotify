package cn.nexus6p.QQMusicNotify.Utils;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.nexus6p.QQMusicNotify.BuildConfig;
import cn.nexus6p.QQMusicNotify.MainActivity;
import de.robv.android.xposed.XposedBridge;

final public class GeneralUtils {

    private static boolean isCheckingUpdate = false;
    private static boolean isDownloading = false;

    public static Context getContext() {
        return AndroidAppHelper.currentApplication().getApplicationContext();
    }

    public static Context getModuleContext(Context context) {
        Context moduleContext = null;
        try {
            moduleContext = context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            XposedBridge.log(e);
        }
        return moduleContext;
    }

    public static Context getModuleContext() {
        return getModuleContext(getContext());
    }

    public static JSONArray getSupportPackages(Context context) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(getAssetsString("packages.json", context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static boolean isStringInJSONArray(String string, JSONArray jsonArray) {
        if (jsonArray == null) return false;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.getJSONObject(i).getString("app").equals(string)) return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getAssetsString(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            File file = new File(context.getExternalFilesDir(null) + File.separator + fileName);
            //File file = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"Android/data/cn.nexus6p.QQMusicNotify/files/" +fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            return null;
        }
        return stringBuilder.toString();
    }

    public static void jumpToLink(PreferenceFragmentCompat fragment, String preference, String link, boolean isCoolapk) {
        fragment.findPreference(preference).setOnPreferenceClickListener(preference1 -> {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            if (isCoolapk) {
                intent.setData(Uri.parse(link));
                try {
                    intent.setData(Uri.parse("coolmarket://" + link));
                    fragment.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(fragment.getActivity(), "未安装酷安", Toast.LENGTH_SHORT).show();
                    intent.setData(Uri.parse(PreferenceUtil.isGooglePlay ? "https://github.com/singleNeuron" : "http://www.coolapk.com/" + link));
                    fragment.startActivity(intent);
                    e.printStackTrace();
                }
            } else {
                intent.setData(Uri.parse(link));
                fragment.startActivity(intent);
            }
            return true;
        });
    }

    public static void bindEditTextSummary(EditTextPreference preference) {
        preference.setSummary(preference.getText());
        preference.setOnPreferenceChangeListener((preference1, newValue) -> {
            preference.setSummary((CharSequence) newValue);
            return true;
        });
    }

    public static void jumpToAlipay(PreferenceFragmentCompat fragment, String preference, String link) {
        fragment.findPreference(preference).setOnPreferenceClickListener(preference1 -> {
            Intent localIntent = new Intent();
            localIntent.setAction("android.intent.action.VIEW");
            localIntent.setData(Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + "https://qr.alipay.com/" + link));
            if (localIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
                fragment.startActivity(localIntent);
                return true;
            }
            localIntent.setData(Uri.parse(("https://qr.alipay.com/" + link).toLowerCase()));
            fragment.startActivity(localIntent);
            return true;
        });
    }

    public static void setWorldReadable(Context context) {
        try {
            File dataDir = new File(context.getApplicationInfo().dataDir);
            File prefsDir = new File(dataDir, "shared_prefs");
            File prefsFile = new File(prefsDir, BuildConfig.APPLICATION_ID + "_preferences.xml");
            if (prefsFile.exists()) {
                for (File file : new File[]{dataDir, prefsDir, prefsFile}) {
                    file.setReadable(true, false);
                    file.setExecutable(true, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void editFile(File file, Context activity) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider", file);
        intent.setDataAndType(uri, "text/*");
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //抄的https://www.jianshu.com/p/4e12da9866a0
    public static void getJsonFromInternet(MainActivity activity, boolean shouldShowToast) {
        if (isCheckingUpdate) return;
        final String url = "https://xposedmusicnotify.singleneuron.me/config/version.json";
        if (!getSharedPreferenceOnUI(activity).getBoolean("network", false)) {
            if (shouldShowToast)
                Toast.makeText(activity, "联网已禁用，无法检查新版本", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            try {
                isCheckingUpdate = true;
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    byte[] jsonBytes = convertIsToByteArray(inputStream);
                    String json = new String(jsonBytes);
                    if (json.length() > 0) {
                        activity.runOnUiThread(() -> {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                int versionCode = jsonObject.optInt("code");
                                if (versionCode > BuildConfig.VERSION_CODE) {
                                    new MaterialAlertDialogBuilder(activity)
                                            .setTitle("发现新版本")
                                            .setMessage(jsonObject.optString("name"))
                                            .setNegativeButton("取消", null)
                                            .setPositiveButton("下载", (dialogInterface, i) -> {
                                                Intent localIntent = new Intent("android.intent.action.VIEW");
                                                localIntent.setData(Uri.parse(PreferenceUtil.isGooglePlay ? "https://play.google.com/store/apps/details?id=cn.nexus6p.QQMusicNotify" : "https://github.com/singleNeuron/XposedMusicNotify/releases"));
                                                activity.startActivity(localIntent);
                                            })
                                            .create()
                                            .show();
                                } else
                                    activity.runOnUiThread(() -> Toast.makeText(activity, "检查更新成功，当前已是最新版本", Toast.LENGTH_SHORT).show());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    }
                }
                activity.runOnUiThread(() -> Toast.makeText(activity, "检查更新时出错", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> Toast.makeText(activity, "检查更新时出错：" + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isCheckingUpdate = false;
            }
        }).start();
    }

    private static byte[] convertIsToByteArray(InputStream inputStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            inputStream.close();
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static void downloadFileFromInternet(String locate, MainActivity context) {
        if (isDownloading) return;
        new Thread(() -> {
            try {
                isDownloading = true;
                SharedPreferences sharedPreferences = getSharedPreferenceOnUI(context);
                String address = sharedPreferences.getString("onlineGit", "https://xposedmusicnotify.singleneuron.me/config/");
                URL url = new URL(address + locate);
                String fileName = locate.substring(locate.lastIndexOf("/") + 1);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == 200) {
                    InputStream inputStream = connection.getInputStream();
                    FileOutputStream fileOutputStream = null;
                    if (inputStream != null) {
                        File file = new File(context.getExternalFilesDir(null) + File.separator + fileName);
                        if (file.exists()) file.delete();
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch;
                        while ((ch = inputStream.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                        }
                        context.runOnUiThread(() -> {
                            Toast.makeText(context, "成功", Toast.LENGTH_LONG).show();
                            try {
                                context.reload();
                            } catch (Exception e) {
                                e.printStackTrace();
                                context.finish();
                            }
                        });
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                context.runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
            } finally {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isDownloading = false;
            }
        }).start();
    }

    public static SharedPreferences getSharedPreferenceOnUI(Context context) {
        setWorldReadable(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID + "_preferences", Context.MODE_PRIVATE);
        setWorldReadable(context);
        return sharedPreferences;
    }

    @Deprecated
    public static void preferenceChangeListener(Preference preference, Object newValue) {
        /*//Log.d("JSONPreference","begin");
        JSONPreference jsonPreference = JSONPreference.Companion.setter();
        //Log.d("JSONPreference",jsonPreference.jsonObject.toString());
        try {
            jsonPreference.jsonObject.putOpt(preference.getKey(),newValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonPreference.commit();
        //Log.d("JSONPreference",jsonPreference.jsonObject.toString());*/
    }

}
