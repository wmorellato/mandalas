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

package com.wmorellato.mandalas.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.wmorellato.mandalas.MandalasPlugin;
import com.wmorellato.mandalas.components.ElementType;
import com.wmorellato.mandalas.components.InnerPath;
import com.wmorellato.mandalas.components.Petal;
import com.wmorellato.mandalas.exceptions.InvalidCurveRangeException;
import com.wmorellato.mandalas.exceptions.InvalidElementAttributeException;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Auxiliary class to get and set configuration attributes.
 */
public class ConfigurationManager {
    private static final String PATH_SELECTION_TOOL = "selection_tool";
    private static final String PATH_SAVE_ALL = "save_all_to_bitmap";
    private static final String PATH_NUMBER_SECTIONS = "mandala.sections";
    private static final String PATH_RANDOM_POOL = "mandala.elements.random.pool";
    private static final String PATH_RANDOM_COUNT = "mandala.elements.random.count";
    private static final String PATH_FIXED_ELEMENTS = "mandala.elements.fixed";

    private static final int DEFAULT_NUMBER_SECTIONS = 8;
    private static final Material DEFAULT_SELECTION_TOOL = Material.RED_TULIP;

    MandalasPlugin mPlugin;
    FileConfiguration mConfig;
    ArrayList<ElementType> mRandomElementPool = new ArrayList<>();
    HashMap<String, ElementType> mFixedElements = new HashMap<>();

    public ConfigurationManager(MandalasPlugin plugin) {
        mPlugin = plugin;
        mConfig = plugin.getConfig();

        setupElementPool();
    }

    public ConfigurationManager(FileConfiguration fc) {
        mConfig = fc;

        setupElementPool();
    }

    /**
     * Get every type of element specified in the config file and map them to be
     * drawn.
     */
    private void setupElementPool() {
        Set<String> keys = mConfig.getConfigurationSection(PATH_RANDOM_POOL).getKeys(false);
        Set<String> fixedKeys = mConfig.getConfigurationSection(PATH_FIXED_ELEMENTS).getKeys(false);

        for (String k : keys) {
            try {
                mRandomElementPool.add(ElementType.valueOf(k));
            } catch (IllegalArgumentException e) {

            }
        }

        for (String k : fixedKeys) {
            try {
                int sepIndex = k.indexOf("_");

                if (sepIndex != -1) {
                    String type = k.substring(0, sepIndex);
                    mFixedElements.put(k, ElementType.valueOf(type));
                } else {
                    mFixedElements.put(k, ElementType.valueOf(k));
                }
            } catch (IllegalArgumentException e) {

            }
        }
    }

    /**
     * Return the types of elements that should be used to form the mandala.
     * 
     * @return a {@link ArrayList} containing the random types of elements set in the config file.
     */
    public ArrayList<ElementType> getPool() {
        return mRandomElementPool;
    }

    /**
     * Return the fixed elements read from the config.yml file.
     * 
     * @return HashMap with identifier and element type for each fixed element.
     */
    public HashMap<String, ElementType> getFixedElements() {
        return mFixedElements;
    }

    /**
     * Get the number of random elements configured to be drawn.
     * 
     * @return total number of random elements
     */
    public int getNumberOfRandomElements() {
        return mConfig.getInt(PATH_RANDOM_COUNT);
    }

    /**
     * Get the number of fixed elements.
     * 
     * @return total number of fixed elements
     */
    public int getNumberOfFixedElements() {
        return mFixedElements.size();
    }

    /**
     * Return the configured number of sections.
     * 
     * @return
     */
    public int getNumberOfSections() {
        return mConfig.getInt(PATH_NUMBER_SECTIONS, DEFAULT_NUMBER_SECTIONS);
    }

    /**
     * Get the configuration option if the plugin should save to a bmp file every
     * mandala created. If options is not present in the file it defaults to false.
     * 
     * @return flag if we should save to file every mandala.
     */
    public boolean shouldSaveToFile() {
        return mConfig.getBoolean(PATH_SAVE_ALL, false);
    }

    /**
     * Get the configured selection tool used for defining regions in the world.
     * 
     * @return the {@link Material} associated with the item used for selection.
     */
    public Material getMaterialForSelectionTool() {
        Material material = Material.getMaterial(mConfig.getString(PATH_SELECTION_TOOL));

        if (material == null) {
            return DEFAULT_SELECTION_TOOL;
        }

        return material;
    }

    /**
     * Set the {@link Material} to be used as the selection tool.
     * 
     * @param material
     */
    public void setMaterialForSelectionTool(Material material) {
        mConfig.set(PATH_SELECTION_TOOL, material.name());

        mPlugin.saveConfig();
        mPlugin.reloadConfig();
    }

    /**
     * Get the number of vertices configured for this type of curve. Allowed
     * elements for this method are {@link InnerPath}.
     * 
     * @param type
     * @return number of vertices to compose the curve
     * @throws InvalidElementAttributeException
     */
    public int getCurveVerticesFromPool(ElementType type) {
        String path = String.format("%s.%s.vertices", PATH_RANDOM_POOL, type.name());

        return mConfig.getInt(path);
    }

    /**
     * Get the number of vertices configured for this type of curve. Allowed
     * elements for this method are {@link Petal} and {@link InnerPath}.
     * 
     * @param type
     * @return number of vertices to compose the curve
     * @throws InvalidCurveRangeException
     */
    public double[] getRangeFromPool(ElementType type) throws InvalidCurveRangeException {
        String path = String.format("%s.%s.range", PATH_RANDOM_POOL, type.name());

        ArrayList<Double> pair = (ArrayList<Double>) mConfig.getDoubleList(path);

        if (pair.get(0) < 0.0 || pair.get(1) > 1.0) {
            throw new InvalidCurveRangeException();
        }

        return new double[] { pair.get(0), pair.get(1) };
    }

    /**
     * Get the number of vertices configured for this type of curve from the fixed
     * array of elements. Allowed elements for this method are {@link InnerPath}.
     * 
     * @param type
     * @return number of vertices to compose the curve
     */
    public int getCurveVerticesFromFixedElement(String elementIdentifier) {
        String path = String.format("%s.%s.vertices", PATH_FIXED_ELEMENTS, elementIdentifier);

        return mConfig.getInt(path);
    }

    /**
     * Get the number of vertices configured for this type of curve. Allowed
     * elements for this method are {@link Petal} and {@link InnerPath}.
     * 
     * @param type
     * @return number of vertices to compose the curve
     * @throws InvalidCurveRangeException
     */
    public double[] getRangeFromFixedElement(String elementIdentifier) throws InvalidCurveRangeException {
        String path = String.format("%s.%s.range", PATH_FIXED_ELEMENTS, elementIdentifier);

        ArrayList<Double> pair = (ArrayList<Double>) mConfig.getDoubleList(path);

        if (pair.get(0) < 0.0 || pair.get(1) > 1.0) {
            throw new InvalidCurveRangeException();
        }

        return new double[] { pair.get(0), pair.get(1) };
    }
}