package dto.review;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    private String reviewContent;
    private String rating;
    private String reviewTime;
    private String review;
    private String itemSoldCntShow;
    private String itemId;
}
