/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.frenchpress.service;

import com.steeplesoft.frenchpress.model.MediaItem;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@RequestScoped
public class MediaService {
    @PersistenceContext
    protected EntityManager em;

    public MediaItem getItem(Long id) {
        return em.find(MediaItem.class, id);
    }

    public MediaItem getItem(int year, int month, String name) throws NoResultException {
        Calendar startDate = new GregorianCalendar(year, month-1, 1);
        Calendar endDate = new GregorianCalendar(year, month-1, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        TypedQuery<MediaItem> query = em.createQuery(
                "SELECT mi from MediaItem mi WHERE mi.uploadedDate >= :START AND mi.uploadedDate <= :END AND mi.name = :NAME",
                MediaItem.class);
        query.setParameter("START", startDate.getTime());
        query.setParameter("END", endDate.getTime());
        query.setParameter("NAME", name);
        return query.getSingleResult();
    }

    public List<MediaItem> getItems() {
        return em.createQuery("SELECT mi from MediaItem mi", MediaItem.class)
                .getResultList();
    }

    @Transactional
    public void addItem(MediaItem item) {
        try {
            em.persist(item);
        }catch (Exception e) {
            throw new RuntimeException (e);
        }
    }

    @Transactional
    public void deleteItem(Long id) {
        deleteItem(getItem(id));
    }

    @Transactional
    public void deleteItem(MediaItem item) {
        if (item != null) {
            em.remove(item);
        }
    }
}
