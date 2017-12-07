package com.craterzone.logginglib.executer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundExecutor {

    private static BackgroundExecutor mInstance;
    private final ExecutorService executorService;

    private BackgroundExecutor() {
        executorService = Executors.newFixedThreadPool(5);
    }

    public static BackgroundExecutor getInstance() {
        if (mInstance == null) {
            mInstance = new BackgroundExecutor();
        }


        return mInstance;
    }

    public void execute(Runnable runnable) {
        executorService.submit(runnable);
    }
}
