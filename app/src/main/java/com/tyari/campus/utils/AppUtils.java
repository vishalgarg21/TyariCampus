package com.tyari.campus.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.tyari.campus.R;
import com.tyari.campus.callback.CustomDialogCallBack;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {
    private static final String TAG = LogUtils.makeLogTag(AppUtils.class);
    private static AppUtils sInstance;
    private static ProgressDialog mProgressDialog;
    private static Dialog mAlertDialog;

    private AppUtils() {

    }

    public static AppUtils getInstance() {
        if (null == sInstance) {
            sInstance = new AppUtils();
        }
        return sInstance;
    }

    public void setLocale(Context context) {
        String lang = PreferenceUtils.getInstance(context).getString(PreferenceUtils.KEY_LANG);
        String langCode = "en";

        if (!TextUtils.isEmpty(lang) && lang.length() >= 2) {
            langCode = lang.substring(0, 2).toLowerCase();
        }

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public boolean isInternetAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    public static void showProgress(Context context, String message) {
        try {
            if (mProgressDialog == null || !mProgressDialog.isShowing()) {
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage(message);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setIndeterminateDrawable(getDrawable(context, R.drawable.progressbar_style));
                mProgressDialog.show();
            } else {
                mProgressDialog.setMessage(message);
            }
        } catch (Exception ex) {
            LogUtils.error(TAG, ex.toString(), ex);
        }
    }

    /**
     * To dismiss progress dialog associated with mProgressDialog instance.
     */
    public static void dismissProgress() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception ex) {
            LogUtils.error(TAG, ex.toString(), ex);
        }
    }

    public void showCustomDialogWithCallBack(Context context, String title, String message,
                                                    String positiveButtonText, String negativeButtonText,
                                                    final CustomDialogCallBack callBack) {
        try {
            if (mAlertDialog == null || !mAlertDialog.isShowing()) {
                mAlertDialog = new Dialog(context);
                mAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mAlertDialog.setContentView(R.layout.custom_dialog);
                if(null != mAlertDialog.getWindow()) {
                    WindowManager.LayoutParams params = mAlertDialog.getWindow().getAttributes();
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    mAlertDialog.getWindow().setAttributes(params);
                }
                mAlertDialog.setCancelable(false);
                mAlertDialog.setCanceledOnTouchOutside(false);
                TextView titleTv = mAlertDialog.findViewById(R.id.title);
                TextView messageTv = mAlertDialog.findViewById(R.id.message);
                TextView positiveBut = mAlertDialog.findViewById(R.id.positive_but);
                TextView negativeBut = mAlertDialog.findViewById(R.id.negative_but);
                titleTv.setText(title);
                messageTv.setText(message);
                positiveBut.setText(positiveButtonText);

                if (negativeButtonText == null) {
                    negativeBut.setVisibility(View.GONE);

                } else {
                    negativeBut.setVisibility(View.VISIBLE);
                    negativeBut.setText(negativeButtonText);
                }


                positiveBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callBack.positiveCallBack(mAlertDialog);
                    }
                });
                negativeBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callBack.negativeCallBack(mAlertDialog);
                    }
                });
                mAlertDialog.show();
            }
        } catch (Exception ex) {
            LogUtils.error(TAG, ex.toString(), ex);
        }
    }


    public static Drawable getDrawable(Context context, int id) {
        if (null != context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                return ContextCompat.getDrawable(context, id);
            } else {
                return context.getResources().getDrawable(id);
            }
        }
        return null;
    }

    public boolean isValidEmail(String emailId) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailId);
        return matcher.matches();
    }

    public boolean isValidMobileNumber(String mobileNum) {
        if (mobileNum.length() > 0) {
            if (mobileNum.charAt(0) == '0') {
                return false;
            }
        }
        String MOBILE_PATTERN = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(MOBILE_PATTERN);
        Matcher matcher = pattern.matcher(mobileNum);
        return matcher.matches();
    }

    public static int getColor(Context context, int id) {
        int color = -1;
        if (null != context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                color = ContextCompat.getColor(context, id);
            } else {
                color = context.getResources().getColor(id);
            }
        }
        return color;
    }

    public static void setText(WebView webView, String text){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String js = "<html><head>"
                + "<link rel='stylesheet' href='file:///android_asset/mathscribe/jqmath-0.4.3.css'>"
                + "<script src = 'file:///android_asset/mathscribe/jquery-1.4.3.min.js'></script>"
                + "<script src = 'file:///android_asset/mathscribe/jqmath-etc-0.4.6.min.js'></script>"
                + "</head><body>"
                + ""+text+"</body></html>";
        webView.loadDataWithBaseURL("",js,"text/html", "UTF-8", "");
    }
}
