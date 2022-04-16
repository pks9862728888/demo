package com.example.demo.artifactsmanager.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Screenshot extends RepresentationModel<Screenshot> {

    private String screenshotName;
    private String createdOn;

}
