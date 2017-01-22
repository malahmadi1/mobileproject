/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.xy;

import android.graphics.PointF;
import android.graphics.RectF;

import com.androidplot.Region;
import java.util.ArrayList;
import java.util.List;

/**
 * RectRegion is just a rectangle with additional methods for determining
 * intersections with other RectRegion instances.  RectRegion operates on
 * "native units" which are simply whatever unit type is passed into it by the user.
 */
public class RectRegion {

    Region xRegion;
    Region yRegion;
    private String label;

    public RectRegion() {
        xRegion = new Region();
        yRegion = new Region();
    }

    public static RectRegion withDefaults(RectRegion defaults) {
        if(defaults == null || !defaults.isFullyDefined()) {
            throw new IllegalArgumentException("When specifying defaults, RectRegion param must contain no null values.");
        }

        RectRegion r = new RectRegion();
        r.xRegion = Region.withDefaults(defaults.getxRegion());
        r.yRegion = Region.withDefaults(defaults.getyRegion());
        return r;
    }

    public RectRegion(XYCoords min, XYCoords max) {
        this(min.x, max.x, min.y, max.y);
    }

    /**
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     */
    public RectRegion(Number minX, Number maxX, Number minY, Number maxY, String label) {
        xRegion = new Region(minX, maxX);
        yRegion = new Region(minY, maxY);
        this.setLabel(label);
    }

    public RectRegion(RectF rect) {
        this(rect.left < rect.right ? rect.left : rect.right,
                rect.right > rect.left ? rect.right : rect.left,
                rect.bottom < rect.top ? rect.bottom : rect.top,
                rect.top > rect.bottom ? rect.top : rect.bottom);
    }

    @SuppressWarnings("SameParameterValue")
    public RectRegion(Number minX, Number maxX, Number minY, Number maxY) {
        this(minX, maxX, minY, maxY, null);
    }

    public XYCoords transform(Number x, Number y, RectRegion region2, boolean flipX, boolean flipY) {
        Number xx = xRegion.transform(x.doubleValue(), region2.xRegion, flipX);
        Number yy = yRegion.transform(y.doubleValue(), region2.yRegion, flipY);
        return new XYCoords(xx, yy);
    }

    public XYCoords transform(Number x, Number y, RectRegion region2) {
        return transform(x, y, region2, false, false);
    }

    public XYCoords transform(XYCoords value, RectRegion region2) {
        return transform(value.x, value.y, region2);
    }

    /**
     * Transform a region (r) from the current region space (this) into the specified one (r2)
     * @param r The region to which the transformation applies
     * @param r2 The region into which r is being transformed
     * @return
     */
    public RectRegion transform(RectRegion r, RectRegion r2, boolean flipX, boolean flipY) {
        return new RectRegion(
                transform(r.getMinX(), r.getMinY(), r2, flipX, flipY),
                transform(r.getMaxX(), r.getMaxY(), r2, flipX, flipY)
        );
    }

    /**
     * Convenience method to transform into screen coordinate space.  Equivalent to invoking
     * {@link #transform(Number, Number, RectF, boolean, boolean)} with
     * flipX = false and flipY = true.
     * @param x
     * @param y
     * @param region2
     * @return
     */
    public PointF transformScreen(Number x, Number y, RectF region2) {
        return transform(x, y, region2, false, true);
    }

    public void transformScreen(PointF result, Number x, Number y, RectF region2) {
        transform(result, x, y, region2, false, true);
    }

    public void transform(PointF result, Number x, Number y, RectF region2, boolean flipX, boolean flipY) {
        result.x = (float) xRegion.transform(x.doubleValue(), region2.left, region2.right, flipX);
        result.y = (float) yRegion.transform(y.doubleValue(), region2.top, region2.bottom, flipY);
    }

    public PointF transform(Number x, Number y, RectF region2, boolean flipX, boolean flipY) {
        PointF result = new PointF();
        transform(result, x, y, region2, flipX, flipY);
        return result;
    }

    public PointF transformScreen(XYCoords value, RectF region2) {
        return transform(value, region2, false, true);
    }

    /**
     * Convenience method to transform into screen coordinate space.  Equivalent to invoking
     * {@link #transform(XYCoords, RectF, boolean, boolean)} with flipX = false and flipY = true.
     * @param value
     * @param region2
     * @param flipX
     * @param flipY
     * @return
     */
    public PointF transform(XYCoords value, RectF region2, boolean flipX, boolean flipY) {
        return transform(value.x, value.y, region2, flipX, flipY);
    }

    public void union(Number x, Number y) {
        xRegion.union(x);
        yRegion.union(y);
    }

    /**
     * Compares the input bounds xy min/max vals against this instance's current xy min/max vals.
     * If the input.min is less than this.min then this.min will be set to input.min.
     * If the input.max is greater than this.max then this.max will be set to input.max
     *
     * The result will always have equal or greater area than the inputs.
     * @param input
     */
    public void union(RectRegion input) {
        xRegion.union(input.xRegion);
        yRegion.union(input.yRegion);
    }

    public boolean intersects(RectRegion region) {
        return intersects(region.getMinX(), region.getMaxX(), region.getMinY(), region.getMaxY());
    }

    /**
     * Tests whether this region intersects the region defined by params.  Use
     * null to represent infinity.  Negative and positive infinity is implied by
     * the boundary edge, ie. a maxX of null equals positive infinity while a
     * minX of null equals negative infinity.
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     * @return
     */
    public boolean intersects(Number minX, Number maxX, Number minY, Number maxY) {
        return xRegion.intersects(minX, maxX) && yRegion.intersects(minY, maxY);
    }

