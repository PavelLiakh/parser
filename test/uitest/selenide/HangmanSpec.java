package uitest.selenide;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import uitest.AbstractHangmanTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class HangmanSpec extends AbstractHangmanTest {
  @Before
  public void startGame() throws InterruptedException {
    open("https://bolt.eu/en-ge/food/");
    Thread.sleep(5000);
    var address = $$(By.tagName("input")).findBy(attribute("placeholder","Enter your address"));
//address.scrollTo();
    address.setValue("Otar Oniashvillis 30");
    Thread.sleep(5000);
    address.sendKeys(Keys.ARROW_DOWN);
    address.sendKeys(Keys.ENTER);
    Thread.sleep(6000);

    result();
  }

  private String result() {
    ElementsCollection providerCards = $$("div[data-testid='components.ProviderCard.providerRatingBadge']");

    List<Map<String, String>> providerInfoList = providerCards.stream().map(card -> {
      String name = card.$("span.provider-name-class").getText();
      String rating = card.$("span:nth-of-type(2)").getText();
      String reviews = card.$("span:nth-of-type(3)").getText();
      String imageUrl = card.parent().$("img").getAttribute("src");

      return Map.of(
              "Name", name,
              "Rating", rating,
              "Reviews", reviews,
              "Image URL", imageUrl
      );
    }).collect(Collectors.toList());

    String formattedText = providerInfoList.stream()
            .map(info -> String.format("Name: %s, Rating: %s, Reviews: %s, Image URL: %s",
                    info.get("Name"),
                    info.get("Rating"),
                    info.get("Reviews"),
                    info.get("Image URL")))
            .collect(Collectors.joining("\n"));

    System.out.println(formattedText);
    return formattedText;
  }

  @Test
  public void showsTopicAndMaskedWordAtTheBeginning() {
    $("#topic").shouldHave(text("house"));
    $("#wordInWork").shouldHave(text("____"));
  }

  @Test
  public void userCanGuessLetters() {
    letter("S").click();
    $("#wordInWork").shouldHave(text("s___"));
    letter("S").shouldHave(cssClass("used"));
  }

  @Test
  public void userWinsWhenAllLettersAreGuessed() {
    letter("S").click();
    letter("O").click();
    letter("F").click();
    letter("A").click();
    $("#gameWin").shouldBe(visible);
  }

  @Test
  public void userHasNoMoreThan6Tries() {
    letter("B").click();
    letter("D").click();
    letter("E").click();
    letter("G").click();
    letter("H").click();
    letter("I").click();
    letter("J").click();
    letter("B").shouldHave(cssClass("nonused"));
    $("#gameLost").shouldBe(visible);
  }

  @Test
  public void userCanChooseLanguage() {
    $(By.linkText("EST")).click();
    $("#topic").shouldHave(text("maja"));
    $("#wordInWork").shouldHave(text("____"));
    $$("#alphabet .letter").shouldHave(size(27));

    $(By.linkText("RUS")).click();
    $("#topic").shouldHave(text("дом"));
    $("#wordInWork").shouldHave(text("______"));
    $$("#alphabet .letter").shouldHave(size(33));

    $(By.linkText("ENG")).click();
    $("#topic").shouldHave(text("house"));
    $("#wordInWork").shouldHave(text("____"));
    $$("#alphabet .letter").shouldHave(size(26));
  }

  private SelenideElement letter(String letter) {
    return $(byText(letter));
  }
}
