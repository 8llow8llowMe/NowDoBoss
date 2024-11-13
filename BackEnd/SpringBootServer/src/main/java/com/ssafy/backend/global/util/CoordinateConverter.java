package com.ssafy.backend.global.util;

import com.ssafy.backend.domain.commercial.exception.CoordinateTransformationException;
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

    private static final GeometryFactory GEOMETRY_FACTORY = JTSFactoryFinder.getGeometryFactory();
    private static final CoordinateReferenceSystem SOURCE_CRS;
    private static final CoordinateReferenceSystem TARGET_CRS;
    private static final MathTransform TRANSFORM;

    static {
        try {
            SOURCE_CRS = CRS.decode("EPSG:5181");
            TARGET_CRS = CRS.decode("EPSG:4326");
            TRANSFORM = CRS.findMathTransform(SOURCE_CRS, TARGET_CRS, true);
        } catch (Exception e) {
            throw new CoordinateTransformationException("좌표 변환 초기화 실패", e);
        }
    }

    public static Point transform(double x, double y) throws Exception {
        Coordinate coordinate = new Coordinate(y, x);
        Geometry point = GEOMETRY_FACTORY.createPoint(coordinate);
        Geometry transformedPoint = JTS.transform(point, TRANSFORM);
        return (Point) transformedPoint;
    }
}

