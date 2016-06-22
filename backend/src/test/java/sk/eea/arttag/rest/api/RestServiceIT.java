package sk.eea.arttag.rest.api;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import sk.eea.arttag.ArttagApp;
import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.model.LocalizedString;
import sk.eea.arttag.model.Tag;
import sk.eea.arttag.repository.CulturalObjectRepository;
import sk.eea.arttag.repository.TagRepository;
import sk.eea.arttag.rest.api.ResultMessageDTO.Status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ArttagApp.class)
@WebIntegrationTest
public class RestServiceIT {

    private static JerseyClient client;
    private static ObjectMapper objectMapper;

    @Autowired
    private CulturalObjectRepository coRepository;

    @Autowired
    private TagRepository tagRepository;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        client = JerseyClientBuilder.createClient().register(Logger.getLogger(RestServiceIT.class.getName()));
        objectMapper = new ObjectMapper();
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testAddTag() {
        CulturalObject co = coRepository.save(CulturalObjectDTO.toCulturalObject(generateCO()));
        try {
            TagDTO tagDto = new TagDTO();
            tagDto.setCulturalObjectId(co.getId());
            tagDto.setLanguage("sk");
            tagDto.setValue("new tag");
            WebTarget target = client.target("http://localhost:8080").path("/api/tag/add");
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .buildPost(Entity.entity(objectMapper.writeValueAsString(tagDto), MediaType.APPLICATION_JSON))
                    .invoke();
            if (response.getStatus() != 201) {
                fail("Error: " + response.getEntity());
            }
            ResultMessageDTO result = objectMapper.readValue(response.readEntity(String.class), ResultMessageDTO.class);
            if (result != null && result.getMessage() != null) {
                Long tagId = Long.parseLong(result.getMessage().substring(4));
                Tag tag = tagRepository.findOne(tagId);
                assertEquals(tagDto.getLanguage(), tag.getValue().getLanguage());
                assertEquals(tagDto.getValue(), tag.getValue().getValue());
                assertEquals(tagDto.getCulturalObjectId(), tag.getCulturalObject().getId());
            } else {
                fail("No Tag entity returned");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception was thrown");
        } finally {
            coRepository.delete(co);
        }
    }

    @Test
    public void testAddCulturalObject() {
        try {
            CulturalObjectDTO culturalObject = generateCO();
            WebTarget target = client.target("http://localhost:8080").path("/api/cultural/add");
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .buildPost(
                            Entity.entity(objectMapper.writeValueAsString(culturalObject), MediaType.APPLICATION_JSON))
                    .invoke();
            if (response.getStatus() != 201) {
                fail("Error: " + response.getEntity());
            }
            ResultMessageDTO result = objectMapper.readValue(response.readEntity(String.class), ResultMessageDTO.class);
            if (result != null && result.getMessage() != null) {
                Long id = Long.parseLong(result.getMessage().substring(4));
                CulturalObject co = coRepository.findOne(id);
                assertEquals(culturalObject.getAuthor(), co.getAuthor());
                assertEquals(culturalObject.getBatchId(), co.getBatchId());
                assertEquals(culturalObject.getExternalId(), co.getExternalId());
                assertEquals(culturalObject.getExternalUrl(), co.getExternalUrl());
                assertEquals(culturalObject.getImagePath(), co.getImagePath());
                assertEquals(culturalObject.getDescription().get("sk"), co.getDescription().get(0).getValue());
                assertEquals(Boolean.FALSE, co.isActive());
                coRepository.delete(co);
            } else {
                fail("No CO entity returned.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception was thrown");
        }
    }

    @Test
    public void testRemoveBatch() {
        CulturalObject co = CulturalObjectDTO.toCulturalObject(generateCO());
        co.setActive(Boolean.TRUE);
        coRepository.save(co);
        try {
            WebTarget target = client.target("http://localhost:8080").path("/api/batch/remove/")
                    .path(co.getBatchId());
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .buildDelete().invoke();
            if (response.getStatus() != HttpStatus.OK.value()) {
                fail("Invalid request " + response.getStatusInfo().getReasonPhrase());
            }
            ResultMessageDTO result = objectMapper.readValue(response.readEntity(String.class), ResultMessageDTO.class);
            if (result == null || result.getStatus().equals(Status.FAILED)) {
                fail("Missing result or result is FAILED.");
            }
            co = coRepository.findOne(co.getId());
            assertEquals(Boolean.FALSE, co.isActive());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception was thrown");
        } finally {
            coRepository.delete(co);
        }
    }

    @Test
    public void testListTagsStringStringString() {
        String fromDate = RestService.FORMATTER.format(Instant.now());
        CulturalObject co = CulturalObjectDTO.toCulturalObject(generateCO());
        co.setActive(Boolean.TRUE);
        co = coRepository.save(co);
        addTags(co, 51);
        String untilDate = RestService.FORMATTER.format(Instant.now());
        try{
            WebTarget target = client.target("http://localhost:8080").path("/api/tag/list")
                    .queryParam("batchId", co.getBatchId())
                    .queryParam("from", fromDate)
                    .queryParam("untilDate", untilDate);
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != HttpStatus.OK.value()) {
                fail("Invalid request " + response.getStatusInfo().getReasonPhrase());
            }
            PageableTagsDTO tags = objectMapper.readValue(response.readEntity(String.class), PageableTagsDTO.class);
            assertNotNull(tags);
            assertNotNull(tags.getResumptionToken());
            assertNotNull(tags.getTags());
            assertEquals(Integer.valueOf(50), tags.getPosition());
            assertEquals(Integer.valueOf(51), tags.getTotalCount());
            assertEquals(co.getBatchId(), tags.getBatchId());
            assertEquals(Integer.valueOf(50),Integer.valueOf(tags.getTags().size()));
        }catch(Exception e){
            e.printStackTrace();
            fail("Exception was thrown");            
        }finally{
            coRepository.delete(co);
        }
    }

    @Test
    public void testListTagsString() {
        String fromDate = RestService.FORMATTER.format(Instant.now());
        CulturalObject co = CulturalObjectDTO.toCulturalObject(generateCO());
        co.setActive(Boolean.TRUE);
        co = coRepository.save(co);
        addTags(co, 51);
        String untilDate = RestService.FORMATTER.format(Instant.now());
        try{
            WebTarget target = client.target("http://localhost:8080").path("/api/tag/list")
                    .queryParam("batchId", co.getBatchId())
                    .queryParam("from", fromDate)
                    .queryParam("untilDate", untilDate);
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != HttpStatus.OK.value()) {
                fail("Invalid request " + response.getStatusInfo().getReasonPhrase());
            }
            PageableTagsDTO tags = objectMapper.readValue(response.readEntity(String.class), PageableTagsDTO.class);
            assertNotNull(tags.getResumptionToken());
            target = client.target("http://localhost:8080").path("/api/tag/listNext")
                    .queryParam("resumptionToken", tags.getResumptionToken());
            response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != HttpStatus.OK.value()) {
                fail("Invalid request " + response.getStatusInfo().getReasonPhrase());
            }
            tags = objectMapper.readValue(response.readEntity(String.class), PageableTagsDTO.class);
            assertNotNull(tags);
            assertEquals("",tags.getResumptionToken());
            assertNotNull(tags.getTags());
            assertEquals(Integer.valueOf(51), tags.getPosition());
            assertEquals(Integer.valueOf(51), tags.getTotalCount());
            assertEquals(co.getBatchId(), tags.getBatchId());
            assertEquals(Integer.valueOf(1),Integer.valueOf(tags.getTags().size()));
        }catch(Exception e){
            e.printStackTrace();
            fail("Exception was thrown");            
        }finally{
            coRepository.delete(co);
        }
    }

    @Test
    public void testStartEnrichment(){
        CulturalObject co = CulturalObjectDTO.toCulturalObject(generateCO());
        co = coRepository.save(co);
        try{
            WebTarget target = client.target("http://localhost:8080").path("/api/batch/publish").queryParam("batchId",co.getBatchId());
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
            if(response.getStatus() != HttpStatus.OK.value()){
                fail("Response is: " + response.getStatus());
            }
            ResultMessageDTO result = objectMapper.readValue(response.readEntity(String.class), ResultMessageDTO.class);
            if(result.getStatus().equals(Status.SUCCESS)){
                co = coRepository.findOne(co.getId());
                assertEquals(Boolean.TRUE, co.isActive());
            }else{
                fail("Not processed correctly: " + result.getMessage());
            }
        }catch(Exception e){
            e.printStackTrace();
            fail("Exception was thrown");
        }
    }
    
    public CulturalObjectDTO generateCO() {
        CulturalObjectDTO culturalObject = new CulturalObjectDTO();
        culturalObject.setAuthor("Author");
        culturalObject.setBatchId("test");
        HashMap<String, String> descriptions = new HashMap<String, String>();
        descriptions.put("sk", "Test sk");
        culturalObject.setDescription(descriptions);
        culturalObject.setExternalId("externalId");
        culturalObject.setExternalUrl("http://localhost:8080/");
        culturalObject.setImagePath("http://localhost:8080/test_image.jpeg");
        return culturalObject;
    }
    
    public void addTags(CulturalObject co, Integer count){
        for(int i=0;i<count;i++){
            Tag tag = new Tag();
            tag.setCreated(new Date());
            tag.setCulturalObject(co);
            tag.setHitScore(1l);
            tag.setValue(new LocalizedString("sk", "tag"+i));
            tagRepository.save(tag);
        }
    }
}
