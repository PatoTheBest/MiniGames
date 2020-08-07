import me.patothebest.gamecore.util.ServerVersion;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ServerVersion.class)
public class MaterialTest {

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ServerVersion.class);
        when(ServerVersion.getVersion()).thenReturn("v1_8_R3");
        when(ServerVersion.getBukkitVersion()).thenReturn("git-Paper-29 (MC: 1.8.8)");
    }

    @Test
    public void test14RedWool() {
        ItemStack itemStack = Utils.itemStackFromString("wool:14,14");
        Assert.assertEquals(itemStack.getType(), Material.WOOL);
        Assert.assertEquals(itemStack.getDurability(), 14);
        Assert.assertEquals(14, itemStack.getAmount());
    }

    @Test
    public void test14WhiteWool() {
        ItemStack itemStack = Utils.itemStackFromString("wool,14");
        Assert.assertEquals(itemStack.getType(), Material.WOOL);
        Assert.assertEquals(itemStack.getDurability(), 0);
        Assert.assertEquals(14, itemStack.getAmount());
    }

    @Test
    public void testSingleRedWool() {
        ItemStack itemStack  = Utils.itemStackFromString("wool:14");
        Assert.assertEquals(itemStack.getType(), Material.WOOL);
        Assert.assertEquals(itemStack.getDurability(), 14);
        Assert.assertEquals(1, itemStack.getAmount());
    }

    @Test
    public void testOrangeWool() {
        ItemStack itemStack = Utils.itemStackFromString("wool:1,2");
        Assert.assertEquals(itemStack.getType(), Material.WOOL);
        Assert.assertEquals(itemStack.getDurability(), 1);
        Assert.assertEquals(2, itemStack.getAmount());
    }

    @Test
    public void test2MagentaWool() {
        ItemStack itemStack = Utils.itemStackFromString("wool:2,1");
        Assert.assertEquals(itemStack.getType(), Material.WOOL);
        Assert.assertEquals(itemStack.getDurability(), 2);
        Assert.assertEquals(1, itemStack.getAmount());
    }

    @Test
    public void testMagentaWool() {
        ItemStack itemStack = Utils.itemStackFromString("wool:2,2");
        Assert.assertEquals(itemStack.getType(), Material.WOOL);
        Assert.assertEquals(itemStack.getDurability(), 2);
        Assert.assertEquals(2, itemStack.getAmount());
    }

    @Test
    public void testComparator() {
        me.patothebest.gamecore.itemstack.Material material = me.patothebest.gamecore.itemstack.Material.COMPARATOR;
        Assert.assertEquals(Material.REDSTONE_COMPARATOR, material.parseMaterial());
    }

    @Test
    public void testAcaciaDoor() {
        me.patothebest.gamecore.itemstack.Material material = me.patothebest.gamecore.itemstack.Material.ACACIA_DOOR;
        Assert.assertEquals(Material.ACACIA_DOOR_ITEM, material.parseMaterial());
    }

    @Test
    public void testTerracota() {
        ItemStack itemStack = Utils.itemStackFromString("red_terracotta");
        Assert.assertEquals(itemStack.getType(), Material.STAINED_CLAY);
        Assert.assertEquals(14, itemStack.getDurability());
        Assert.assertEquals(1, itemStack.getAmount());
    }
}