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

    @Query(value="select tag.* from tag as tag left join cultural_object as co on co.id = tag.co_id where tag.created >= :fromDate and tag.created < :untilDate and co.batch_id = :batchId order by tag.created asc offset :cursor limit :limit", nativeQuery=true)
    List<Tag> findTagsForEnrichment(@Param("fromDate") Date fromDate, @Param("untilDate") Date untilDate, @Param("batchId") String batchId, @Param("cursor")int page, @Param("limit") int count);

    @Query(value="select count(tag.id) from tag as tag left join cultural_object as co on co.id = tag.co_id where tag.created >= :fromDate and tag.created < :untilDate and co.batch_id = :batchId", nativeQuery=true)
    Integer countTagsForEnrichment(@Param("fromDate")Date fromDate, @Param("untilDate")Date untilDate, @Param("batchId")String batchId);

}
