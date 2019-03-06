package wang.relish.textsample.util;

import com.google.gson.Gson;

/**
 * @author Relish Wang
 * @since 2019/03/06
 */
public class SingleInstanceUtils {

    private static volatile Gson mGson;

    public static synchronized Gson getGsonInstance(){
        synchronized (Gson.class) {
            if (mGson == null) {
                synchronized (Gson.class) {
                    if (mGson == null) {
                        mGson = new Gson();
                    }
                }
            }
            return mGson;
        }
    }
}
