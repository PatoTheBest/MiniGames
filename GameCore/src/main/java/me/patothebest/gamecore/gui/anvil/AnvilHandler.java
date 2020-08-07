package me.patothebest.gamecore.gui.anvil;

import me.patothebest.gamecore.util.Utils;

import java.lang.reflect.Constructor;

class AnvilHandler {

    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //

    static final Class<?> CHAT_MESSAGE_CLASS = Utils.getNMSClass("ChatMessage");
    static final Class<?> ICHAT_BASE_COMPONENT_CLASS = Utils.getNMSClass("IChatBaseComponent");
    static final Class<?> ICRAFTING_CLASS = Utils.getNMSClass("ICrafting");
    static final Class<?> PACKET_PLAY_OUT_OPEN_WINDOW_CLASS = Utils.getNMSClass("PacketPlayOutOpenWindow");
    static final Class<?> CONTAINERS_CLASS = Utils.getNMSClassOrNull("Containers");

    private static final Class<?> ANVIL_CONTAINER_CLASS = Utils.getNMSClass("ContainerAnvil");
    private static final Class<?> CONTAINER_CLASS = Utils.getNMSClass("Container");
    private static final Class<?> ENTITY_HUMAN_CLASS = Utils.getNMSClass("EntityHuman");
    private static final Class<?> BLOCK_POSITION_CLASS = Utils.getNMSClassOrNull("BlockPosition");
    private static final Class<?> ENTITY_CLASS = Utils.getNMSClass("Entity");
    private static final Class<?> CONTAINER_ACCESS_CLASS = Utils.getNMSClassOrNull("ContainerAccess");
    private static final Class<?> WORLD_CLASS = Utils.getNMSClassOrNull("World");

    private static Object BASE_BLOCK_POSITION;
    static Object ANVIL_CONTAINER;

    static {
        if(!Utils.SERVER_VERSION.contains("1_7")) {
            try {
                Constructor<?> constructor = BLOCK_POSITION_CLASS.getConstructor(int.class, int.class, int.class);
                BASE_BLOCK_POSITION = constructor.newInstance(0, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (CONTAINERS_CLASS != null) {
            ANVIL_CONTAINER = Utils.getFieldValue(CONTAINERS_CLASS, "ANVIL", null);
        }
    }

    // -------------------------------------------- //
    // CLASS METHODS
    // -------------------------------------------- //

    /**
     * Returns a new anvil container for the entityHuman.
     * This is achieved by creating a new ContainerAnvil using
     * the players position, world, and entityhuman. Then to
     * make everything work, we disable the checkReachable. This
     * is key to making it work.
     *
     * @param entityHuman the entity human object
     * @param containerId the container id
     * @return the anvil container
     */
    static Object getNewAnvilContainer(Object entityHuman, int containerId) {
        Object anvilContainer = null;

        try {
            if(BLOCK_POSITION_CLASS == null) {
                anvilContainer = ANVIL_CONTAINER_CLASS.getConstructors()[0].newInstance(Utils.getFieldValue(ENTITY_HUMAN_CLASS, "inventory", entityHuman), Utils.getFieldValue(ENTITY_CLASS, "world", entityHuman), 0, 0, 0, entityHuman);
            } else if (CONTAINERS_CLASS == null) {
                anvilContainer = ANVIL_CONTAINER_CLASS.getConstructors()[0].newInstance(Utils.getFieldValue(ENTITY_HUMAN_CLASS, "inventory", entityHuman), Utils.getFieldValue(ENTITY_CLASS, "world", entityHuman), BASE_BLOCK_POSITION, entityHuman);
            } else {
                Object world = Utils.invokeMethod(entityHuman, Utils.getMethodNotDeclaredValue(ENTITY_HUMAN_CLASS, "getWorld"));
                Object containerAccess = Utils.invokeStaticMethod(CONTAINER_ACCESS_CLASS, "at", new Class[] {WORLD_CLASS, BLOCK_POSITION_CLASS}, world, BASE_BLOCK_POSITION);
                anvilContainer = ANVIL_CONTAINER_CLASS.getConstructors()[1].newInstance(containerId, Utils.getFieldValue(ENTITY_HUMAN_CLASS, "inventory", entityHuman), containerAccess);
                Object chatMessage = AnvilHandler.CHAT_MESSAGE_CLASS.getConstructor(String.class, Object[].class).newInstance("Repair & Name", new Object[0]);
                Utils.invokeMethod(anvilContainer, Utils.getMethodNotDeclaredValue(CONTAINER_CLASS, "setTitle", ICHAT_BASE_COMPONENT_CLASS), chatMessage);
            }

            Utils.setFieldValue(CONTAINER_CLASS, "checkReachable", anvilContainer, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return anvilContainer;
    }
}