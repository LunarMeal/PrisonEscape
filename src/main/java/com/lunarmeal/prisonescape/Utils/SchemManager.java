package com.lunarmeal.prisonescape.Utils;


import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SchemManager {
    // 保存领地为schem文件的示例

    public static void saveSchematic(String filePath, Location loc1, Location loc2) {
        File schemFile = new File(filePath);

        World world = BukkitAdapter.adapt(loc1.getWorld());
        CuboidRegion region = new CuboidRegion(BukkitAdapter.asBlockVector(loc1), BukkitAdapter.asBlockVector(loc2));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                world, region, clipboard, region.getMinimumPoint()
        );
        // configure here
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schemFile))) {
                writer.write(clipboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAndPasteSchematic(String filePath, Location pasteLocation) {
        File schemFile = new File(filePath);
        World world = BukkitAdapter.adapt(pasteLocation.getWorld());
        if (!schemFile.exists()) {
            // 文件不存在
            return;
        }
        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile))) {
                Clipboard clipboard = reader.read();
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(pasteLocation.getX(), pasteLocation.getY(), pasteLocation.getZ()))
                            // configure here
                            .build();
                    Operations.complete(operation);
                } catch (WorldEditException e) {
                    throw new RuntimeException(e);
                }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearEntitiesInRegion(Location loc, Vector min, Vector max) {
        World world = BukkitAdapter.adapt(loc.getWorld());
        for (Entity entity : world.getEntities()) {
            Vector3 entityLocation = entity.getLocation().toVector();

            // 检查实体位置是否在指定区域内
            if (entityLocation.containedWithin(Vector3.at(min.getX(), min.getY(), min.getZ()),Vector3.at(max.getX(), max.getY(), max.getZ()))) {
                entity.remove();
            }
        }
    }
    public static void deleteSchematic(String filePath){
        File schemFile = new File(filePath);
        if (!schemFile.exists()) {
            // 文件不存在
            return;
        }
        schemFile.delete();
    }
}

