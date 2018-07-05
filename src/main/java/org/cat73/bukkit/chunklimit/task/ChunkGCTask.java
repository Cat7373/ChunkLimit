package org.cat73.bukkit.chunklimit.task;

import org.cat73.bukkit.chunklimit.ChunkLimit;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 清理区块的定时任务
 */
public class ChunkGCTask implements Runnable {
    /**
     * Logger
     */
    private final Logger logger;
    /**
     * 插件主类
     */
    private final ChunkLimit plugin;

    public ChunkGCTask(ChunkLimit plugin) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.listNeedUnloadChunks()
                .collect(Collectors.toMap(c -> c.getWorld().getName(), c -> c.unload() ? 1 : 0, (a, b) -> a + b))
                .entrySet().stream()
                .map(e -> String.format("%s: %d, ", e.getKey(), e.getValue()))
                .reduce((a, b) -> a + b)
                .ifPresent(log -> this.logger.log(Level.INFO, "[debug] Unload Chunks --> " + log));
    }
}
