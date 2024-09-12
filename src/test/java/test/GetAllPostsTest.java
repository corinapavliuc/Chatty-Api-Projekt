package test;

import dto.PostGetResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GetAllPostsTest extends BaseTest{
//Verificarea accesului autorizat
    //Confirmarea completitudinii datelor

    @Test
    @org.testng.annotations.Test
    public void successGetAllPostsTest() {
        // Obține un token de acces de administrator pentru a autoriza cererea
        String accessAdminToken = getAdminAccessToken();

        // Trimite o solicitare GET pentru a obține toate postările, folosind tokenul de acces
        Response response = getRequestWithAccessToken(GET_ALL_POSTS_PATH, 200, accessAdminToken);

        // Verifică dacă statusul răspunsului este 200 OK
        assertEquals(200, response.getStatusCode(), "Status code should be 200 OK");

        // Extrage lista tuturor postărilor din răspuns
        List<PostGetResponse> allPosts = response.jsonPath().getList("$", PostGetResponse.class);

        // Verifică dacă lista de postări nu este goală
        assertTrue(allPosts.size() > 0, "There should be posts returned");

        // Verifică fiecare postare din lista obținută
        for (PostGetResponse post : allPosts) {
            assertNotNull(post.getId(), "Post ID should not be null");
            assertNotNull(post.getTitle(), "Post title should not be null");
            assertNotNull(post.getDescription(), "Post description should not be null");
            assertNotNull(post.getBody(), "Post body should not be null");
            assertNotNull(post.getCreatedAt(), "Post creation date should not be null");
            assertNotNull(post.getUpdatedAt(), "Post update date should not be null");
        }
    }

//tokenuri de acces invalide
    @Test
    @org.testng.annotations.Test
    public void unauthorizedGetAllPostsTest() {
        // Folosește un token de acces invalid pentru a autoriza cererea
        String accessAdminToken = "invalidToken";

        // Trimite o solicitare GET pentru a obține toate postările, folosind tokenul invalid
        Response response = getRequestWithAccessToken(GET_ALL_POSTS_PATH, 401, accessAdminToken);

        // Verifică dacă statusul răspunsului este 401 Unauthorized
        assertEquals(401, response.getStatusCode(), "Status code should be 401 Unauthorized");

        // Extrage mesajul de eroare din corpul răspunsului
        String errorText = response.getBody().jsonPath().getString("message");

        // Verifică dacă mesajul de eroare este "Unauthorized"
        assertEquals("Unauthorized", errorText, "Error message should indicate Unauthorized access");
    }

}
