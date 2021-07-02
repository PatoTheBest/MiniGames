package me.patothebest.gamecore.experience;

public class ExperienceCalculator {

    private final long base;
    private final long factor;
    private final long left;
    private final long baseFactDiff;
    private final long baseFactDiffSquared;
    private final long discRight;
    private final long denominator;

    public ExperienceCalculator(long base, long factor) {
        this.base = base;
        this.factor = factor;
        left = this.factor - (2 * this.base);
        baseFactDiff = -this.factor + (2* this.base);
        baseFactDiffSquared = (int) Math.pow(baseFactDiff, 2);
        discRight = 4 * this.factor * 2;
        denominator = 2 * this.factor;
    }

    public double calculateLevelProgress(long experience) {
        int level = expToLevelFloor(experience);
        int nextLevel = level + 1;
        long nextLevelExp = levelToExp(nextLevel);
        long currLevelExp = levelToExp(level);
        double nextLevelExpNormal = nextLevelExp - currLevelExp;

        return (experience - currLevelExp) / nextLevelExpNormal;
    }

    public double expToLevel(long exp) {
        return ((left + Math.sqrt(baseFactDiffSquared + (discRight *exp))) / denominator);
    }

    public int expToLevelFloor(long exp) {
        return (int) Math.floor(expToLevel(exp));
    }

    public long levelToExp(int level) {
        return ((factor * level * level) + (level * baseFactDiff)) / 2;
    }

    public long expToNextLevel(long exp) {
        return levelToExp(expToLevelFloor(exp) + 1) - exp;
    }

    public long getBase() {
        return base;
    }

    public long getFactor() {
        return factor;
    }
}
