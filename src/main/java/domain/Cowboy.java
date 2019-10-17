package domain;

import lombok.Data;

@Data
public class Cowboy {
    private String name;
    private volatile int health;
    private int damage;

    public boolean isDead() {
        return this.health <= 0;
    }

    public synchronized void takeHit(int damage) {
        System.out.println(this.getName() + " with health [" + this.health + "] receives a damage with damage [" + damage + "]");
        setHealth(this.health - damage);
    }

    public boolean hasSameName(Cowboy cowboy) {
        return this.getName().equals(cowboy.getName());
    }

    @Override
    public String toString() {
        return "Cowboy{" +
                "name='" + name + '\'' +
                ", health=" + health +
                ", damage=" + damage +
                '}';
    }
}
