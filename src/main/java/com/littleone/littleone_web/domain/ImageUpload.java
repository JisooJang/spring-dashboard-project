package com.littleone.littleone_web.domain;

import org.springframework.web.multipart.MultipartFile;

public class ImageUpload {

    private String attatchPath;
    private String Filename;
    private MultipartFile upload;
    private String CKEditorFuncNuM;


    public String getAttatchPath() {
        return attatchPath;
    }

    public void setAttatchPath(String attatchPath) {
        this.attatchPath = attatchPath;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
    }

    public MultipartFile getUpload() {
        return upload;
    }

    public void setUpload(MultipartFile upload) {
        this.upload = upload;
    }

    public String getCKEditorFuncNuM() {
        return CKEditorFuncNuM;
    }

    public void setCKEditorFuncNuM(String CKEditorFuncNuM) {
        this.CKEditorFuncNuM = CKEditorFuncNuM;
    }

    @Override
    public String toString() {
        return "ImageUpload{" +
                "attatchPath='" + attatchPath + '\'' +
                ", Filename='" + Filename + '\'' +
                ", upload=" + upload +
                ", CKEditorFuncNuM='" + CKEditorFuncNuM + '\'' +
                '}';
    }
}
