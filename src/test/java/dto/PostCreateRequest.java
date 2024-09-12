package dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCreateRequest {
    private String title;
    private String description;
    private String body;
    private String imageUrl;
    private String publishDate;
    private Boolean draft;
}
