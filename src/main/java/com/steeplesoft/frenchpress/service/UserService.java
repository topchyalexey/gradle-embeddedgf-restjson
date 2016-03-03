/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.steeplesoft.frenchpress.service;

import com.steeplesoft.frenchpress.model.User;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author jdlee
 */
public class UserService {

    @PersistenceContext
    protected EntityManager em;
    @Inject
    PostService postService;

    public List<User> getUsers() {
        return (List<User>) em.createQuery("SELECT u FROM User u ORDER BY u.firstName, u.lastName").getResultList();
    }

    public List<User> getUsersForRole(String role) {
        return (List<User>) em.createQuery("SELECT u FROM User u WHERE u.userRole = :role")
                .setParameter("role", role)
                .getResultList();
    }

    @Transactional
    public void createUser(User user) {
        em.persist(user);
    }

    public User getUser(Long id) {
        if (id != null) {
            return em.find(User.class, id);
        } else {
            return null;
        }
    }

    @Transactional
    public void deleteUser(User user) {
        List<User> admins = getUsersForRole("Administrator");
        if (!admins.isEmpty()) {
            User admin = admins.get(0);
//            postService.migratePostsToUser
        }

        em.merge(user);
        em.remove(user);
    }

    @Transactional
    public void updateUser(User user) {
        em.merge(user);
    }
}
