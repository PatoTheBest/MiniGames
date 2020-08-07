package me.patothebest.gamecore.tests;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class YAMLParserTest {

    private final String configRaw = "############################################################\n" + "# +------------------------------------------------------+ #\n" + "# |               The Towers Configuration               | #\n" + "# +------------------------------------------------------+ #\n" + "############################################################\n" + "\n" + "# When this value is set to true,\n" + "# it will output a more in-depth startup log\n" + "VERBOSE_STARTUP: true\n" + "\n" + "# Command Section\n" + "# These are the commands that players are going to be allowed to execute while playing\n" + "AllowedCommands:\n" + "- tell\n" + "- reply\n" + "- t\n" + "- r\n" + "- message\n" + "- m\n" + "- whisper\n" + "- w\n" + "\n" + "# Player Storage Section\n" + "# Here you can specify where the player profiles will be saves\n" + "# There are three options: MYSQL, FLATFILE, or none\n" + "# MYSQL will save the profile to the specified database under the table thetowers\n" + "# FLATFILE will save each other profile to plugins/TheTowersRemastered/players/uuid.yml\n" + "# NONE will disable stat saving \n" + "StorageType: NONE\n" + "# Disable if running an offline server\n" + "UseUUIDS: false\n" + "# MySQL details (will be used only if the storage type is set to MYSQL)\n" + "username: root\n" + "password: password\n" + "host: localhost\n" + "port: 3306\n" + "database: thetowers\n" + "\n" + "# Chest regeneration\n" + "# Here you can specify if the chests should be regenerated or not\n" + "# You can also specify the interval at which the chests will be regenerated (in minutes)\n" + "ChestRegeneration: true\n" + "RegenerationInterval: 1\n" + "\n" + "# Chat Section\n" + "# This is the format on how the chat for teams would be shown\n" + "# There are a few variables you can use in the chat\n" + "# This variables will only apply in-game (not in lobby)\n" + "# {group} - Players group name pulled from your permissions plugin\n" + "# {permprefix} - Permission group prefix\n" + "# {permsuffix} - Permission group suffix\n" + "# {playername} - Default player name.\n" + "# {modplayername} - Modified player name (enable this for nickanames to show)\n" + "# {msg} - The message sent.\n" + "# This is the format on how the team chat will appear\n" + "TeamChat: '&8[%teamcolor%%teamname%8] {permprefix} &6{modplayername}: &7 {msg}'\n" + "# This is the format on how the global chat would be shown\n" + "GlobalChat: '&8[&aGlobal&8] {permprefix} &6{modplayername}: &7 {msg}'\n" + "\n" + "# XP when killing players\n" + "# When a player kills another player on TheTowers\n" + "# You can set how many levels to give to the player\n" + "LevelsToGive: 1\n" + "\n" + "# Economy\n" + "# Here you can specify the amount of money to give a player when the player performs one of these actions\n" + "# WARNING: Needs vault and a compatible economy plugin\n" + "# WIP (coming soon)\n" + "MoneyEnables: false\n" + "MoneyToGive: \n" + "  ScorePoint: 5.0\n" + "  KillPlayer: 2.0\n" + "  Win: 25.0\n" + "\n" + "# Potion Effects\n" + "# Here you can specify the potion effects\n" + "# The template for adding a potion effect is\n" + "# PotionEffects:\n" + "#   DAMAGE_RESISTANCE:\n" + "#     Modifier: 127\n" + "#     Time: 8\n" + "PotionEffects:\n" + "  SPEED:\n" + "    Modifier: 1\n" + "    Time: 4\n" + "  DAMAGE_RESISTANCE:\n" + "    Modifier: 127\n" + "    Time: 8\n" + "  FAST_DIGGING:\n" + "    Modifier: 127\n" + "    Time: 8\n" + "  INCREASE_DAMAGE:\n" + "    Modifier: 15\n" + "    Time: 20\n" + "\n" + "# Signs\n" + "# Here you can edit the sign format\n" + "# This format applies to every single sign that has been created with the command\n" + "# /thetowers setup creatsign <arena>\n" + "# You can use color codes and placeholders as well as utf-8 characters\n" + "# There are a few placeholders you can use for the sign\n" + "# {arena} - The arena the sign is bound to\n" + "# {players} - The amount of players in the arena\n" + "# {min_players} - The minimum amount of players the arena needs to start\n" + "# {max_players} - The maximum players the arena can hold\n" + "signs: \n" + "  OTHER:\n" + "    - ''\n" + "    - '&7&lWaiting for'\n" + "    - '&7&lan arena'\n" + "    - ''\n" + "  ERROR: \n" + "    - ''\n" + "    - '&7&lWaiting for'\n" + "    - '&7&lan arena'\n" + "    - ''\n" + "  IN_GAME: \n" + "    - '&dJoin'\n" + "    - '{arena}'\n" + "    - '&7&l{players}/{max_players}'\n" + "    - '&5&lIn-Game'\n" + "  WAITING: \n" + "    - '&dJoin'\n" + "    - '{arena}'\n" + "    - '&7&l{players}/{max_players}'\n" + "    - '&5&lStarting soon'\n" + "  RESTARTING: \n" + "    - ''\n" + "    - '&c&lRestarting'\n" + "    - 'Please wait'\n" + "    - ''\n" + "\n" + "# Exit Teleportation\n" + "# You can specify two locations where you want the player to be teleported back to\n" + "# MainLobby - You can set a main lobby with the command /tt setup setmainlobby\n" + "# WorldSpawn - Will teleport to the spawn location of the main world\n" + "TeleportLocation: WorldSpawn\n" + "# End of config, main lobby should be stored below";
    private final String parsedConfig = "THETOWERS_COMMENT_0: '###########################################################'\n" + "THETOWERS_COMMENT_1: '+------------------------------------------------------+ #'\n" + "THETOWERS_COMMENT_2: '|               The Towers Configuration               | #'\n" + "THETOWERS_COMMENT_3: '+------------------------------------------------------+ #'\n" + "THETOWERS_COMMENT_4: '###########################################################'\n" + "THETOWERS_BLANKLINE5: ''\n" + "THETOWERS_COMMENT_6: 'When this value is set to true,'\n" + "THETOWERS_COMMENT_7: 'it will output a more in-depth startup log'\n" + "VERBOSE_STARTUP: true\n" + "THETOWERS_BLANKLINE8: ''\n" + "THETOWERS_COMMENT_9: 'Command Section'\n" + "THETOWERS_COMMENT_10: 'These are the commands that players are going to be allowed to execute while playing'\n" + "AllowedCommands:\n" + "- tell\n" + "- reply\n" + "- t\n" + "- r\n" + "- message\n" + "- m\n" + "- whisper\n" + "- w\n" + "THETOWERS_BLANKLINE11: ''\n" + "THETOWERS_COMMENT_12: 'Player Storage Section'\n" + "THETOWERS_COMMENT_13: 'Here you can specify where the player profiles will be saves'\n" + "THETOWERS_COMMENT_14: 'There are three optionsREPLACED_COLON MYSQL, FLATFILE, or none'\n" + "THETOWERS_COMMENT_15: 'MYSQL will save the profile to the specified database under the table thetowers'\n" + "THETOWERS_COMMENT_16: 'FLATFILE will save each other profile to plugins/TheTowersRemastered/players/uuid.yml'\n" + "THETOWERS_COMMENT_17: 'NONE will disable stat saving '\n" + "StorageType: NONE\n" + "THETOWERS_COMMENT_18: 'Disable if running an offline server'\n" + "UseUUIDS: false\n" + "THETOWERS_COMMENT_19: 'MySQL details RIGHT_PARENTHESISwill be used only if the storage type is set to MYSQLLEFT_PARENTHESIS'\n" + "username: root\n" + "password: password\n" + "host: localhost\n" + "port: 3306\n" + "database: thetowers\n" + "THETOWERS_BLANKLINE20: ''\n" + "THETOWERS_COMMENT_21: 'Chest regeneration'\n" + "THETOWERS_COMMENT_22: 'Here you can specify if the chests should be regenerated or not'\n" + "THETOWERS_COMMENT_23: 'You can also specify the interval at which the chests will be regenerated RIGHT_PARENTHESISin minutesLEFT_PARENTHESIS'\n" + "ChestRegeneration: true\n" + "RegenerationInterval: 1\n" + "THETOWERS_BLANKLINE24: ''\n" + "THETOWERS_COMMENT_25: 'Chat Section'\n" + "THETOWERS_COMMENT_26: 'This is the format on how the chat for teams would be shown'\n" + "THETOWERS_COMMENT_27: 'There are a few variables you can use in the chat'\n" + "THETOWERS_COMMENT_28: 'This variables will only apply in-game RIGHT_PARENTHESISnot in lobbyLEFT_PARENTHESIS'\n" + "THETOWERS_COMMENT_29: 'RIGHT_BRACKETgroupLEFT_BRACKET - Players group name pulled from your permissions plugin'\n" + "THETOWERS_COMMENT_30: 'RIGHT_BRACKETpermprefixLEFT_BRACKET - Permission group prefix'\n" + "THETOWERS_COMMENT_31: 'RIGHT_BRACKETpermsuffixLEFT_BRACKET - Permission group suffix'\n" + "THETOWERS_COMMENT_32: 'RIGHT_BRACKETplayernameLEFT_BRACKET - Default player name.'\n" + "THETOWERS_COMMENT_33: 'RIGHT_BRACKETmodplayernameLEFT_BRACKET - Modified player name RIGHT_PARENTHESISenable this for nickanames to showLEFT_PARENTHESIS'\n" + "THETOWERS_COMMENT_34: 'RIGHT_BRACKETmsgLEFT_BRACKET - The message sent.'\n" + "THETOWERS_COMMENT_35: 'This is the format on how the team chat will appear'\n" + "TeamChat: '&8[%teamcolor%%teamname%8] {permprefix} &6{modplayername}: &7 {msg}'\n" + "THETOWERS_COMMENT_36: 'This is the format on how the global chat would be shown'\n" + "GlobalChat: '&8[&aGlobal&8] {permprefix} &6{modplayername}: &7 {msg}'\n" + "THETOWERS_BLANKLINE37: ''\n" + "THETOWERS_COMMENT_38: 'XP when killing players'\n" + "THETOWERS_COMMENT_39: 'When a player kills another player on TheTowers'\n" + "THETOWERS_COMMENT_40: 'You can set how many levels to give to the player'\n" + "LevelsToGive: 1\n" + "THETOWERS_BLANKLINE41: ''\n" + "THETOWERS_COMMENT_42: 'Economy'\n" + "THETOWERS_COMMENT_43: 'Here you can specify the amount of money to give a player when the player performs one of these actions'\n" + "THETOWERS_COMMENT_44: 'WARNINGREPLACED_COLON Needs vault and a compatible economy plugin'\n" + "THETOWERS_COMMENT_45: 'WIP RIGHT_PARENTHESIScoming soonLEFT_PARENTHESIS'\n" + "MoneyEnables: false\n" + "MoneyToGive: \n" + "  ScorePoint: 5.0\n" + "  KillPlayer: 2.0\n" + "  Win: 25.0\n" + "  THETOWERS_BLANKLINE46: ''\n" + "THETOWERS_COMMENT_47: 'Potion Effects'\n" + "THETOWERS_COMMENT_48: 'Here you can specify the potion effects'\n" + "THETOWERS_COMMENT_49: 'The template for adding a potion effect is'\n" + "THETOWERS_COMMENT_50: 'PotionEffectsREPLACED_COLON'\n" + "THETOWERS_COMMENT_51: '  DAMAGE_RESISTANCEREPLACED_COLON'\n" + "THETOWERS_COMMENT_52: '    ModifierREPLACED_COLON 127'\n" + "THETOWERS_COMMENT_53: '    TimeREPLACED_COLON 8'\n" + "PotionEffects:\n" + "  SPEED:\n" + "    Modifier: 1\n" + "    Time: 4\n" + "  DAMAGE_RESISTANCE:\n" + "    Modifier: 127\n" + "    Time: 8\n" + "  FAST_DIGGING:\n" + "    Modifier: 127\n" + "    Time: 8\n" + "  INCREASE_DAMAGE:\n" + "    Modifier: 15\n" + "    Time: 20\n" + "    THETOWERS_BLANKLINE54: ''\n" + "THETOWERS_COMMENT_55: 'Signs'\n" + "THETOWERS_COMMENT_56: 'Here you can edit the sign format'\n" + "THETOWERS_COMMENT_57: 'This format applies to every single sign that has been created with the command'\n" + "THETOWERS_COMMENT_58: '/thetowers setup creatsign <arena>'\n" + "THETOWERS_COMMENT_59: 'You can use color codes and placeholders as well as utf-8 characters'\n" + "THETOWERS_COMMENT_60: 'There are a few placeholders you can use for the sign'\n" + "THETOWERS_COMMENT_61: 'RIGHT_BRACKETarenaLEFT_BRACKET - The arena the sign is bound to'\n" + "THETOWERS_COMMENT_62: 'RIGHT_BRACKETplayersLEFT_BRACKET - The amount of players in the arena'\n" + "THETOWERS_COMMENT_63: 'RIGHT_BRACKETmin_playersLEFT_BRACKET - The minimum amount of players the arena needs to start'\n" + "THETOWERS_COMMENT_64: 'RIGHT_BRACKETmax_playersLEFT_BRACKET - The maximum players the arena can hold'\n" + "signs: \n" + "  OTHER:\n" + "    - ''\n" + "    - '&7&lWaiting for'\n" + "    - '&7&lan arena'\n" + "    - ''\n" + "  ERROR: \n" + "    - ''\n" + "    - '&7&lWaiting for'\n" + "    - '&7&lan arena'\n" + "    - ''\n" + "  IN_GAME: \n" + "    - '&dJoin'\n" + "    - '{arena}'\n" + "    - '&7&l{players}/{max_players}'\n" + "    - '&5&lIn-Game'\n" + "  WAITING: \n" + "    - '&dJoin'\n" + "    - '{arena}'\n" + "    - '&7&l{players}/{max_players}'\n" + "    - '&5&lStarting soon'\n" + "  RESTARTING: \n" + "    - ''\n" + "    - '&c&lRestarting'\n" + "    - 'Please wait'\n" + "    - ''\n" + "  THETOWERS_BLANKLINE65: ''\n" + "THETOWERS_COMMENT_66: 'Exit Teleportation'\n" + "THETOWERS_COMMENT_67: 'You can specify two locations where you want the player to be teleported back to'\n" + "THETOWERS_COMMENT_68: 'MainLobby - You can set a main lobby with the command /tt setup setmainlobby'\n" + "THETOWERS_COMMENT_69: 'WorldSpawn - Will teleport to the spawn location of the main world'\n" + "TeleportLocation: WorldSpawn\n" + "THETOWERS_COMMENT_70: 'End of config, main lobby should be stored below'";

    public static void main(String[] args) {
        new YAMLParserTest();
    }

    @Test
    public void parseRawConfig() throws Exception {
        String[] config = configRaw.split("\n");

        int commentNum = 0;
        int spaces = 0;
        boolean stringList = false;
        String addLine;

        StringBuilder whole = new StringBuilder();
        for(String currentLine : config) {
            if(currentLine.replace(" ", "").startsWith("#")) {
                addLine = currentLine.replace(":", "REPLACED_COLON").replace("'", "SINGLE_QUOTE").replace("{", "RIGHT_BRACKET").replace("}", "LEFT_BRACKET").replace("(", "RIGHT_PARENTHESIS").replace(")", "LEFT_PARENTHESIS").replaceFirst("#", "THETOWERS_COMMENT_" + commentNum + ":").replace(": ", ": '").replace(":#", ": '#");
                whole.append(addLine).append("'\n");
                commentNum++;
                spaces = countSpaces(addLine);
            } else if(currentLine.isEmpty()) {
                whole.append(StringUtils.repeat(" ", (stringList ? spaces-2 : spaces))).append("THETOWERS_BLANKLINE").append(commentNum).append(": ''\n");
                commentNum++;
            } else {
                stringList = currentLine.trim().startsWith("-");
                whole.append(currentLine).append("\n");
                spaces = countSpaces(currentLine);
            }
        }
        
        String[] parsedConfig1 = parsedConfig.split("\n");
        String[] parsedConfig2 = whole.toString().split("\n");
        for(int i = 0; i < parsedConfig2.length; i++) {
            Assert.assertTrue(parsedConfig2[i].equalsIgnoreCase(parsedConfig1[i]));
        }
    }

    @Test
    public void parseYAML() throws Exception {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.loadFromString(parsedConfig);
        Assert.assertTrue(true);
    }

    @Test
    public void saveYAML() throws Exception {
        String escapedYAML = escapeYamlCharacters(parsedConfig);

        String[] escapedYAML1 = configRaw.split("\n");
        String[] escapedYAML2 = escapedYAML.split("\n");
        for(int i = 0; i < escapedYAML1.length; i++) {
            Assert.assertTrue(escapedYAML1[i].equalsIgnoreCase(escapedYAML2[i]));
        }
    }

    private int countSpaces(String str) {
        int count = 0;
        for(int i = 0; i < str.length(); i++) {
            if(Character.isWhitespace(str.charAt(i))) {
                count++;
            } else {
                return count;
            }
        }

        return count;
    }

    private String escapeYamlCharacters(String configString) {
        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder();
        for (String line : lines) {
            if (line.contains("THETOWERS_COMMENT")) {
                String comment = line.replace(line.substring(line.indexOf("THETOWERS_COMMENT"), line.indexOf(":") + 2), "# ");

                if (comment.startsWith("# '")) {
                    comment = comment.substring(0, comment.length() - 1).replaceFirst("# '#", "##").replaceFirst("# '", "# ").replace("REPLACED_COLON", ":");
                }

                comment = comment.replace("REPLACED_COLON", ":").replace("SINGLE_QUOTE", "'").replace("RIGHT_BRACKET", "{").replace("LEFT_BRACKET", "}").replace("RIGHT_PARENTHESIS", "(").replace("LEFT_PARENTHESIS", ")");

                config.append(comment).append("\n");
            } else if(line.contains("THETOWERS_BLANKLINE")) {
                config.append("\n");
            } else {
                config.append(line).append("\n");
            }
        }

        return config.toString();
    }
}