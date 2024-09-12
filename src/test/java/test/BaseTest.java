package test;

import com.github.javafaker.Faker;
import dto.LoginRequest;
import dto.PostCreateRequest;
import dto.RegisterRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;

import static io.restassured.RestAssured.given;

public class BaseTest {
    final static String BASE_URI = "http://chatty.telran-edu.de:8989/api";
    final static String AUTH_PATH = "/auth/login";
    final static String REFRESH_PATH = "/auth/refresh";
    final static String FEEDBACK_PATH = "/feedback";
    final static String REG_PATH = "/auth/register";
    final static String ME_PATH = "/me";
    final static String UPDATE_PASSWORD_PATH = "/user/password/update";
    final static String UPDATE_USER_PATH = "/users/";
    final static String GET_USERS_PATH = "/users";
    final static String POST_IMAGE_PATH = "/images";
    final static String DELETE_USER_PATH = "/users/";
    final static String DELETE_POST_PATH = "/posts/";
    final static String GET_POST_PATH = "/posts/";
    final static String UPDATE_POST_PATH = "/posts/";
    final static String GET_POSTS_PATH = "/users/{user_id}/posts";
    final static String GET_ALL_POSTS_PATH = "/posts";
    final static String GET_DRAFT_POSTS_PATH = "/posts/drafts";
    final static String CREATE_POST_PATH = "/posts";//impoind
    static RequestSpecification specification = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setContentType(ContentType.JSON).build();
    static RequestSpecification specificationImage = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setContentType("multipart/form-data")
            .build();

    public static Response postRequest(String endPoint, Integer expectedStatusCode, Object body) {
        Response response = given()
                .spec(specification)
                .body(body)
                .when().log().all()
                .post(endPoint)
                .then().log().all()
                .statusCode(expectedStatusCode)
                .extract().response();
        return response;
    }
    public static Response postRequestWithAccessToken(String endPoint, Integer expectedStatusCode, Object body, String accessToken) {
        Response response = given()
                .spec(specification)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when().log().all()
                .post(endPoint)
                .then().log().all()
                .statusCode(expectedStatusCode)
                .extract().response();
        return response;
    }
    public static Response postRequestWithImage(String endPoint, Integer expectedStatusCode, File imageFile, String accessToken) {
        Response response = given()
                .spec(specificationImage)
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("multipartFile", imageFile, "image/jpeg")
                .when().log().all()
                .post(endPoint)
                .then().log().all()
                .statusCode(expectedStatusCode)
                .extract().response();
        return response;
    }

    public static Response getRequestWithAccessToken (String endPoint, Integer expectedStatusCode, String accessToken) {
        Response response = given()
                .spec(specification)
                .header("Authorization", "Bearer " + accessToken)
                .when().log().all()
                .get(endPoint)
                .then().log().all()
                .statusCode(expectedStatusCode)
                .extract().response();
        return response;
    }
    public static Response putRequest(String endPoint, Integer expectedStatusCode, Object body, String accessToken) {
        Response response = given()
                .spec(specification)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when().log().all()
                .put(endPoint)
                .then().log().all()
                .statusCode(expectedStatusCode)
                .extract().response();
        return response;
    }
    public static Response deleteRequest(String endPoint, Integer expectedStatusCode, String accessToken) {
        Response response = given()
                .spec(specification)
                .header("Authorization", "Bearer " + accessToken)
                .when().log().all()
                .delete(endPoint)
                .then().log().all()
                .statusCode(expectedStatusCode)
                .extract().response();
        return response;
    }
    public static Response getUserPosts(String userId, int skip, int limit, String accessToken) {
        return given()
                .spec(specification)
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("skip", skip)
                .queryParam("limit", limit)
                .when().log().all()
                .get(GET_POSTS_PATH.replace("{user_id}", userId))
                .then().log().all()
                .extract().response();
    }
    public static Response registerUser(Integer expectedStatusCode) {
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String password = "User1234";
        String role = "user";

        RegisterRequest reqBodyBuilder = RegisterRequest.builder()
                .email(email)
                .password(password)
                .confirmPassword(password)
                .role(role).build();

        Response response = postRequest(REG_PATH, expectedStatusCode, reqBodyBuilder);
        return response;
    }
    public Response createPost(Integer expectedStatusCode) {
        Faker faker = new Faker();
        String title = faker.lorem().word();
        String description = faker.lorem().sentence();
        String body = faker.lorem().paragraph();
        String imageUrl = faker.internet().image();

        String accessToken = getAdminAccessToken();

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title(title)
                .description(description)
                .body(body)
                .imageUrl(imageUrl)
                .draft(false)
                .build();

        return postRequestWithAccessToken(CREATE_POST_PATH, expectedStatusCode, postCreateRequest, accessToken);
    }

    public static String getAdminAccessToken() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("qwerty@gm.com").password("Sh123456").build();

        Response authAdminResponse = postRequest(AUTH_PATH, 200, loginRequest);
        return authAdminResponse.jsonPath().getString("accessToken");
    }
}
