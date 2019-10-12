import domain.Cowboy;
import provider.CowboysProvider;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final int COWBOYS_COUNT = 5;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Game Start");

        var cowboysProvider = new CowboysProvider();
        var gameState = new ConcurrentHashMap<String, Cowboy>(COWBOYS_COUNT);

        final var cowboys = cowboysProvider.getCowboys();

        for (var cowboy : cowboys) {
            System.out.println("Adding cowboy: " + cowboy);
            gameState.put(cowboy.getName(), cowboy);
        }

        var latch = new CountDownLatch(4);
        var executorService = Executors.newFixedThreadPool(COWBOYS_COUNT);

        for (var cowboy : gameState.values()) {
            executorService.execute(() -> {
                while (true) {
                    if (!cowboy.isAlive() || isOnlyOneManStanding(gameState)) {
                        latch.countDown();
                        break;
                    }

                    gameState.entrySet().stream()
                            .filter(e -> !e.getKey().equals(cowboy.getName()) && e.getValue().isAlive())
                            .map(Map.Entry::getValue)
                            .findAny()
                            .ifPresent(selectedCowboy -> {
                                System.out.println(cowboy.getName() + " selected the target: " + selectedCowboy.getName());
                                selectedCowboy.takeHit(cowboy.getDamage());
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

        gameState.values().stream().filter(Cowboy::isAlive).findFirst().ifPresent(winner -> System.out.println("WINNER: " + winner));
        System.out.println("Game End");

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    private static boolean isOnlyOneManStanding(Map<String, Cowboy> gameState) {
        return gameState.values().stream().filter(Cowboy::isAlive).count() == 1;
    }
}
