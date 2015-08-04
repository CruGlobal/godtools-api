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
public class JPADeviceService implements DeviceService
{

    @PersistenceContext(name = "gtDatasource")
    EntityManager entityManager;

    public Device selectById(UUID id) { return entityManager.find(Device.class, id); }

    public void insert(Device device) { entityManager.persist(device); }

    public void setAutoCommit(boolean autoCommit) { /*Do Nothing*/ }

    public void rollback() { clear(); }

    private void clear()
    {
        List<Device> devices = entityManager.createQuery("FROM Device").getResultList();

        for(Device device : devices){
            entityManager.remove(entityManager.find(Device.class,device.getId()));}
    }

}
