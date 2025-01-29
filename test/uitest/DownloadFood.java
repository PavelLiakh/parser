package uitest;

import com.codeborne.selenide.ElementsCollection;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static uitest.Params.longDelay;

public class DownloadFood extends AbstractHangmanTest {
    @Before
    public void startGame() {
    }


    @Test
    public void testresult_bolt() throws InterruptedException {
        open("https://bolt.eu/en-ge/food/");
        Thread.sleep(5000);
        setBoltAddress();

        ElementsCollection providerCards = $$("div[data-testid='components.ProviderCard.view']");

        List<Map<String, String>> providerInfoList = providerCards.stream().map(card -> {

            try {
                if (card.$("span[data-testid='components.ProviderCard.providerName']").exists()
                        && card.$("div[data-testid='components.ProviderCard.providerRatingBadge'] span:nth-of-type(2)").exists()
                        && card.$("div[data-testid='components.ProviderCard.providerRatingBadge'] span:nth-of-type(3)").exists()
                        && card.parent().$("img").exists()) {
                } else {
                    return Map.of("error", "Some elements are missing");
                }

                String name = card.$("span[data-testid='components.ProviderCard.providerName']").getText();

                String rating = card.$("div[data-testid='components.ProviderCard.providerRatingBadge'] span:nth-of-type(2)").getText();
                String reviews = card.$("div[data-testid='components.ProviderCard.providerRatingBadge'] span:nth-of-type(3)").getText();
                String imageUrl = card.parent().$("img").getAttribute("src");

                var info = Map.of(
                        "Name", name,
                        "Rating", rating,
                        "Reviews", reviews,
                        "Image URL", imageUrl
                );
                var print = String.format("Name: %s, Rating: %s, Reviews: %s, Image URL: %s",
                        info.get("Name"),
                        info.get("Rating"),
                        info.get("Reviews"),
                        info.get("Image URL"));
                System.out.println(print);
                return info;
            } catch (Exception e) {
                return Map.of("error", e.getMessage());
            }
        }).collect(Collectors.toList());

        String formattedText = providerInfoList.stream()
                .map(info -> String.format("Name: %s, Rating: %s, Reviews: %s, Image URL: %s",
                        info.get("Name"),
                        info.get("Rating"),
                        info.get("Reviews"),
                        info.get("Image URL")))
                .collect(Collectors.joining("\n"));

        System.out.println(formattedText);
    }

    @Test
    public void download_food_wolt() throws InterruptedException {
        open("https://wolt.com/en/discovery/restaurants");
        Thread.sleep(longDelay);

        setWoltAddress();
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
