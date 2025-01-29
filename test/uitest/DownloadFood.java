package uitest;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
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
//        Thread.sleep(longDelay);
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

        ElementsCollection shopTiles = $$(By.xpath("//*[@data-variant='dense']"));
        for (SelenideElement shopTile : shopTiles) {
            Map<String, String> shopData = parseShopTile(shopTile);
            FoodDataWriter.FoodRecord foodRecord = buildFoodRecord(shopData);
            foodRecord.site = "Wolt";
            foodRecords.add(foodRecord);
        }


        FoodDataWriter.writeToCSV(foodRecords);
        int a =0;

    }

    private static Map<String, String> parseShopTile(SelenideElement shopTile) {
        Map<String, String> shopData = new HashMap<>();

        var link = shopTile.findAll(By.tagName("a")).get(1);

        shopData.put("shopName", link.text());
        shopData.put("link", link.getAttribute("href"));
//        shopData.put("deliveryTime", "//div[matches(text(), '^\\d{1,2}-\\d{1,2}$')]");

        return shopData;
    }

    private static FoodDataWriter.FoodRecord buildFoodRecord(Map<String, String> shopData) {
        FoodDataWriter.FoodRecord foodRecord = new FoodDataWriter.FoodRecord();
        foodRecord.shopName = shopData.get("shopName");
        foodRecord.name = shopData.get("category");
        foodRecord.price = shopData.get("price");
        foodRecord.link = shopData.get("link");
        return foodRecord;
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
