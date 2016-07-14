/**
 *
 */
package sk.eea.arttag.rest.api;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.IllegalFormatCodePointException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.repository.CulturalObjectRepository;
import sk.eea.arttag.rest.api.ResultMessageDTO.Status;
import sk.eea.arttag.service.StoreService;

/**
 * Provides API for public use.
 *
 * @author Maros Strmensky
 */
@RestController
@Transactional
public class RestService {

    private static final Logger LOG = LoggerFactory.getLogger(RestService.class);

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @Autowired
    private StoreService storeService;

    @Autowired
    private CulturalObjectRepository culturalObjectRepository;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper mapper;

    @ApiOperation(consumes = "application/json", httpMethod = "POST", produces = "application/json", value = "Used to add tags to the object.")
    @RequestMapping(value = "/api/tag/add", method = RequestMethod.POST)
    public ResponseEntity<String> addTag(@Valid @RequestBody TagDTO tagDto) throws Exception {
        ResultMessageDTO result = new ResultMessageDTO();
        try {
            TagDTO object = storeService.addTag(tagDto);
            result.setStatus(Status.SUCCESS);
            result.setMessage("id: {0}", object.getId().toString());
            return new ResponseEntity<String>(mapper.writeValueAsString(result), HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Error adding tag.", e);
            result.setStatus(Status.FAILED);
            result.setMessage("Adding tag: \'{0}\' to CO: \'{1}\' has failed with message: {2}", tagDto.getValue(), tagDto.getCulturalObjectId(), e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(consumes = "application/json", httpMethod = "POST", produces = "application/json", value = "Used to add cultural object.")
    @RequestMapping(value = "/api/cultural/add/{batchId}", method = RequestMethod.POST)
    public ResponseEntity<String> addCulturalObject(@PathVariable Long batchId, @Valid @RequestBody CulturalObjectDTO culturalObjectDTO) throws Exception {
        ResultMessageDTO result = new ResultMessageDTO();
        try {
            CulturalObject object = storeService.addCulturalObject(batchId, CulturalObjectDTO.toCulturalObject(culturalObjectDTO));
            result.setStatus(Status.SUCCESS);
            result.setMessage("id: {0}", object.getId().toString());
            return new ResponseEntity<String>(mapper.writeValueAsString(result), HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Error adding cultural object", e);
            result.setStatus(Status.FAILED);
            result.setMessage("Adding cultural object within batch: {0} with external ID: {1} has failed with message: {2}", batchId,
                    culturalObjectDTO.getExternalId(), e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(consumes = "application/json", httpMethod = "POST", produces = "application/json", value = "Used to create batch collection. Returns batchID which is required for adding Cultural object.")
    @RequestMapping(value = "/api/batch/create", method = RequestMethod.POST)
    public ResponseEntity<String> createBatch() throws Exception {
        ResultMessageDTO result = new ResultMessageDTO();
        try {
            Long batchId = culturalObjectRepository.getNextSequence();
            result.setStatus(Status.SUCCESS);
            result.setMessage("id: {0}", batchId.toString());
            return new ResponseEntity<>(mapper.writeValueAsString(result), HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Error creating batch.", e);
            result.setStatus(Status.FAILED);
            result.setMessage("Creating batch has failed with message: {0}", e.toString());
            return new ResponseEntity<>(mapper.writeValueAsString(result),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(httpMethod = "DELETE", value = "Used to remove batch from enrichment process.")
    @RequestMapping(value = "/api/batch/remove/{batchId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeBatch(@PathVariable("batchId") Long batchId) throws Exception {
        ResultMessageDTO result = new ResultMessageDTO();
        try {
            storeService.stopEnrichingBatch(batchId);
            result.setStatus(Status.SUCCESS);
            result.setMessage("Batch: {0} removed from enrichment.", batchId.toString());
            return new ResponseEntity<String>(mapper.writeValueAsString(result), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error removing batch", e);
            result.setStatus(Status.FAILED);
            result.setMessage("Removing batch: {0} from enrichment process has failed with message: {1}", batchId, e.toString());
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(httpMethod = "GET", value = "Used to get new tags for batch")
    @RequestMapping(value = "/api/tag/list", method = RequestMethod.GET)
    public ResponseEntity<String> listTags(@QueryParam("from") String from, @QueryParam("until") String until, @QueryParam("batchId") Long batchId)
            throws Exception {
        try {
            if (batchId == null) {
                throw new IllegalArgumentException("batchId parameter needs to be provided!");
            }

            Date fromDate = parseDate(from, Date.from(Instant.EPOCH));
            Date untilDate = parseDate(until, Date.from(Instant.now()));

            PageableTagsDTO pageableTags = storeService.listTags(fromDate, untilDate, batchId);
            return new ResponseEntity<>(mapper.writeValueAsString(pageableTags), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            LOG.error("Error listing batch", e);
            return new ResponseEntity<>(mapper.writeValueAsString(new ErrorResponseDTO(e.getMessage())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOG.error("Error listing batch", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(httpMethod = "GET", value = "Used to get new tags for batch")
    @RequestMapping(value = "/api/tag/listNext", method = RequestMethod.GET)
    public ResponseEntity<String> listTags(@QueryParam("resumptionToken") String resumptionToken) throws Exception {
        try {
            PageableTagsDTO pageableTags = storeService.listTags(resumptionToken);
            return new ResponseEntity<String>(mapper.writeValueAsString(pageableTags), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error removing batch", e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(httpMethod = "GET", value = "Used to start enritching batch")
    @RequestMapping(value = "/api/batch/publish/{batchId}", method = RequestMethod.GET)
    public ResponseEntity<String> publishBatch(@PathVariable Long batchId) {
        ResultMessageDTO result = new ResultMessageDTO();
        try {
            storeService.publish(batchId);
            result.setStatus(Status.SUCCESS);
            return new ResponseEntity<>(mapper.writeValueAsString(result), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error publishing batch", e);
            result.setStatus(Status.FAILED);
            result.setMessage("Publishing batch: {0} has failed with message: {1}", batchId.toString(), e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Date parseDate(String date, Date defaultDate) {
        if (date == null)
            return defaultDate;
        return Date.from(Instant.from(RestService.FORMATTER.parse(date)));
    }
    
}
