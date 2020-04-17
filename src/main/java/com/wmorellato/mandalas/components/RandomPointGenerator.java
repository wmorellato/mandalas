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

import java.util.ArrayList;
import java.util.Random;
import java.awt.*;

import com.wmorellato.mandalas.drawing.PointUtils;

/**
 * Point generator.
 */
public class RandomPointGenerator {
    int mSeed;
    Random mRandom;

    public RandomPointGenerator(int seed) {
        this.mSeed = seed;
        this.mRandom = new Random(seed);
    }

    public Point[] generateRandomPoints(int numberOfPoints, int centerX, int centerY, int radius, int arcAngle) {

        Point[] points = new Point[numberOfPoints];

        for (int i = 0; i < numberOfPoints; i++) {
            points[i] = generateSinglePoint(centerX, centerY, 0, radius, 0, arcAngle);
        }

        return points;
    }

    /**
     * This method create random points within the segment but connecting some of
     * them to the face that will be mirrored
     * 
     * @param numberOfPoints
     * @param connectingPoints
     * @param centerX
     * @param centerY
     * @param minArcRadius
     * @param maxArcRadius
     * @param arcAngle
     * @return
     */
    public Point[] generatePointsForReflection(int numberOfPoints, int connectingPoints, int centerX, int centerY,
            int minArcRadius, int maxArcRadius, int arcAngle) {

        Point[] points = new Point[numberOfPoints];

        // first point sticked to axis
        // TODO: review this
        points[0] = PointUtils.polarToXY(centerX, centerY, minArcRadius, 0);

        for (int i = 1; i < numberOfPoints; i++) {
            // considerar uma função polinomial
            int randomRadius = mRandom.nextInt(maxArcRadius);
            int randomAngle = mRandom.nextInt(arcAngle);
            points[i] = PointUtils.polarToXY(centerX, centerY, randomRadius, randomAngle);
        }

        while (connectingPoints > 0) {
            int index = mRandom.nextInt(numberOfPoints);
            points[index].y = centerY;
            connectingPoints--;
        }

        return points;
    }

    public Point[] generatePointsForClosedCurve(int numberOfPoints, int centerX, int centerY, int minArcRadius,
            int maxArcRadius, int arcAngle, boolean stickFirst, boolean stickLast) {
        ArrayList<Point> points = new ArrayList<>();
        Point last = null;

        int centralAngle = (int) (arcAngle / 2);
        int startIndex = 0;
        int endIndex = numberOfPoints;

        if (stickFirst) {
            points.add(generateSinglePoint(centerX, centerY, minArcRadius, maxArcRadius, centralAngle, centralAngle));
            startIndex++;
        }

        if (stickLast) {
            last = generateSinglePoint(centerX, centerY, minArcRadius, maxArcRadius, centralAngle, centralAngle);
            endIndex--;
        }

        for (int i = startIndex; i < endIndex; i++) {
            points.add(generateSinglePoint(centerX, centerY, minArcRadius, maxArcRadius, 0, arcAngle));
        }

        if (stickLast) {
            points.add(last);
        }

        for (int i = endIndex - 1; i > startIndex - 1; i--) {
            points.add(PointUtils.getSymmetricPoint(points.get(i), centerX, centerY, centralAngle));
        }

        points.add(points.get(0));

        Point[] pointsArray = new Point[points.size()];
        return points.toArray(pointsArray);
    }

    /**
     * Create points in a crescent order (from the pole to the border of the
     * mandala).
     * 
     * @param numberOfPoints
     * @param centerX
     * @param centerY
     * @param minArcRadius
     * @param maxArcRadius
     * @param arcAngle
     * @param stickFirst
     * @param stickLast
     * @return
     */
    public Point[] generateOrderedPath(int numberOfPoints, int centerX, int centerY, int minArcRadius, int maxArcRadius,
            int arcAngle, boolean stickFirst, boolean stickLast) {
        ArrayList<Point> points = new ArrayList<>();
        Point last = null;

        int lastRadius = minArcRadius;
        int centralAngle = (int) (arcAngle / 2);
        int startIndex = 0;
        int endIndex = numberOfPoints;

        if (stickFirst) {
            points.add(generateSinglePoint(centerX, centerY, minArcRadius, minArcRadius, centralAngle, centralAngle));
            lastRadius = (int) PointUtils.XYToPolar(centerX, centerY, points.get(0)).r;
            startIndex++;
        }

        if (stickLast) {
            last = generateSinglePoint(centerX, centerY, maxArcRadius, maxArcRadius, centralAngle, centralAngle);
            endIndex--;
        }

        for (int i = startIndex; i < endIndex; i++) {
            points.add(generateSinglePoint(centerX, centerY, lastRadius, maxArcRadius, 0, arcAngle));
            lastRadius = (int) PointUtils.XYToPolar(centerX, centerY, points.get(i)).r;
        }

        if (stickLast) {
            points.add(last);
        }

        for (int i = endIndex - 1; i > startIndex - 1; i--) {
            points.add(PointUtils.getSymmetricPoint(points.get(i), centerX, centerY, centralAngle));
        }

        points.add(points.get(0));

        Point[] pointsArray = new Point[points.size()];
        return points.toArray(pointsArray);
    }

    /**
     * 
     * @param centerX
     * @param centerY
     * @param minArcRadius
     * @param maxArcRadius
     * @param minAngle
     * @param maxAngle
     * @return
     */
    private Point generateSinglePoint(int centerX, int centerY, int minArcRadius, int maxArcRadius, int minAngle,
            int maxAngle) {
        int randomRadius;
        int randomAngle;

        if (minArcRadius == maxArcRadius) {
            randomRadius = minArcRadius;
        } else {
            randomRadius = mRandom.nextInt(maxArcRadius - minArcRadius) + minArcRadius + 1;
        }

        if (minAngle == maxAngle) {
            randomAngle = minAngle;
        } else {
            randomAngle = mRandom.nextInt(maxAngle - minAngle) + minAngle + 1;
        }

        return PointUtils.polarToXY(centerX, centerY, randomRadius, randomAngle);
    }
}