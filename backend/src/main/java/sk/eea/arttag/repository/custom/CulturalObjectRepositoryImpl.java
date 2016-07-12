package sk.eea.arttag.repository.custom;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.repository.CulturalObjectRepository;

public class CulturalObjectRepositoryImpl implements CulturalObjectRepositoryCustom {

    @Autowired
    CulturalObjectRepository repository;
    @Autowired
    EntityManager entityManager;

    private static final Logger LOG = LoggerFactory.getLogger(CulturalObjectRepositoryImpl.class);

    /*    @Override
    public CulturalObject findOne() {
        LOG.debug("BEFORE_FIND");
        CulturalObject co = repository.findTop1ByOrderByLastSelectedAsc();
        LOG.debug("AFTER_FIND");
        if (co != null) {
            co.setLastSelected(new Date());
            co.setNumberOfSelections(co.getNumberOfSelections() + 1);
            LOG.debug("BEFORE_SAVE");
            repository.save(co);
            LOG.debug("AFTER_SAVE");
        }
        return co;
    }*/

    @Override
    @Transactional
    public CulturalObject findOne() {

        synchronized (this) {
            LOG.debug("BEFORE_FIND");
            CulturalObject co = repository.findTop1ByOrderByLastSelectedAsc();
            LOG.debug("AFTER_FIND: {}", co == null ? null: co.getId());
            if (co != null) {
                co.setLastSelected(new Date());
                co.setNumberOfSelections(co.getNumberOfSelections() + 1);
                LOG.debug("BEFORE_SAVE");
                repository.save(co);
                LOG.debug("AFTER_SAVE");
            }
            return co;
        }
    }
    
/*    @Override
    @Transactional
    public CulturalObject findOne() {
        TypedQuery<CulturalObject> q = entityManager.createQuery("SELECT c FROM CulturalObject c ORDER BY c.lastSelected ASC", CulturalObject.class);
        q.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        q.setHint("javax.persistence.lock.timeout", 3000);
        LOG.debug("BEFORE_FIND");
        List<CulturalObject> objects = q.setMaxResults(1).getResultList();
        LOG.debug("AFTER_FIND: {}", objects != null && objects.size() > 0 ? objects.get(0).getId() : null);
        if (objects != null && objects.size() > 0) {
            CulturalObject co = objects.get(0);
            co.setLastSelected(new Date());
            co.setNumberOfSelections(co.getNumberOfSelections() + 1);
            LOG.debug("BEFORE_SAVE");
            repository.save(co);
            LOG.debug("AFTER_SAVE");
            return co;
        }
        return null;
    }*/
}
