package io.github.froger.instamaterial.ui.utils;

import com.backendless.geo.GeoPoint;

import java.io.File;

public class PhotoService {

    public static final String IMAGE_PATH_METADATA = "imagePath";
    public static final ImageManager manager = new ImageManager();

    public File getPhoto(GeoPoint point)
    {
        String path = (String)point.getMetadata(IMAGE_PATH_METADATA);
        return manager.downloadFromUrl(path, "temp");
    }

}
