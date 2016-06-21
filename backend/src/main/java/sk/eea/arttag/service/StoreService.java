package sk.eea.arttag.service;

import java.util.Date;

import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.rest.api.PageableTagsDTO;
import sk.eea.arttag.rest.api.TagDTO;

/**
 * Service for managing cultural objects and tags.
 * @author Maros Strmensky
 *
 */
public interface StoreService {

	/**
	 * Adds new cultural object into database.
	 * @param culturalObject
	 * @return 
	 */
	CulturalObject addCulturalObject(CulturalObject culturalObject);

	/**
	 * Stops enrichment of collection identified by batchId.
	 * @param batchId
	 */
	void stopEnrichingBatch(String batchId);

	/**
	 * Adds tag to CulturalObject.
	 * @param tagDto
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	TagDTO addTag(TagDTO tagDto) throws ObjectNotFoundException;

	/**
	 * Returns list of tags created within time specified belonging to batch identified by batchId.
	 * @param fromDate
	 * @param untilDate
	 * @param batchId
	 * @return
	 */
    PageableTagsDTO listTags(Date fromDate, Date untilDate, String batchId);

    PageableTagsDTO listTags(String resumptionToken) throws TokenExpiredException;
}
