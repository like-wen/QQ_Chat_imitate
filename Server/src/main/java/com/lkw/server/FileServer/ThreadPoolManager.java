package com.lkw.server.FileServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用于服务端的FileServer线程管理的线程池
 */

public class ThreadPoolManager {
    private static ExecutorService fileServerExecutor = Executors.newFixedThreadPool(16);//创建FileServer所用的线程池

    //获取服务端文件模块专用的线程池
    public static ExecutorService getFileServerExecutor() {
        return fileServerExecutor;
    }

    //关闭服务端文件模块专用的线程池
    public static void shutdownFileServerExecutor() {
        fileServerExecutor.shutdown();
    }
}
