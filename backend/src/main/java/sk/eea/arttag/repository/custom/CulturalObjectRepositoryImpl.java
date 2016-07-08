package sk.eea.arttag.repository.custom;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.repository.CulturalObjectRepository;

public class CulturalObjectRepositoryImpl implements CulturalObjectRepositoryCustom {

    @Autowired
    CulturalObjectRepository repository;

    @Override
    public CulturalObject findOne() {
        CulturalObject co = repository.findTop1ByOrderByLastSelectedAsc();
        if (co != null) {
            co.setLastSelected(new Date());
            co.setNumberOfSelections(co.getNumberOfSelections() + 1);
            repository.save(co);
        }
        return co;
    }

}
