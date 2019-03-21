package com.littleone.littleone_web.controller;

import com.google.gson.Gson;
import com.littleone.littleone_web.domain.ImageUpload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class ImageUploadController {
    @RequestMapping(value = "/imageUpload")
    public String fileUpload(@ModelAttribute("ImageUpload") ImageUpload fileUploadVO, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String rootPath = session.getServletContext().getRealPath("/");
        String attachPath = "/";

        MultipartFile upload = fileUploadVO.getUpload();
        String filename = "";
        String CKEditorFuncNum = "";

        if (upload != null) {
            filename = upload.getOriginalFilename();
            fileUploadVO.setFilename(filename);
            CKEditorFuncNum = fileUploadVO.getCKEditorFuncNuM();
            try {
                File file = new File(rootPath + attachPath + filename);
                upload.transferTo(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("filePath", attachPath + filename);
        model.addAttribute("CKEditorFuncNum", CKEditorFuncNum);
        return "community/product/view";
    }

}


