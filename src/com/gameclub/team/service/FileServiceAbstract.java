package com.gameclub.team.service;

import java.io.IOException;

//Shows what the File Service class does
public abstract class FileServiceAbstract {


    protected String filePath;

    public FileServiceAbstract(String filePath) {
        this.filePath = filePath;
    }
    //==================================================//
    //Application of template method
    public final void processFile() throws IOException {
        try {
            openFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            readData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            validateData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            saveResults();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            closeFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    protected abstract void openFile() throws Exception;
    protected abstract void readData() throws Exception;
    protected abstract void validateData() throws Exception;
    protected abstract void saveResults() throws Exception;
    protected abstract void closeFile() throws Exception;


}
