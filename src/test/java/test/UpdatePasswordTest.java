package test;

import com.github.javafaker.Faker;
import dto.LoginRequest;
import dto.PasswordUpdateRequest;
import dto.RegisterRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdatePasswordTest extends BaseTest{
//schimbarea parolei cu succes
    @Test
    @org.testng.annotations.Test//test unit
    public void successUpdatePasswordTest() {
        // Generează un email random pentru test
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();

        // Creează o cerere de înregistrare a unui utilizator
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(email)
                .password("User1234")
                .confirmPassword("User1234")
                .role("user")
                .build();

        // Trimite cererea de înregistrare și obține token-ul de acces
        Response registerUser = postRequest(REG_PATH, 201, registerRequest);
        String accessToken = registerUser.jsonPath().getString("accessToken");

        // Creează o cerere pentru actualizarea parolei
        PasswordUpdateRequest updateRequest = PasswordUpdateRequest.builder()
                .currentPassword("User1234")
                .newPassword("User12345")
                .confirmPassword("User12345")
                .build();

        // Trimite cererea de actualizare a parolei cu token-ul de acces
        putRequest(UPDATE_PASSWORD_PATH, 200, updateRequest, accessToken);

        // Verifică dacă utilizatorul se poate autentifica cu noua parolă
        Response authResponse = postRequest(AUTH_PATH, 200, new LoginRequest(email, "User12345"));
        assertEquals(200, authResponse.getStatusCode(), "Failed to login with updated password");
    }

//actualizarea parolei cu o parolă care nu respectă cerințele minime
    @Test
    @org.testng.annotations.Test//test unit
    public void updateWithInvalidPasswordTest() {
        // Generează un email random pentru test
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();

        // Creează o cerere de înregistrare a unui utilizator
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(email)
                .password("User1234")
                .confirmPassword("User1234")
                .role("user")
                .build();

        // Trimite cererea de înregistrare și obține token-ul de acces
        Response registerUser = postRequest(REG_PATH, 201, registerRequest);
        String accessToken = registerUser.jsonPath().getString("accessToken");

        // Creează o cerere pentru actualizarea parolei cu o parolă invalidă
        PasswordUpdateRequest updateRequest = PasswordUpdateRequest.builder()
                .currentPassword("User1234")
                .newPassword("User") // Parola nouă este prea scurtă și nu respectă cerințele
                .confirmPassword("User")
                .build();

        // Trimite cererea de actualizare a parolei și așteaptă un răspuns de eroare (400 Bad Request)
        Response updateResponse = putRequest(UPDATE_PASSWORD_PATH, 400, updateRequest, accessToken);

        // Verifică mesajul de eroare pentru a confirma că acesta indică probleme de validare a parolei
        String errorText = updateResponse.getBody().jsonPath().getString("newPassword");
        assertTrue(errorText.contains("Password must contain letters and numbers") || errorText.contains("Password must contain at least 8 characters"));
    }

//actualizarea parolei fără un token de acces valid
    @Test
    @org.testng.annotations.Test//test unit
    public void unauthorizedUpdatePasswordTest() {
        // Înregistrează un utilizator
        registerUser(201);

        // Creează o cerere pentru actualizarea parolei
        PasswordUpdateRequest updateRequest = PasswordUpdateRequest.builder()
                .currentPassword("User1234")
                .newPassword("User12345")
                .confirmPassword("User12345")
                .build();

        // Trimite cererea de actualizare a parolei fără token de acces (neautorizată) și așteaptă un cod 401
        Response updateResponse = putRequest(UPDATE_PASSWORD_PATH, 401, updateRequest, null);

        // Verifică mesajul de eroare pentru a confirma că este "Unauthorized"
        String errorText = updateResponse.getBody().jsonPath().getString("message");
        assertEquals("Unauthorized", errorText);
    }
}
