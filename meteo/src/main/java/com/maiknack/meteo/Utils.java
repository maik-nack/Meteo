package com.maiknack.meteo;

import java.io.Closeable;
import java.io.IOException;

public class Utils {

    public static void sleepMillisec(int time) {
        try {
            Thread.sleep(time);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static void closeStream(Closeable stream) {
        try {
            stream.close();
        } catch (IOException e) {}
    }

}
