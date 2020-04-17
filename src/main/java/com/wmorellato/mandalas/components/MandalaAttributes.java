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

package com.wmorellato.mandalas.components;

import java.util.Random;

/**
 * Hold the necessary attributes of a mandala.
 */
public class MandalaAttributes {
    public int CX;
    public int CY;

    private Random mRandom;
    public long seed;
    public int radius;
    public int numberOfSections;
    public int numberOfElements;

    public MandalaAttributes(long seed, int radius, int numberOfSections, int numberOfElements) {
        this.seed = seed;
        this.radius = radius;
        this.numberOfSections = numberOfSections;
        this.numberOfElements = numberOfElements;

        // setting center
        CX = radius;
        CY = radius;

        mRandom = new Random(seed);
    }

    /**
     * Wrapper for Random.nextInt(int)
     */
    public int nextInt(int bound) {
        return mRandom.nextInt(bound);
    }

    public int getSectionAngle() {
        return 360 / numberOfSections;
    }

    @Override
    public String toString() {
        return String.format("seed: %d, radius: %d, sections: %d, elements: %d", seed, radius, numberOfSections, numberOfElements);
    }
}