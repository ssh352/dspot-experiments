package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_Issue_519 extends TestCase {
    public void test_issue() throws Exception {
        String json = "{\"accomTypes\":[1],\"address\":\"\",\"airportIds\":[],\"airportrailwayIds\":[],\"areaIds\":[0,14,673],\"avgPrice\":0,\"avgScore\":3.8,\"baseScore\":-0.0035256863871981348,\"brandId\":23762,\"brandLogo\":\"\",\"brandName\":\"\",\"brandStory\":\"\",\"campaignsScore\":0,\"cates\":[392,20,79],\"cityIds\":[1],\"collegeIds\":[58],\"competeDiffPrice\":0,\"couponCount\":990,\"customAvgScore\":76.00890238508207,\"declineScore\":0,\"distance\":0,\"drLowestPrice\":289,\"festCanuse\":0,\"hasDR\":1,\"hasDRGroup\":0,\"hasGroup\":1,\"hasHR\":0,\"hasHRGroup\":0,\"hasInvoice\":0,\"hospitalIds\":[23599],\"hotelTypes\":[1,0,888],\"hrLowestPrice\":0,\"inBlackList\":0,\"innCates\":[],\"introduction\":\"\",\"landmarkScore\":0,\"lastModifyTime\":1457924599643,\"latitude\":39.997828,\"location\":\"39.997828,116.466004\",\"longitude\":116.466004,\"lowestPrice\":289,\"mapSmartPartScore\":69.74729610098822,\"markNumbers\":270,\"name\":\"\u5e03\u4e01\u9152\u5e97\uff08\u5317\u4eac\u671b\u4eac\u5e97\uff09\",\"newDealScore\":0,\"phone\":\"010-64728973\",\"poiid\":52209391,\"prds\":[{\"areaIds\":[14,673],\"beginTime\":1436371200,\"bookingType\":0,\"cates\":[0,1],\"cityIds\":[1],\"dateCantUse\":[\"20160313\",\"20160314\",\"20160315\",\"20160316\",\"20160317\",\"20160318\",\"20160319\",\"20160320\",\"20160321\",\"20160322\",\"20160323\",\"20160324\",\"20160325\",\"20160326\",\"20160327\",\"20160328\",\"20160329\",\"20160330\",\"20160331\",\"20160401\",\"20160402\",\"20160403\",\"20160404\",\"20160405\",\"20160406\",\"20160407\",\"20160408\",\"20160409\"],\"did\":30513601,\"endTime\":1460131199,\"gid\":749878,\"hasCampaigns\":0,\"hasInvoice\":0,\"nobooking\":0,\"poiids\":[],\"price\":59,\"soldQuantity\":535,\"value\":80},{\"areaIds\":[14,673],\"beginTime\":1438531200,\"bookingType\":0,\"cates\":[0,1],\"cityIds\":[1],\"dateCantUse\":[\"20160313\",\"20160314\",\"20160315\",\"20160316\",\"20160317\",\"20160318\",\"20160319\",\"20160320\",\"20160321\",\"20160322\",\"20160323\",\"20160324\",\"20160325\",\"20160326\",\"20160327\",\"20160328\",\"20160329\",\"20160330\",\"20160331\",\"20160401\",\"20160402\",\"20160403\",\"20160404\"],\"did\":31035361,\"endTime\":1459699199,\"gid\":858227,\"hasCampaigns\":0,\"hasInvoice\":0,\"nobooking\":0,\"poiids\":[],\"price\":309,\"soldQuantity\":60,\"value\":319},{\"areaIds\":[14,673],\"beginTime\":1438531200,\"bookingType\":0,\"cates\":[0,1],\"cityIds\":[1],\"dateCantUse\":[\"20160313\",\"20160314\",\"20160315\",\"20160316\",\"20160317\",\"20160318\",\"20160319\",\"20160320\",\"20160321\",\"20160322\",\"20160323\",\"20160324\",\"20160325\",\"20160326\",\"20160327\",\"20160328\",\"20160329\",\"20160330\",\"20160331\",\"20160401\",\"20160402\",\"20160403\",\"20160404\"],\"did\":31035397,\"endTime\":1459699199,\"gid\":858226,\"hasCampaigns\":0,\"hasInvoice\":0,\"nobooking\":0,\"poiids\":[],\"price\":289,\"soldQuantity\":157,\"value\":309}],\"railwayStationIds\":[],\"roomSizes\":[0,1,3,4],\"roomStates\":{},\"scenicSpotIds\":[5655],\"showFlag\":1,\"smartAvgBaseScore\":5.7669880413567585,\"smartPartScore\":58.21997185816042,\"smartSoldBaseScore\":1.6134262836027502,\"subwayLineIds\":[3,75],\"subwayStationIds\":[1490,1485,147],\"yfSourceTypes\":[],\"zlSourceType\":0}";
        JSON.parse(json);
    }
}

