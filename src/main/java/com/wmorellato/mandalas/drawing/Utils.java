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

import java.util.ArrayList;
import java.awt.image.*;
import java.awt.*;

/**
 * Utils
 */
public class Utils {
    public static Point[] concatArraysRemovingNulls(Point[] a1, Point[] a2) {
        ArrayList<Point> cleanList = new ArrayList<>();

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != null) {
                cleanList.add(a1[i]);
            }
        }

        for (int i = 0; i < a2.length; i++) {
            if (a2[i] != null) {
                cleanList.add(a2[i]);
            }
        }

        Point[] points = new Point[cleanList.size()];
        return cleanList.toArray(points);
    }

    public static int[][] convertTo2DUsingGetRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getRGB(col, row);
            }
        }

        return result;
    }

    /**
     * Paint a shape
     * 
     * @param g2
     * @param s
     * @param c
     */
    public static void paintShape(Graphics2D g2, Shape s, Color c) {
        g2.setPaint(c);
        g2.draw(s);
        g2.setPaint(Color.BLACK);
    }

    /**
     * Fill a shape
     * 
     * @param g2
     * @param s
     * @param c
     */
    public static void fillShape(Graphics2D g2, Shape s, Color c) {
        g2.setPaint(c);
        g2.fill(s);
        g2.setPaint(Color.BLACK);
    }
}