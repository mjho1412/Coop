package com.hb.coop.utils;


import androidx.annotation.Keep;
import com.hb.coop.utils.otp.TOTP;
import timber.log.Timber;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppOTP {

    static {
        System.loadLibrary("native-lib");
    }

    private static AppOTP instance = new AppOTP();

    public static AppOTP getInstance() {
        return instance;
    }

    @Keep
    native String getSecretKey();

    public String generate() {
        String temp;
        Calendar calendar = Calendar.getInstance();
        int second = calendar.get(Calendar.SECOND);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        temp = sdf.format(calendar.getTime());
        long timeNoSecond = calendar.getTimeInMillis() / 1000;
        try {
            Date d = sdf.parse(temp);
            timeNoSecond = d.getTime() / 1000;
        } catch (Exception ignored) {
        }
        if (second < 30) {
            timeNoSecond += 29;
        } else {
            timeNoSecond += 59;
        }

        try {
            String secretStr = getSecretKey();
//            Totp generator = new Totp(secretStr.toUpperCase());

//            return totp.now();
//            String timeTest = "1540819695";
            String timeTest = "" + timeNoSecond;
            String pin = TOTP.generateTOTP(secretStr, timeTest, "6");
//            Timber.d("Test OTP: %s - %s", pin, generator.now());
            return pin;
        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }


}
