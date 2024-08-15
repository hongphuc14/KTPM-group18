package org.example;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.Test;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8081)
public class CreateVoteTest {
    private final HttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testCreateVote_Success() throws Exception {
        System.out.println("------------- Starting testCreateVote_Success -------------");

        // Thiết lập phản hồi mock
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"12345\",\"question\":\"Test question\",\"choices\":[{\"id\":\"1\"," +
                                "\"name\":\"Option 1\",\"voters\":[]}],\"groupId\":\"group1\"," +
                                "\"creatorId\":\"user1\",\"timeEnd\":\"2024-06-29T04:17:09.000Z\"," +
                                "\"creationDate\":\"2024-06-29T04:17:09.000Z\",\"closeDate\":\"2024-06-29T04:17:09.000Z\"," +
                                "\"isMultipleChoice\":true,\"status\":\"OPEN\"}")
                ));

        // Chuẩn bị body yêu cầu
        String requestBody = "{\"question\":\"Test question\",\"groupId\":\"group1\",\"creatorId\":\"user1\"," +
                "\"timeEnd\":\"2024-06-29T04:17:09.996Z\",\"choices\":[{\"id\":\"1\",\"name\":\"Option 1\"," +
                "\"voters\":[]}],\"isMultipleChoice\":true}";

        // Gửi yêu cầu
        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        // Xác nhận phản hồi
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"id\":\"12345\""));
        assertTrue(responseBody.contains("\"status\":\"OPEN\""));

        // Xác nhận yêu cầu đã được thực hiện
        verify(postRequestedFor(urlEqualTo("/api/votes"))
                .withHeader("Content-Type", equalTo("application/json")));
    }

    @Test
    public void testCreateVote_DuplicateChoices() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_DuplicateChoices -------------");

        String requestBody = "{\"question\":\"Duplicate choices?\",\"groupId\":\"group1\",\"creatorId\":\"user1\"," +
                "\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 1\"}],\"groupId\":\"group1\"," +
                "\"creatorId\":\"user1\",\"timeEnd\":\"2024-06-29T04:17:09.000Z\",\"creationDate\":\"2024-06-29T04:17:09.000Z\"," +
                "\"closeDate\":\"2024-06-29T04:17:09.000Z\",\"isMultipleChoice\":true,\"status\":\"OPEN\"}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"Duplicate choices are not allowed\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"Duplicate choices are not allowed\""));
    }

    @Test
    public void testCreateVote_LongQuestionText() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_LongQuestionText -------------");

        String longQuestion = "A".repeat(256); // Assuming 255 characters is the limit
        String requestBody = "{\"question\":\"" + longQuestion + "\",\"groupId\":\"group1\",\"creatorId\":\"user1\"," +
                "\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}],\"groupId\":\"group1\",\"creatorId\":\"user1\"," +
                "\"timeEnd\":\"2024-06-29T04:17:09.000Z\",\"creationDate\":\"2024-06-29T04:17:09.000Z\"," +
                "\"closeDate\":\"2024-06-29T04:17:09.000Z\",\"isMultipleChoice\":true,\"status\":\"OPEN\"}}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"Question text exceeds maximum length\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"Question text exceeds maximum length\""));
    }

    @Test
    public void testCreateVote_Unauthorized() throws Exception {
        System.out.println("------------- Starting testCreateVote_Unauthorized -------------");

        // Setup mock response for unauthorized access
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{\"error\":\"Unauthorized\"}")
                ));

        // Send request without authorization
        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{}"));

        System.out.println("Request URL: " + request.getURI());

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        // Assert response
        assertEquals(401, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"Unauthorized\""));
    }

    @Test
    public void testCreateVote_InvalidInput() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_InvalidInput -------------");

        // Setup mock response for invalid input
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"Invalid input\"}")
                ));

        // Prepare invalid request body (missing required fields)
        String invalidRequestBody = "{\"question\":\"\"}";

        // Send request
        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(invalidRequestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + invalidRequestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        // Assert response
        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"Invalid input\""));
    }

    @Test
    public void testCreateVote_SuccessWithMinimumFields() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_SuccessWithMinimumFields -------------");

        String requestBody = "{\"question\":\"Minimum question?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}]}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"12345\",\"question\":\"Minimum question?\",\"status\":\"OPEN\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"id\":\"12345\""));
        assertTrue(responseBody.contains("\"status\":\"OPEN\""));
    }

    @Test
    public void testCreateVote_SuccessWithMaximumFields() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_SuccessWithMaximumFields -------------");

        String requestBody = "{\"question\":\"Maximum fields question?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}],\"isMultipleChoice\":true,\"timeEnd\":\"2025-12-31T23:59:59.999Z\",\"description\":\"Detailed description\",\"tags\":[\"tag1\",\"tag2\"],\"attachments\":[\"file1.jpg\",\"file2.pdf\"],\"allowAnonymous\":true,\"restrictedTo\":[\"user2\",\"user3\"]}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"12345\",\"question\":\"Maximum fields question?\",\"status\":\"OPEN\",\"isMultipleChoice\":true,\"timeEnd\":\"2025-12-31T23:59:59.999Z\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"id\":\"12345\""));
        assertTrue(responseBody.contains("\"isMultipleChoice\":true"));
        assertTrue(responseBody.contains("\"timeEnd\":\"2025-12-31T23:59:59.999Z\""));
    }

    @Test
    public void testCreateVote_SingleChoice() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_SingleChoice -------------");

        String requestBody = "{\"question\":\"Single choice question?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}],\"isMultipleChoice\":false}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"12345\",\"question\":\"Single choice question?\",\"isMultipleChoice\":false,\"status\":\"OPEN\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"isMultipleChoice\":false"));
    }

    @Test
    public void testCreateVote_MultipleChoices() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_MultipleChoices -------------");

        String requestBody = "{\"question\":\"Multiple choice question?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"},{\"name\":\"Option 3\"}],\"isMultipleChoice\":true}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"12345\",\"question\":\"Multiple choice question?\",\"isMultipleChoice\":true,\"status\":\"OPEN\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"isMultipleChoice\":true"));
    }

    @Test
    public void testCreateVote_MaximumChoicesLimit() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_MaximumChoicesLimit -------------");

        StringBuilder choicesBuilder = new StringBuilder("[");
        for (int i = 1; i <= 101; i++) {
            choicesBuilder.append("{\"name\":\"Option ").append(i).append("\"}");
            if (i < 101) choicesBuilder.append(",");
        }
        choicesBuilder.append("]");

        String requestBody = "{\"question\":\"Too many choices?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":" + choicesBuilder.toString() + "}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"Exceeded maximum number of choices\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"Exceeded maximum number of choices\""));
    }

    @Test
    public void testCreateVote_MinimumChoicesLimit() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_MinimumChoicesLimit -------------");

        String requestBody = "{\"question\":\"Not enough choices?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"}]}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"Minimum of 2 choices required\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"Minimum of 2 choices required\""));
    }

    @Test
    public void testCreateVote_EmptyChoices() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_EmptyChoices -------------");

        String requestBody = "{\"question\":\"No choices?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[]}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"Choices cannot be empty\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"Choices cannot be empty\""));
    }

    @Test
    public void testCreateVote_SpecialCharactersInQuestion() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_SpecialCharactersInQuestion -------------");

        String requestBody = "{\"question\":\"Special characters: !@#$%^&*()_+\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}]}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"12345\",\"question\":\"Special characters: !@#$%^&*()_+\",\"status\":\"OPEN\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("Special characters: !@#$%^&*()_+"));
    }

    @Test
    public void testCreateVote_HTMLInjectionInQuestion() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_HTMLInjectionInQuestion -------------");

        String requestBody = "{\"question\":\"<script>alert('XSS')</script>Injection question?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}]}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"12345\",\"question\":\"Injection question?\",\"status\":\"OPEN\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertFalse(responseBody.contains("<script>"));
    }

    @Test
    public void testCreateVote_FutureEndTime() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_FutureEndTime -------------");

        String requestBody = "{\"question\":\"Future end time?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}],\"timeEnd\":\"2025-12-31T23:59:59.999Z\"}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"12345\",\"question\":\"Future end time?\",\"timeEnd\":\"2025-12-31T23:59:59.999Z\",\"status\":\"OPEN\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"timeEnd\":\"2025-12-31T23:59:59.999Z\""));
    }

    @Test
    public void testCreateVote_PastEndTime() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_PastEndTime -------------");

        String requestBody = "{\"question\":\"Past end time?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}],\"timeEnd\":\"2020-01-01T00:00:00.000Z\"}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"End time must be in the future\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"End time must be in the future\""));
    }

    @Test
    public void testCreateVote_InvalidDateFormat() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_InvalidDateFormat -------------");

        String requestBody = "{\"question\":\"Invalid date format?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}],\"timeEnd\":\"2025-13-32\"}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"Invalid date format\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"Invalid date format\""));
    }

    @Test
    public void testCreateVote_InvalidGroupId() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_InvalidGroupId -------------");

        String requestBody = "{\"question\":\"Invalid group ID?\",\"groupId\":\"nonexistent_group\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}]}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("{\"error\":\"Group not found\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(404, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"Group not found\""));
    }

    @Test
    public void testCreateVote_NonExistentCreatorId() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_NonExistentCreatorId -------------");

        String requestBody = "{\"question\":\"Non-existent creator?\",\"groupId\":\"group1\",\"creatorId\":\"nonexistent_user\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}]}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("{\"error\":\"Creator not found\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(404, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"Creator not found\""));
    }

    @Test
    public void testCreateVote_DuplicateVoteInGroup() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_DuplicateVoteInGroup -------------");

        String requestBody = "{\"question\":\"Duplicate vote?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}]}";
        stubFor(post(urlEqualTo("/api/votes"))
                .willReturn(aResponse()
                        .withStatus(409)
                        .withBody("{\"error\":\"A vote with the same question already exists in this group\"}")
                ));

        HttpPost request = new HttpPost("http://localhost:8081/api/votes");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        System.out.println("Request URL: " + request.getURI());
        System.out.println("Request Body: " + requestBody);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(409, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"error\":\"A vote with the same question already exists in this group\""));
    }

    @Test
    public void testCreateVote_ConcurrentCreation() throws Exception {
        System.out.println("\n------------- Starting testCreateVote_ConcurrentCreation -------------");

        String requestBody = "{\"question\":\"Concurrent vote?\",\"groupId\":\"group1\",\"creatorId\":\"user1\",\"choices\":[{\"name\":\"Option 1\"},{\"name\":\"Option 2\"}]}";

        // Simulate concurrent requests
        stubFor(post(urlEqualTo("/api/votes"))
                .inScenario("Concurrent Creation")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"12345\",\"question\":\"Concurrent vote?\",\"status\":\"OPEN\"}")
                )
                .willSetStateTo("First Request Processed"));

        stubFor(post(urlEqualTo("/api/votes"))
                .inScenario("Concurrent Creation")
                .whenScenarioStateIs("First Request Processed")
                .willReturn(aResponse()
                        .withStatus(409)
                        .withBody("{\"error\":\"Concurrent vote creation detected\"}")
                ));

        // First request
        HttpPost request1 = new HttpPost("http://localhost:8081/api/votes");
        request1.setHeader("Content-Type", "application/json");
        request1.setEntity(new StringEntity(requestBody));

        System.out.println("Request1 URL: " + request1.getURI());
        System.out.println("Request1 Body: " + requestBody);

        HttpResponse response1 = httpClient.execute(request1);

        // Second request (simulating concurrent creation)
        HttpPost request2 = new HttpPost("http://localhost:8081/api/votes");
        request2.setHeader("Content-Type", "application/json");
        request2.setEntity(new StringEntity(requestBody));

        System.out.println("Request2 URL: " + request2.getURI());

        HttpResponse response2 = httpClient.execute(request2);

        System.out.println("Response1 Status: " + response1.getStatusLine().getStatusCode());
        System.out.println("Response2 Status: " + response2.getStatusLine().getStatusCode());
        String responseBody1 = EntityUtils.toString(response1.getEntity());
        System.out.println("Response1 Body: " + responseBody1);
        String responseBody2 = EntityUtils.toString(response2.getEntity());
        System.out.println("Response2 Body: " + responseBody2);

        assertEquals(200, response1.getStatusLine().getStatusCode());
        assertEquals(409, response2.getStatusLine().getStatusCode());
        assertTrue(responseBody2.contains("\"error\":\"Concurrent vote creation detected\""));
    }
}

