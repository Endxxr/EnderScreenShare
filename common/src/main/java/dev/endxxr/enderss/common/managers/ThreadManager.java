package dev.endxxr.enderss.common.managers;

import dev.endxxr.enderss.common.utils.LogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadManager {

    private final static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void runConnectionTask(Runnable runnable) {
        Future<?> future = executorService.submit(runnable);
        try {
            future.get();
        } catch (Exception e) {
            LogUtils.prettyPrintException(e, "Error while running connection task");
        }
    }



}
