package com.fayupable.multithread_demo.service;

import com.fayupable.multithread_demo.dto.PersonDto;
import com.fayupable.multithread_demo.entity.Person;
import com.fayupable.multithread_demo.request.AddPersonRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IPersonService {
    CompletableFuture<Person> addPerson(AddPersonRequest person);

    CompletableFuture<Person> getPerson(Long id);

    CompletableFuture<Person> getAllPersons();

    CompletableFuture<Person> removePerson(Long id);

    PersonDto convertToDto(Person person);

    List<PersonDto> convertToDto(List<Person> persons);

}
