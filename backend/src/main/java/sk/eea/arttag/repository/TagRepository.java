package sk.eea.arttag.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.model.Tag;

@Repository
public interface TagRepository  extends JpaRepository<Tag, Long>{

	@Query("select tag from Tag as tag inner join tag.value as value where value.language = :language AND value.value = :value AND tag.culturalObject = :culturalObject")
	Tag findByValueAndCulturalObjectId(@Param("language") String language, @Param("value") String value, @Param("culturalObject") CulturalObject culturalObject);

	@Query(value=//
    	"select " +//
        "    cast(min(tag.id) as bigint) as id, " +//
        "    tag.co_id, " +//
        "    min(tag.created) as created, " +//
        "    min(tag.value) as value, " +//
        "    avg(tag.hit_score) as hit_score " +// 
        "from  " +//
        "    tag " +//
        "    join localized_string as ls on ls.id = tag.value " +//
        "    left join cultural_object as co on co.id = tag.co_id " +//
        "where " +//
        "    tag.created >= :fromDate and tag.created < :untilDate and co.batch_id = :batchId " +//
        "group by " +//
        "    tag.co_id, ls.language, ls.value " +//
        "having " +//
        "    avg(tag.hit_score) >= :tagThreshold " +// 
        "order by " +//
        "    min(tag.created) asc " +//
        "offset :cursor limit :limit ",//
        nativeQuery=true//
    )
	List<Tag> findTagsForEnrichment(@Param("fromDate") Date fromDate, @Param("untilDate") Date untilDate, @Param("batchId") Long batchId, @Param("tagThreshold") Float tagThreshold, @Param("cursor")int page, @Param("limit") int count);

    @Query(value=//
        "select count(*) from ( " +//
        "   select " +//
        "       0 " +// 
        "   from  " +//
        "       tag " +//
        "       join localized_string as ls on ls.id = tag.value " +//
        "       left join cultural_object as co on co.id = tag.co_id " +//
        "   where " +//
        "       tag.created >= :fromDate and tag.created < :untilDate and co.batch_id = :batchId " +//
        "   group by " +//
        "       tag.co_id, ls.language, ls.value " +//
        "   having " +//
        "       avg(tag.hit_score) >= :tagThreshold " +//
        ") t",
        nativeQuery=true//
    )
    Integer countTagsForEnrichment(@Param("fromDate")Date fromDate, @Param("untilDate")Date untilDate, @Param("batchId")Long batchId, @Param("tagThreshold") Float tagThreshold);

}
