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
        // 遍历世界
        String logStr = this.server.getWorlds().stream().map(w -> {
            // 当前世界的视距
            int viewDistance = this.server.getViewDistance();
            // 合理的区块区域范围
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
            {
                Location location = w.getSpawnLocation();
                Chunk chunk = location.getChunk();

                int minX = chunk.getX() - 12;
                int maxX = chunk.getX() + 12;
                int minZ = chunk.getZ() - 12;
                int maxZ = chunk.getZ() + 12;
                allowChunkRange.add(new AABBBox(minX, minZ, maxX, maxZ));
            }

            // 遍历区块
            int unloadCount = Stream.of(w.getLoadedChunks()).map(c -> {
                // 如果区块被加载且不再合理区域内
                if (c.isLoaded() && allowChunkRange.stream().noneMatch(b -> b.contains(c.getX(), c.getZ()))) {
                    // 卸载区块
                    if (c.unload()) {
                        return 1;
                    }
                }

                return 0;
            }).reduce(0, (a, b) -> a + b);

            return String.format("%s: %d, ", w.getName(), unloadCount);
        })
        .reduce("[debug] Unload Chunks --> ", (a, b) -> a + b);

        this.logger.log(Level.INFO, logStr);
    }
}
