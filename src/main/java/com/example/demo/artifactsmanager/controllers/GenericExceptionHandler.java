package com.example.demo.artifactsmanager.controllers;

import com.example.demo.artifactsmanager.models.Message;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> handleAllException(HttpServletRequest request, Exception exception) {
        Message message = new Message("Internal server error occurred!");

        // Log the exception
        exception.printStackTrace();
        System.out.println("Exception occurred: " + exception.getMessage());

        // Add link to redirect
        message.add(linkTo(methodOn(DirectoryController.class)
                .listAllRegressionTestCategories(request))
                .withSelfRel()
                .withTitle(DirectoryController.VIEW_ALL_REGRESSIONS));

        return new HttpEntity<>(message);
    }

}
