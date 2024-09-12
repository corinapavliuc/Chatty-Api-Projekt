package test;

import com.github.javafaker.Faker;
import dto.RegisterRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrationUserTest extends BaseTest{
    //Înregistrează cu succes

    @Test
    @org.testng.annotations.Test//unit test
    public void successRegistrationTest() {
        // Înregistrează un utilizator și obține răspunsul
        Response registerUser = registerUser(201);

        // Verifică dacă răspunsul are codul de status 201 (Created), indicând că înregistrarea a fost succes
        assertEquals(201, registerUser.getStatusCode());

        // Verifică dacă răspunsul conține "accessToken", confirmând că tokenul de acces a fost inclus în răspuns
        assertTrue(registerUser.getBody().asString().contains("accessToken"));

        // Verifică dacă răspunsul conține "refreshToken", confirmând că tokenul de refresh a fost inclus în răspuns
        assertTrue(registerUser.getBody().asString().contains("refreshToken"));
    }

//Testul asigură că sistemul returnează un răspuns corect
// și conține mesajele de eroare potrivite atunci
// când se încearcă înregistrarea unui utilizator fără a furniza o parolă validă.
    @Test
    @org.testng.annotations.Test//unit test
    public void registrationWithoutPasswordDataTest() {
        // Generează un email random pentru test
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();

        // Creează o cerere de înregistrare cu date de parolă invalide (parolă goală)
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(email)
                .password("") // Parola este goală
                .confirmPassword("") // Confirmarea parolei este goală
                .role("user")
                .build();

        // Trimite cererea de înregistrare și așteaptă un răspuns cu cod 400 (Bad Request)
        Response registerResponse = postRequest(REG_PATH, 400, registerRequest);

        // Verifică dacă codul de status al răspunsului este 400
        assertEquals(400, registerResponse.getStatusCode());

        // Verifică dacă mesajul de eroare conține toate mesajele așteptate privind validitatea parolei
        assertTrue(registerResponse.getBody().asString().contains("Password cannot be empty")
                && registerResponse.getBody().asString().contains("Password must contain at least 8 characters")
                && registerResponse.getBody().asString().contains("Password must contain letters and numbers"));
    }

}
