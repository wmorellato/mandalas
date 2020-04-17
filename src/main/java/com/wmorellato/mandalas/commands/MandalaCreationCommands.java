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

import java.util.ArrayList;
import java.util.List;

import com.wmorellato.mandalas.BlockMapper;
import com.wmorellato.mandalas.MandalasPlugin;
import com.wmorellato.mandalas.selection.RegionSelection;
import com.wmorellato.mandalas.components.Mandala;
import com.wmorellato.mandalas.components.MandalaAttributes;
import com.wmorellato.mandalas.exceptions.CenterNotDefinedException;
import com.wmorellato.mandalas.exceptions.RadiusNotDefinedException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * CommandExecutor for mandala creation related commands. This currently includes:
 * 
 *  - Standard (random) creation
 *  - Creation using seed 
 *  - Setting radius by command line
 */
public class MandalaCreationCommands implements CommandExecutor, TabCompleter, Listener {
    private final MandalasPlugin mPlugin;

    private Mandala mMandala;
    private Material[] mMaterials;
    private Player mPlayer;
    private RegionSelection mSelection;

    public MandalaCreationCommands(MandalasPlugin plugin) {
        mPlugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command" + ChatColor.RESET);
            return true;
        }

        mPlayer = (Player) sender;

        if (!mPlayer.hasPermission("mandalas.create")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command." + ChatColor.RESET);
            return true;
        }

        mSelection = mPlugin.getPlayerSelection(mPlayer);

        // check if center is defined
        if (!isCenterDefined()) {
            sender.sendMessage(ChatColor.RED + "You must define a block center first!" + ChatColor.RESET);
            return true;
        }

        // create plain
        if (command.getName().equals("mandala")) {
            return plainCommand(args);
        }

        // create with seed
        if (command.getName().equals("ms")) {
            return fixedSeedCommand(args);
        }

        // set radius
        if (command.getName().equals("mr")) {
            return setRadiusCommand(args);
        }

        return true;
    }

    /**
     * Return a list of possible materials matching the argument typed. This seems
     * to be ok, but if it turns out to be slow, I'll implement a binary search
     * using contains as comparison method.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> filter = new ArrayList<>();
        String materialArgUpper = "";

        if (args.length > 0) {
            materialArgUpper = args[args.length - 1].toUpperCase();
        }

        for (String s : BlockMapper.AVAILABLE_MATERIALS) {
            if (s.contains(materialArgUpper)) {
                filter.add(s);
            }
        }

        return filter;
    }

    /**
     * Treat the 'mandala' command (no radius and no seed providade).
     * 
     * @param player
     */
    private boolean plainCommand(String args[]) {
        long seed = System.currentTimeMillis();
        int sections = mPlugin.getConfigManager().getNumberOfSections();

        if (mSelection.getRadius() == 0) {
            mPlayer.sendMessage(String.format("%sRadius not defined.", ChatColor.RED));
            return false;
        }

        mMaterials = new Material[args.length];

        for (int i = 0; i < mMaterials.length; i++) {
            mMaterials[i] = Material.getMaterial(args[i]);
        }

        MandalaAttributes attr = new MandalaAttributes(seed, mSelection.getRadius(), sections, mMaterials.length);
        mMandala = new Mandala(mPlugin.getConfigManager(), attr);
        BlockMapper bm = new BlockMapper(mSelection, mMaterials, mMandala.getRGBPixels());
        
        try {
            bm.drawMandala();
        } catch (RadiusNotDefinedException e) {
            mPlayer.sendMessage(String.format("%sRadius not defined.", ChatColor.RED));
            return false;
        } catch (CenterNotDefinedException e) {
            mPlayer.sendMessage(String.format("%sCenter not defined.", ChatColor.RED));
            return false;
        }

        return true;
    }

    /**
     * Treat the 'mandala' command (no radius and no seed providade).
     * 
     * @param player
     */
    private boolean fixedSeedCommand(String args[]) {
        long seed = 0;

        if (mSelection.getRadius() == 0) {
            mPlayer.sendMessage(String.format("%sRadius not defined.", ChatColor.RED));
            return false;
        }

        try {
            seed = Long.parseLong(args[0]);
        } catch (Exception e) {
            mPlayer.sendMessage(String.format("%sInvalid seed provided.", ChatColor.RED));
            return false;
        }

        mMaterials = new Material[args.length - 1];

        for (int i = 0; i < mMaterials.length; i++) {
            Material m = Material.getMaterial(args[i + 1]);
            if (m == null) {
                mPlayer.sendMessage(String.format("%sInvalid material \"%s\".", ChatColor.RED, args[i + 1]));
                return false;
            }

            mMaterials[i] = m;
        }

        MandalaAttributes attr = new MandalaAttributes(seed, mSelection.getRadius(), 8, mMaterials.length);
        mMandala = new Mandala(mPlugin.getConfigManager(), attr);
        BlockMapper bm = new BlockMapper(mSelection, mMaterials, mMandala.getRGBPixels());

        try {
            bm.drawMandala();
        } catch (RadiusNotDefinedException e) {
            mPlayer.sendMessage(String.format("%sRadius not defined.", ChatColor.RED));
            return false;
        } catch (CenterNotDefinedException e) {
            mPlayer.sendMessage(String.format("%sCenter not defined.", ChatColor.RED));
            return false;
        }

        return true;
    }

    /**
     * Command to set the radius without the need to go to the border.
     * @param args
     * @return
     */
    private boolean setRadiusCommand(String args[]) {
        try {
            int radius = Integer.parseInt(args[0]);
            mSelection.setRadius(radius);
            mPlayer.sendMessage(String.format("%sRadius set to %d blocks.", ChatColor.DARK_PURPLE, radius));
        } catch (Exception e) {
            mPlayer.sendMessage(String.format("%sInvalid radius provided.", ChatColor.RED));
            return false;
        }

        return true;
    }

    /**
     * Check if center block is defined
     */
    private boolean isCenterDefined() {
        if (mSelection == null || mSelection.getCentralBlock() == null) {
            return false;
        }

        return true;
    }
}