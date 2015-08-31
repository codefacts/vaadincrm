package vaadincrm.model;

/**
 * Created by someone on 10-Aug-2015.
 */
public class Query {
    public static final String brs = "brs";
    public static final String locations = "locations";
    public static final String regions = "regions";
    public static final String regionCount = "regionCount";
    public static final String areaCount = "areaCount";
    public static final String houseCount = "houseCount";
    public static final String brCount = "brCount";
    public static final String locationCount = "locationCount";
    public static final String name = "name";
    public static final String elementCount = "elementCount";
    public static final String region = "region";
    public static final String id = "_id";
    public static final String createDate = "createDate";
    public static final String modifyDate = "modifyDate";
    public static final String createdBy = "createdBy";
    public static final String modifiedBy = "modifiedBy";
    public static final String message = "message";
    public static final String area = "area";
    public static final String distributionHouse = "distributionHouse";
    public static final String distributionHouseId = "distributionHouseId";
    public static final String label = "label";
    public static final String userId = "userId";
    public static final String params = "params";

    public static final String regionId = Area.region + "." + id;
    public static final String areaId = House.area + "." + id;
    public static final String houseId = concat(distributionHouse, id);
    public static final String brId = concat(Contact.br, id);
    public static final String userTypeId = concat(User.userType, id);
    public static final String count = "count";
    public static final String prefix = "prefix";
    public static final String brand = "brand";
    public static final String caption = "caption";

    public static final String concat(String... strings) {
        return String.join(".", strings);
    }
}
