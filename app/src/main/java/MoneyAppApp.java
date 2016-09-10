import android.app.Application;
import android.util.Log;

/**
 * Created by SilmiNawaz on 5/9/16.
 */
public class MoneyAppApp extends Application {

    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler(){

        public void uncaughtException(Thread thread, Throwable e){
            Log.e("MoneyApp", "uncaught exception: ", e );

            defaultUncaughtExceptionHandler.uncaughtException(thread, e);
        }
    };

    @Override
    public void onCreate(){
        super.onCreate();
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

}
