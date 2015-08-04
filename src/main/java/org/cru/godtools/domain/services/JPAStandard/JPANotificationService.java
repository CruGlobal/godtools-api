package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import javax.persistence.*;
import java.util.*;

/**
 * Created by justinsturm on 7/7/15.
 */
@JPAStandard
public class JPANotificationService implements NotificationService
{
    @PersistenceContext(name = "gtDatasource")
    EntityManager entityManager;

    public List<Notification> selectAllUnsentNotifications() {
        return entityManager.createQuery("FROM Notification WHERE notificationSent = false").getResultList();
    }

    public Notification selectNotificationByRegistrationIdAndType(Notification notification)
    {
        return (Notification) entityManager.createQuery("FROM Notification WHERE registrationId = :rId AND notificationType = :nType")
                .setParameter("rId", notification.getRegistrationId())
                .setParameter("nType", notification.getNotificationType())
                .getSingleResult();
    }

    public void setNotificationAsSent(UUID id) {
        (entityManager.find(Notification.class, id)).setNotificationSent(true); }

    public void updateNotification(Notification notification) { entityManager.merge(notification); }

    public void insertNotification(Notification notification) { entityManager.persist(notification); }

    public void setAutoCommit(boolean autoCommit) { /* Do Nothing */ }

    public void rollback()
    {
        List<Notification> notifications = entityManager.createQuery("FROM Notification").getResultList();

        for(Notification notification : notifications) {
            entityManager.remove(entityManager.find(Notification.class, notification.getId()));}
    }
}
