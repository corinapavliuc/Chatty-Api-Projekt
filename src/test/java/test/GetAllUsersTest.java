package test;

import dto.UserResponseForAdmin;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetAllUsersTest extends BaseTest{
//funcționează corect atunci când este accesat cu un token valid de admin.
    //lista utilizatorilor nu este goală și că informațiile despre utilizatori sunt prezente și valide.

    @Test
    @org.testng.annotations.Test
    public void getAllUsersSuccessTest() {
        // Obține un token de acces de admin valid
        String accessAdminToken = getAdminAccessToken();

        // Trimite o solicitare GET pentru a obține lista tuturor utilizatorilor, folosind token-ul de acces de admin
        Response getUsersResponse = getRequestWithAccessToken(GET_USERS_PATH, 200, accessAdminToken);

        // Verifică dacă statusul răspunsului este 200 OK
        assertEquals(200, getUsersResponse.getStatusCode(), "Get all users request should return status code 200");

        // Deserializăm răspunsul JSON într-o listă de obiecte UserResponseForAdmin
        List<UserResponseForAdmin> userList = getUsersResponse.jsonPath().getList("", UserResponseForAdmin.class);

        // Verifică dacă lista utilizatorilor nu este goală
        assertFalse(userList.isEmpty(), "List of users should not be empty");

        // Verifică dacă adresa de email a primului utilizator din listă nu este null
        UserResponseForAdmin firstUser = userList.get(0);
        assertNotNull(firstUser.getEmail(), "First user's email should not be null");
    }

//toker invalid
    @Test
    @org.testng.annotations.Test
    public void getAllUsersUnauthorizedTest() {
        // Trimite o solicitare GET pentru a obține lista tuturor utilizatorilor fără un token de acces valid
        Response getUsersResponse = getRequestWithAccessToken(GET_USERS_PATH, 401, "");

        // Verifică dacă statusul răspunsului este 401 Unauthorized
        assertEquals(401, getUsersResponse.getStatusCode(), "Get all users request without access token should return status code 401");

        // Verifică mesajul de eroare din răspuns
        String errorText = getUsersResponse.getBody().jsonPath().getString("message");
        assertEquals("Authentication failed: Full authentication is required to access this resource", errorText);
    }

}
