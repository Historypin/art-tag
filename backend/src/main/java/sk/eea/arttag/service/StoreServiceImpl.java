/**
 *
 */
package sk.eea.arttag.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import sk.eea.arttag.ApplicationProperties;
import sk.eea.arttag.helpers.FilesHelper;
import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.model.LocalizedString;
import sk.eea.arttag.model.Tag;
import sk.eea.arttag.repository.CulturalObjectRepository;
import sk.eea.arttag.repository.TagRepository;
import sk.eea.arttag.rest.api.PageableTagsDTO;
import sk.eea.arttag.rest.api.TagDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Maros Strmensky
 */
public class StoreServiceImpl implements StoreService {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private static final TemporalAmount EXPIRATION = Duration.of(30, ChronoUnit.MINUTES);

    private static final Logger LOG = LoggerFactory.getLogger(StoreService.class);

    @Autowired
    private CulturalObjectRepository culturalObjectRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Value("${working.dir}")
    private String workingDir;

    /* (non-Javadoc)
     * @see sk.eea.arttag.service.StoreService#addCulturalObject(java.lang.Object)
     */
    @Override
    public CulturalObject addCulturalObject(CulturalObject culturalObject) {
        return culturalObjectRepository.save(culturalObject);
    }

    @Override
    public void stopEnrichingBatch(Long batchId) {
        culturalObjectRepository.stopEnrichingBatch(batchId);
    }

    @Override
    public TagDTO addTag(TagDTO tagDto) throws ObjectNotFoundException {
        LocalizedString tagValue = new LocalizedString(tagDto.getLanguage(), tagDto.getValue());
        Long culturalObjectId = tagDto.getCulturalObjectId();
        CulturalObject co = culturalObjectRepository.findOne(culturalObjectId);
        Tag tag = tagRepository.findByValueAndCulturalObjectId(tagDto.getLanguage(), tagDto.getValue(), co);
        if (co == null) {
            throw new ObjectNotFoundException();
        }
        if (tag != null) {
            tag.setHitScore(tag.getHitScore() + 1);
            tagRepository.save(tag);
        } else {
            tag = new Tag();
            tag.setValue(tagValue);
            tag.setCulturalObject(co);
            tag.setHitScore(0L);
            tagRepository.save(tag);
        }
        return TagDTO.toDto(tag);
    }

    @Override
    public PageableTagsDTO listTags(Date fromDate, Date untilDate, Long batchId) {
        if (fromDate == null || untilDate == null)
            throw new NullPointerException();
        Integer total = tagRepository.countTagsForEnrichment(fromDate, untilDate, batchId);
        List<Tag> tags = tagRepository.findTagsForEnrichment(fromDate, untilDate, batchId, 0, DEFAULT_PAGE_SIZE);
        List<TagDTO> tagList = new ArrayList<>();
        tags.stream().forEach(tag -> tagList.add(TagDTO.toDto(tag)));
        String token = (total.compareTo(DEFAULT_PAGE_SIZE) > 0) ?
                generateToken(new TokenAttr(fromDate, untilDate, batchId, tags.size())) : "";
        return new PageableTagsDTO(tags.size(), total, batchId, token, tagList);
    }

    @Override
    public PageableTagsDTO listTags(String resumptionToken) throws TokenExpiredException {
        TokenAttr attr = parseToken(resumptionToken);
        Integer total = tagRepository
                .countTagsForEnrichment(attr.getFromDate(), attr.getUntilDate(), attr.getBatchId());
        List<Tag> tags = tagRepository
                .findTagsForEnrichment(attr.fromDate, attr.untilDate, attr.batchId, attr.getCursor(), DEFAULT_PAGE_SIZE);
        List<TagDTO> tagList = new ArrayList<TagDTO>();
        tags.forEach(tag -> tagList.add(TagDTO.toDto(tag)));
        String token = (total.compareTo(attr.getCursor() + tags.size()) > 0) ? generateToken(
                new TokenAttr(attr.fromDate, attr.untilDate, attr.getBatchId(), attr.getCursor() + DEFAULT_PAGE_SIZE)) : "";
        return new PageableTagsDTO(attr.getCursor() + tags.size(), total, attr.batchId, token, tagList);

    }

    @Override
    public void publish(Long batchId) throws Exception {
        final List<CulturalObject> coList = culturalObjectRepository.findByBatchId(batchId);
        Path batchDirectory = Paths.get(workingDir).resolve(batchId.toString());
        try {
            if (batchDirectory.toFile().exists())
                throw new Exception("Working file for batch exists. This shouldn't happen!");
            batchDirectory = Files.createDirectories(batchDirectory);
            List<CulturalObject> failedObjects = new ArrayList<>();
            for (CulturalObject co : coList) {
                final String targetFileName = String.format("%d.jpeg", co.getId());
                final Path targetFilePath = batchDirectory.resolve(targetFileName);
                final boolean downloadSuccess = FilesHelper.download(co.getExternalSource(), targetFilePath);
                if (!downloadSuccess) {
                    failedObjects.add(co);
                } else {
                    final Path relativePath = Paths.get(workingDir).relativize(targetFilePath);
                    co.setInternalFileSystemPath(
                            Paths.get(applicationProperties.getCulturalObjectsFileSystemPath()).resolve(relativePath).toAbsolutePath().toString());

                    final String source = String.format("%s://%s/%s/%s/%s", applicationProperties.getHostnamePrefix(), applicationProperties.getHostname(),
                            applicationProperties.getCulturalObjectsPublicPath(), batchId, targetFileName);
                    co.setPublicSource(source);
                }
            }
            // TODO: add imagemagick transformation!
            FileUtils.copyDirectoryToDirectory(batchDirectory.toFile(), Paths.get(applicationProperties.getCulturalObjectsFileSystemPath()).toFile());

            coList.removeAll(failedObjects);
            coList.forEach(co -> co.setActive(Boolean.TRUE));
            culturalObjectRepository.save(coList);
        } finally {
            try {
                FileUtils.deleteDirectory(batchDirectory.toFile());
            } catch (IOException e) {
                LOG.error("Could not delete working directory: {}", batchDirectory);
            }
        }
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
        if (Instant.now().minus(EXPIRATION).isAfter(createdDate.toInstant())) {
            throw new TokenExpiredException();
        }
        return new TokenAttr(Date.from(Instant.ofEpochMilli(Long.parseLong(parsed[1]))),
                Date.from(Instant.ofEpochMilli(Long.parseLong(parsed[2]))), Long.parseLong(parsed[3]), Integer.parseInt(parsed[4]));
    }

    private class TokenAttr {
        private Date fromDate;
        private Date untilDate;
        private Long batchId;
        private int number;

        private TokenAttr(Date fromDate, Date untilDate, Long batchId, int number) {
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

        public Long getBatchId() {
            return batchId;
        }

        public int getCursor() {
            return number;
        }
    }
}
