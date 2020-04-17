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
import java.awt.geom.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.wmorellato.mandalas.config.ConfigurationManager;
import com.wmorellato.mandalas.drawing.Utils;
import com.wmorellato.mandalas.exceptions.InvalidCurveRangeException;

/**
 * Main class used to create a mandala. It reads from the configuration file
 * what and how many types of elements should be drawn, combine and paint them,
 * and generate the pixels of the figure that will be used to draw it in the
 * world.
 */
public class Mandala {
    Area mMandalaArea;
    MandalaAttributes mAttributes;
    MandalaElement[] mElements;
    int[][] mPixels;

    public Mandala(ConfigurationManager config, MandalaAttributes attr) {
        mAttributes = attr;
        mMandalaArea = new Area();

        createElements(config);
        compose(config);
    }

    /**
     * Get the types and the number of elements from the configuration file and
     * create each one.
     * 
     * @param config
     */
    private void createElements(ConfigurationManager config) {
        int numRandom = config.getNumberOfRandomElements();
        int numFixed = config.getNumberOfFixedElements();
        mElements = new MandalaElement[numRandom + numFixed];

        // get from config
        int i = 0;
        while (i < numRandom) {
            try {
                mElements[i++] = getRandomElement(config);
            } catch (InvalidCurveRangeException e) {
                System.out.println(String.format("Invalid element configured on random pool in config.yml"));
            }
        }

        for (HashMap.Entry<String, ElementType> element : config.getFixedElements().entrySet()) {
            try {
                mElements[i++] = getFixedElement(element.getKey(), element.getValue(), config);
            } catch (InvalidCurveRangeException e) {
                System.out.println(
                        String.format("Invalid element configured for element %s config.yml", element.getKey()));
            }
        }
    }

    /**
     * Method to create a random element based on the random pool set in the
     * configuration file.
     * 
     * @param config
     * @return
     * @throws InvalidCurveRangeException
     */
    private MandalaElement getRandomElement(ConfigurationManager config) throws InvalidCurveRangeException {
        int randIndex = mAttributes.nextInt(config.getPool().size());
        ElementType elementType = config.getPool().get(randIndex);

        switch (elementType) {
            case CURVE_CONCAVE:
                return new InnerPath(mAttributes, config.getCurveVerticesFromPool(ElementType.CURVE_CONCAVE),
                        config.getRangeFromPool(ElementType.CURVE_CONCAVE), InnerPath.CONCAVE_PATH);
            case CURVE_CONVEX:
                return new InnerPath(mAttributes, config.getCurveVerticesFromPool(ElementType.CURVE_CONVEX),
                        config.getRangeFromPool(ElementType.CURVE_CONVEX), InnerPath.CONVEX_PATH);
            case CURVE_RANDOM:
                return new InnerPath(mAttributes, config.getCurveVerticesFromPool(ElementType.CURVE_RANDOM),
                        config.getRangeFromPool(ElementType.CURVE_RANDOM), InnerPath.RANDOM_PATH);
            case PETAL:
                return new Petal(mAttributes, config.getRangeFromPool(ElementType.PETAL));
            case STRIP:
                return new Petal(mAttributes, config.getRangeFromPool(ElementType.PETAL));
            default:
                return new InnerPath(mAttributes, config.getCurveVerticesFromPool(ElementType.CURVE_RANDOM),
                        config.getRangeFromPool(ElementType.CURVE_RANDOM), InnerPath.RANDOM_PATH);
        }
    }

    /**
     * Method to create the exact elements specified in the configuration file.
     * 
     * @param id
     * @param elementType
     * @param config
     * @return
     * @throws InvalidCurveRangeException
     */
    private MandalaElement getFixedElement(String id, ElementType elementType, ConfigurationManager config)
            throws InvalidCurveRangeException {
        switch (elementType) {
            case CURVE_CONCAVE:
                return new InnerPath(mAttributes, config.getCurveVerticesFromFixedElement(id),
                        config.getRangeFromFixedElement(id), InnerPath.CONCAVE_PATH);
            case CURVE_CONVEX:
                return new InnerPath(mAttributes, config.getCurveVerticesFromFixedElement(id),
                        config.getRangeFromFixedElement(id), InnerPath.CONVEX_PATH);
            case CURVE_RANDOM:
                return new InnerPath(mAttributes, config.getCurveVerticesFromFixedElement(id),
                        config.getRangeFromFixedElement(id), InnerPath.RANDOM_PATH);
            case PETAL:
                return new Petal(mAttributes, config.getRangeFromFixedElement(id));
            case STRIP:
                return new Petal(mAttributes, config.getRangeFromFixedElement(id));
            default:
                return new InnerPath(mAttributes, config.getCurveVerticesFromPool(ElementType.CURVE_RANDOM),
                        config.getRangeFromPool(ElementType.CURVE_RANDOM), InnerPath.RANDOM_PATH);
        }
    }

    /**
     * Combine all elements into a Graphics2D object, painting each one separately
     * and returning the resulting pixels.
     * 
     * @param config
     */
    private void compose(ConfigurationManager config) {
        BufferedImage image = new BufferedImage(mAttributes.radius * 2 + 1, mAttributes.radius * 2 + 1,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        mMandalaArea = new Area();

        for (int i = 0; i < mElements.length; i++) {
            Color c = new Color(mAttributes.nextInt(255), mAttributes.nextInt(255), mAttributes.nextInt(255));
            g2.setPaint(c);

            mElements[i].distribute(g2);
        }

        mPixels = Utils.convertTo2DUsingGetRGB(image);

        // should we save to file?
        if (config.shouldSaveToFile()) {
            try {
                File outputfile = new File(mAttributes.seed + ".bmp");
                ImageIO.write(image, "bmp", outputfile);
            } catch (IOException e) {
            }
        }

        g2.dispose();
    }

    public Area getArea() {
        return this.mMandalaArea;
    }

    public int[][] getRGBPixels() {
        return mPixels;
    }

    public MandalaElement[] getElements() {
        return mElements;
    }
}