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

import java.awt.*;

import com.wmorellato.mandalas.drawing.CurveUtils;

/**
 * Create a petal shape.
 */
public class Petal extends MandalaElement {
    int mStartPoint;
    int mminRadius, mmaxRadius;

    /**
     * Create a petal shape in the the segment to bem distributed around the
     * mandala. This shape reminds has the form ().
     */
    public Petal(MandalaAttributes attr, double[] range) {
        super(attr);

        mminRadius = (int) range[0] * attr.radius;
        mmaxRadius = (int) range[1] * attr.radius;

        createOpenPetal();
    }

    private void createOpenPetal() {
        RandomPointGenerator rpg = new RandomPointGenerator(mAttributes.nextInt(Integer.MAX_VALUE));
        Point[] points = rpg.generatePointsForReflection(2, 2, mAttributes.CX, mAttributes.CY, mminRadius,
                mAttributes.radius, mAttributes.getSectionAngle());

        mShape = CurveUtils.generateInOrderQuadCurve(points, false);
    }
}