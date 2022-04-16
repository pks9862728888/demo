package com.example.demo.artifactsmanager.models;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message extends RepresentationModel<Message> {

    private String message;

}
