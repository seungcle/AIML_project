package com.group.receiptapp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // 요청에서 HTTP 상태 코드 가져오기
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            // 특정 HTTP 상태 코드에 따라 처리 가능
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "404 - Page Not Found";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "403 - Access Denied";
            }
        }
        return "An error occurred";
    }

}