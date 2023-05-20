package org.rbaygildin.testing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.rbaygildin.domain.User;

import javax.persistence.*;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class SimplePersistenceTest {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("learnHibernateUnit");

    @Before
    public void clear(){
        withinTransaction(emf, em -> {
            Query q = em.createQuery("DELETE FROM User");
            q.executeUpdate();
        });
    }

    @Test
    public void testSaveUser() {
        withinTransaction(emf, em -> {
            User user = new User();
            user.setName("John Doe");
            em.persist(user);
            User foundUser = em.find(User.class, user.getId());
            assertNotNull(user.getId());
            assertEquals(user, foundUser);
        });
    }

    @Test
    public void testSaveMultipleUsers() {
        withinTransaction(emf, em -> {
            List<User> users = List.of(User.builder().name("Anna").build(), User.builder().name("Peter").build());
            for (var user : users) {
                em.persist(user);
            }
            Query q = em.createQuery("SELECT u FROM User u");
            List<User> foundUsers = (List<User>) q.getResultList();
            assertEquals(2, foundUsers.size());
            assertEquals("Anna", foundUsers.get(0).getName());
            assertEquals("Peter", foundUsers.get(1).getName());
        });
    }

    private void withinTransaction(EntityManagerFactory emf, Consumer<EntityManager> block) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            block.accept(em);
            em.flush();
            tx.commit();
        } catch (Throwable e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
