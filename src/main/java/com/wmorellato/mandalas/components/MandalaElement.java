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

/**
 * Abstract class describing a generic element on the Mandala. This class will be
 * extended by others.
 */
abstract public class MandalaElement {
    MandalaAttributes mAttributes;
    Shape mShape;

    public MandalaElement(MandalaAttributes attr) {
        this.mAttributes = attr;
    }

    public Shape mirror() {
        AffineTransform at = new AffineTransform();
        at.translate(mAttributes.CX, mAttributes.CY);
        at.scale(1, -1);
        at.translate(-mAttributes.CX, -mAttributes.CY);

        return at.createTransformedShape(mShape);
    }

    /**
     * Ditribute this element symmetrically around the center of the mandala
     * (counter-clockwise).
     * 
     * @return
     */
    public MandalaElement distribute(Graphics2D g2) {
        int rotations = (int) (360 / mAttributes.getSectionAngle());

        AffineTransform at = new AffineTransform();
        Shape mirrorShape = mirror();

        for (int j = 0; j < rotations; j++) {
            at.rotate(Math.toRadians(mAttributes.getSectionAngle() * j), mAttributes.CX, mAttributes.CY);
            g2.draw(at.createTransformedShape(mShape));
            g2.draw(at.createTransformedShape(mirrorShape));
        }

        return this;
    }

    /**
     * Rotate this element by n degrees.
     * 
     * @param degrees
     * @return
     */
    public MandalaElement rotate(int degrees) {
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(degrees), mAttributes.CX, mAttributes.CY);

        return this;
    }

    /**
     * Rotate this element by n degrees with the pivotal point being on the center
     * of the form.
     * 
     * @param degrees
     * @return
     */
    public MandalaElement rotateLocal(int degrees) {
        Rectangle2D bounds = mShape.getBounds2D();
        int cx = (int) bounds.getCenterX();
        int cy = (int) bounds.getCenterY();

        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(degrees), cx, cy);
        // mTransformedArea = mTransformedArea.createTransformedArea(at);

        return this;
    }

    public Shape getShape() {
        return mShape;
    }
}