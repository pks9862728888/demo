package com.example.demo.artifactsmanager.controllers;

import com.example.demo.artifactsmanager.models.Message;
import lombok.NonNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> handleAccessDenied(
            HttpServletRequest request, Authentication authentication, AccessDeniedException exception) {
        return getResponseMessage(
                "Forbidden! Unauthorized access to endpoint occurred!",
                request,
                authentication,
                exception);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> handleAllException(
            HttpServletRequest request, Authentication authentication, Exception exception) {
        return getResponseMessage(
                "Internal server error occurred!",
                request,
                authentication,
                exception);
    }

    private HttpEntity<Message> getResponseMessage(
            @NonNull String exceptionMessage, HttpServletRequest request, Authentication authentication,
            Exception exception) {
        Message message = new Message(exceptionMessage);

        // Log the exception
        exception.printStackTrace();
        System.out.println("Exception occurred: " + exception.getMessage());

        // Add link to redirect
        message.add(linkTo(methodOn(DirectoryController.class)
                .listAllRegressionTestCategories(request, authentication))
                .withSelfRel()
                .withTitle(DirectoryController.VIEW_ALL_REGRESSIONS));

        return new HttpEntity<>(message);
    }

}
