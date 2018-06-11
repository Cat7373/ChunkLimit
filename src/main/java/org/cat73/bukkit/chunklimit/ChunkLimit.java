package org.cat73.bukkit.chunklimit;

import org.bukkit.plugin.java.JavaPlugin;
import org.cat73.bukkit.chunklimit.task.ChunkGCTask;

/**
 * 插件主类
 */
public class ChunkLimit extends JavaPlugin {
    @Override
    public void onEnable() {
        // 启动 ChunkGC 定时任务
        this.getServer().getScheduler().runTaskTimer(this, new ChunkGCTask(this.getServer(), this.getLogger()), 60 * 20, 60 * 20);
        // TODO Listener 阻止无效区块的加载
        // TODO Command
        // TODO Setting
    }
}
