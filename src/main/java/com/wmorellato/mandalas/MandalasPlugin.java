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

package com.wmorellato.mandalas;

import java.util.HashMap;

import com.wmorellato.mandalas.commands.ConfigurationCommands;
import com.wmorellato.mandalas.commands.MandalaCreationCommands;
import com.wmorellato.mandalas.config.ConfigurationManager;
import com.wmorellato.mandalas.selection.RegionSelection;
import com.wmorellato.mandalas.selection.SelectionTool;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class
 */
public class MandalasPlugin extends JavaPlugin {
    private ConfigurationManager mConfig;
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private final HashMap<Player, RegionSelection> mSelections = new HashMap<>();

    @Override
    public void onDisable() {
        getLogger().info("Disabling Mandalas plugin.");
    }

    @Override
    public void onEnable() {
        // saving default config.yml
        saveDefaultConfig();

        // configuration manager
        mConfig = new ConfigurationManager(this);

        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );

        // set listener for selection tool
        new SelectionTool(this);

        // executors
        setCommandExecutors();

        // load materials
        int numMaterials = BlockMapper.loadMaterialSet();
        getLogger().info("Loaded " + numMaterials + " materials for building.");
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }

    private void setCommandExecutors() {
        MandalaCreationCommands mandalaCommand = new MandalaCreationCommands(this);
        getCommand("mandala").setExecutor(mandalaCommand);
        getCommand("ms").setExecutor(mandalaCommand);
        getCommand("mr").setExecutor(mandalaCommand);

        ConfigurationCommands configCommand = new ConfigurationCommands(this);
        getCommand("mtool").setExecutor(configCommand);
    }

    /**
     * Try to get a current Selection made by the player.
     * @param p
     * @return
     */
    public RegionSelection getPlayerSelection(Player p) {
        return mSelections.get(p);
    }

    /**
     * Set a player selection.
     * @param p
     * @param s
     */
    public void setPlayerSelection(Player p, RegionSelection s) {
        mSelections.put(p, s);
    }

    /**
     * Return the current ConfigurationManager instance.
     * @return
     */
    public ConfigurationManager getConfigManager() {
        return mConfig;
    }
}
