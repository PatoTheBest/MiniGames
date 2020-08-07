package me.patothebest.gamecore.tests;

import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.util.ServerVersion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ServerVersion.class)
public class MaterialTest {

    @Test
    public void testMaterial() {
        PowerMockito.mockStatic(ServerVersion.class);
        when(ServerVersion.getVersion()).thenReturn("v1_15_R1");
        when(ServerVersion.getBukkitVersion()).thenReturn("git-Paper-29 (MC: 1.15.1)");

//        ItemStack itemStack = Utils.itemStackFromString("wool:14,14");
//        Assert.assertSame(itemStack.getType(), Material.RED_WOOL);
//        Assert.assertEquals(14, itemStack.getAmount());

        ItemStack itemStack = Utils.itemStackFromString("wool,14");
        Assert.assertSame(itemStack.getType(), Material.WHITE_WOOL);
        Assert.assertEquals(14, itemStack.getAmount());

//        itemStack = Utils.itemStackFromString("wool:14");
//        Assert.assertSame(itemStack.getType(), Material.RED_WOOL);
//        Assert.assertEquals(1, itemStack.getAmount());
//
//        itemStack = Utils.itemStackFromString("wool:1,2");
//        Assert.assertSame(itemStack.getType(), Material.ORANGE_WOOL);
//        Assert.assertEquals(2, itemStack.getAmount());
//
//        itemStack = Utils.itemStackFromString("wool:2,1");
//        Assert.assertSame(itemStack.getType(), Material.MAGENTA_WOOL);
//        Assert.assertEquals(1, itemStack.getAmount());
//
//        itemStack = Utils.itemStackFromString("wool:2,2");
//        Assert.assertSame(itemStack.getType(), Material.MAGENTA_WOOL);
//        Assert.assertEquals(2, itemStack.getAmount());
    }

    @Test
    public void testMaterial2() {
//        PowerMockito.mockStatic(ServerVersion.class);
//        when(ServerVersion.getVersion()).thenReturn("v1_8_R3");
//        when(ServerVersion.getBukkitVersion()).thenReturn("git-Paper-29 (MC: 1.8.8)");
//
//        ItemStack itemStack = Utils.itemStackFromString("wool:14,14");
//        Assert.assertSame(itemStack.getType(), Material.RED_WOOL);
//        Assert.assertEquals(14, itemStack.getAmount());
//
//        itemStack = Utils.itemStackFromString("wool,14");
//        Assert.assertSame(itemStack.getType(), Material.WHITE_WOOL);
//        Assert.assertEquals(14, itemStack.getAmount());
//
//        itemStack = Utils.itemStackFromString("wool:14");
//        Assert.assertSame(itemStack.getType(), Material.RED_WOOL);
//        Assert.assertEquals(1, itemStack.getAmount());
//
//        itemStack = Utils.itemStackFromString("wool:1,2");
//        Assert.assertSame(itemStack.getType(), Material.ORANGE_WOOL);
//        Assert.assertEquals(2, itemStack.getAmount());
//
//        itemStack = Utils.itemStackFromString("wool:2,1");
//        Assert.assertSame(itemStack.getType(), Material.MAGENTA_WOOL);
//        Assert.assertEquals(1, itemStack.getAmount());
//
//        itemStack = Utils.itemStackFromString("wool:2,2");
//        Assert.assertSame(itemStack.getType(), Material.MAGENTA_WOOL);
//        Assert.assertEquals(2, itemStack.getAmount());
    }
}
