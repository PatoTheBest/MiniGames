package me.patothebest.gamecore.leaderboards;

public class TopEntry {

    private final String name;
    private final Integer amount;

    public TopEntry(String name, Integer amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public Integer getAmount() {
        return amount;
    }
}
