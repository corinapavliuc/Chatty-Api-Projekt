package test;

import com.github.javafaker.Faker;
import dto.FeedbackRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeedbackTest extends BaseTest{

    // generează date aleatorii pentru feedback, le trimite printr-o cerere POST
    @Test
    @org.testng.annotations.Test
    public void successFeedbackTest() {
        // Generează date aleatorii pentru nume, email și conținutul feedback-ului folosind biblioteca Faker
        Faker faker = new Faker();
        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String content = faker.lorem().paragraph();

        // Creează un obiect FeedbackRequest cu datele generate
        FeedbackRequest feedbackRequest = FeedbackRequest.builder()
                .name(name)
                .email(email)
                .content(content)
                .build();

        // Trimite o cerere POST pentru a trimite feedback-ul și așteaptă codul de status 201 (Created)
        Response feedbackResponse = postRequest(FEEDBACK_PATH, 201, feedbackRequest);

        // Verifică dacă trimiterea feedback-ului a fost cu succes (HTTP 201)
        assertEquals(201, feedbackResponse.getStatusCode(), "Trimiterea feedback-ului a eșuat");
    }

//trimiterea unui feedback fără a completa câmpul de nume.
    @Test
    @org.testng.annotations.Test
    public void feedbackWithoutNameTest() {

        // Generează date aleatorii pentru email și conținut, dar lasă câmpul de nume gol
        Faker faker = new Faker();
        String name = ""; // Numele este lăsat gol
        String email = faker.internet().emailAddress();
        String content = faker.lorem().paragraph();

        // Creează un obiect FeedbackRequest cu numele gol
        FeedbackRequest feedbackRequest = FeedbackRequest.builder()
                .name(name)
                .email(email)
                .content(content)
                .build();

        // Trimite o cerere POST și așteaptă un răspuns cu codul de status 400 (Bad Request)
        Response feedbackResponse = postRequest(FEEDBACK_PATH, 400, feedbackRequest);

        // Extrage mesajul de eroare legat de câmpul "name" din corpul răspunsului
        String responseMessage = feedbackResponse.getBody().jsonPath().getString("name");

        // Verifică dacă mesajul de eroare conține informația corectă: "Name can not be empty!"
        assertTrue(responseMessage.contains("Name can not be empty!"));
    }

//trimiterea unui feedback fără email
    @Test
    @org.testng.annotations.Test
    public void feedbackWithoutEmailTest() {

        // Generează un nume și conținut random, dar lasă câmpul de email gol
        Faker faker = new Faker();
        String name = faker.name().firstName(); // Nume generat aleatoriu
        String email = ""; // Email-ul este gol
        String content = faker.lorem().paragraph(); // Conținutul feedback-ului

        // Creează un obiect FeedbackRequest cu email gol
        FeedbackRequest feedbackRequest = FeedbackRequest.builder()
                .name(name)
                .email(email)
                .content(content)
                .build();

        // Trimite cererea POST și așteaptă un răspuns cu codul 400 (Bad Request)
        Response feedbackResponse = postRequest(FEEDBACK_PATH, 400, feedbackRequest);

        // Extrage mesajul de eroare pentru câmpul "email" din corpul răspunsului
        String responseMessage = feedbackResponse.getBody().jsonPath().getString("email");

        // Verifică dacă mesajul de eroare conține informația corectă: "Email can not be empty!"
        assertTrue(responseMessage.contains("Email can not be empty!"));
    }


//faca decriere continut
@Test
@org.testng.annotations.Test
public void feedbackWithoutContentTest() {

    // Generează un nume și o adresă de email random, dar lasă câmpul de conținut gol
    Faker faker = new Faker();
    String name = faker.name().firstName(); // Nume generat aleatoriu
    String email = faker.internet().emailAddress(); // Email generat aleatoriu
    String content = ""; // Conținutul este gol

    // Creează un obiect FeedbackRequest cu conținut gol
    FeedbackRequest feedbackRequest = FeedbackRequest.builder()
            .name(name)
            .email(email)
            .content(content)
            .build();

    // Trimite cererea POST și așteaptă un răspuns cu codul 400 (Bad Request)
    Response feedbackResponse = postRequest(FEEDBACK_PATH, 400, feedbackRequest);

    // Extrage mesajul de eroare pentru câmpul "content" din corpul răspunsului
    String responseMessage = feedbackResponse.getBody().jsonPath().getString("content");

    // Verifică dacă mesajul de eroare conține informația corectă: "Content can not be empty!"
    assertTrue(responseMessage.contains("Content can not be empty!"));
}

}
