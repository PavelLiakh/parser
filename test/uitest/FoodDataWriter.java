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
        String fileName = generateFileName();
        var isFirstWrite = currentFileName == null;
        currentFileName = fileName;

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            if (isFirstWrite) {
                writeHeader(writer);
            }
            writeRecords(writer, foodRecords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendToCSV(FoodRecord foodRecord) {
        if (currentFileName == null) {
            writeToCSV(List.of(foodRecord));
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(currentFileName, true))) {
            writeRecords(writer, List.of(foodRecord));
        } catch (IOException e) {
            e.printStackTrace();
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
        writer.println("Site, Shop Name, Name, Price, Link");
        for (FoodRecord food : foodRecords) {
            var record = String.format("%s,%s,%s,%s,%s",
                    food.site,
                    food.shopName,
                    food.name,
                    food.price,
                    food.link);
            record = record.replace("null", "");
            writer.println(record);
        }
    }

    public static class FoodRecord {
        String site;
        String shopName;
        String name;
        String price;
        String link;
        String deliveryTime;
    }

}



