package com.dymchenko.reports.models;

public class PdfFile {
    private String fileName;
    private String filePath;

    public PdfFile(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

}
