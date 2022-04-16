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
public class Directory extends RepresentationModel<Directory> {

    private String name;

}
