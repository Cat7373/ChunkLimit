package org.cat73.bukkit.chunklimit.task;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.cat73.bukkit.chunklimit.bean.AABBBox;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 清理区块的定时任务
 */
public class ChunkGCTask implements Runnable {
    /**
     * 服务器实例
     */
    private final Server server;
    /**
     * Logger
     */
    private final Logger logger;

    public ChunkGCTask(Server server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void run() {
        // 服务器的视距
        int viewDistance = this.server.getViewDistance();
        // 遍历世界
        String logStr = this.server.getWorlds().stream().map(w -> {
            // 合理的区块区域范围(玩家视距+1 内的区块)
            List<AABBBox> allowChunkRange = w.getPlayers().stream()
                    .map(p -> {
                        Location location = p.getLocation();
                        Chunk chunk = location.getChunk();
                        int minX = chunk.getX() - viewDistance - 1;
                        int maxX = chunk.getX() + viewDistance + 1;
                        int minZ = chunk.getZ() - viewDistance - 1;
                        int maxZ = chunk.getZ() + viewDistance + 1;
                        return new AABBBox(minX, minZ, maxX, maxZ);
                    })
                    .collect(Collectors.toList());

            // 出生点区域
            if (w.getKeepSpawnInMemory()) {
                Location location = w.getSpawnLocation();
                Chunk chunk = location.getChunk();

                int minX = chunk.getX() - 9;
                int maxX = chunk.getX() + 9;
                int minZ = chunk.getZ() - 9;
                int maxZ = chunk.getZ() + 9;
                allowChunkRange.add(new AABBBox(minX, minZ, maxX, maxZ));
            }

            // 遍历区块
            int unloadCount = Stream.of(w.getLoadedChunks()).map(c -> {
                // 如果区块被加载且不在合理区域内
                if (c.isLoaded() && allowChunkRange.stream().noneMatch(b -> b.contains(c.getX(), c.getZ()))) {
                    // 卸载区块
                    if (c.unload()) {
                        return 1;
                    }
                }

                return 0;
            }).reduce(0, (a, b) -> a + b);

            // 调试信息
            return String.format("%s: %d, ", w.getName(), unloadCount);
        })
        // 调试信息
        .reduce("[debug] Unload Chunks --> ", (a, b) -> a + b);

        // 输出调试信息
        this.logger.log(Level.INFO, logStr);
    }
}
