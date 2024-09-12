package test;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeletePostTest extends BaseTest{
    //sterge postarea cu succes

    @Test
    @org.testng.annotations.Test //marchează metoda ca un test unit
    public void successDeletePostTest() {
        // Creează o postare și obține răspunsul
        Response createPostResponse = createPost(201);
        String postId = createPostResponse.jsonPath().getString("id");

        // Obține token-ul de acces pentru admin
        String accessAdminToken = getAdminAccessToken();

        // Trimite cererea DELETE pentru a șterge postarea și verifică codul de status 204
        Response deletePostResponse = deleteRequest(DELETE_POST_PATH + postId, 204, accessAdminToken);
        assertEquals(204, deletePostResponse.getStatusCode(), "Status code should be 204 No Content");

        // Trimite cererea GET pentru a verifica că postarea a fost ștearsă și obține codul de status 404
        Response getDeletedPostResponse = getRequestWithAccessToken(GET_POST_PATH + postId, 404, accessAdminToken);
        assertEquals(404, getDeletedPostResponse.getStatusCode(), "Deleted post should not be found");
    }


    // șterge o postare fără a avea autentificare (fără un token de acces valid)

    @Test
    @org.testng.annotations.Test//marchează metoda ca un test unit
    public void unauthorizedDeletePostTest() {
        // Trimite o cerere DELETE la un endpoint cu așteptarea codului 401 (Unauthorized)
        Response deletePostResponse = deleteRequest(DELETE_POST_PATH + "postId", 401, null);

        // Verifică dacă răspunsul are codul de stare 401
        assertEquals(401, deletePostResponse.getStatusCode(), "Status code should be 401 Unauthorized");

        // Extrage mesajul din răspuns și verifică dacă este "Unauthorized"
        String errorText = deletePostResponse.getBody().jsonPath().getString("message");
        assertEquals("Unauthorized", errorText);
    }    }

