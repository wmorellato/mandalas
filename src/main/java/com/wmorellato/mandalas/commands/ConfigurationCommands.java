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

package com.wmorellato.mandalas.commands;

import com.wmorellato.mandalas.MandalasPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * CommandExecutor for configuration related commands.
 */
public class ConfigurationCommands implements CommandExecutor {
    private final MandalasPlugin mPlugin;

    private Player mPlayer;

    public ConfigurationCommands(MandalasPlugin plugin) {
        mPlugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command" + ChatColor.RESET);
            return false;
        }

        mPlayer = (Player) sender;

        // create plain
        if (command.getName().equals("mtool")) {
            return setSelectionTool();
        }
        
        return false;
    }

    /**
     * Set the selection tool to the item held in the main hand.
     * @return true if the command was successful, false otherwise.
     */
    private boolean setSelectionTool() {
        if (!mPlayer.hasPermission("mandalas.set_tool")) {
            mPlayer.sendMessage(String.format("%sYou do not have the permission to set the selection tool.", ChatColor.RED));
            return true;
        }

        ItemStack item = mPlayer.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            mPlayer.sendMessage(String.format("%sHold an item in your main hand and try again.", ChatColor.RED));
            return true;
        }

        mPlugin.getConfigManager().setMaterialForSelectionTool(item.getType());
        mPlayer.sendMessage(String.format("%sNew selection tool set.", ChatColor.DARK_PURPLE));

        return true;
    }

}