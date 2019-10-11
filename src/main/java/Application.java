import domain.Cowboy;
import provider.CowboysProvider;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class Application {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Game Start");

        var cowboysProvider = new CowboysProvider();
        var gameSate = new ConcurrentHashMap<String, Cowboy>(5);

        final var cowboys = cowboysProvider.getCowboys();

        for (var cowboy : cowboys) {
            System.out.println("Adding cowboy: " + cowboy);
            gameSate.put(cowboy.getName(), cowboy);
        }

        var latch = new CountDownLatch(4);

        for (var cowboy : gameSate.values()) {
            Executors.newFixedThreadPool(1).execute(() -> {
                while (true) {
                    var stillAlive = gameSate.values().stream().filter(Cowboy::isAlive).count();

                    if (!cowboy.isAlive() || stillAlive == 1) {
                        latch.countDown();
                        break;
                    }

                    gameSate.entrySet().stream()
                            .filter(e -> !e.getKey().equals(cowboy.getName()) && e.getValue().isAlive())
                            .map(Map.Entry::getValue)
                            .findFirst()
                            .ifPresent(selectedCowboy -> {
                                System.out.println(cowboy.getName() + " selected the target: " + selectedCowboy.getName());
                                selectedCowboy.takeHit(cowboy.getDamage());
                                gameSate.put(selectedCowboy.getName(), selectedCowboy);
                            });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        latch.await();

        gameSate.values().stream().filter(Cowboy::isAlive).findFirst().ifPresent(winner -> System.out.println("WINNER: " + winner));
        System.out.println("Game End");
    }
}
