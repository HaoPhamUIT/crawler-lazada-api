package dto.review;

import lombok.Data;

import java.util.List;

@Data
public class Model {
   private List<Item> items;
   private Item item;
}
