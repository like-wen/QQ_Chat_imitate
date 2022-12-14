package com.lkw.server.FileServer;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.concurrent.Callable;

public class FindFile implements Callable {
    ObservableList<String> fileList;

    public FindFile(ObservableList<String> fileList) {
        super();
        this.fileList=fileList;
    }

    @Override
    public String call() {//未知原因不设置返回值也通过了检查
        while (true) {
            Platform.runLater(() -> {

                File file = new File(System.getProperty("user.dir") + "\\MyFile\\");//TODO
                if(!file.exists())
                    file.mkdir();
                File[] files = file.listFiles();
                fileList.clear();
                for (int i = 0; i < files.length; i++) {
                    fileList.add(files[i].getName());
                }
            });
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

