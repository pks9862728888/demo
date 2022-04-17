package com.example.demo.artifactsmanager.services;

import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Service
public class UriBuilderService {

    public String getBaseUri(@NonNull HttpServletRequest httpServletRequest) {
        return String.format("%s://%s",
                httpServletRequest.getProtocol().contains("https") ? "https" : "http",
                httpServletRequest.getHeader("host"));
    }

    public String buildUri(@NonNull HttpServletRequest httpServletRequest, String... params) {
        StringBuilder sb = new StringBuilder(getBaseUri(httpServletRequest));
        Arrays.stream(params)
                .forEach(p -> sb.append("/").append(p));
        return sb.toString();
    }
}
