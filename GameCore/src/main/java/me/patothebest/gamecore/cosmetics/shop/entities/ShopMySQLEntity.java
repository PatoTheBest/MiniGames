package me.patothebest.gamecore.cosmetics.shop.entities;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.cosmetics.shop.ShopItem;
import me.patothebest.gamecore.cosmetics.shop.ShopManager;
import me.patothebest.gamecore.cosmetics.shop.ShopRegistry;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;
import me.patothebest.gamecore.player.modifiers.ShopModifier;
import me.patothebest.gamecore.storage.mysql.LoadSynchronously;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;
import me.patothebest.gamecore.util.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
@LoadSynchronously
public class ShopMySQLEntity implements MySQLEntity {

    private final ShopRegistry shopRegistry;

    @Inject ShopMySQLEntity(ShopRegistry shopRegistry) {
        this.shopRegistry = shopRegistry;
    }

    /**
     * Loads a player's extra information
     *
     * @param player     the player loaded
     * @param connection the connection
     */
    @Override
    public void loadPlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(Queries.SELECT_OWNED_ITEMS);
        preparedStatement.setInt(1, player.getPlayerId());
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String shopName = resultSet.getString("shop_name");
            String itemName = resultSet.getString("item_name");
            int uses = resultSet.getInt("uses");

            ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(shopName);
            if(shopManager == null) {
                Utils.printError("Loading owned items", "Shop " + shopName + " not found!", "Player: " + player.getName());
                continue;
            }

            ShopItem shopItem = shopManager.getShopItemsMap().get(itemName);

            if(shopItem == null) {
                Utils.printError("Loading owned items", "Shop item " + itemName + " not found!", "Player: " + player.getName());
                continue;
            }

            player.buyItemUses(shopItem, uses);
        }

        resultSet.close();
        preparedStatement.close();


        preparedStatement = connection.prepareStatement(Queries.SELECT_SELECTED_ITEMS);
        preparedStatement.setInt(1, player.getPlayerId());
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String shopName = resultSet.getString("shop_name");
            String itemName = resultSet.getString("item_name");

            ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(shopName);
            if(shopManager == null) {
                Utils.printError("Loading selected items", "Shop " + shopName + " not found!", "Player: " + player.getName());
                continue;
            }

            ShopItem shopItem = shopManager.getShopItemsMap().get(itemName);

            if(shopItem == null) {
                Utils.printError("Loading selected items", "Shop item " + itemName + " not found!", "Player: " + player.getName());
                continue;
            }

            player.selectItem(shopItem);
        }

        resultSet.close();
        preparedStatement.close();
    }

    /**
     * Saves a player's extra information
     *
     * @param player     the player to save
     * @param connection the connection
     */
    @Override
    public void savePlayer(CorePlayer player, Connection connection) throws SQLException {
    }

    @Override
    public void updatePlayer(CorePlayer player, Connection connection, PlayerModifier updatedType, Object... args) throws SQLException {
        if(!(updatedType instanceof ShopModifier)) {
            return;
        }

        ShopItem shopItem = (ShopItem) args[0];

        switch ((ShopModifier)updatedType) {
            case BOUGHT_ITEM: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.INSERT_OWNED_ITEM);
                preparedStatement.setInt(1, player.getPlayerId());
                preparedStatement.setString(2, shopRegistry.getShopItemsManagers().get(shopItem.getClass()).getShopName());
                preparedStatement.setString(3, shopItem.getName());
                preparedStatement.setInt(4, (Integer) args[1]);
                preparedStatement.execute();
                preparedStatement.close();
                break;
            } case BOUGHT_ITEM_USES:
              case USED_ITEM: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.UPDATE_OWNED_ITEM);
                preparedStatement.setInt(1, (Integer) args[1]);
                preparedStatement.setInt(2, player.getPlayerId());
                preparedStatement.setString(3, shopRegistry.getShopItemsManagers().get(shopItem.getClass()).getShopName());
                preparedStatement.setString(4, shopItem.getName());
                preparedStatement.execute();
                preparedStatement.close();
                break;
            } case DEPLETED_ITEM: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.REMOVE_OWNED_ITEM);
                preparedStatement.setInt(1, player.getPlayerId());
                preparedStatement.setString(2, shopRegistry.getShopItemsManagers().get(shopItem.getClass()).getShopName());
                preparedStatement.setString(3, shopItem.getName());
                preparedStatement.execute();
                preparedStatement.close();
                break;
            } case SELECT_DEFAULT: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.INSERT_SELECTED_ITEM);
                preparedStatement.setInt(1, player.getPlayerId());
                preparedStatement.setString(2, shopRegistry.getShopItemsManagers().get(shopItem.getClass()).getShopName());
                preparedStatement.setString(3, shopItem.getName());
                preparedStatement.execute();
                preparedStatement.close();
                break;
            } case UPDATE_SELECT_DEFAULT: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.UPDATE_SELECTED_ITEM);
                preparedStatement.setString(1, shopItem.getName());
                preparedStatement.setInt(2, player.getPlayerId());
                preparedStatement.setString(3, shopRegistry.getShopItemsManagers().get(shopItem.getClass()).getShopName());
                preparedStatement.execute();
                preparedStatement.close();
                break;
            } case REMOVE_SELECTED_ITEM: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.REMOVE_SELECTED_ITEM);
                preparedStatement.setInt(1, player.getPlayerId());
                preparedStatement.setString(2, shopRegistry.getShopItemsManagers().get(shopItem.getClass()).getShopName());
                preparedStatement.execute();
                preparedStatement.close();
                break;
            }
        }
    }

    /**
     * Gets all the statements needed to create the
     * table(s) for the specific entity.
     *
     * @return the create table statements
     */
    @Override
    public String[] getCreateTableStatements() {
        return new String[]{Queries.CREATE_TABLE_SHOP_ITEMS, Queries.CREATE_TABLE_SELECTED_ITEMS};
    }
}