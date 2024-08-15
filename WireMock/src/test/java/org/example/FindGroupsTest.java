package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8081)
public class FindGroupsTest {
    private final HttpClient httpClient = HttpClients.createDefault();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testFindGroups_Success() throws Exception {
        System.out.println("------------- Starting testFindGroups_Success -------------");

        // Mock API response
        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("page", equalTo("0"))
                .withQueryParam("pageSize", equalTo("25"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("find_groups_success_response.json")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find")
                .addParameter("page", "0")
                .addParameter("pageSize", "25")
                .build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, HashMap.class);

        assertTrue((Boolean) responseMap.get("success"));
        assertEquals(0, responseMap.get("returnCode"));

        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
        assertFalse(content.isEmpty());

        Map<String, Object> firstGroup = content.get(0);
        assertEquals("ACTIVE", firstGroup.get("status"));

        // Verify that the stub was called
        verify(getRequestedFor(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("page", equalTo("0"))
                .withQueryParam("pageSize", equalTo("25")));
    }

    @Test
    public void testFindGroups_EmptyResult() throws Exception {
        System.out.println("\n------------- Starting testFindGroups_EmptyResult -------------");

        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("find_groups_empty_response.json")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find")
                .addParameter("groupName", "NonExistentGroup")
                .build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, HashMap.class);

        assertTrue((Boolean) responseMap.get("success"));
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
        assertTrue(content.isEmpty());
    }

    @Test
    public void testFindGroups_Pagination() throws Exception {
        System.out.println("\n------------- Starting testFindGroups_Pagination -------------");

        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("find_groups_pagination_response.json")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find")
                .addParameter("page", "1")
                .addParameter("pageSize", "10")
                .build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, HashMap.class);

        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        assertEquals(1, data.get("number"));
        assertEquals(10, data.get("size"));
        assertFalse((Boolean) data.get("first"));
    }

    @Test
    public void testFindGroups_FilterByStatus() throws Exception {
        System.out.println("\n------------- Starting testFindGroups_FilterByStatus -------------");

        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("status", equalTo("ACTIVE"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("find_groups_active_response.json")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find")
                .addParameter("status", "ACTIVE")
                .build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, HashMap.class);

        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
        for (Map<String, Object> group : content) {
            assertEquals("ACTIVE", group.get("status"));
        }

        verify(getRequestedFor(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("status", equalTo("ACTIVE")));
    }

    @Test
    public void testFindGroups_FilterByCategory() throws Exception {
        System.out.println("\n------------- Starting testFindGroups_FilterByCategory -------------");

        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("groupCategory", equalTo("TestCategory"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("find_groups_category_response.json")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find")
                .addParameter("groupCategory", "TestCategory")
                .build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, HashMap.class);

        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
        for (Map<String, Object> group : content) {
            assertEquals("TestCategory", group.get("category"));
        }

        verify(getRequestedFor(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("groupCategory", equalTo("TestCategory")));
    }

    @Test
    public void testFindGroups_InvalidPageNumber() throws Exception {
        System.out.println("\n------------- Starting testFindGroups_InvalidPageNumber -------------");

        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("page", equalTo("-1"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"Invalid page number\"}")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find")
                .addParameter("page", "-1")
                .build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("Invalid page number"));
    }

    @Test
    public void testFindGroups_InvalidPageSize() throws Exception {
        System.out.println("------------- Starting testFindGroups_InvalidPageSize --------------");

        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("pageSize", equalTo("1001"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"Invalid page size\"}")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find")
                .addParameter("pageSize", "1001")
                .build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("Invalid page size"));
    }

    @Test
    public void testFindGroups_InvalidStatus() throws Exception {
        System.out.println("\n------------- Starting testFindGroups_InvalidStatus -------------");

        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("status", equalTo("INVALID_STATUS"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\":\"Invalid status\"}")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find")
                .addParameter("status", "INVALID_STATUS")
                .build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("Invalid status"));
    }

    @Test
    public void testFindGroups_Unauthorized() throws Exception {
        System.out.println("\n------------- Starting testFindGroups_Unauthorized -------------");

        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{\"error\":\"Unauthorized access\"}")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find").build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(401, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("Unauthorized access"));
    }

    @Test
    public void testFindGroups_ServerError() throws Exception {
        System.out.println("\n------------- Starting testFindGroups_ServerError -------------");

        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("{\"error\":\"Internal server error\"}")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find").build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(500, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("Internal server error"));
    }

    @Test
    public void testFindGroups_TimeFilter() throws Exception {
        System.out.println("\n------------- Starting testFindGroups_TimeFilter -------------");
        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("timeStart", matching("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"))
                .withQueryParam("timeEnd", matching("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("find_groups_time_filter_response.json")));
        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find")
                .addParameter("timeStart", "2024-01-01T00:00:00")
                .addParameter("timeEnd", "2024-12-31T23:59:59")
                .build();
        System.out.println("Request URL: " + uri);
        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);
        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);
        assertEquals(200, response.getStatusLine().getStatusCode());
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, HashMap.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
        assertFalse(content.isEmpty());
        verify(getRequestedFor(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("timeStart", matching("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"))
                .withQueryParam("timeEnd", matching("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")));
    }

    @Test
    public void testFindGroups_CombinedFilters() throws Exception {
        System.out.println("\n------------- Starting testFindGroups_CombinedFilters -------------");

        stubFor(get(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("groupName", equalTo("TestGroup"))
                .withQueryParam("groupCategory", equalTo("TestCategory"))
                .withQueryParam("status", equalTo("ACTIVE"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("find_groups_combined_filters_response.json")));

        URI uri = new URIBuilder("http://localhost:8081/api/analytic/find")
                .addParameter("groupName", "TestGroup")
                .addParameter("groupCategory", "TestCategory")
                .addParameter("status", "ACTIVE")
                .build();
        System.out.println("Request URL: " + uri);

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        assertEquals(200, response.getStatusLine().getStatusCode());
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, HashMap.class);

        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
        assertFalse(content.isEmpty());
        for (Map<String, Object> group : content) {
            assertTrue(((String) group.get("name")).contains("TestGroup"));
            assertEquals("TestCategory", group.get("category"));
            assertEquals("ACTIVE", group.get("status"));
        }

        verify(getRequestedFor(urlPathEqualTo("/api/analytic/find"))
                .withQueryParam("groupName", equalTo("TestGroup"))
                .withQueryParam("groupCategory", equalTo("TestCategory"))
                .withQueryParam("status", equalTo("ACTIVE")));
    }
}
