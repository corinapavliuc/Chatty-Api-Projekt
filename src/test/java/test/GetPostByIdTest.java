package test;

import dto.PostGetResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetPostByIdTest extends  BaseTest{
    //după ce ai creat un post, poți recupera acel post folosind ID-ul său și dacă informațiile returnate sunt corecte și bine formatate.
    @Test
    @org.testng.annotations.Test
    public void successGetPostById() {
        // Creează un post și obține răspunsul, inclusiv ID-ul postului creat
        Response createPostResponse = createPost(201); // Presupune că metoda `createPost` returnează un răspuns cu codul 201 pentru creație reușită
        String postId = createPostResponse.jsonPath().getString("id"); // Extrage ID-ul postului din răspuns

        // Obține token-ul de acces administrativ
        String accessAdminToken = getAdminAccessToken(); // Presupune că metoda `getAdminAccessToken` returnează un token valid pentru acces administrativ

        // Trimite o cerere GET pentru a obține detaliile postului folosind ID-ul și token-ul de acces
        Response getPostResponse = getRequestWithAccessToken(GET_POST_PATH + postId, 200, accessAdminToken);

        // Verifică dacă codul de status al răspunsului este 200 (OK)
        assertEquals(200, getPostResponse.getStatusCode(), "Status code should be 200 OK");

        // Deserializați răspunsul în obiectul `PostGetResponse` pentru a verifica detaliile postului
        PostGetResponse post = getPostResponse.as(PostGetResponse.class);

        // Verifică dacă ID-ul postului nu este null
        assertNotNull(post.getId(), "Post ID should not be null");

        // Verifică dacă titlul postului nu este null
        assertNotNull(post.getTitle(), "Post title should not be null");
    }

///verifică dacă posturile nu pot fi accesate fără autentificare.
// Dacă încerci să accesezi detalii ale unui post fără un token de acces valid
    @Test
    @org.testng.annotations.Test
    public void unauthorizedGetPostById() {
        // Creează un post și obține răspunsul, inclusiv ID-ul postului creat
        Response createPostResponse = createPost(201); // Presupune că metoda `createPost` returnează un răspuns cu codul 201 pentru creație reușită
        String postId = createPostResponse.jsonPath().getString("id"); // Extrage ID-ul postului din răspuns

        // Trimite o cerere GET pentru a obține detaliile postului folosind ID-ul, dar fără token de acces (neautorizat)
        Response getPostResponse = getRequestWithAccessToken(GET_POST_PATH + postId, 401, null);

        // Verifică dacă codul de status al răspunsului este 401 (Unauthorized)
        assertEquals(401, getPostResponse.getStatusCode(), "Status code should be 401 Unauthorized");

        // Verifică mesajul de eroare din răspunsul JSON
        String errorText = getPostResponse.getBody().jsonPath().getString("message");
        assertEquals("Unauthorized", errorText);
    }

}
