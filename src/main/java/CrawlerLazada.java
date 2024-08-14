import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.review.Item;
import dto.review.ItemResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CrawlerLazada {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        // Create an ArrayNode to hold all responses
        List<ItemResult> itemResults = new ArrayList<>();
        List<String> iTemUse = new ArrayList<>();

        for (int i = 1; i <= 80; i++) {
            try {
                int sleepTime = ThreadLocalRandom.current().nextInt(3056, 6001);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Accumulate the response for each page
            System.out.println("------------Page product: " + i);
            JsonNode responseJson = callApiGetListProduct(i);
            JsonNode objectMods = responseJson.get("mods");
            List<Item> listItems = objectMapper.convertValue(objectMods.get("listItems"), new TypeReference<>() {});
            System.out.println("Begin-----------------get Product");
            System.out.println("listItems: " + listItems.size());
            listItems.forEach(item -> {
                if (item.getReview() == null || item.getReview().isEmpty()) {
                    return;
                }
                String itemId = item.getItemId();
                if (iTemUse.contains(itemId)) {
                    return;
                }
                iTemUse.add(itemId);
                System.out.println("Item use Id: " + iTemUse);
                String itemSoldCntShow = item.getItemSoldCntShow();
                String review = item.getReview();
                JsonNode model = null;
                int j = 1;
                System.out.println("Begin-----------------get Review");
                do {
                    // Accumulate the response for each page
                    JsonNode responseJsonReview = callApiGetReviewList(itemId, j);
                    if (responseJsonReview != null) {
                        model = responseJsonReview.get("model");
                        if (model != null) {
                            if("null".equals(model.toString())) {
                                break;
                            }
                            List<Item> items = objectMapper.convertValue(model.get("items"), new TypeReference<>() {});
                            if (items != null && !items.isEmpty()) {
                                System.out.println("items: " + items.size());
                                items.forEach(itemReview -> {
                                    System.out.println("Review Content: " + itemReview.getReviewContent());
                                    System.out.println("Rating: " + itemReview.getRating());
                                    System.out.println("Review Time: " + itemReview.getReviewTime());
                                    System.out.println("Item Sold Count Show: " + itemSoldCntShow);
                                    System.out.println("Item Id: " + itemId);
                                    ItemResult itemResult = new ItemResult();
                                    itemResult.setId(itemId);
                                    itemResult.setComment(itemReview.getReviewContent());
                                    itemResult.setRating(itemReview.getRating());
                                    itemResult.setReviewTime(itemReview.getReviewTime());
                                    itemResult.setOrderCount(itemSoldCntShow);
                                    itemResult.setTotalReview(review);
                                    itemResults.add(itemResult);
                                });
                            } else {
                                break;
                            }
                        }
                    }
                    j++;
                    System.out.println("Page: " + j);
                } while (model != null);

            });
            // Save the results for the current iteration to a file
            saveResponsesToFile(itemResults, "responses_page_" + i + ".json");
        }
    }

    private static JsonNode callApiGetListProduct(int pageNo) {
        try {
            try {
                int sleepTime = ThreadLocalRandom.current().nextInt(2056, 5001);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String apiUrl = String.format(
                    "https://www.lazada.vn/hasaki-vn-chinh-hang/?ajax=true&from=wangpu&langFlag=vi&page=%s&pageTypeId=2&q=All-Products", pageNo);

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set the Cookie header with your values
            String cookieValue = "client_type=desktop; " +
                    "_tb_token_=e5e6bd8e7ba3b; " +
                    "isg=BJubqGcVeZmSL4VteEByFUa7KvkFcK9y0QISL43YHhqxbLpOFUBtwwqvBtRizAdq; " +
                    "lzd_cid=18f13a2c-ba6e-43ed-857d-37c90df75a8d; " +
                    "lzd_sid=107c3ee3da0658736a4810bce8c047d0; " +
                    "tfstk=f0wiYj9NC2TS_StASJD1MSWwDV1LfFMjArpxkx3VT2uBWFpT0iD0jr2AfooYiy4_odI_W5eHgy03BrFTDn235rcYWAW_JyFUOKhOlOaLJWPI7qnTDt045Alb6q3toxqbrMQReTE_fxMNyaBRlx7q8Y9Zuf8xYvofxV7kTTE_fH-QBKfPeIjzXpSiurlZTkoq8c84ujkFxmov7IJaukSnc2Jw7moZTXoScKRwzVtEWrz_LNyiOxEL3QwEjf0D62v08J9-_4RoRKvmS0cMZluH3Ky3OZfdp4XH82E4VSl3nO9thkFuoSzF0Ku0T04o2PBeIxPU-8czAavsSWz_nAh5nnm3ZkzgBxfkfDegY8G0CNvESyZUwAwPRK4zvuesO-bk8qNtVvogtiRE7jSzLBRzzgJjYieHGIGZAD0WE-svWr73UQIhxQPS_DiNeMjHGhhZAD0RxMAP1foIbTC..; " +
                    "x5sec=7b22617365727665722d6c617a6164613b33223a22307c434d667638625547454b79496a506a2f2f2f2f2f2f77457769706d5a392f762f2f2f2f2f41513d3d3b617c434c507a38625547455057432b756f4749676c795a574e686348526a614745772f3871586d766a2f2f2f2f2f415570514d444d774f575a6d4d4441774d4441774d4441774d4441774d4441774d4441774d4441774d4441774d4441774d4441784d4441774d4441774d4441774d4441304e7a4d305a4459344e4459334f4467324e6a45774f574a6a4e4749304e3249774d4441774d4441774d44413d222c22733b32223a2266623465666139636337363838353633227d";
            connection.setRequestProperty("Cookie", cookieValue);
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            connection.setRequestProperty("Referer", "https://my.lazada.vn/");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return objectMapper.readTree(response.toString());
            } else {
                System.out.println("GET request failed for page " + pageNo);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JsonNode callApiGetReviewList(String itemId, int pageNo) {
        try {
            try {
                int sleepTime = ThreadLocalRandom.current().nextInt(1600, 3401);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String apiUrl = String.format(
                    "https://my.lazada.vn/pdp/review/getReviewList?itemId=%s&pageSize=40&filter=0&sort=0&pageNo=%s", itemId, pageNo);

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set the request method (GET)
            connection.setRequestMethod("GET");

            // Set the Cookie header with your values
            String cookieValue = "client_type=desktop; " +
                    "_tb_token_=e5e6bd8e7ba3b; " +
                    "isg=BJubqGcVeZmSL4VteEByFUa7KvkFcK9y0QISL43YHhqxbLpOFUBtwwqvBtRizAdq; " +
                    "lzd_cid=18f13a2c-ba6e-43ed-857d-37c90df75a8d; " +
                    "lzd_sid=107c3ee3da0658736a4810bce8c047d0; " +
                    "tfstk=fHsScciNF6IVjl6XtUe2lHAYNIxCP_Zavv9dI9nrp3KRAkdfM8DUrWxClO12wQKUt9Cfd1gFU8mPGX1NBpbyLaoI9ifS4D-8qs5VkZmezWqlgs7AoQdeU9klo1Wt4gzkYDtkxHFa_lrZETxHvuYpkThkDLvaYDL-vXW2QmNa_lrayxKQN57E607McpAvepL-y-OvdInJ96dJkjp6IHdd9HeXDLJHp0pJyjHvIUBqVKyWiTwYJWVM5zxReIi1si9-aCrMiDn1cL6X6TUtvDIXFUsEvyagoHQRUsTNP5iWbTQGtdC-w79fVOsCWsEqVH6AWMtADSH2wNXfA3S8LuKAV_sJl6HEuUQGaNLf07iD6ZBfLE_0K4RFSpCHSMPiqQBdIGbwfkMBhN6vcgJIbCiC42MXSD9X_-wj-25LFZdcb-efaUpD3WybhVWHyKvXn-wj-2YJnK84h-gNK; " +
                    "x5sec=7b22617365727665722d6c617a6164613b33223a22307c434d667638625547454b79496a506a2f2f2f2f2f2f77457769706d5a392f762f2f2f2f2f41513d3d222c22733b32223a2261323334333439393631666362343261227d";
            connection.setRequestProperty("Cookie", cookieValue);
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            connection.setRequestProperty("Referer", "https://my.lazada.vn/");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return objectMapper.readTree(response.toString());
            } else {
                System.out.println("GET request failed for page " + pageNo);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void saveResponsesToFile(List<ItemResult> responses, String filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE)) {
            writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responses));
            System.out.println("All responses saved to " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
