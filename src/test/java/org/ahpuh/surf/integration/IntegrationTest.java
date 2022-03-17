package org.ahpuh.surf.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public abstract class IntegrationTest {

    @PersistenceContext
    protected EntityManager entityManager;

}
