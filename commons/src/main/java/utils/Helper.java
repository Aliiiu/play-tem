package utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class Helper {
    public static String generatePin(){
        Random random = new Random();
        int pin = random.nextInt(10000);
        return String.format("%04d", pin);
    }

    public static ObjectNode createResponse(Object response, boolean ok) {
        ObjectNode result = Json.newObject();
        result.put("isSuccessful", ok);
        if (response instanceof String) {
            result.put("body", (String) response);
        } else {
            result.putPOJO("body", response);
        }
        return result;
    }

    // Helper methods for checking working day and working hours
    public static boolean isWorkingDay(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    public static boolean isWorkingHours(LocalDateTime dateTime) {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        LocalTime currentTime = dateTime.toLocalTime();
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }
}
