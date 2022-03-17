package org.ahpuh.surf.acceptance;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.stream.Collectors;

@ActiveProfiles("test")
@Component
public class DatabaseCleaner implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> entityNames;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        entityNames = entityManager.getMetamodel().getEntities().stream()
                .filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
                .map(EntityType::getName)
                .collect(Collectors.toList());
        tableNames = entityNames.stream()
                .map(entityName -> switch (entityName) {
                    case "User" -> "USERS";
                    case "Category" -> "CATEGORIES";
                    case "Post" -> "POSTS";
                    case "Like" -> "LIKES";
                    default -> entityName;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void cleanUp() {
        entityManager.flush();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        for (int i = 0; i < tableNames.size(); i++) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableNames.get(i)).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableNames.get(i) +
                    " ALTER COLUMN " + entityNames.get(i) + "_id RESTART WITH 1").executeUpdate();
        }
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}