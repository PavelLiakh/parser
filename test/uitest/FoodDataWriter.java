package uitest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FoodDataWriter {
    private static String currentFileName;

    public static void writeToCSV(List<FoodRecord> foodRecords) {
        var isFirstWrite = currentFileName == null;
        if (isFirstWrite) {
            currentFileName = generateFileName();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(currentFileName))) {
            if (isFirstWrite) {
                writeHeader(writer);
            }
            writeRecords(writer, foodRecords);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void appendToCSV(FoodRecord foodRecord) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentFileName, true))) {
            writeRecords(writer, List.of(foodRecord));
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    private static String generateFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        return "food_data_" + now.format(formatter) + ".csv";
    }

    private static void writeHeader(PrintWriter writer) {
        writer.println("FoodName,FoodPrice,FoodCategory,ShopName,ShopLocation,ShopRating");
    }

    private static void writeRecords(PrintWriter writer, List<FoodRecord> foodRecords) {
        for (FoodRecord food : foodRecords) {
            var record = String.format("%s,%s,%s,%s,%s",
                    food.site,
                    food.shopName,
                    food.name,
                    food.price,
                    food.link);
            record = record.replace("null", "");
            writer.println(record);
            writer.flush();
        }
    }

    public static class FoodRecord {
        String site;
        String shopName;
        String name;
        String price;
        String link;
    }

}



