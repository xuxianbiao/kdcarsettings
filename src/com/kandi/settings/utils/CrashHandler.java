package com.kandi.settings.utils;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {

	// 需求是 整个应用程序 只有一个 MyCrash-Handler   
    private static CrashHandler INSTANCE ;  
    private Context context;  
      
    private Properties mDeviceCrashInfo = new Properties();  
    private static final String STACK_TRACE = "STACK_TRACE";  
    private static final String CRASH_REPORTER_EXTENSION = ".log";
    //1.私有化构造方法  
    private CrashHandler(){  
          
    }  
      
    public static synchronized CrashHandler getInstance(){  
        if (INSTANCE == null)  
            INSTANCE = new CrashHandler();  
        return INSTANCE;
    }

    public void init(Context context){  
        this.context = context;
    }  
      
  
    public void uncaughtException(Thread arg0, Throwable arg1) {  
//        System.out.println("程序挂掉了 ");  
        saveCrashInfoToFile(arg1);
        arg1.printStackTrace();
        // 在此可以把用户手机的一些信息以及异常信息捕获并上传,由于UMeng在这方面有很程序的api接口来调用，故没有考虑
//        Toast.makeText(context, "开了个小差", Toast.LENGTH_SHORT).show();
        //干掉当前的程序   
        try {
            Thread.sleep(3000);  
        } catch (InterruptedException e) { 
        	
        } 
        android.os.Process.killProcess(android.os.Process.myPid());  
    }  
    
    /** 
     * 保存错误信息到文件中 
     *  
     * @param ex 
     * @return 
     */  
    private String saveCrashInfoToFile(Throwable ex) {  
        Writer info = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(info);  
        // printStackTrace(PrintWriter s)  
        // 将此 throwable 及其追踪输出到指定的 PrintWriter  
        ex.printStackTrace(printWriter);  
  
        // getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
  
        // toString() 以字符串的形式返回该缓冲区的当前值。  
        String result = info.toString();  
        printWriter.close();  
        mDeviceCrashInfo.put(STACK_TRACE, result);  
  
        try {  
            long timestamp = System.currentTimeMillis();  
            String fileName = "kandi_setting_crash-" + timestamp + CRASH_REPORTER_EXTENSION;  
            // 保存文件  
            FileOutputStream trace = context.openFileOutput(fileName, Context.MODE_PRIVATE);  
            mDeviceCrashInfo.store(trace, "");  
            trace.flush();  
            trace.close();  
            return fileName;  
        } catch (Exception e) {  
            Log.e("CrashHandle", "an error occured while writing report file...", e);  
        }  
        return null;  
    }  

}
