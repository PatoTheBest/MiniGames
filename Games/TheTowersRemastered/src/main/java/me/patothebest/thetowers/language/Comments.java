package me.patothebest.thetowers.language;

import me.patothebest.gamecore.lang.interfaces.IComment;

public enum Comments implements IComment {

    HEADER(true, "The Towers Messages"),
    GENERAL_MESSAGES(false, "General Messages"),
    COMMAND_DESCRIPTIONS(false, "Command Descriptions"),
    USER_COMMANDS(true, "",  "User Commands"),
    ERRORS_IN_CMD_ARGS(false, "", "Errors in command arguments"),
    SUCCESSFUL_EXECUTION(false, "", "Successful Messages"),
    SETUP_COMMANDS(true, "",  "Setup Commands"),
    KIT_COMMANDS(true, "",  "Kit Commands"),
    ADMIN_COMMANDS(true, "",  "Admin Commands"),
    ERRORS(true, "",  "Errors"),
    SETUP_ERRORS(false, "Setup errors"),
    GAME_MESSAGES(true, "",  "Game Messages"),
    GENERAL_GAME_MESSAGES(false, "", "General game messages"),
    GAME_ITEMS(false, "", "Item messages"),
    GUI_MESSAGES(true, "", "Graphical User Interface Messages"),
    GUI_GENERAL_MESSAGES(false, "General GUI messages"),
    GUI_MAIN(false, "", "Main menu messages"),
    GUI_CHOOSE_ARENA(false, "", "Choose arena menu messages"),
    GUI_CHOOSE_KIT(false, "", "Choose kit menu messages"),
    GUI_USER_CHOOSE_TEAM(false, "", "Choose team menu messages"),
    GUI_CHOOSE_LOCALE(false, "", "Locale menu messages"),
    GUI_EDIT_ARENA(false, "", "Edit arena menu messages"),
    GUI_CHOOSE_TEAM(false, "", "Choose team menu messages"),
    GUI_EDIT_TEAM(false, "", "Edit team menu messages"),
    GUI_CHOOSE_POINT_AREA(false, "", "Choose point area menu messages"),
    GUI_EDIT_POINT_AREA(false, "", "Edit point area menu messages"),
    GUI_CHOOSE_DROPPER(false, "", "Choose dropper menu messages"),
    GUI_EDIT_DROPPER(false, "", "Edit dropper menu messages"),
    GUI_EDIT_INTERVAL(false, "", "Edit interval menu messages"),
    GUI_EDIT_PLAYERS(false, "", "Edit players menu messages"),
    GUI_EDIT_ITEM(false, "", "Edit item menu messages"),
    GUI_CHANGE_AMOUNT(false, "", "Change item amount menu messages"),
    GUI_CHANGE_DATA(false, "", "Change item data menu messages"),
    GUI_CHOOSE_KIT_TO_EDIT(false, "", "Choose kit to edit menu messages"),
    GUI_CHOOSE_POTION_EFFECT(false, "", "Choose potion effects menu messages"),
    GUI_EDIT_KIT(false, "", "Edit kit menu messages"),
    GUI_EDIT_PRICE(false, "", "Edit price menu messages"),
    GUI_PREVIEW_KIT(false, "", "Preview kit menu messages"),
    GUI_KIT_EDIT_POTION_EFFECT(false, "", "Edit potion effect menu messages"),
    GUI_DESCRIPTION_EDITOR(false, "", "Description editor line menu messages"),
    GUI_DESCRIPTION_EDITOR_MAIN(false, "", "Description editor main page menu messages"),
    GUI_ADMIN(false, "", "Admin menu messages");

    private final boolean header;
    private final String[] lines;

    Comments(boolean header, String... lines) {
        this.header = header;
        this.lines = lines;
    }

    public boolean isHeader() {
        return header;
    }

    public String[] getLines() {
        return lines;
    }
}
