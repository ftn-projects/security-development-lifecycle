package ftn.security.minikms.service;

import ftn.security.minikms.entity.TestEntity;
import ftn.security.minikms.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Autowired
    TestRepository testRepository;

    public TestEntity getById(Long id){
        return testRepository.findById(id).get();
    }

    public TestEntity addTest(TestEntity testEntity){
        return testRepository.save(testEntity);
    }
}
