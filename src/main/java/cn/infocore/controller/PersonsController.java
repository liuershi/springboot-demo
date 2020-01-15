package cn.infocore.controller;

import cn.infocore.bean.Persons;
import cn.infocore.mapper.PersonsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/15 14:52
 * @instructions
 */
@RestController
public class PersonsController {

    @Autowired
    PersonsMapper personsMapper;

    @GetMapping("/person/{age}")
    public Persons getByAge(@PathVariable("age") Integer age) {
        Persons person = personsMapper.getPersonByAge(age);
        return person;
    }

    @GetMapping("/person")
    public Persons insertPerson(Persons persons) {
        personsMapper.insertPerson(persons);
        return persons;
    }

    @GetMapping("/updatePerson")
    public String updatePersonByAge(String city, Integer age){
        personsMapper.updatePersonByAge(city, age);
        return city;
    }

    @GetMapping("/delPerson/{age}")
    public Integer delPerson(@PathVariable("age") Integer age){
        personsMapper.delPersonByAge(age);
        return age;
    }
}
