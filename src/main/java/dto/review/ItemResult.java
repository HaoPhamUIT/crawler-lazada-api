package dto.review;


import lombok.Data;

@Data
public class ItemResult {
    private String id;
    private String comment;
    private String rating;
    private String reviewTime;
    private String totalReview;
    private String orderCount;
}
