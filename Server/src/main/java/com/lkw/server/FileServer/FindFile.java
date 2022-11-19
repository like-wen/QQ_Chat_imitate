package com.lkw.server.FileServer;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.File;

public class FindFile implements Runnable{
    ObservableList<String> fileList;

    public FindFile(ObservableList<String> fileList) {
        super();
        this.fileList=fileList;
    }

    @Override
    public void run() {
        while (true) {
            Platform.runLater(() -> {
                File file = new File(System.getProperty("user.dir") + "\\MyFile\\");
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
