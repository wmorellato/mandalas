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
 * This element corresponds to curves inside the mandala. These curves are
 * called paths because they are created using a certain number of points which
 * are connected to form a pattern.
 * 
 * Currently there are three ways these curves can be drawn: conves, concave and
 * random. The curve is convex or concave using as reference the point in the
 * center of the figure.
 */
public class InnerPath extends MandalaElement {
    public static final int CONCAVE_PATH = 0;
    public static final int CONVEX_PATH = 1;
    public static final int RANDOM_PATH = 2;

    int mtype, mlength, mminRadius, mmaxRadius;

    /**
     * 
     * @param attr     MandalaAttributes instance
     * @param length:  number of points (if "centered" is True, this will be
     *                 doubled)
     * @param range:   range from the center of the figure inside of which the
     *                 points must be drawn
     * @param concave: if the curve should be created as concave when seen from the
     *                 center
     */
    public InnerPath(MandalaAttributes attr, int length, double[] range, int type) {
        super(attr);

        mtype = type;
        mminRadius = (int) range[0] * attr.radius;
        mmaxRadius = (int) range[1] * attr.radius;
        mlength = length;

        switch (type) {
            case CONCAVE_PATH:
                createConcavePath(length, mminRadius, mmaxRadius);
                break;
            case CONVEX_PATH:
                createConvexPath(length, mminRadius, mmaxRadius);
                break;
            case RANDOM_PATH:
            default:
                createRandomPath(length);
                break;

        }
    }

    private void createConcavePath(int length, int minRadius, int maxRadius) {
        RandomPointGenerator rpg = new RandomPointGenerator(mAttributes.nextInt(Integer.MAX_VALUE));
        Point[] points = rpg.generatePointsForReflection(length, length, mAttributes.CX, mAttributes.CY, minRadius,
                maxRadius, mAttributes.getSectionAngle());

        mShape = CurveUtils.generateInOrderQuadCurve(points, false);
    }

    private void createConvexPath(int length, int minRadius, int maxRadius) {
        RandomPointGenerator rpg = new RandomPointGenerator(mAttributes.nextInt(Integer.MAX_VALUE));
        Point[] points = rpg.generateOrderedPath(length, mAttributes.CX, mAttributes.CY, minRadius, maxRadius,
                mAttributes.getSectionAngle(), false, true);

        mShape = CurveUtils.generateInOrderQuadCurve(points, true);
    }

    private void createRandomPath(int length) {
        RandomPointGenerator rpg = new RandomPointGenerator(mAttributes.nextInt(Integer.MAX_VALUE));
        Point[] points = rpg.generateRandomPoints(length, mAttributes.CX, mAttributes.CY, mAttributes.radius,
                mAttributes.getSectionAngle());

        mShape = CurveUtils.generateInOrderQuadCurve(points, false);
    }

    @Override
    public String toString() {
        return String.format("path type: %d, length: %d, minRadius: %d, maxRadius: %d", mtype, mlength, mminRadius,
                mmaxRadius);
    }
}