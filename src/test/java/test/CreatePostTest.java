package test;

import com.github.javafaker.Faker;
import dto.PostCreateRequest;
import dto.PostResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreatePostTest extends BaseTest {
  //creaza un post

    @Test
    @org.testng.annotations.Test //arata ca este unit test
    public void successCreatePostTest() {
        // Generează date de test folosind Faker
        Faker faker = new Faker();
        String title = faker.lorem().words(1).get(0); // Titlu random
        String description = faker.lorem().sentence(); // Descriere random
        String body = faker.lorem().paragraph(); // Corp de text random
        String imageUrl = faker.internet().image(); // URL de imagine random

        // Înregistrează un utilizator și obține token-ul de acces
        Response registerUser = registerUser(201);
        String accessToken = registerUser.jsonPath().getString("accessToken");

        // Creează un request pentru postare folosind datele generate
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title(title)
                .description(description)
                .body(body)
                .imageUrl(imageUrl)
                .draft(false)
                .build();

        // Trimite cererea de creare postare cu token-ul de acces
        Response createPostResponse = postRequestWithAccessToken(CREATE_POST_PATH, 201, postCreateRequest, accessToken);
        PostResponse createdPostResponse = createPostResponse.as(PostResponse.class);

        // Verifică dacă titlul și descrierea din răspunsul primit corespund cu cele trimise
        assertEquals(title, createdPostResponse.getTitle(), "Returned post title should match the sent one");
        assertEquals(description, createdPostResponse.getDescription(), "Returned post description should match the sent one");
    }


    // postare cu o dată de publicare invalidă apare eroarie
    @Test
    @org.testng.annotations.Test //arata ca e unit test
    public void createPostInvalidDateTest() {
        // Generează date de test folosind Faker
        Faker faker = new Faker();
        String title = faker.lorem().sentence(); // Titlu random
        String description = faker.lorem().paragraph(); // Descriere random
        String publishDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME); // Data de publicare în format ISO

        // Înregistrează un utilizator și obține token-ul de acces
        Response registerUser = registerUser(201);
        String accessToken = registerUser.jsonPath().getString("accessToken");

        // Creează un request pentru postare cu o dată de publicare invalidă (formatul LocalDateTime nu este acceptat de server)
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title(title)
                .description(description)
                .publishDate(publishDate) // Camp cu dată de publicare incorectă
                .draft(false)
                .build();

        // Trimite cererea de creare postare și așteaptă un răspuns de eroare (400 Bad Request)
        Response createPostResponse = postRequestWithAccessToken(CREATE_POST_PATH, 400, postCreateRequest, accessToken);
        String errorMessage = createPostResponse.getBody().asString();

        // Verifică dacă mesajul de eroare conține textul așteptat referitor la deserializarea datelor
        assertTrue(errorMessage.contains("Cannot deserialize value of type `java.time.LocalDateTime`"));
    }


//nu include un token de acces.
    @Test
    @org.testng.annotations.Test //arata ca e unit test
    public void unauthorizedCreatePostTest() {
        // Generează date de test folosind Faker
        Faker faker = new Faker();
        String title = faker.lorem().words(1).get(0); // Titlu random
        String description = faker.lorem().sentence(); // Descriere random
        String body = faker.lorem().paragraph(); // Corpul postării random
        String imageUrl = faker.internet().image(); // URL-ul imaginii random

        // Creează un obiect de cerere pentru crearea unei postări fără a include un token de acces (neautorizat)
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title(title)
                .description(description)
                .body(body)
                .imageUrl(imageUrl)
                .draft(false)
                .build();

        // Trimite cererea de creare postare fără un token de acces (401 Unauthorized)
        Response createPostResponse = postRequestWithAccessToken(CREATE_POST_PATH, 401, postCreateRequest, null);
        String errorText = createPostResponse.getBody().jsonPath().getString("message");

        // Verifică dacă mesajul de eroare din răspuns este "Unauthorized"
        assertEquals("Unauthorized", errorText);
    }

}

