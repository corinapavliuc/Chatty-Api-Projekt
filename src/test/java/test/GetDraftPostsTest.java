package test;

import dto.PostGetResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GetDraftPostsTest extends BaseTest{

    //Testul verifică dacă endpoint-ul pentru posturile
    // în stadiul de draft funcționează corect pentru admini
    // și dacă răspunsul conține posturi cu toate câmpurile necesare complet populate.
    @Test
    @org.testng.annotations.Test
    public void successGetDraftPostsTest() {
        // Obținerea token-ului de acces pentru admin
        String accessAdminToken = getAdminAccessToken();

        // Solicitarea posturilor în stadiul de draft folosind token-ul de acces
        Response response = getRequestWithAccessToken(GET_DRAFT_POSTS_PATH, 200, accessAdminToken);

        // Extrage lista posturilor din răspunsul JSON
        List<PostGetResponse> draftPosts = response.jsonPath().getList("$", PostGetResponse.class);

        // Verifică dacă lista conține cel puțin un post
        assertTrue(draftPosts.size() > 0, "There should be draft posts returned");

        // Verifică că fiecare post din listă conține toate câmpurile necesare
        for (PostGetResponse post : draftPosts) {
            assertNotNull(post.getId(), "Post ID should not be null");
            assertNotNull(post.getTitle(), "Post title should not be null");
            assertNotNull(post.getDescription(), "Post description should not be null");
            assertNotNull(post.getBody(), "Post body should not be null");
            assertNotNull(post.getCreatedAt(), "Post creation date should not be null");
            assertNotNull(post.getUpdatedAt(), "Post update date should not be null");
        }
    }

// Token-ul de acces pentru admin este intenționat invalid
    @Test
    @org.testng.annotations.Test
    public void unauthorizedGetDraftPostsTest() {
        // Token-ul de acces pentru admin este intenționat invalid
        String accessAdminToken = "invalidToken";

        // Se face o solicitare GET către endpoint-ul care returnează posturile în stadiul de draft, folosind token-ul invalid
        Response response = getRequestWithAccessToken(GET_DRAFT_POSTS_PATH, 401, accessAdminToken);

        // Extrage mesajul de eroare din răspunsul JSON
        String errorText = response.getBody().jsonPath().getString("message");

        // Verifică dacă mesajul de eroare indică un acces neautorizat
        assertEquals("Unauthorized", errorText, "Error message should indicate Unauthorized access");
    }

}
