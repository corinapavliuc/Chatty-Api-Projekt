package dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostUpdateRequest {
    private String title;
    private String description;
    private String body;
    private String imageUrl;
    private Boolean draft;
}
