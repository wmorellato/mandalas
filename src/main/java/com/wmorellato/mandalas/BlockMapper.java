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

import java.util.ArrayList;
import java.util.HashMap;

import com.wmorellato.mandalas.exceptions.CenterNotDefinedException;
import com.wmorellato.mandalas.exceptions.RadiusNotDefinedException;
import com.wmorellato.mandalas.selection.RegionSelection;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Class that effectively draw the mandala in the world, mapping from the RGB
 * pixels to actual blocks.
 */
public class BlockMapper {
    // this value (0xff000000, or black) is the color value the Graphics2D set to
    // pixels not set to anything.
    private int backgroudnValue = -16777216;

    static ArrayList<Material> mAvailableMaterials;
    public static ArrayList<String> AVAILABLE_MATERIALS;

    HashMap<Integer, Material> mMaterials = new HashMap<>();

    RegionSelection mRegion;
    int[][] mRGBPixels;

    public BlockMapper(RegionSelection region, Material[] materials, int[][] rgbPixels) {
        mRegion = region;
        mRGBPixels = rgbPixels;

        mapPixelsToMaterials(materials);
    }

    /**
     * Map each possible value of a pixel to a Material
     * 
     * @throws CenterNotDefinedException
     * @throws RadiusNotDefinedException
     */
    private void mapPixelsToMaterials(Material[] materials) {
        ArrayList<Integer> distinct = new ArrayList<>();

        for (int i = 0; i < mRegion.getRadius() * 2 + 1; i++) {
            for (int j = 0; j < mRegion.getRadius() * 2 + 1; j++) {
                if (!distinct.contains(mRGBPixels[i][j])) {
                    distinct.add(mRGBPixels[i][j]);
                }
            }
        }

        backgroudnValue = distinct.get(0);
        mMaterials.put(distinct.get(0), Material.AIR);
        for (int i = 1; i < distinct.size(); i++) {
            mMaterials.put(distinct.get(i), materials[i % materials.length]);
        }
    }

    /**
     * Replace the blocks on the specified region in the world by the corresponding
     * pixels of the mandala image on the plane specified by the region.
     * 
     * @throws CenterNotDefinedException
     * @throws RadiusNotDefinedException
     */
    public void drawMandala() throws RadiusNotDefinedException, CenterNotDefinedException {
        switch (mRegion.getDrawingPlane()) {
            case XY:
                drawOnXYPlane();
                break;
            case XZ:
                drawOnXZPlane();
                break;
            case YZ:
                drawOnYZPlane();
                break;
        }
    }

    private void drawOnXYPlane() throws RadiusNotDefinedException, CenterNotDefinedException {
        Block firstBlock = mRegion.getFirstBlock();

        for (int x = 0; x < mRegion.getRadius() * 2 + 1; x++) {
            for (int y = 0; y < mRegion.getRadius() * 2 + 1; y++) {
                int pixel = mRGBPixels[x][y];

                Block b = firstBlock.getRelative(x, -y, 0);
                b.setType(mMaterials.get(pixel));
            }
        }

        mRGBPixels = null;
    }

    private void drawOnXZPlane() throws RadiusNotDefinedException, CenterNotDefinedException {
        Block firstBlock = mRegion.getFirstBlock();

        for (int x = 0; x < mRegion.getRadius() * 2 + 1; x++) {
            for (int z = 0; z < mRegion.getRadius() * 2 + 1; z++) {
                int pixel = mRGBPixels[x][z];

                Block b = firstBlock.getRelative(x, 0, z);
                b.setType(mMaterials.get(pixel));
            }
        }

        mRGBPixels = null;
    }

    private void drawOnYZPlane() throws RadiusNotDefinedException, CenterNotDefinedException {
        Block firstBlock = mRegion.getFirstBlock();

        for (int y = 0; y < mRegion.getRadius() * 2 + 1; y++) {
            for (int z = 0; z < mRegion.getRadius() * 2 + 1; z++) {
                int pixel = mRGBPixels[y][z];

                Block b = firstBlock.getRelative(0, -y, z);
                b.setType(mMaterials.get(pixel));
            }
        }

        mRGBPixels = null;
    }

    /**
     * Load the available materials for building mandalas.
     * 
     * @return
     */
    public static int loadMaterialSet() {
        mAvailableMaterials = new ArrayList<Material>();
        AVAILABLE_MATERIALS = new ArrayList<String>();

        Material[] materials = Material.values();

        for (int i = 0; i < materials.length; i++) {
            if (materials[i].isBlock() && !materials[i].isInteractable()) {
                mAvailableMaterials.add(materials[i]);
                AVAILABLE_MATERIALS.add(materials[i].name());
            }
        }

        return mAvailableMaterials.size();
    }
}