package uitest;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static uitest.Params.longDelay;
import static uitest.Params.shortDelay;

public class DownloadFood extends AbstractHangmanTest {
    List<FoodDataWriter.FoodRecord> foodRecords = new ArrayList<>();

    @Before
    public void prepare_food_download() {
    }

    @Test
    public void download_food_bolt() throws InterruptedException {
        setBoltAddress();
    }

    @Test
    public void download_food_wolt() throws InterruptedException {
        open("https://wolt.com/en/discovery/restaurants");
        Thread.sleep(shortDelay);

//        setWoltAddress();
        Thread.sleep(longDelay);
//        setWoltFilter();
//        Thread.sleep(longDelay);
        downloadWoltFood();
    }

    private void setWoltFilter() throws InterruptedException {
        $$(By.xpath("//button[@data-test-id='sorting.button']")).get(0).click();
        Thread.sleep(shortDelay);

        var priceRange = $$(By.xpath("//label[@data-test-id='categories.price-range-2.label']")).get(0);
        priceRange.click();
        priceRange.scrollTo();

//        $$(By.xpath(String.format("//input[@id=//label[text()='%s']/@for]", "Delivery price"))).get(0).click();

        $$(By.xpath("//button[@data-test-id='closeFilterButton']")).get(0).click();

        downloadWoltFood();
    }

    private void downloadWoltFood() {
        var allShops = $$(By.xpath("//*[@data-test-id='VenueVerticalListGrid']")).get(0);
        FoodDataWriter.writeToCSV(foodRecords);


        ElementsCollection shopTiles = $$(By.xpath("//*[@data-variant='dense']"));
        for (SelenideElement shopTile : shopTiles) {
            shopTile.scrollTo();
            var shopData = parseShopTile(shopTile);
            var foods = getShopFood(shopData.get("link"));

            foods.forEach(food -> food.putAll(shopData));
            FoodDataWriter.FoodRecord foodRecord = new FoodDataWriter.FoodRecord();
            foodRecord.site = "Wolt";
            foodRecord.shopName = shopData.get("shopName");
            foodRecord.link = shopData.get("link");
            foodRecords.add(foodRecord);

            FoodDataWriter.appendToCSV(foodRecord);
        }
    }

    private static Map<String, String> parseShopTile(SelenideElement shopTile) {
        Map<String, String> shopData = new HashMap<>();

        var link = shopTile.findAll(By.tagName("a")).get(1);

        shopData.put("shopName", link.text());
        shopData.put("link", link.getAttribute("href"));

        return shopData;
    }

    private List<Map<String, String>> getShopFood(String shopLink) {
        // Download the HTML page
        var url = shopLink + "/collections/popular";
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Find all elements with data-test-id="ItemCard"
        Elements itemCards = doc.select("[data-test-id=ItemCard]");

        List<Map<String, String>> result = new ArrayList<>();

        // Extract and print name and price for each item card
        for (Element card : itemCards) {
            String name = card.select("[data-test-id=ImageCentricProductCard\\.Title]").text();
            String price = card.select("[data-test-id=ImageCentricProductCardPrice]").text();

            var food = new HashMap<String, String>();
            food.put("name", name);
            food.put("price", price);
            result.add(food);
        }

        return result;
    }


    private void setBoltAddress() throws InterruptedException {
        var address = $$(By.tagName("input")).findBy(attribute("placeholder", "Enter your address"));
        address.setValue(Params.address);
        Thread.sleep(longDelay);
        address.sendKeys(Keys.ARROW_DOWN);
        address.sendKeys(Keys.ENTER);
        Thread.sleep(longDelay);
    }

    private void setWoltAddress() throws InterruptedException {
        $$(By.tagName("button")).findBy(attribute("data-test-id", "header.address-select-button")).click();
        Thread.sleep(longDelay);

        $$(By.id("address-query-input")).get(0).setValue(Params.address);
        Thread.sleep(longDelay);
        $$(By.xpath("//*[@role='option']")).get(0).click();
        Thread.sleep(longDelay);
        $$(By.xpath("//button[@data-test-id='continue-button']")).get(0).click();
    }
}
