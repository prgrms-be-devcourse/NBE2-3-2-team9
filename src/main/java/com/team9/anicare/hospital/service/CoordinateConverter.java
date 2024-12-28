package com.team9.anicare.hospital.service;

import org.locationtech.proj4j.*;
import org.springframework.stereotype.Component;

@Component
public class CoordinateConverter {

    // 좌표 변환 클래스 생성
    public double[] convertEPSG5179ToWGS84(double x, double y) {
        CRSFactory factory = new CRSFactory();
        CoordinateReferenceSystem source = factory.createFromName("EPSG:5179");
        CoordinateReferenceSystem target = factory.createFromName("EPSG:4326");

        CoordinateTransform transform = new CoordinateTransformFactory().createTransform(source, target);

        ProjCoordinate sourceCoord = new ProjCoordinate(x, y);
        ProjCoordinate targetCoord = new ProjCoordinate();

        transform.transform(sourceCoord, targetCoord);

        return new double[]{targetCoord.y, targetCoord.x}; // (latitude, longitude)
    }
}

