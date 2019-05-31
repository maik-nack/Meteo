package com.maiknack.meteo.pref;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class IPAddressPreference extends EditTextPreference {

    private static String mRegIP = "^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?";

    public IPAddressPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public IPAddressPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IPAddressPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IPAddressPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
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
                        if (end > start) {
                            String destTxt = dest.toString();
                            String resultingTxt =
                                    destTxt.substring(0, dstart)
                                            + source.subSequence(start, end)
                                            + destTxt.substring(dend);
                            if (!resultingTxt.matches(mRegIP)) {
                                return "";
                            } else {
                                String[] splits = resultingTxt.split("\\.");
                                for (String split : splits) {
                                    if (Integer.valueOf(split) > 255) {
                                        return "";
                                    }
                                }
                            }
                        }
                        return null;
                    }
                }
        });

        getEditText().addTextChangedListener(new TextWatcher() {
            boolean deleting = false;
            int lastCount = 0;

            @Override
            public void afterTextChanged(Editable s) {
                if (!deleting) {
                    String working = s.toString();
                    String[] split = working.split("\\.");
                    String string = split[split.length - 1];
                    if (string.length() == 3 || string.equalsIgnoreCase("0")
                            || (string.length() == 2 && Integer.parseInt(string) > 25)) {
                        s.append('.');
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                deleting = lastCount >= count;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
    }

}
