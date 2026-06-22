package net.snowteb.warriorcats_events.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static final String KEY_CATEGORY_WARRIORCATSEVENTS = "key.category.warriorcats_events.key";
    public static final String KEY_HISS = "key.warriorcats_events.hiss";
    public static final String KEY_WATER = "key.warriorcats_events.water";
    public static final String KEY_CLIMB = "key.warriorcats_events.climb";
    public static final String KEY_OPTIONS = "key.warriorcats_events.options";
    public static final String KEY_LEAP = "key.warriorcats_events.leap";

    public static final KeyMapping HISSING_KEY = new KeyMapping(KEY_HISS, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, KEY_CATEGORY_WARRIORCATSEVENTS);

    public static final KeyMapping WATERDRINK_KEY = new KeyMapping(KEY_WATER, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, KEY_CATEGORY_WARRIORCATSEVENTS);

    public static final KeyMapping CLIMB_KEY = new KeyMapping(KEY_CLIMB, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, KEY_CATEGORY_WARRIORCATSEVENTS);

    public static final KeyMapping OPTIONS_KEY = new KeyMapping(KEY_OPTIONS, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, KEY_CATEGORY_WARRIORCATSEVENTS);

    public static final KeyMapping LEAP_KEY = new KeyMapping(KEY_LEAP, KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, KEY_CATEGORY_WARRIORCATSEVENTS);

//
//    public static final KeyMapping SKILLMENU_KEY = new KeyMapping(KEY_SKILLMENU, KeyConflictContext.IN_GAME,
//            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, KEY_CATEGORY_WARRIORCATSEVENTS);

}
