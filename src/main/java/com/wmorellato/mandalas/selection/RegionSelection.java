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

import com.wmorellato.mandalas.exceptions.CenterNotDefinedException;
import com.wmorellato.mandalas.exceptions.RadiusNotDefinedException;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Class to represent the region in the world where the mandala will be drawn.
 * The distances are measured in blocks.
 */
public class RegionSelection {
    public enum Plane {
        XY, XZ, YZ,
    }

    Plane mPlane;
    Block mCentralBlock;
    Block mBorder;
    int mRadius = 0;

    public RegionSelection() {
    }

    /**
     * Get the block set as the center of the mandala.
     * 
     * @return the block defined as the center of the region.
     */
    public Block getCentralBlock() {
        return mCentralBlock;
    }

    /**
     * Set the block as the center of the mandala. This method also sets the plane
     * (xy, xz or yz) of the figure.
     * 
     * @param block
     */
    public void setCentralBlock(Block block, BlockFace blockFace) {
        mCentralBlock = block;

        switch (blockFace) {
            case NORTH:
            case SOUTH:
                mPlane = Plane.XY;
                break;
            case EAST:
            case WEST:
                mPlane = Plane.YZ;
                break;
            case UP:
            case DOWN:
            default:
                mPlane = Plane.XZ;
                break;
        }
    }

    /**
     * Get the radius of the region measured in number of blocks.
     * 
     * @return the radius of the region as the number of blocks.
     */
    public int getRadius() {
        return mRadius;
    }

    /**
     * Set the radius of the region measured in number of blocks.
     */
    public void setRadius(int radius) {
        mRadius = radius;
    }

    /**
     * This method sets the radius of the region that will hold the mandala by a
     * block located on the border of the region.
     * 
     * @param borderBlock
     * @return the radius of the region.
     */
    public int setRadius(Block borderBlock) throws CenterNotDefinedException {
        if (mCentralBlock == null) {
            throw new CenterNotDefinedException();
        }

        mRadius = (int) mCentralBlock.getLocation().distance(borderBlock.getLocation());

        return mRadius;
    }

    /**
     * Get the plane that will hold the mandala. Either xy, xz or yz.
     * 
     * @return the {@link com.wmorellato.mandalas.Plane} to draw the mandala.
     */
    public Plane getDrawingPlane() {
        return mPlane;
    }

    @Override
    public String toString() {
        return String.format("Plane: %s, Radius: %d, Center: (%d, %d, %d)", mPlane.name(), mRadius,
                mCentralBlock.getX(), mCentralBlock.getY(), mCentralBlock.getZ());
    }

    /**
     * Get the first block of the selected region which is dependente of the plane.
     * 
     * @return a {@link Block} instance of the first block of the region.
     * @throws RadiusNotDefinedException
     * @throws CenterNotDefinedException
     */
    public Block getFirstBlock() throws RadiusNotDefinedException, CenterNotDefinedException {
        if (mRadius == 0) {
            throw new RadiusNotDefinedException();
        }

        if (mCentralBlock == null) {
            throw new CenterNotDefinedException();
        }

        switch (mPlane) {
            case XY:
                return mCentralBlock.getRelative(-mRadius, mRadius, 0);
            case XZ:
                return mCentralBlock.getRelative(-mRadius, 0, -mRadius);
            case YZ:
                return mCentralBlock.getRelative(0, mRadius, -mRadius);
            default:
                throw new CenterNotDefinedException();
        }
    }
}