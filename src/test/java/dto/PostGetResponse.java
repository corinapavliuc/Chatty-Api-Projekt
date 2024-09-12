package dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostGetResponse {
    private String id;
    private String title;
    private String description;
    private String body;
    private String imageUrl;
    private String createdAt;
    private String updatedAt;
    private Boolean draft;
    private  UserResponse user;
}
