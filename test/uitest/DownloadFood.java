package uitest;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

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

        $$(By.xpath("//button[@data-test-id='closeFilterButton']")).get(0).click();

        downloadWoltFood();
    }

    private void downloadWoltFood() throws InterruptedException {
        FoodDataWriter.writeToCSV(new ArrayList<>());


        List<Map<String, String>> shops = new ArrayList<>();
        ElementsCollection shopTiles = $$(By.xpath("//*[@data-variant='dense']"));
        for (SelenideElement shopTile : shopTiles) {
            shopTile.scrollTo();
            shops.add(parseShopTile(shopTile));
        }

        for (Map<String, String> shopData : shops) {
            downloadShopFood(shopData);
        }
    }

    private void downloadShopFood(Map<String, String> shopData) throws InterruptedException {
        var link = shopData.get("link");

        open(link + "/collections/popular");
        Thread.sleep(longDelay);

        $$(By.xpath("//*[@data-test-id='ItemCard']"))
                .stream()
                .toList()
                .forEach(food ->
                        getAndSaveFood(shopData, food)
                );
    }

    private void getAndSaveFood(Map<String, String> shopData, SelenideElement foodTile) {
        foodTile.scrollTo();
        Document doc = Jsoup.parse(foodTile.innerHtml());
        var name = doc.getAllElements()
                .stream()
                .filter(element -> element.text() != null)
                .filter(element -> element.tagName().equals("h3"))
                .toList()
                .get(0)
                .text();

        var price = doc.getAllElements()
                .stream()
                .filter(element -> element.hasAttr("aria-label"))
                .toList().get(0)
                .text()
                .replace("GEL ", "");

        FoodDataWriter.FoodRecord foodRecord = new FoodDataWriter.FoodRecord();
        foodRecord.site = "Wolt";
        foodRecord.shopName = shopData.get("shopName");
        foodRecord.link = shopData.get("link");
        foodRecord.name = name;
        foodRecord.price = price;

        FoodDataWriter.appendToCSV(foodRecord);
    }

    private static Map<String, String> parseShopTile(SelenideElement shopTile) {
        Map<String, String> shopData = new HashMap<>();

        var link = shopTile.findAll(By.tagName("a")).get(1);

        shopData.put("shopName", link.text());
        shopData.put("link", link.getAttribute("href"));

        return shopData;
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
