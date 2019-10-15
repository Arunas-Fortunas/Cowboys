import domain.Cowboy;
import provider.CowboysProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final int COWBOYS_COUNT = 5;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Game Start");
        var gameState = new ConcurrentHashMap<String, Cowboy>(COWBOYS_COUNT);

        var cowboys = new CowboysProvider().getCowboys();
        for (var cowboy : cowboys) {
            System.out.println("Adding cowboy: " + cowboy);
            gameState.put(cowboy.getName(), cowboy);
        }

        var latch = new CountDownLatch(4);
        var executorService = Executors.newFixedThreadPool(COWBOYS_COUNT);

        for (var shootingCowboy : cowboys) {
            executorService.execute(() -> {
                while (true) {
                    if (!shootingCowboy.isAlive() || isOnlyOneManStanding(gameState)) {
                        latch.countDown();
                        break;
                    }

                    gameState.values().stream()
                            .filter(selectedCowboy -> !shootingCowboy.hasSameName(selectedCowboy))
                            .skip((int) (gameState.size() * Math.random()))
                            .findAny()
                            .ifPresent(selectedCowboy -> {
                                System.out.println(shootingCowboy.getName() + " selected the target: " + selectedCowboy.getName());
                                selectedCowboy.takeHit(shootingCowboy.getDamage());

                                if (!selectedCowboy.isAlive()) {
                                    System.out.println("Cowboy is dead: " + selectedCowboy);
                                    gameState.remove(selectedCowboy.getName());
                                }
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

        System.out.println("WINNER: " + getWinner(gameState));
        System.out.println("Game End");

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    private static boolean isOnlyOneManStanding(Map<String, Cowboy> gameState) {
        return gameState.size() == 1;
    }

    private static Cowboy getWinner(Map<String, Cowboy> gameState) {
        if (gameState.size() != 1)
            throw new IllegalStateException("should be only one man standing");

        return gameState.values().stream().findAny().get();
    }
}
