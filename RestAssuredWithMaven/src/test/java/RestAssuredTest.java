import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import io.restassured.response.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class RestAssuredTest {

    // Set base url
    String baseUrl = "https://mentor.fit.hcmus.edu.vn/";
    String access_token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiNzNlNjc0MC0zM2ViLTQxM2MtYTE1Yi03ZThmNGY3NDhmNWEiLCJyb2xlcyI6WyJVU0VSIl0sIm5hbWUiOiJudmRodXluaDIxQGNsYy5maXR1cy5lZHUudm4iLCJlbWFpbGFkZHJlc3MiOiJudmRodXluaDIxQGNsYy5maXR1cy5lZHUudm4iLCJuYW1laWRlbnRpZmllciI6Im52ZGh1eW5oMjFAY2xjLmZpdHVzLmVkdS52biIsImlzcyI6Ik1lbnRvclVTIiwiaWF0IjoxNzE5NjM0MjI2LCJleHAiOjE3MTk3MjA2MjZ9.CC3wzo5Ywtj1BSXREUU-l2C_Kw8eNuuNzhtyXpvzFSrvTyrrJXSaeHUL4HT-uPTm6casKwoajvML03JEHSBlUw";
    String groupId = "c22c3c32-4236-4fe5-94c9-8541198710a6";
    String creatorId = "b73e6740-33eb-413c-a15b-7e8f4f748f5a";

    @Test
    public void Unauthorized() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "Hôm nay có đi học không?");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2024-06-30T03:15:18.877Z");

        Map<String, Object> choice = new HashMap<>();
        choice.put("id", "1");
        choice.put("name", "Có");
        choice.put("voters", Collections.singletonList("string"));

        payload.put("choices", Collections.singletonList(choice));
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + "123")
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
            .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(401)
                .extract().response();
    }

    @Test
    public void EmptyChoice() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "Hôm nay có đi học không?");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2024-06-30T03:15:18.877Z");
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(500)
                .extract().response();
    }

    @Test
    public void MultipleChoices() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "Hôm nay có đi học không?");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2024-06-30T03:15:18.877Z");

        // Create the list to hold multiple choice maps
        List<Map<String, Object>> choices = new ArrayList<>();

        // Create and populate the first choice map
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("id", "1");
        choice1.put("name", "Có");
        choice1.put("voters", Collections.singletonList("voter1"));

        // Add the first choice map to the list
        choices.add(choice1);

        // Create and populate the second choice map
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("id", "2");
        choice2.put("name", "Không");
        choice2.put("voters", Collections.singletonList("voter2"));

        // Add the second choice map to the list
        choices.add(choice2);

        payload.put("choices", choices);
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(200)
                .extract().response();
    }

    @Test
    public void SingleChoice() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "Hôm nay có đi học không?");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2024-06-30T03:15:18.877Z");

        Map<String, Object> choice = new HashMap<>();
        choice.put("id", "1");
        choice.put("name", "Có");
        choice.put("voters", Collections.singletonList("string"));

        payload.put("choices", Collections.singletonList(choice));
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(500)
                .extract().response();
    }

    @Test
    public void QuestionName_1_Character() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "A");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2024-06-30T03:15:18.877Z");

        // Create the list to hold multiple choice maps
        List<Map<String, Object>> choices = new ArrayList<>();

        // Create and populate the first choice map
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("id", "1");
        choice1.put("name", "Có");
        choice1.put("voters", Collections.singletonList("voter1"));

        // Add the first choice map to the list
        choices.add(choice1);

        // Create and populate the second choice map
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("id", "2");
        choice2.put("name", "Không");
        choice2.put("voters", Collections.singletonList("voter2"));

        // Add the second choice map to the list
        choices.add(choice2);

        payload.put("choices", choices);
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(200)
                .extract().response();
    }

    @Test
    public void QuestionName_226_Character() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "In the heart of the bustling city, a hidden gem awaited discovery. Amidst the chaos and noise, a small, tranquil garden flourished, offering a sanctuary of peace and beauty. Visitors found solace in the vibrant flowers,aaaaaaa");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2024-06-30T03:15:18.877Z");

        // Create the list to hold multiple choice maps
        List<Map<String, Object>> choices = new ArrayList<>();

        // Create and populate the first choice map
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("id", "1");
        choice1.put("name", "Có");
        choice1.put("voters", Collections.singletonList("voter1"));

        // Add the first choice map to the list
        choices.add(choice1);

        // Create and populate the second choice map
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("id", "2");
        choice2.put("name", "Không");
        choice2.put("voters", Collections.singletonList("voter2"));

        // Add the second choice map to the list
        choices.add(choice2);

        payload.put("choices", choices);
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(200)
                .extract().response();
    }

    @Test
    public void QuestionNameBlank() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2024-06-30T03:15:18.877Z");

        // Create the list to hold multiple choice maps
        List<Map<String, Object>> choices = new ArrayList<>();

        // Create and populate the first choice map
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("id", "1");
        choice1.put("name", "Có");
        choice1.put("voters", Collections.singletonList("voter1"));

        // Add the first choice map to the list
        choices.add(choice1);

        // Create and populate the second choice map
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("id", "2");
        choice2.put("name", "Không");
        choice2.put("voters", Collections.singletonList("voter2"));

        // Add the second choice map to the list
        choices.add(choice2);

        payload.put("choices", choices);
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(500)
                .extract().response();
    }

    @Test
    public void QuestionName_227_Character() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "In the heart of the bustling city, a hidden gem awaited discovery. Amidst the chaos and noise, a small, tranquil garden flourished, offering a sanctuary of peace and beauty. Visitors found solace in the vibrant flowers,aaaaaaaa");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2024-06-30T03:15:18.877Z");

        // Create the list to hold multiple choice maps
        List<Map<String, Object>> choices = new ArrayList<>();

        // Create and populate the first choice map
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("id", "1");
        choice1.put("name", "Có");
        choice1.put("voters", Collections.singletonList("voter1"));

        // Add the first choice map to the list
        choices.add(choice1);

        // Create and populate the second choice map
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("id", "2");
        choice2.put("name", "Không");
        choice2.put("voters", Collections.singletonList("voter2"));

        // Add the second choice map to the list
        choices.add(choice2);

        payload.put("choices", choices);
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(500)
                .extract().response();
    }

    @Test
    public void AnswerEmptyString() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "Hôm nay có đi học không?");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2024-06-30T03:15:18.877Z");

        // Create the list to hold multiple choice maps
        List<Map<String, Object>> choices = new ArrayList<>();

        // Create and populate the first choice map
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("id", "1");
        choice1.put("name", "Có");
        choice1.put("voters", Collections.singletonList("voter1"));

        // Add the first choice map to the list
        choices.add(choice1);

        // Create and populate the second choice map
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("id", "2");
        choice2.put("name", "");
        choice2.put("voters", Collections.singletonList("voter2"));

        // Add the second choice map to the list
        choices.add(choice2);

        payload.put("choices", choices);
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(500)
                .extract().response();
    }

    @Test
    public void DuplicatedAnswer() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "Hôm nay có đi học không?");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2024-06-30T03:15:18.877Z");

        // Create the list to hold multiple choice maps
        List<Map<String, Object>> choices = new ArrayList<>();

        // Create and populate the first choice map
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("id", "1");
        choice1.put("name", "Có");
        choice1.put("voters", Collections.singletonList("voter1"));

        // Add the first choice map to the list
        choices.add(choice1);

        // Create and populate the second choice map
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("id", "2");
        choice2.put("name", "Có");
        choice2.put("voters", Collections.singletonList("voter2"));

        // Add the second choice map to the list
        choices.add(choice2);

        payload.put("choices", choices);
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(500)
                .extract().response();
    }

    @Test
    public void EmptyTimeEnd() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "Hôm nay có đi học không?");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "");

        // Create the list to hold multiple choice maps
        List<Map<String, Object>> choices = new ArrayList<>();

        // Create and populate the first choice map
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("id", "1");
        choice1.put("name", "Có");
        choice1.put("voters", Collections.singletonList("voter1"));

        // Add the first choice map to the list
        choices.add(choice1);

        // Create and populate the second choice map
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("id", "2");
        choice2.put("name", "Không");
        choice2.put("voters", Collections.singletonList("voter2"));

        // Add the second choice map to the list
        choices.add(choice2);

        payload.put("choices", choices);
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(200)
                .extract().response();
    }

    @Test
    public void UnexistTimeEnd() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "Hôm nay có đi học không?");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2023-02-29T03:15:18.877Z");

        // Create the list to hold multiple choice maps
        List<Map<String, Object>> choices = new ArrayList<>();

        // Create and populate the first choice map
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("id", "1");
        choice1.put("name", "Có");
        choice1.put("voters", Collections.singletonList("voter1"));

        // Add the first choice map to the list
        choices.add(choice1);

        // Create and populate the second choice map
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("id", "2");
        choice2.put("name", "Không");
        choice2.put("voters", Collections.singletonList("voter2"));

        // Add the second choice map to the list
        choices.add(choice2);

        payload.put("choices", choices);
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(500)
                .extract().response();
    }

    @Test
    public void TimeEndBeforeCurrent() {
        // Create payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("question", "Hôm nay có đi học không?");
        payload.put("groupId", groupId);
        payload.put("creatorId", creatorId);
        payload.put("timeEnd", "2023-02-28T03:15:18.877Z");

        // Create the list to hold multiple choice maps
        List<Map<String, Object>> choices = new ArrayList<>();

        // Create and populate the first choice map
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("id", "1");
        choice1.put("name", "Có");
        choice1.put("voters", Collections.singletonList("voter1"));

        // Add the first choice map to the list
        choices.add(choice1);

        // Create and populate the second choice map
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("id", "2");
        choice2.put("name", "Không");
        choice2.put("voters", Collections.singletonList("voter2"));

        // Add the second choice map to the list
        choices.add(choice2);

        payload.put("choices", choices);
        payload.put("isMultipleChoice", true);


        Response response = given()
                .header("Authorization", "Bearer " + access_token)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "api/votes");

        response.then().log().all()
                .statusCode(500)
                .extract().response();
    }
}
