package ua.police.service;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.geo.GeoPoint;
import com.backendless.servercode.IBackendlessService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by koxa on 05.09.2015.
 */
public class PoliceService implements IBackendlessService {

    public final static String IMAGE_FILE_PRE_PATH = "/police/users/";
    public final static String GEO_POINT_CATEGORY = "Offender";
    public final static String USER_METADATA = "user";
    public final static String IMAGE_PATH_METADATA = "imagePath";
    public final static String IMAGE_DESCRIPTION_METADATA = "description";

    public void report(String userId, byte[] image, double latitude, double longitude, String description) throws Exception {
        PoliceServiceValidator.notNullValidation(image);

        BackendlessUser user = Backendless.UserService.findById(userId);
        GeoPoint geoPoint = Backendless.Geo.savePoint(latitude, longitude, Arrays.asList(GEO_POINT_CATEGORY), Collections.EMPTY_MAP);

        String imagePath = saveFile(image, userId, geoPoint);
        Map<String, Object> meta = createMetadata(user, imagePath, description);
        geoPoint.setMetadata(meta);
        Backendless.Geo.savePoint(geoPoint);
    }

    private String saveFile(byte[] image, String userId, GeoPoint geoPoint) {
        String imagePath = IMAGE_FILE_PRE_PATH + userId;
        String imageName = geoPoint.getObjectId() + ".png";
        Backendless.Files.saveFile(imagePath, imageName, image);

        return imageName + "/" + imageName;
    }

    private Map<String, Object> createMetadata(BackendlessUser user, String imageFullPath, String description) {
        Map<String, Object> metadata = new HashMap<>(3);

        metadata.put(USER_METADATA, user);
        metadata.put(IMAGE_PATH_METADATA, imageFullPath);
        metadata.put(IMAGE_DESCRIPTION_METADATA, description);

        return metadata;
    }
}
