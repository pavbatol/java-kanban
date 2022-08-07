package managers;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class ScheduleTest {


    @Test
    void temp() {
        long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        String startStr = String.format("start: %7d, %s\n" , start, LocalDateTime.now());

        Schedule schedule = new Schedule(1);

//        System.out.println(schedule.occupieds.size());
//        schedule.occupieds.forEach((k, v) -> System.out.printf("%s = %s%n", k, v));

//        schedule.occupiedTimes.forEach((k, v) -> System.out.printf("%s = %s%n", k, v));



//        List<String> times = new ArrayList<>(schedule.occupiedTimes.keySet());
//        times.sort((o1, o2) -> LocalDateTime.parse(o1).isAfter(LocalDateTime.parse(o2)) ? 1
//                : LocalDateTime.parse(o1).isBefore(LocalDateTime.parse(o2)) ? -1 : 0);


//        times.forEach(System.out::println);



//        long count = times.stream()
//                        //.takeWhile(s -> s.contains("2022"))
//                        .limit(20)
//                        .peek(System.out::println)
//                        .count();
//        System.out.println(count);


        long count = schedule.occupiedTimes.entrySet().stream()
                .limit(20)
                .peek(System.out::println)
                .count();
        System.out.println(count);

        long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        System.out.printf(startStr);
        System.out.printf("end: %15d, %s\n" , end, LocalDateTime.now());
        System.out.println("Заняло: " + ((end -start) / 1000.) + " sec");
    }
}