package com.example.testalgorithm;

import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.library.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private static final String TAG = "ThreadPoolManager";

    /**
     * 延迟使用的线程池
     */
    private ScheduledExecutorService serviceDelayManager;

    /**
     * io使用的线程池,包括数据库操作、文件读写、http请求
     */
    private ExecutorService ioActionPoolService;

    /**
     * 发送协议使用的线程池
     */
    private ExecutorService sendActionPoolService;

    /**
     * 接收协议使用的线程池
     */
    private ExecutorService receiveActionPoolService;

    private ExecutorService noneCorePoolService;
    private ExecutorService backupCorePoolService;

    private ThreadPoolManager() {
        init();
    }

    private void init() {
        int num = Runtime.getRuntime().availableProcessors();
        initDelayManager(num);
        initOtherServices(num);
    }

    private void initDelayManager(int num) {
        serviceDelayManager = Executors.newScheduledThreadPool(num <= 2 ? 1 : num / 2);
    }

    private void initOtherServices(int num) {
        ioActionPoolService =
                new ThreadPoolExecutor(num, num, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        sendActionPoolService =
                new ThreadPoolExecutor(num, num, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        receiveActionPoolService =
                new ThreadPoolExecutor(num, num, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        noneCorePoolService =
                new ThreadPoolExecutor(0, 7 * num, 15L, TimeUnit.SECONDS, new SynchronousQueue<>());
        backupCorePoolService = Executors.newFixedThreadPool(num);
        // computingActionService = Executors.newFixedThreadPool(num);
    }

    private static final ThreadPoolManager manager = new ThreadPoolManager();

    public static ThreadPoolManager getInstance() {
        return manager;
    }

    public synchronized void clearAllTask() {
        closeDelayService();
        closeOtherServices();
        init();
    }

    private void closeDelayService() {
        if (serviceDelayManager != null) {
            serviceDelayManager.shutdown();
            serviceDelayManager.shutdownNow();
            serviceDelayManager = null;
        }
    }

    private void closeOtherServices() {
        if (ioActionPoolService != null) {
            ioActionPoolService.shutdown();
            ioActionPoolService.shutdownNow();
            ioActionPoolService = null;
        }

        if (sendActionPoolService != null) {
            sendActionPoolService.shutdown();
            sendActionPoolService.shutdownNow();
            sendActionPoolService = null;
        }

        if (receiveActionPoolService != null) {
            receiveActionPoolService.shutdown();
            receiveActionPoolService.shutdownNow();
            receiveActionPoolService = null;
        }

        if (noneCorePoolService != null) {
            noneCorePoolService.shutdown();
            noneCorePoolService.shutdownNow();
            noneCorePoolService = null;
        }

        if (backupCorePoolService != null) {
            backupCorePoolService.shutdown();
            backupCorePoolService.shutdownNow();
            backupCorePoolService = null;
        }

        // if (computingActionPoolService != null) {
        // computingActionPoolService.shutdown();
        // computingActionPoolService.shutdownNow();
        // computingActionPoolService = null;
        // }
    }

    @SuppressWarnings("unused")
    public synchronized void clearDelayManager() {
        closeDelayService();

        int num = Runtime.getRuntime().availableProcessors();
        initDelayManager(num);
    }

    public synchronized ScheduledFuture<?> addTaskDelay(Runnable runnable, long delay) {
        try {
            return serviceDelayManager.schedule(runnable, delay, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LogUtil.e(TAG, "addTaskDelay:" + e);
            return null;
        }
    }

    public synchronized ScheduledFuture<?> addTaskDelayAtFixedRate(Runnable runnable, long delayMillis,
                                                                long periodMillis) {
        try {
            return serviceDelayManager.scheduleAtFixedRate(runnable, delayMillis, periodMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LogUtil.e(TAG, "addTaskDelayAtFixedRate:" + e);
            return null;
        }
    }

    /**
     * 负责普通任务
     */
    public void addTask(Runnable runnable) {
        try {
            execute(ioActionPoolService, noneCorePoolService, backupCorePoolService, runnable);
        } catch (Exception e) {
            LogUtil.e(TAG, "addTask:" + e);
        }
    }

    /**
     * <font color="red">负责IO任务 (including file, image, voice, video, sharedPreference...), Database IO, Database
     * IO</font>
     */
    public void addIOTask(Runnable runnable) {
        try {
            execute(ioActionPoolService, noneCorePoolService, backupCorePoolService, runnable);
        } catch (Exception e) {
            LogUtil.e(TAG, "addIOTask:" + e);
        }
    }

    /**
     * 如果当前在mainLooper，则添加至IOPoolService;否则直接执行。
     * @param runnable 目标任务
     */
    public void addIOTaskIfMainLooper(@NonNull Runnable runnable){
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            addIOTask(runnable);
        } else {
            runnable.run();
        }
    }

    public ExecutorService getIOTaskExecutor() {
        return backupCorePoolService;
    }

    /**
     * <font color="red">负责发送协议</font>
     */
    public void addSendCommandTask(Runnable runnable) {
        try {
            execute(sendActionPoolService, noneCorePoolService, backupCorePoolService, runnable);
        } catch (Exception e) {
            LogUtil.e(TAG, "addSendCommandTask:" + e);
        }
    }

    public ExecutorService getSendTaskExecutor() {
        return backupCorePoolService;
    }

    /**
     * <font color="red">负责接受协议</font>
     */
    public void addReceiveCommandTask(Runnable runnable) {
        try {
            execute(receiveActionPoolService, noneCorePoolService, backupCorePoolService, runnable);
        } catch (Exception e) {
            LogUtil.e(TAG, "addReceiveCommandTask:" + e);
        }
    }

    private void execute(ExecutorService corePoolService, ExecutorService noneCorePoolService,
            ExecutorService backupCorePoolService, Runnable runnable) throws RejectedExecutionException {
        try {
            corePoolService.execute(runnable);
        } catch (RejectedExecutionException e) {
            try {
                noneCorePoolService.execute(runnable);
            } catch (RejectedExecutionException e1) {
                backupCorePoolService.execute(runnable);
            }
        }
    }
}
