package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sample;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;

/**
 * Integration tests for working with sequence files and samples.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleSequenceFilesIntegrationTest {

    @Test
    public void testAddSequenceFileToSample() throws IOException {
        String sampleUri = "http://localhost:8080/api/projects/c4893f30-b054-46e5-8ebe-ed1b295dfa38" +
                "/samples/07ac0624-8f04-43ba-b45f-e6d65a8bd6ba";
        Response response = expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
        String sampleBody = response.getBody().asString();
        String sequenceFileUri = from(sampleBody).getString("resource.links.find{it.rel == 'sample/sequenceFiles'}.href");
        // prepare a file for sending to the server
        Path sequenceFile = Files.createTempFile(null, null);
        Files.write(sequenceFile, ">test read\nACGTACTCATG".getBytes());

        Response r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("file", sequenceFile.toFile()).expect().statusCode(HttpStatus.CREATED.value())
                .when().post(sequenceFileUri);

        // check that the location and link headers were created:
        String location = r.getHeader(HttpHeaders.LOCATION);

        assertNotNull(location);
        assertTrue(location.matches("http://localhost:8080/api/projects/[a-f0-9\\-]+/samples/[a-f0-9\\-]+/sequenceFiles/[a-f0-9\\-]+"));

        // clean up
        Files.delete(sequenceFile);
    }

    @Test
    public void testAddExistingSequenceFileToSample() throws IOException {
        // for now, add a sequence file to another sample
        String projectUri = "http://localhost:8080/api/projects/c4893f30-b054-46e5-8ebe-ed1b295dfa38";
        String sampleUri = projectUri + "/samples/07ac0624-8f04-43ba-b45f-e6d65a8bd6ba";
        Response response = expect().statusCode(HttpStatus.OK.value()).when().get(projectUri);
        String projectBody = response.getBody().asString();
        String sequenceFileUri = from(projectBody).getString("resource.links.find{it.rel == 'project/sequenceFiles'}.href");
        // prepare a file for sending to the server
        Path sequenceFile = Files.createTempFile(null, null);
        Files.write(sequenceFile, ">test read\nACGTACTCATG".getBytes());

        Response r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("file", sequenceFile.toFile()).expect().statusCode(HttpStatus.CREATED.value())
                .when().post(sequenceFileUri);

        // figure out what the identifier for that sequence file is
        String location = r.getHeader(HttpHeaders.LOCATION);
        String identifier = location.substring(location.lastIndexOf('/') + 1);
        Map<String, String> existingSequenceFile = new HashMap<>();
        existingSequenceFile.put("sequenceFileId", identifier);

        // now figure out where to post the sequence file to add it to the sample
        String sampleBody = expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri).getBody().asString();
        sequenceFileUri = from(sampleBody).getString("resource.links.find{it.rel == 'sample/sequenceFiles'}.href");

        // add the sequence file to the sample
        r = given().body(existingSequenceFile).expect().statusCode(HttpStatus.CREATED.value()).when()
                .post(sequenceFileUri);
        location = r.getHeader(HttpHeaders.LOCATION);

        assertNotNull(location);
        assertTrue(location.matches("http://localhost:8080/api/projects/[a-f0-9\\-]+/samples/[a-f0-9\\-]+/sequenceFiles/[a-f0-9\\-]+"));

        // confirm that the sequence file was removed from the project
        expect().body("relatedResources.sequenceFiles.identifier", not(hasItem(identifier)))
                .when().get(projectUri).getBody().asString();
    }

    @Test
    public void testRemoveSequenceFileFromSample() throws IOException {
        // for now, add a sequence file to the sample so that we can remove it
        String projectUri = "http://localhost:8080/api/projects/c4893f30-b054-46e5-8ebe-ed1b295dfa38";
        String sampleUri = projectUri + "/samples/07ac0624-8f04-43ba-b45f-e6d65a8bd6ba";
        Response response = expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
        String sampleBody = response.getBody().asString();
        String sequenceFileUri = from(sampleBody).getString("resource.links.find{it.rel == 'sample/sequenceFiles'}.href");
        // prepare a file for sending to the server
        Path sequenceFile = Files.createTempFile(null, null);
        Files.write(sequenceFile, ">test read\nACGTACTCATG".getBytes());

        Response r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("file", sequenceFile.toFile()).expect().statusCode(HttpStatus.CREATED.value())
                .when().post(sequenceFileUri);

        String location = r.getHeader(HttpHeaders.LOCATION);
        String identifier = location.substring(location.lastIndexOf('/') + 1);

        r = expect().statusCode(HttpStatus.OK.value()).when().delete(location);
        String responseBody = r.getBody().asString();
        String sampleLocation = from(responseBody).getString("resource.links.find{it.rel == 'sample'}.href");
        String sequenceFileLocation = from(responseBody).getString("resource.links.find{it.rel == 'project/sequenceFile'}.href");

        assertNotNull(sampleLocation);
        assertEquals(sampleUri, sampleLocation);

        assertNotNull(sequenceFileLocation);
        assertEquals(projectUri + "/sequenceFiles/" + identifier, sequenceFileLocation);
    }
}
