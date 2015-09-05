package ua.police.service;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.geo.BackendlessGeoQuery;
import com.backendless.geo.GeoPoint;
import com.backendless.geo.Units;
import com.backendless.servercode.IBackendlessService;

import java.util.*;

public class PoliceService implements IBackendlessService {
    private static final int DEFAULT_TIME_INTERVAL = 6;

    public static final String IMAGE_FILE_PRE_PATH = "/police/users/";
    public static final String GEO_POINT_CATEGORY = "Offender";
    public static final String USER_METADATA = "user";
    public static final String IMAGE_PATH_METADATA = "imagePath";
    public static final String IMAGE_DESCRIPTION_METADATA = "description";
    public static final String TIMESTAMP_METADATA = "timestamp";
    public static final String IS_ACTIVE_METADATA = "isActive";
    public static final String POLICEMAN = "policeman";
    public static final String ROLE_KEY = "role";

    public void report(String userId, byte[] image, double latitude, double longitude, String description) throws Exception {
        if (image == null || image.length == 0)
            throw new IllegalArgumentException("Some arguments are null.");

        BackendlessUser user = Backendless.UserService.findById(userId);
        GeoPoint geoPoint = Backendless.Geo.savePoint(latitude, longitude, Arrays.asList(GEO_POINT_CATEGORY), Collections.EMPTY_MAP);

        String imagePath = saveFile(image, userId, geoPoint);
        Map<String, Object> meta = createMetadata(user, imagePath, description);
        geoPoint.setMetadata(meta);
        Backendless.Geo.savePoint(geoPoint);
    }

    public BackendlessCollection<GeoPoint> getReports(double latitude, double longitude, double radius)
    {
        BackendlessGeoQuery query = new BackendlessGeoQuery();
        query.setLatitude(latitude);
        query.setLongitude(longitude);
        query.setUnits(Units.METERS);
        query.setRadius(radius);
        query.setWhereClause(String.format("timestamp > %s", getDateTimeBefore(DEFAULT_TIME_INTERVAL).getTime()));
        HashMap<String, Object> metaSearch = new HashMap<String, Object>();
        metaSearch.put(IS_ACTIVE_METADATA, true);
        query.setMetadata(metaSearch);
        query.setIncludeMeta(true);
        BackendlessCollection<GeoPoint> points = Backendless.Geo.getPoints(query);
        return points;
    }

    public BackendlessCollection<GeoPoint> getInMap(double nlatitude, double wlongiture, double slatitude, double elongitude, int mapWidth)
    {
        BackendlessGeoQuery query = new BackendlessGeoQuery();
        query.setWhereClause(String.format("timestamp > %s", getDateTimeBefore(DEFAULT_TIME_INTERVAL).getTime()));
        query.setSearchRectangle(new double[]{nlatitude, wlongiture, slatitude, elongitude});
        query.setClusteringParams(wlongiture, elongitude, mapWidth);
        return Backendless.Geo.getPoints(query);
    }

    public void completeRequest(GeoPoint point)
    {
        BackendlessUser user = (BackendlessUser)point.getMetadata(USER_METADATA);
        if (!isPolicemen(user))
            throw new RuntimeException("Only policemen can complete requests");
        point.putMetadata(IS_ACTIVE_METADATA, false);
        Backendless.Geo.savePoint(point);
    }

    private boolean isPolicemen(BackendlessUser user) {
        return POLICEMAN.equals(user.getProperty(ROLE_KEY));
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
        metadata.put(TIMESTAMP_METADATA, new Date().getTime());
        metadata.put(IS_ACTIVE_METADATA, true);

        return metadata;
    }

    private static Date getDateTimeBefore( int hours )
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -hours);
        return cal.getTime();
    }
}
