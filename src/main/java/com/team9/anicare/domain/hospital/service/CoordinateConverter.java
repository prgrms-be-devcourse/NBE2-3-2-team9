package com.team9.anicare.domain.hospital.service;

import org.locationtech.proj4j.*;
import org.springframework.stereotype.Component;

@Component
public class CoordinateConverter {

    private static final CRSFactory crsFactory = new CRSFactory();
    private static final CoordinateReferenceSystem srcCrs = crsFactory.createFromName("EPSG:5179"); // UTM-K
    private static final CoordinateReferenceSystem destCrs = crsFactory.createFromName("EPSG:4326"); // WGS84
    private static final CoordinateTransform transform = new CoordinateTransformFactory().createTransform(srcCrs, destCrs);




    public double[] convertEPSG5179ToWGS84(double x, double y) {
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem epsg5179 = crsFactory.createFromName("EPSG:5179");
        CoordinateReferenceSystem wgs84 = crsFactory.createFromName("EPSG:4326");

        ProjCoordinate sourceCoord = new ProjCoordinate(x, y);
        ProjCoordinate targetCoord = new ProjCoordinate();

        new org.locationtech.proj4j.CoordinateTransformFactory()
                .createTransform(epsg5179, wgs84)
                .transform(sourceCoord, targetCoord);

        return new double[]{targetCoord.y, targetCoord.x}; // [위도, 경도]
    }


    // 좌표 변환 클래스 생성
//    public double[] convertEPSG5179ToWGS84(double x, double y) {
//        CRSFactory factory = new CRSFactory();
//        CoordinateReferenceSystem source = factory.createFromName("EPSG:5179");
//        CoordinateReferenceSystem target = factory.createFromName("EPSG:4326");
//
//        CoordinateTransform transform = new CoordinateTransformFactory().createTransform(source, target);
//
//        ProjCoordinate sourceCoord = new ProjCoordinate(x, y);
//        ProjCoordinate targetCoord = new ProjCoordinate();
//
//        transform.transform(sourceCoord, targetCoord);
//
//        return new double[]{targetCoord.y, targetCoord.x}; // (latitude, longitude)
//    }
}

