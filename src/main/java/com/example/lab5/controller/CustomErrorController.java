package com.example.lab5.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Controller
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping
    public ResponseEntity<Map<String, Object>> handleError(WebRequest webRequest) {
        // Retrieve error attributes with basic options
        Map<String, Object> errorAttributesMap = errorAttributes.getErrorAttributes(
            webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE)
        );

        // Return error details as JSON
        return ResponseEntity.status((int) errorAttributesMap.get("status")).body(errorAttributesMap);
    }
}
