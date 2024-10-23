package com.ssafy.backend.global.util;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;


public class CoordinateConverter {
    public static Point transform(double x, double y) throws Exception {
        CoordinateReferenceSystem sourceCrs = CRS.decode("EPSG:5181");
        CoordinateReferenceSystem targetCrs = CRS.decode("EPSG:4326");

        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        Coordinate coordinate = new Coordinate(y, x);
        Geometry point = geometryFactory.createPoint(coordinate);

        MathTransform transform = CRS.findMathTransform(sourceCrs, targetCrs, true);
        Geometry transFormedPoint = JTS.transform(point, transform);

        return (Point) transFormedPoint;
    }
}

