package ua.police.config;

import com.backendless.Backendless;

/**
 * Created by koxa on 05.09.2015.
 */
public class Config {
    private static final String APP_ID = "F485B119-4D6E-60E7-FF9C-36BBE53B4A00";
    private static final String SECRET_KEY = "5A620E30-CD30-FC05-FF94-61A3C9195500";

    public static void initApp()
    {
        Backendless.initApp(APP_ID, SECRET_KEY, "v1");
    }
}
