package test;

import dto.PostGetResponse;
import dto.PostUpdateRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdatePostTest extends BaseTest {
//actulizeaza postare

    //verifică dacă actualizarea unei postări funcționează corect atunci când cererea este efectuată cu un token de acces valid.
    @Test
    @org.testng.annotations.Test
    public void successUpdatePostTest() {
        // Creează o postare și obține ID-ul postării
        Response createPostResponse = createPost(201);
        String postId = createPostResponse.jsonPath().getString("id");

        // Pregătește datele pentru actualizarea postării
        PostUpdateRequest updateRequest = new PostUpdateRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setBody("Updated Body");
        updateRequest.setImageUrl("Updated Image URL");
        updateRequest.setDraft(false);

        // Obține token-ul de acces al adminului
        String accessAdminToken = getAdminAccessToken();

        // Trimite cererea de actualizare și verifică răspunsul
        Response updatePostResponse = putRequest(UPDATE_POST_PATH + postId, 200, updateRequest, accessAdminToken);

        // Verifică dacă codul de status este 200 OK
        assertEquals(200, updatePostResponse.getStatusCode(), "Status code should be 200 OK");

        // Verifică dacă postarea a fost actualizată corect
        PostGetResponse updatedPost = updatePostResponse.as(PostGetResponse.class);
        assertEquals("Updated Title", updatedPost.getTitle(), "Post title should be updated");
        assertEquals("Updated Description", updatedPost.getDescription(), "Post description should be updated");
    }

//când cererea nu include un token de acces
    @Test
    @org.testng.annotations.Test //arata ca e test unit
    public void unauthorizedUpdatePostTest() {
        // Creează o postare și obține ID-ul acesteia
        Response createPostResponse = createPost(201);
        String postId = createPostResponse.jsonPath().getString("id");

        // Pregătește datele pentru actualizare
        PostUpdateRequest updateRequest = new PostUpdateRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setBody("Updated Body");
        updateRequest.setImageUrl("Updated Image URL");
        updateRequest.setDraft(false);

        // Trimite cererea de actualizare fără token de acces (neautorizată) și așteaptă un cod 401
        Response updatePostResponse = putRequest(UPDATE_POST_PATH + postId, 401, updateRequest, null);

        // Verifică că răspunsul are codul de status 401 Unauthorized
        assertEquals(401, updatePostResponse.getStatusCode(), "Status code should be 401 Unauthorized");

        // Verifică mesajul de eroare pentru a confirma că este "Unauthorized"
        String errorText = updatePostResponse.getBody().jsonPath().getString("message");
        assertEquals("Unauthorized", errorText);
    }

}
