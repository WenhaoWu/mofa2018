package com.mofa.metropolia.architectmuseo;

public class Constains_BackendAPI_Url {
    public static final String URL_POIList_Distant =
            "http://www.arkkitehtuurimuseo.fi/newpro/Wikitude_1/geoLocator/distance_matrix.php?lat=";//+60+"&lng="+24+"&cate="+String;
            //"http://dev.mw.metropolia.fi/mofa/Wikitude_1/geoLocator/poi.json";

    public static final String URL_POIList_Popular=
            "http://www.arkkitehtuurimuseo.fi/newpro/Wikitude_1/geoLocator/PopularFilter.php";//+"&cate="+String;

    public static final String URL_POIDetail =
            "http://www.arkkitehtuurimuseo.fi/newpro/Wikitude_1/geoLocator/poi_detail.php?id=";

    public static final String URL_Sound_Cloud = "https://api.soundcloud.com/tracks/228179666/stream?client_id=76bf4a478f95a82ca090ecd8fa5b99db";// trackid is only one variable

    //Do not use browser to access this api to rate the POI
    //Because for one attempt the browser actually send the request twice
    public static final String URL_POIRate =
            "http://www.arkkitehtuurimuseo.fi/newpro/Wikitude_1/geoLocator/poi_rate_first.php?";//id=42&rate=4

    public static final String URL_POISearch =
            "http://www.arkkitehtuurimuseo.fi/newpro/Wikitude_1/geoLocator/search.php?query=";//+string

    public static final String URL_3dModels =
            "http://www.arkkitehtuurimuseo.fi/newpro/Wikitude_1/3dModel/";//+(string)3d Model Name+ .wt3

    public static final String URL_GetModelsName =
            "http://www.arkkitehtuurimuseo.fi/newpro/Wikitude_1/3dModel/getModelName.php";

    public static final String URL_POIList_Suggest =
            "http://www.arkkitehtuurimuseo.fi/newpro/Wikitude_1/public/get_third_view.php";

    public static final String URL_GetCatagories =
            "http://arkkitehtuurimuseo.fi/newpro/Wikitude_1/geoLocator/Catagories.php";
}
