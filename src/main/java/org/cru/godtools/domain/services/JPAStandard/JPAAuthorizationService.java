package org.cru.godtools.domain.services.JPAStandard;

import com.google.common.base.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.annotations.JPAStandard;
import org.cru.godtools.domain.services.*;

import javax.persistence.*;
import java.util.*;

/**
 * Created by justinsturm on 6/29/15.
 */
@JPAStandard
public class JPAAuthorizationService implements AuthorizationService
{
    @PersistenceContext(name = "gtDatasource")
    EntityManager entityManager;

    public Optional<AuthorizationRecord> getAuthorizationRecord(String authTokenParam, String authTokenHeader)
    {
        return Optional.fromNullable((AuthorizationRecord) entityManager.createQuery("FROM AuthorizationRecord WHERE authToken = :authToken")
            .setParameter("authToken", authTokenHeader == null ? authTokenParam : authTokenHeader)
            .getSingleResult());
    }

    public void recordNewAuthorization(AuthorizationRecord authenticationRecord) { entityManager.persist(authenticationRecord); }

    public AccessCodeRecord getAccessCode(String accessCode) { return entityManager.find(AccessCodeRecord.class, accessCode); }

    public void setAutoCommit(boolean autoCommit) { /* Do Nothing */ }

    public void rollback() { clear(); }

    private void clear()
    {
        List<AuthorizationRecord> authorizationRecords = entityManager.createQuery("FROM AuthorizationRecord").getResultList();

        for(AuthorizationRecord authorizationRecord : authorizationRecords) {
            entityManager.remove(entityManager.find(AuthorizationRecord.class, authorizationRecord.getId()));}

        List<AccessCodeRecord> accessCodeRecords = entityManager.createQuery("FROM AccessCodeRecord").getResultList();

        for(AccessCodeRecord accessCodeRecord : accessCodeRecords) {
            entityManager.remove(entityManager.find(AccessCodeRecord.class, accessCodeRecord.getCode()));}
    }
}
