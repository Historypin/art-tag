/**
 * 
 */
package sk.eea.arttag.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.model.CulturalObjectRepository;
import sk.eea.arttag.model.LocalizedString;
import sk.eea.arttag.model.Tag;
import sk.eea.arttag.model.TagRepository;
import sk.eea.arttag.rest.api.PageableTagsDTO;
import sk.eea.arttag.rest.api.TagDTO;

/**
 * @author Maros Strmensky
 *
 */
public class StoreServiceImpl implements StoreService {

	private static final int DEFAULT_PAGE_SIZE = 50;

    private static final TemporalAmount EXPIRATION = Duration.of(30, ChronoUnit.MINUTES);

    @Autowired
	private CulturalObjectRepository culturalObjectRepository;
	
	@Autowired
	private TagRepository tagRepository;
	
	
	/* (non-Javadoc)
	 * @see sk.eea.arttag.service.StoreService#addCulturalObject(java.lang.Object)
	 */
	@Override
	public CulturalObject addCulturalObject(CulturalObject culturalObject) {
		return culturalObjectRepository.save(culturalObject);
	}
	
	@Override
	public void stopEnrichingBatch(String batchId) {
		culturalObjectRepository.stopEnrichingBatch(batchId);
	}

	@Override
	public TagDTO addTag(TagDTO tagDto) throws ObjectNotFoundException {
		LocalizedString tagValue = new LocalizedString(tagDto.getLanguage(), tagDto.getValue());
		Long culturalObjectId = tagDto.getCulturalObjectId();
		CulturalObject co = culturalObjectRepository.findOne(culturalObjectId);
        Tag tag = tagRepository.findByValueAndCulturalObjectId(tagDto.getLanguage(), tagDto.getValue(), co);
        if(co == null){
            throw new ObjectNotFoundException();
        }
		if(tag != null){
			tag.setHitScore(tag.getHitScore()+1);
			tagRepository.save(tag);
		}else{
			tag = new Tag();
			tag.setValue(tagValue);
            tag.setCulturalObject(co);
			tag.setHitScore(Long.valueOf(0));
			tagRepository.save(tag);
		}
		return TagDTO.toDto(tag);
	}
	
	@Override
	public PageableTagsDTO listTags(Date fromDate, Date untilDate, String batchId) {
	    if(fromDate == null || untilDate == null) throw new NullPointerException();
	    Integer total = tagRepository.countTagsForEnrichment(fromDate, untilDate, batchId);
	    List<Tag> tags = tagRepository.findTagsForEnrichment(fromDate, untilDate, batchId, 0, DEFAULT_PAGE_SIZE);
	    List<TagDTO> tagList = new ArrayList<>();
	    tags.stream().forEach(tag -> tagList.add(TagDTO.toDto(tag)));
	    String token = (total.compareTo(Integer.valueOf(DEFAULT_PAGE_SIZE))>0) ? generateToken(new TokenAttr(fromDate,untilDate,batchId,tags.size())) : "";
        return new PageableTagsDTO(tags.size(), total, batchId, token, tagList);
	}
	
    @Override
	public PageableTagsDTO listTags(String resumptionToken) throws TokenExpiredException{
	    TokenAttr attr = parseToken(resumptionToken);
        Integer total = tagRepository.countTagsForEnrichment(attr.getFromDate(), attr.getUntilDate(), attr.getBatchId());
        List<Tag> tags = tagRepository.findTagsForEnrichment(attr.fromDate, attr.untilDate, attr.batchId, attr.getCursor(), DEFAULT_PAGE_SIZE);
        List<TagDTO> tagList = new ArrayList<TagDTO>();
        tags.forEach(tag -> tagList.add(TagDTO.toDto(tag)));
        String token = (total.compareTo(attr.getCursor()+tags.size())>0) ? generateToken(new TokenAttr(attr.fromDate,attr.untilDate,attr.getBatchId(),attr.getCursor()+DEFAULT_PAGE_SIZE)) :"";
        return new PageableTagsDTO(attr.getCursor()+tags.size(), total, attr.batchId, token, tagList);
	    
	}

    private String generateToken(TokenAttr attr) {
        return new StringBuilder().append(new Date().getTime())
                .append(":").append(attr.getFromDate().getTime())
                .append(":").append(attr.getUntilDate().getTime())
                .append(":").append(attr.getBatchId()).append(":").append(attr.getCursor()).toString();
    }
	
    private TokenAttr parseToken(String token) throws TokenExpiredException {
        String[] parsed = token.split(":");
        Date createdDate = Date.from(Instant.ofEpochMilli(Long.parseLong(parsed[0])));
        if(Instant.now().minus(EXPIRATION).isAfter(createdDate.toInstant())){
            throw new TokenExpiredException();
        }
        return new TokenAttr(Date.from(Instant.ofEpochMilli(Long.parseLong(parsed[1]))), Date.from(Instant.ofEpochMilli(Long.parseLong(parsed[2]))), parsed[3], Integer.parseInt(parsed[4]));   
    }
    
    private class TokenAttr {
        private Date fromDate;
        private Date untilDate;
        private String batchId;
        private int number;

        private TokenAttr(Date fromDate, Date untilDate, String batchId, int number) {
            super();
            this.fromDate = fromDate;
            this.untilDate = untilDate;
            this.batchId = batchId;
            this.number = number;
        }

        public Date getFromDate() {
            return fromDate;
        }

        public Date getUntilDate() {
            return untilDate;
        }

        public String getBatchId() {
            return batchId;
        }

        public int getCursor() {
            return number;
        }
    }    
}
