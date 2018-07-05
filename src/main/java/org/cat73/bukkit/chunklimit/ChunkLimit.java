package org.cat73.bukkit.chunklimit;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.cat73.bukkit.chunklimit.bean.ChunkRange;
import org.cat73.bukkit.chunklimit.command.CommandManager;
import org.cat73.bukkit.chunklimit.listener.ExtraListener;
import org.cat73.bukkit.chunklimit.task.ChunkGCTask;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 插件主类
 */
public class ChunkLimit extends JavaPlugin {
    @Override
    public void onEnable() {
        // 启动 ChunkGC 定时任务
        this.getServer().getScheduler().runTaskTimer(this, new ChunkGCTask(this), 600 * 20, 600 * 20);
        // Command
        this.getCommand("chunklimit").setExecutor(new CommandManager(this));
        // TODO Setting
        // 注册 Listener
        this.getServer().getPluginManager().registerEvents(new ExtraListener(), this);
    }

    public Stream<Chunk> listNeedUnloadChunks() {
        Server server = this.getServer();

        // 服务器的视距
        int viewDistance = server.getViewDistance();
        // 遍历世界
        return server.getWorlds().stream().flatMap(w -> {
            // 合理的区块区域范围(玩家视距内的区块)
            List<ChunkRange> allowChunkRange = w.getPlayers().stream()
                    .map(p -> {
                        // 玩家坐标和所在区块
                        Location location = p.getLocation();
                        Chunk chunk = location.getChunk();

                        // 玩家区块范围 (中心区块 +- 视距范围，保险起见再加 3)
                        int minX = chunk.getX() - viewDistance - 3;
                        int maxX = chunk.getX() + viewDistance + 3;
                        int minZ = chunk.getZ() - viewDistance - 3;
                        int maxZ = chunk.getZ() + viewDistance + 3;
                        return new ChunkRange(minX, minZ, maxX, maxZ);
                    })
                    .collect(Collectors.toList());

            // 出生点区域
            if (w.getKeepSpawnInMemory()) {
                // 出生点坐标和所在区块
                Location location = w.getSpawnLocation();
                Chunk chunk = location.getChunk();

                // 出生点的区块范围 (中心区块 +-128 个方块，等于 8 个区块，保险起见再加 3)
                int minX = chunk.getX() - 8 - 3;
                int maxX = chunk.getX() + 8 + 3;
                int minZ = chunk.getZ() - 8 - 3;
                int maxZ = chunk.getZ() + 8 + 3;
                allowChunkRange.add(new ChunkRange(minX, minZ, maxX, maxZ));
            }

            // 遍历区块
            return Stream.of(w.getLoadedChunks())
                    // 如果区块被加载且不在合理区域内
                    .filter(c -> c.isLoaded() && allowChunkRange.stream().noneMatch(b -> b.contains(c.getX(), c.getZ())));
        });
    }
}
