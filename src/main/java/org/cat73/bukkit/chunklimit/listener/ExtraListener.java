package org.cat73.bukkit.chunklimit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * 顺道加的一些与插件功能无关的 Listener
 */
public class ExtraListener implements Listener {
    /**
     * 屏蔽带有 PickaxeChat 的聊天消息
     */
    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        if (e.getMessage().contains("PickaxeChat")) {
            e.setCancelled(true);
        }
    }
}