    public RectF asRectF() {
        return new RectF(getMinX().floatValue(), getMinY().floatValue(),
                getMaxX().floatValue(), getMaxY().floatValue());
    }

    /**
     * The result of an intersect is always a RectRegion with an equal or smaller area.
     * @param clippingBounds
     */
    public void intersect(RectRegion clippingBounds) {
        if(intersects(clippingBounds)) {
            xRegion.intersect(clippingBounds.xRegion);
            yRegion.intersect(clippingBounds.yRegion);
        } else {
            setMinY(null);
            setMaxY(null);
            setMinX(null);
            setMaxX(null);
        }
    }

    /**
     * Returns a list of XYRegions that either completely or partially intersect the area
     * defined by params. A null value for any parameter represents infinity / no boundary.
     * @param regions The list of regions to search through
     * @return
     */
    public List<RectRegion> intersects(List<RectRegion> regions) {
        ArrayList<RectRegion> intersectingRegions = new ArrayList<>();
        for (RectRegion r : regions) {
            if (r.intersects(getMinX(), getMaxX(), getMinY(), getMaxY())) {
                intersectingRegions.add(r);
            }
        }
        return intersectingRegions;
    }

    /**
     * @return Width of this region, in native units
     */
    public Number getWidth() {
        return distanceBetween(getMinX(), getMaxX());
    }

    /**
     * @return Height of this region, in native units
     */
    public Number getHeight() {
        return distanceBetween(getMinY(), getMaxY());
    }

    /**
     * Calculate the distance between two points in a single dimension.
     * @param x
     * @param y
     * @return
     */
    private Number distanceBetween(Number x, Number y) {
        return Math.abs(x.doubleValue() - y.doubleValue());
    }

    public void set(Number minX, Number maxX, Number minY, Number maxY) {
        setMinX(minX);
        setMaxX(maxX);
        setMinY(minY);
        setMaxY(maxY);
    }

    public boolean isMinXSet() {
        return  xRegion.isMinSet();
    }

    public Number getMinX() {
        return xRegion.getMin();
    }

    public void setMinX(Number minX) {
        xRegion.setMin(minX);
    }

    public boolean isMaxXSet() {
        return  xRegion.isMaxSet();
    }

    public Number getMaxX() {
        return xRegion.getMax();
    }

    public void setMaxX(Number maxX) {
        xRegion.setMax(maxX);
    }

    public boolean isMinYSet() {
        return  yRegion.isMinSet();
    }

    public Number getMinY() {
        return yRegion.getMin();
    }

    public void setMinY(Number minY) {
        yRegion.setMin(minY);
    }

    public boolean isMaxYSet() {
        return  yRegion.isMaxSet();
    }

    public Number getMaxY() {
        return yRegion.getMax();
    }

    public void setMaxY(Number maxY) {
        yRegion.setMax(maxY);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Region getxRegion() {
        return xRegion;
    }

    public void setxRegion(Region xRegion) {
        this.xRegion = xRegion;
    }

    public Region getyRegion() {
        return yRegion;
    }

    public void setyRegion(Region yRegion) {
        this.yRegion = yRegion;
    }

    /**
     *
     * @return True if both xRegion and yRegion are defined, false otherwise
     */
    public boolean isFullyDefined() {
        return xRegion.isDefined() && yRegion.isDefined();
    }

    /**
     * True if this region contains the specified coordinates.
     * @param x
     * @param y
     * @return
     */
    public boolean contains(Number x, Number y) {
        return getxRegion().contains(x) && getyRegion().contains(y);
    }

    /**
     * Checks to see whether the specified line.  Note that this implementation will return
     * true even if the line is completely enclosed by this RectRegion.
     * WARNING: this implementation has problems.  See associated unit test for details.
     * @param x1 x-coord of the line beginning
     * @param y1 y-coord of the line beginning
     * @param x2 x-coord of the line end
     * @param y2 y-coord of the line end
     * @return True if this RectRegion overlaps any part of the specified line.
     */
    public boolean intersectsWithLine(Number x1, Number y1, Number x2, Number y2) {
        if(contains(x1, y1) || contains(x2, y2)) {
            return true;
        }

        // if true, it means that these points exist on different sides of the rect's edges
        final boolean x1MinRelation = x1.doubleValue() < getMinX().doubleValue();
        final boolean x2MinRelation = x2.doubleValue() < getMinX().doubleValue();
        final boolean xMinRelation = x1MinRelation &! x2MinRelation;

        final boolean x1MaxRelation = x1.doubleValue() < getMaxX().doubleValue();
        final boolean x2MaxRelation = x2.doubleValue() < getMaxX().doubleValue();
        final boolean xMaxRelation = x1MaxRelation &! x2MaxRelation;

        final boolean y1MinRelation = y1.doubleValue() < getMinY().doubleValue();
        final boolean y2MinRelation = y2.doubleValue() < getMinY().doubleValue();
        final boolean yMinRelation = y1MinRelation &! y2MinRelation;

        final boolean y1MaxRelation = y1.doubleValue() < getMaxY().doubleValue();
        final boolean y2MaxRelation = y2.doubleValue() < getMaxY().doubleValue();
        final boolean yMaxRelation = y1MaxRelation &!  y2MaxRelation;

        return ((xMinRelation | xMaxRelation) || getxRegion().contains(x1) & (yMinRelation | yMaxRelation) || getyRegion().contains(y1));
    }
}
