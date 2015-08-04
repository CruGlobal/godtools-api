package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.model.Package;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import javax.persistence.*;
import java.util.*;

/**
 * Created by justinsturm on 7/13/15.
 */
@JPAStandard
public class JPAPackageService implements PackageService
{
    @PersistenceContext(name = "gtDatasource")
    EntityManager entityManager;

    public Package selectById(UUID id) { return entityManager.find(Package.class, id); }

    public Package selectByCode(String code) {
        return (Package) entityManager.createQuery("FROM Package WHERE code = :code")
                .setParameter("code", code)
                .getSingleResult();
    }

    public List<Package> selectAllPackages() {
        return entityManager.createQuery("FROM Package").getResultList();
    }

    public Package selectByOneskyProjectId(Integer translationProjectId) {
        return (Package) entityManager.createQuery("FROM Package WHERE translationProjectId = :projectId")
            .setParameter("projectId",translationProjectId)
            .getSingleResult();
    }

    public void insert(Package godToolsPackage) { entityManager.persist(godToolsPackage); }

    public void setAutoCommit(boolean autoCommit) { /* Do Nothing */ }

    public void rollback() { clear(); }

    private void clear()
    {
        List<Package> packages = selectAllPackages();

        for(Package gtPackage : packages)
        {
            List<PackageStructure> packageStructures = entityManager.createQuery("FROM PackageStructure WHERE gtPackage.id = :packageId")
                    .setParameter("packageId",gtPackage.getId())
                    .getResultList();

            for(PackageStructure packageStructure : packageStructures) {
                (entityManager.find(PackageStructure.class, packageStructure.getId())).setPackage(null);}

            List<Translation> translations = entityManager.createQuery("FROM Translation WHERE gtPackage.id = :packageId")
                    .setParameter("packageId", gtPackage.getId())
                    .getResultList();

            for(Translation translation : translations) {
                (entityManager.find(Translation.class, translation.getId())).setPackage(null);}

            entityManager.remove(entityManager.find(Package.class, gtPackage.getId()));
        }
    }
}
