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

package com.wmorellato.mandalas.drawing;

import java.awt.*;

/**
 * Utilities for working with points on both XY and Polar coordinates.
 */
public class PointUtils {
    /**
     * Polar to XY coordinates.
     * 
     * @param cx
     * @param cy
     * @param radius
     * @param degrees
     * @return
     */
    public static Point polarToXY(int cx, int cy, int radius, int degrees) {
        return new Point(cx + (int) (Math.cos(Math.toRadians(degrees)) * radius),
                cy + (int) (Math.sin(Math.toRadians(degrees)) * radius));
    }

    public static PolarPoint XYToPolar(int cx, int cy, Point p) {
        int tx = p.x - cx;
        int ty = p.y - cy;

        double r = Math.sqrt(tx * tx + ty * ty);
        double theta = Math.toDegrees(Math.atan2(ty, tx));

        return new PolarPoint(r, theta);
    }

    /**
     * Get a point symmetric to the the line passing through the central angle of an
     * arc.
     * 
     * @param p1
     * @param cx
     * @param cy
     * @param arcAngle
     * @return
     */
    public static Point getSymmetricPoint(Point p1, int cx, int cy, int centralAngle) {
        int tx = p1.x - cx;
        int ty = p1.y - cy;

        double r = Math.sqrt(tx * tx + ty * ty);
        double theta = Math.toDegrees(Math.atan2(ty, tx));

        double deltaAngle = Math.abs(theta - centralAngle * 1.0d);

        if (theta > centralAngle) {
            return polarToXY(cx, cy, (int) r, (int) (centralAngle - deltaAngle));
        } else {
            return polarToXY(cx, cy, (int) r, (int) (centralAngle + deltaAngle));
        }
    }

    /**
     * Mid point between p1 and p2.
     * 
     * @param p1
     * @param p2
     * @return
     */
    public static Point midPoint(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    /**
     * Get a "good" control point between two points. I may move this to a class
     * specific for Bezier curves.
     * 
     * @param p1
     * @param p2
     * @return
     */
    public static Point getControlPoint(Point p1, Point p2) {
        Point controlPoint = PointUtils.midPoint(p1, p2);

        int diffx = Math.abs(p1.x - p2.x);
        int diffy = Math.abs(p1.y - p2.y);

        controlPoint.y += diffx;
        controlPoint.x += diffy;

        return controlPoint;
    }

    /**
     * Get a "good" control point between two points. This one is located at the
     * left of a path.
     * 
     * @param p1
     * @param p2
     * @return
     */
    public static Point getOppositeControlPoint(Point p1, Point p2) {
        Point controlPoint = PointUtils.midPoint(p1, p2);

        int diffx = Math.abs(p1.x - p2.x);
        int diffy = Math.abs(p1.y - p2.y);

        controlPoint.y -= diffx;
        controlPoint.x -= diffy;

        return controlPoint;
    }

    /**
     * Distance between two Points.
     * 
     * @param p1
     * @param p2
     * @return
     */
    public static double getDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }
}