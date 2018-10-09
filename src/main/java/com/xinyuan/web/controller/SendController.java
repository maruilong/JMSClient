package com.xinyuan.web.controller;

import com.xinyuan.entity.UploadInfo;
import com.xinyuan.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send")
public class SendController {

    @Autowired
    private SendService sendService;

    @RequestMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestBody UploadInfo uploadInfo) {
        try {
            sendService.send(uploadInfo);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.ok("fail");
        }
    }
}
