package com.maiknack.meteo.pref;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.AttributeSet;

public class PortPreference extends EditTextPreference {

    private static int mIntMin = 0, mIntMax = 65535;

    public PortPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public PortPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PortPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PortPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        getEditText().setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(
                            CharSequence source,
                            int start,
                            int end,
                            Spanned dest,
                            int dstart,
                            int dend) {
                        if (source.length() != 0 && !Character.isDigit(source.charAt(start))) {
                            return "";
                        }
                        int input = Integer.parseInt(dest.toString() + source.toString());
                        if (isInRange(mIntMin, mIntMax, input))
                            return null;
                        return "";
                    }
                }
        });
    }

    private static boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

}
