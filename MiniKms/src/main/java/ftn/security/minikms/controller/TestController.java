package ftn.security.minikms.controller;

import ftn.security.minikms.dto.TestDTO;
import ftn.security.minikms.entity.TestEntity;
import ftn.security.minikms.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/test")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/{id}")
    public TestEntity getTest(@PathVariable Long id) {
        return testService.getById(id);
    }

    @PostMapping("/add")
    public TestEntity addTest(@RequestBody TestDTO testDTO){
        TestEntity entity = new TestEntity();
        entity.setName(testDTO.getName());

        TestEntity savedEntity = testService.addTest(entity);
        return ResponseEntity.ok(savedEntity).getBody();
    }
}
