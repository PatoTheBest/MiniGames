package me.patothebest.gamecore.treasure.reward;

import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.treasure.type.TreasureType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RewardRandomizer {

    private final IPlayer iPlayer;
    private final TreasureType treasureType;
    private final RewardFile rewardFile;

    @Inject private RewardRandomizer(@Assisted IPlayer iPlayer, @Assisted TreasureType treasureType, RewardFile rewardFile) {
        this.iPlayer = iPlayer;
        this.treasureType = treasureType;
        this.rewardFile = rewardFile;
    }

    public RewardData giveRandomThing() {
        List<Reward> rewards = new ArrayList<>();
        double totalChance = 0;
        for (Reward reward : rewardFile.getRewards().get(treasureType)) {
            if(reward.canGiveReward(iPlayer)) {
                for (int i = 0; i < reward.getChance(); i++) {
                    rewards.add(reward);
                    totalChance++;
                }
            }
        }

        if(rewards.isEmpty()) {
            return new RewardData("ERROR: Check log and rewards.yml", new ItemStackBuilder(Material.BARRIER));
        }

        Collections.shuffle(rewards);
        Reward reward = rewards.get(0);
        return reward.give(iPlayer).rareItem(reward.getChance()/totalChance < 0.01);
    }
}
