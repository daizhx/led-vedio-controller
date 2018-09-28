package demo.com.ledvediocontroller.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class SharePreferencesUtil {

    private static final String fileName = "data";
    private Context mContext;

    public SharePreferencesUtil(Context c) {
        mContext = c;
    }

    public String[] getSharedPreference(String key) {
        String regularEx = "#";
        String[] str = null;
        SharedPreferences sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String values;
        values = sp.getString(key, null);
        if(values != null) {
            str = values.split(regularEx);
        }
        return str;
    }

    public void setSharedPreference(String key, String[] values) {
        String regularEx = "#";
        String str = "";
        SharedPreferences sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        if (values != null && values.length > 0) {
            for (String value : values) {
                str += value;
                str += regularEx;
            }
            SharedPreferences.Editor et = sp.edit();
            et.putString(key, str);
            et.commit();
        }
    }

    public void setSharedPreference(String key, List<String> values) {
        String regularEx = "#";
        String str = "";
        SharedPreferences sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        if (values != null && values.size() > 0) {
            for (String value : values) {
                str += value;
                str += regularEx;
            }
            SharedPreferences.Editor et = sp.edit();
            et.putString(key, str);
            et.commit();
        }
    }

    public void delete(String key){
        SharedPreferences sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

}
