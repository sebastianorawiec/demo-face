package com.azure.face.demo.exception;


import com.azure.face.demo.model.Persons;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(UploadException.class)
    public ModelAndView handleException(Exception exception, HttpServletRequest request) {

        ModelAndView mav = new ModelAndView();
        mav.addObject("message", exception.getMessage());
        mav.addObject("persons",new Persons());
        if(!request.getRequestURI().isEmpty() &&  !request.getRequestURI().
                equalsIgnoreCase("/")){

            String url = request.getRequestURI();
            String viewName = url.substring(url.lastIndexOf("/") + 1, url.length());
            mav.setViewName(viewName);

        } else {
            mav.setViewName("index");

        }

        return mav;

    }
}
