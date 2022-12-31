package com.example.REST.controllers;

import com.example.REST.dto.PersonDTO;
import com.example.REST.models.Person;
import com.example.REST.services.PersonService;
import com.example.REST.util.PersonErrorResponse;
import com.example.REST.util.PersonNotCreatedException;
import com.example.REST.util.PersonNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PersonController {
    private final PersonService personService;
    private final ModelMapper modelMapper;

    @Autowired
    public PersonController(PersonService personService, ModelMapper mapper) {
        this.personService = personService;
        this.modelMapper = mapper;
    }

    @GetMapping
    public List<PersonDTO> getPeople() {

        return personService.findAll().stream()
                .map(person -> modelMapper.map(person, PersonDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id) {

        return modelMapper.map(personService.findOne(id), PersonDTO.class);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg
                        .append(error.getField())
                        .append(" -- ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }

            throw new PersonNotCreatedException(errorMsg.toString());
        }
        personService.save(modelMapper.map(personDTO, Person.class));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Person with such id was not found", System.currentTimeMillis());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }
}


