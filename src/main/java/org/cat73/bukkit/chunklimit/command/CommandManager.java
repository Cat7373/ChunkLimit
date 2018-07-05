package org.cat73.bukkit.chunklimit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.cat73.bukkit.chunklimit.ChunkLimit;

import java.util.Objects;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor {
    /**
     * 插件主类
     */
    private final ChunkLimit plugin;

    public CommandManager(ChunkLimit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("您无权执行该命令");
            return true;
        }

        switch (args[0]) {
            case "list":
                String worldName = args.length > 1 ? args[1] : null;

                this.plugin.listNeedUnloadChunks()
                        .filter(c -> worldName == null || Objects.equals(c.getWorld().getName(), worldName))
                        .sorted((a, b) -> {
                            int w = a.getWorld().getName().compareTo(b.getWorld().getName());
                            int x = a.getX() - b.getX();
                            int z = a.getZ() - b.getZ();

                            return w != 0 ? w : x != 0 ? x : z;
                        })
                        .forEach(c -> sender.sendMessage(String.format("[%s] chunk: (%s, %s), pos: (%s, %s), entityCount: %d, tileCount: %d", c.getWorld().getName(), c.getX(), c.getZ(), c.getX() * 16, c.getZ() * 16, c.getEntities().length, c.getTileEntities().length)));
                break;
            case "count":
                this.plugin.listNeedUnloadChunks()
                        .collect(Collectors.toMap(c -> c.getWorld().getName(), c -> 1, (a, b) -> a + b))
                        .entrySet().stream()
                        .map(e -> String.format("%s: %d, ", e.getKey(), e.getValue()))
                        .reduce((a, b) -> a + b)
                        .ifPresent(log -> sender.sendMessage("Count Chunks --> " + log));
                break;
            case "gc":
                this.plugin.listNeedUnloadChunks()
                        .collect(Collectors.toMap(c -> c.getWorld().getName(), c -> c.unload() ? 1 : 0, (a, b) -> a + b))
                        .entrySet().stream()
                        .map(e -> String.format("%s: %d, ", e.getKey(), e.getValue()))
                        .reduce((a, b) -> a + b)
                        .ifPresent(log -> sender.sendMessage("Unload Chunks --> " + log));
                break;
            default:
                return true;
        }

        return true;
    }
}
