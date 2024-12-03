package com.fayupable.multithread_demo.service;

import com.fayupable.multithread_demo.dto.PersonDto;
import com.fayupable.multithread_demo.entity.Person;
import com.fayupable.multithread_demo.repository.IPersonRepository;
import com.fayupable.multithread_demo.request.AddPersonRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PersonService implements IPersonService {
    private final IPersonRepository personRepository;
    private final ModelMapper modelMapper;

    public PersonService(IPersonRepository personRepository, ModelMapper modelMapper) {
        this.personRepository = personRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public CompletableFuture<Person> addPerson(AddPersonRequest person) {
        return CompletableFuture.supplyAsync(() -> Optional.of(person)
                .map(this::createPerson)
                .map(personRepository::save)
                .orElseThrow(() -> new RuntimeException("Person not created")));

    }

    private Person createPerson(AddPersonRequest request) {
        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setAge(request.getAge());
        return person;
    }

    @Override
    public CompletableFuture<Person> getPerson(Long id) {
        return null;
    }

    @Override
    public CompletableFuture<Person> getAllPersons() {
        return null;
    }

    @Override
    public CompletableFuture<Person> removePerson(Long id) {
        return null;
    }

    @Override
    public PersonDto convertToDto(Person person) {
        return modelMapper.map(person, PersonDto.class);
    }

    @Override
    public List<PersonDto> convertToDto(List<Person> persons) {
        return persons.stream()
                .map(this::convertToDto)
                .toList();
    }
}
