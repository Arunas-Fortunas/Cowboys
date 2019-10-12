package domain;

import lombok.Data;

@Data
public class Cowboy {
    private String name;
    private volatile int health;
    private volatile int damage;

    public boolean isAlive() {
        return this.health > 0;
    }

    public void takeHit(int hit) {
        System.out.println(this.getName() + " with health [" + this.health + "] receives a hit with damage [" + hit + "]");
        setHealth(this.health - hit);
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