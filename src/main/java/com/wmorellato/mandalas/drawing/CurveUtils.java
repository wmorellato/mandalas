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
import java.awt.geom.*;

/**
 * Utils to create curves.
 * 
 * TODO: I am only using quadratic curves. Cubic curves may provide some good
 * drawings too.
 */
public class CurveUtils {
    /**
     * Connect points using quadCurve following the order of the points in the
     * points array.
     * 
     * @param points
     * @return a {@link Path2D.Float} connecting the given points.
     */
    public static Path2D.Float generateInOrderQuadCurve(Point[] points, boolean reversed) {
        Path2D.Float path = new GeneralPath();

        path.moveTo(points[0].x, points[0].y);
        for (int i = 0; i < points.length - 1; i++) {
            Point control;

            if (reversed) {
                control = PointUtils.getOppositeControlPoint(points[i], points[i + 1]);
            } else {
                control = PointUtils.getControlPoint(points[i], points[i + 1]);
            }

            path.quadTo(control.x, control.y, points[i + 1].x, points[i + 1].y);
        }

        Point control = PointUtils.getControlPoint(points[points.length - 2], points[points.length - 1]);
        path.quadTo(control.x, control.y, points[points.length - 1].x, points[points.length - 1].y);
        path.closePath();

        return path;
    }
}