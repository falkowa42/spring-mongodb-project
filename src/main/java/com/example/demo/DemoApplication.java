package com.example.demo;


import com.example.demo.repositories.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
//@EnableMongoRepositories(basePackageClasses = StudentRepository.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(StudentRepository repository,
                             MongoTemplate mongoTemplate) {
        return args -> {
            Address address = new Address("USA", "Test", "123456");

            Student student = new Student(
                    "adam", "falk", "test@gmail.com", Gender.MALE, address, List.of("Computer Science"), BigDecimal.TEN,
                    LocalDateTime.now()
            );
            String email = "test@gmail.com";
//            usingMongoTemplateAndQuery(repository, mongoTemplate, student, email);
            repository.findStudentByEmail(email).ifPresentOrElse(
                    s -> {
                        System.out.println("This is the student: " + student);
                    }, () -> {
                        System.out.println("Inserting student" + student);
                        repository.insert(student);
                    }
            );
        };
    }

    private void usingMongoTemplateAndQuery(StudentRepository repository, MongoTemplate mongoTemplate, Student student, String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        List<Student> students = mongoTemplate.find(query, Student.class);
        if (students.size() > 1) {
            throw new IllegalStateException("found many students with email" + email);
        }

        if (students.isEmpty()) {
            System.out.println("Inserting student " + student);
            repository.insert(student);
        } else {
            System.out.println(student + " already exists");
        }
    }

}
