package wang.relish.textsample;

import android.app.Application;

import wang.relish.textsample.util.SPUtil;

/**
 * @author Relish Wang
 * @since 2019/03/06
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SPUtil.init(this);
    }
}
