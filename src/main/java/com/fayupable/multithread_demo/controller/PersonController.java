package com.fayupable.multithread_demo.controller;

import com.fayupable.multithread_demo.dto.PersonDto;
import com.fayupable.multithread_demo.request.AddPersonRequest;
import com.fayupable.multithread_demo.service.IPersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final IPersonService personService;

    public PersonController(IPersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<PersonDto>> addPerson(@RequestBody AddPersonRequest addPersonRequest) {
        return personService.addPerson(addPersonRequest)
                .thenApply(person -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(personService.convertToDto(person)));
    }
}
