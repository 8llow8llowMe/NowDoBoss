package com.ssafy.backend.global.component.geotools;

import com.ssafy.backend.domain.commercial.exception.CoordinateTransformationException;
import javax.annotation.PostConstruct;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.springframework.stereotype.Component;

@Component
public class CoordinateConverter {

    private final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    private MathTransform transform;

    @PostConstruct
    public void init() {
        try {
            CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:5181", true);
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326", true);
            transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
        } catch (Exception e) {
            throw new CoordinateTransformationException("좌표 변환 초기화 실패", e);
        }
    }

    public Point transform(double x, double y) throws Exception {
        Coordinate coordinate = new Coordinate(y, x);
        Geometry point = geometryFactory.createPoint(coordinate);
        Geometry transformedPoint = JTS.transform(point, transform);
        return (Point) transformedPoint;
    }
}

