package com.example.REST.services;

import com.example.REST.models.Person;
import com.example.REST.repositories.PersonRepo;
import com.example.REST.util.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepo personRepo;

    @Autowired
    public PersonService(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    public List<Person> findAll() {
        return personRepo.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> person = personRepo.findById(id);
        return person.orElseThrow(PersonNotFoundException::new);
    }

    public void save(Person person){
        enrichPerson(person);
        personRepo.save(person);
    }
    private void enrichPerson(Person person) {
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setCreatedWho("ADMIN");
    }


}
