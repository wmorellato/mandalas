/**
Mandalas is an open-source Minecraft plugin.
Copyright (C) 2020  Wesley Morellato

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
**/

package com.wmorellato.mandalas.selection;

import com.wmorellato.mandalas.MandalasPlugin;
import com.wmorellato.mandalas.exceptions.CenterNotDefinedException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Class to listen for events using the selection tool.
 */
public class SelectionTool implements Listener {
    MandalasPlugin mPlugin;

    public SelectionTool(MandalasPlugin plugin) {
        mPlugin = plugin;
        mPlugin.getServer().getPluginManager().registerEvents(this, mPlugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent pie) {
        Material definedTool = mPlugin.getConfigManager().getMaterialForSelectionTool();

        if (pie == null || pie.getItem() == null || !pie.getItem().getType().equals(definedTool)) {
            return;
        }

        if (pie.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            setCenter(pie);
        } else if (pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            setRadius(pie);
        }
    }

    /**
     * Set the center of the region.
     * 
     * @param pie
     */
    private void setCenter(PlayerInteractEvent pie) {
        Player player = pie.getPlayer();
        Block clickedBlock = pie.getClickedBlock();
        BlockFace face = pie.getBlockFace();
        RegionSelection playerSelection = mPlugin.getPlayerSelection(player);

        if (playerSelection == null) {
            playerSelection = new RegionSelection();
        }

        playerSelection.setCentralBlock(clickedBlock, face);
        mPlugin.setPlayerSelection(player, playerSelection);

        player.sendMessage(String.format("%sSet center on position (%d,%d,%d), facing %s", ChatColor.DARK_PURPLE,
                clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ(), face.toString()));
    }

    /**
     * Set the radius of the region from the distance of a block located on the
     * border of the region to the block on its center.
     * 
     * @param pie: {@link PlayerInteractEvent}
     */
    private void setRadius(PlayerInteractEvent pie) {
        Player player = pie.getPlayer();
        Block clickedBlock = pie.getClickedBlock();
        RegionSelection playerSelection = mPlugin.getPlayerSelection(player);

        if (playerSelection == null || playerSelection.getCentralBlock() == null) {
            player.sendMessage(
                    String.format("%s %s", ChatColor.RED, "You must set the central block first! (Left-Click)"));
            return;
        }

        try {
            playerSelection.setRadius(clickedBlock);
        } catch (CenterNotDefinedException e) {
            player.sendMessage(
                    String.format("%s %s", ChatColor.RED, "You must set the central block first! (Left-Click)"));
        }

        player.sendMessage(
                String.format("%sSet radius of %d blocks.", ChatColor.DARK_PURPLE, playerSelection.getRadius()));
    }
}