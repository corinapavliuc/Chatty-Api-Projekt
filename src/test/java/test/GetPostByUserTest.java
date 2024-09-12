package test;

import com.github.javafaker.Faker;
import dto.PostCreateRequest;
import dto.PostGetResponse;
import dto.UserResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetPostByUserTest extends BaseTest{

    //Înregistra un utilizator,crea posturi,Obține posturile create
    //verifica că posturile sunt corecte și asociate cu utilizatorul potrivi

    @Test
    @org.testng.annotations.Test
    public void successGetUserPostsTest() {
        // Generarea de date fictive pentru crearea unui post
        Faker faker = new Faker();
        String title = faker.lorem().words(1).get(0); // Titlu aleator
        String description = faker.lorem().sentence(); // Descriere aleatoare
        String body = faker.lorem().paragraph(); // Corpul postului
        String imageUrl = faker.internet().image(); // URL-ul imaginii

        // Înregistrarea unui utilizator nou și obținerea token-ului de acces
        Response registerUser = registerUser(201); // Presupune că metoda `registerUser` returnează un răspuns cu codul 201 pentru înregistrare reușită
        String accessToken = registerUser.jsonPath().getString("accessToken"); // Extrage token-ul de acces din răspuns

        // Obținerea informațiilor despre utilizator folosind token-ul de acces
        Response userInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);
        UserResponse userResponse = userInfo.as(UserResponse.class);
        String userId = userResponse.getId(); // Extrage ID-ul utilizatorului

        // Crearea a două posturi pentru utilizatorul înregistrat
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title(title)
                .description(description)
                .body(body)
                .imageUrl(imageUrl)
                .draft(false)
                .build();

        postRequestWithAccessToken(CREATE_POST_PATH, 201, postCreateRequest, accessToken); // Creează primul post
        postRequestWithAccessToken(CREATE_POST_PATH, 201, postCreateRequest, accessToken); // Creează al doilea post

        // Obținerea token-ului de acces pentru administrator
        String accessAdminToken = getAdminAccessToken();

        // Obținerea posturilor utilizatorului folosind token-ul de acces al administratorului
        Response response = getRequestWithAccessToken(GET_POSTS_PATH.replace("{user_id}", userId), 200, accessAdminToken);

        // Verificarea că codul de status al răspunsului este 200 (OK)
        assertEquals(200, response.getStatusCode(), "Status code should be 200 OK");

        // Conversia răspunsului JSON în array de obiecte PostGetResponse
        PostGetResponse[] posts = response.as(PostGetResponse[].class);

        // Verificarea că numărul de posturi returnate este 2
        assertEquals(2, posts.length, "There should be 2 posts returned");

        // Verificarea că fiecare post aparține utilizatorului specificat
        for (PostGetResponse post : posts) {
            assertEquals(userId, post.getUser().getId(), "Each post should belong to the specified user");
        }
    }

//Restricționează accesul neautorizat la informațiile despre posturi.
    @Test
    @org.testng.annotations.Test
    public void unauthorizedGetUserPostsTest() {
        // Generare de date fictive pentru crearea unui post
        Faker faker = new Faker();
        String title = faker.lorem().words(1).get(0); // Titlu aleator
        String description = faker.lorem().sentence(); // Descriere aleatoare
        String body = faker.lorem().paragraph(); // Corpul postului
        String imageUrl = faker.internet().image(); // URL-ul imaginii

        // Înregistrarea unui utilizator nou și obținerea token-ului de acces
        Response registerUser = registerUser(201); // Presupune că metoda `registerUser` returnează un răspuns cu codul 201 pentru înregistrare reușită
        String accessToken = registerUser.jsonPath().getString("accessToken"); // Extrage token-ul de acces din răspuns

        // Obținerea informațiilor despre utilizator folosind token-ul de acces
        Response userInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);
        UserResponse userResponse = userInfo.as(UserResponse.class);
        String userId = userResponse.getId(); // Extrage ID-ul utilizatorului

        // Crearea unui post pentru utilizatorul înregistrat
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title(title)
                .description(description)
                .body(body)
                .imageUrl(imageUrl)
                .draft(false)
                .build();

        postRequestWithAccessToken(CREATE_POST_PATH, 201, postCreateRequest, accessToken); // Creează un post

        // Încercarea de a obține posturile utilizatorului fără a furniza un token de acces
        Response response = getRequestWithAccessToken(GET_POSTS_PATH.replace("{user_id}", userId), 401, null);

        // Verificarea că codul de status al răspunsului este 401 (Neautorizat)
        assertEquals(401, response.getStatusCode(), "Status code should be 401 Unauthorized");

        // Verificarea mesajului de eroare în corpul răspunsului
        String errorText = response.getBody().jsonPath().getString("message");
        assertEquals("Unauthorized", errorText);
    }

}
