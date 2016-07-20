package sk.eea.arttag.rest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sk.eea.arttag.TestApp;
import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.model.LocalizedString;
import sk.eea.arttag.model.Tag;
import sk.eea.arttag.repository.CulturalObjectRepository;
import sk.eea.arttag.repository.TagRepository;
import sk.eea.arttag.rest.api.ResultMessageDTO.Status;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Integration test for RestServiceImpl class.
 * Integration test consists of basic CRUD operations. Because of that we need to assure the execution order of the test methods.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApp.class)
@WebIntegrationTest({"server.port=9180"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RestServiceIT {

    private static JerseyClient client;
    private static ObjectMapper objectMapper;

    private static CulturalObject culturalObject;

    @Autowired
    private CulturalObjectRepository coRepository;

    @Autowired
    private TagRepository tagRepository;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("api", "admin");
        client = JerseyClientBuilder.createClient().register(Logger.getLogger(RestServiceIT.class.getName())).register(feature);
        objectMapper = new ObjectMapper();
    }

    /* CREATE */
    @Test
    public void test_AA_AddCulturalObject() {
        try {
            CulturalObjectDTO culturalObjectDTO = generateCO();
            WebTarget target = client.target("http://localhost:9180").path("/api/cultural/add").path("/").path(String.valueOf(350l));
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .buildPost(
                            Entity.entity(objectMapper.writeValueAsString(culturalObjectDTO), MediaType.APPLICATION_JSON))
                    .invoke();
            if (response.getStatus() != 201) {
                fail("Error: " + response.getEntity());
            }
            ResultMessageDTO result = objectMapper.readValue(response.readEntity(String.class), ResultMessageDTO.class);
            if (result != null && result.getMessage() != null) {
                Long id = Long.parseLong(result.getMessage().split(":", 2)[1].trim());
                culturalObject = coRepository.findOne(id);
                assertEquals(culturalObject.getAuthor(), culturalObjectDTO.getAuthor());
                assertEquals(culturalObject.getBatchId(), Long.valueOf(350l));
                assertEquals(culturalObject.getExternalId(), culturalObjectDTO.getExternalId());
                assertEquals(culturalObject.getExternalUrl(), culturalObjectDTO.getExternalUrl());
                assertEquals(culturalObject.getExternalSource(), culturalObjectDTO.getExternalSource());
                assertEquals(culturalObject.getDescription().get(0).getValue(), culturalObjectDTO.getDescription().get("sk"));                
                assertEquals(Boolean.FALSE, culturalObject.isActive());
            } else {
                fail("No CO entity returned.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception was thrown");
        }
    }

    @Test
    public void test_A_AddTag() {
        try {
            TagDTO tagDto = new TagDTO();
            tagDto.setCulturalObjectId(culturalObject.getId());
            tagDto.setLanguage("sk");
            tagDto.setValue("new tag");
            WebTarget target = client.target("http://localhost:9180").path("/api/tag/add");
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .buildPost(Entity.entity(objectMapper.writeValueAsString(tagDto), MediaType.APPLICATION_JSON))
                    .invoke();
            if (response.getStatus() != HttpStatus.CREATED.value()) {
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
        }
    }

    /* READ */
    @Test
    public void test_B_ListTagsStringStringString() {
        String fromDate = RestService.FORMATTER.format(Instant.now());
        culturalObject.setActive(Boolean.TRUE);
        culturalObject = coRepository.save(culturalObject);
        addTags(culturalObject, 51);
        String untilDate = RestService.FORMATTER.format(Instant.now());
        try {
            WebTarget target = client.target("http://localhost:9180").path("/api/tag/list")
                    .queryParam("batchId", culturalObject.getBatchId())
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
            assertEquals(culturalObject.getBatchId(), tags.getBatchId());
            assertEquals(Integer.valueOf(50), Integer.valueOf(tags.getTags().size()));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception was thrown");
        } finally {
            // restore state
            culturalObject.setActive(false);
            culturalObject = coRepository.save(culturalObject);
        }
    }

    @Test
    public void test_B_ListTagsString() {
        String fromDate = RestService.FORMATTER.format(Instant.now());
        culturalObject.setActive(Boolean.TRUE);
        culturalObject = coRepository.save(culturalObject);
        addTags(culturalObject, 51);
        String untilDate = RestService.FORMATTER.format(Instant.now());
        try {
            WebTarget target = client.target("http://localhost:9180").path("/api/tag/list")
                    .queryParam("batchId", culturalObject.getBatchId())
                    .queryParam("from", fromDate)
                    .queryParam("untilDate", untilDate);
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != HttpStatus.OK.value()) {
                fail("Invalid request " + response.getStatusInfo().getReasonPhrase());
            }
            PageableTagsDTO tags = objectMapper.readValue(response.readEntity(String.class), PageableTagsDTO.class);
            assertNotNull(tags.getResumptionToken());
            target = client.target("http://localhost:9180").path("/api/tag/listNext")
                    .queryParam("resumptionToken", tags.getResumptionToken());
            response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != HttpStatus.OK.value()) {
                fail("Invalid request " + response.getStatusInfo().getReasonPhrase());
            }
            tags = objectMapper.readValue(response.readEntity(String.class), PageableTagsDTO.class);
            assertNotNull(tags);
            assertEquals("", tags.getResumptionToken());
            assertNotNull(tags.getTags());
            assertEquals(Integer.valueOf(51), tags.getPosition());
            assertEquals(Integer.valueOf(51), tags.getTotalCount());
            assertEquals(culturalObject.getBatchId(), tags.getBatchId());
            assertEquals(Integer.valueOf(1), Integer.valueOf(tags.getTags().size()));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception was thrown");
        } finally {
            // restore state
            culturalObject.setActive(false);
            culturalObject = coRepository.save(culturalObject);
        }
    }

    /* UPDATE */
    @Test
    public void test_C_StartEnrichment() {
        try {
            WebTarget target = client.target("http://localhost:9180").path("/api/batch/publish/" + culturalObject.getBatchId());
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != HttpStatus.ACCEPTED.value()) {
                fail("Response is: " + response.getStatus());
            }
            ResultMessageDTO result = objectMapper.readValue(response.readEntity(String.class), ResultMessageDTO.class);
            if (result.getStatus().equals(Status.SUCCESS)) {

                culturalObject = coRepository.findOne(culturalObject.getId());
                assertTrue(culturalObject.isActive());

                Path internalFile = Paths.get(culturalObject.getInternalFileSystemPath());
                assertTrue(Files.exists(internalFile));

                // clean up
                FileUtils.deleteDirectory(internalFile.getParent().toFile());
            } else {
                fail("Not processed correctly: " + result.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception was thrown");
        }
    }

    /* DELETE */
    @Test
    public void test_D_RemoveBatch() {
        try {
            WebTarget target = client.target("http://localhost:9180").path("/api/batch/remove/").path(culturalObject.getBatchId().toString());
            Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .buildDelete().invoke();
            if (response.getStatus() != HttpStatus.OK.value()) {
                fail("Invalid request " + response.getStatusInfo().getReasonPhrase());
            }
            ResultMessageDTO result = objectMapper.readValue(response.readEntity(String.class), ResultMessageDTO.class);
            if (result == null || result.getStatus().equals(Status.FAILED)) {
                fail("Missing result or result is FAILED.");
            }
            culturalObject = coRepository.findOne(culturalObject.getId());
            assertEquals(Boolean.FALSE, culturalObject.isActive());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception was thrown");
        } finally {
            coRepository.delete(culturalObject);
        }
    }

    private CulturalObjectDTO generateCO() {
        CulturalObjectDTO culturalObject = new CulturalObjectDTO();
        culturalObject.setAuthor("Author");
        HashMap<String, String> descriptions = new HashMap<>();
        descriptions.put("sk", "Test sk");
        culturalObject.setDescription(descriptions);
        culturalObject.setExternalId("externalId");
        culturalObject.setExternalUrl("http://localhost:9180/");
        culturalObject.setExternalSource("http://localhost:9180/img/test_image.jpeg");
        return culturalObject;
    }

    private void addTags(CulturalObject co, Integer count) {
        for (int i = 0; i < count; i++) {
            Tag tag = new Tag();
            tag.setCreated(new Date());
            tag.setCulturalObject(co);
            tag.setHitScore(1f);
            tag.setValue(new LocalizedString("sk", "tag" + i));
            tagRepository.save(tag);
        }
    }
}
