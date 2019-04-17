/**
 * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * For more information: http://www.orientdb.com
 */
package com.orientechnologies.spatial;


import com.orientechnologies.lucene.test.BaseLuceneTest;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Enrico Risa on 08/09/15.
 */
public class LuceneSpatialMultiLineStringTest extends BaseSpatialLuceneTest {
    protected static String WKT = "MULTILINESTRING((-158.0630142 21.3631108,-158.0632373 21.3625033,-158.0633575 21.3619997,-158.0634776 21.3613922,-158.0642072 21.3576912,-158.0643789 21.3570517,-158.0645591 21.3564442,-158.064817 21.3557286,-158.0651428 21.3550453,-158.0654432 21.3544698,-158.0658552 21.3537903,-158.0662242 21.3532227,-158.0668165 21.3524633,-158.0673915 21.3518558,-158.0741478 21.3455763,-158.0760433 21.3438376,-158.0776483 21.3423267,-158.0786096 21.3414713,-158.0792104 21.3408397,-158.0796297 21.3404253,-158.0798026 21.3402481,-158.0826506 21.3369385,-158.0831127 21.3364014,-158.0834075 21.3360589,-158.0837122 21.3357062,-158.083788 21.3356184,-158.0851756 21.3340122,-158.0856134 21.3336045,-158.0860168 21.3333086,-158.0864288 21.3330528,-158.0869695 21.332773,-158.0875961 21.3325491,-158.0882398 21.3323812,-158.0887033 21.3323013,-158.089244 21.3322773,-158.0897933 21.3322773,-158.0904285 21.3323013,-158.0909864 21.3324052,-158.0914756 21.3325811,-158.0920507 21.332805,-158.0926397 21.3331239,-158.0931267 21.333463,-158.0936268 21.3339656,-158.093814 21.3342067),(-158.0608129 21.3660325,-158.0613708 21.3655329,-158.0618214 21.3650933,-158.0621266 21.3647301,-158.0624056 21.3643304,-158.0626368 21.3639343,-158.0628347 21.363591,-158.0630142 21.3631108),(-158.0596223 21.3670474,-158.0608129 21.3660325),(-158.0441757 21.3776379,-158.048071 21.3761154,-158.0488006 21.3758276,-158.0492727 21.3756118,-158.0498563 21.3753001,-158.0505515 21.3748685,-158.0511609 21.3744609,-158.0517274 21.3740213,-158.052251 21.3735977,-158.0526973 21.373198,-158.0563069 21.369968,-158.0596223 21.3670474),(-158.0435563 21.3778737,-158.0441757 21.3776379),(-158.0324485 21.3837899,-158.0343467 21.3826132,-158.0383392 21.380405,-158.039385 21.379744,-158.0402519 21.3792724,-158.0410329 21.3788968,-158.041711 21.3786011,-158.0426294 21.3782414,-158.0435563 21.3778737),(-158.0320356 21.3840079,-158.0324485 21.3837899),(-158.023442 21.3888547,-158.0267354 21.3869747,-158.0277935 21.3863772,-158.028972 21.3857107,-158.0320356 21.3840079),(-158.0231409 21.3890189,-158.023442 21.3888547),(-158.0191075 21.390625,-158.0209094 21.3900405,-158.021798 21.3896905,-158.0226534 21.3892749,-158.0231409 21.3890189),(-158.0165725 21.3914004,-158.0191075 21.390625),(-158.0098819 21.3961801,-158.0106489 21.3956401,-158.0115243 21.3948729,-158.0122625 21.3942175,-158.0132581 21.3933305,-158.0139276 21.3927631,-158.0142967 21.3924594,-158.0148202 21.3921317,-158.0153781 21.391852,-158.0159103 21.3916362,-158.0165725 21.3914004),(-158.0094038 21.3965163,-158.0098819 21.3961801),(-158.0000745 21.3993481,-158.0006152 21.399404,-158.001259 21.39942,-158.001937 21.399404,-158.0025636 21.3993161,-158.0031215 21.3992282,-158.0036107 21.3991163,-158.0044776 21.3988926,-158.0052158 21.3986528,-158.0061427 21.3982932,-158.0070611 21.3978937,-158.0077993 21.3975101,-158.0084001 21.3971584,-158.0094038 21.3965163),(-157.9995749 21.3992637,-158.0000745 21.3993481),(-157.9785825 21.3934344,-157.9795867 21.3938659,-157.980445 21.3942016,-157.9821788 21.3948809,-157.9828483 21.3951765,-157.9836894 21.395648,-157.9841272 21.3959038,-157.9848911 21.3964152,-157.9854919 21.3968308,-157.9860841 21.3972144,-157.9866248 21.3975021,-157.9871398 21.3977019,-157.987878 21.3979336,-157.9888921 21.3981294,-157.9894229 21.3981813,-157.989895 21.3981813,-157.9907203 21.3980974,-157.991407 21.3980494,-157.9921094 21.3979896,-157.9927446 21.3979816,-157.9934069 21.398023,-157.9941865 21.3981494,-157.9954654 21.3984291,-157.9975768 21.3988766,-157.9990639 21.3991671,-157.9995749 21.3992637),(-157.9620701 21.3912806,-157.9642145 21.3904694,-157.9646865 21.3903336,-157.9654332 21.3901577,-157.9661232 21.3900378,-157.9668129 21.3899789,-157.9675876 21.3899579,-157.9683858 21.3899739,-157.9689266 21.3900299,-157.9695875 21.3901338,-157.9702226 21.3902776,-157.970832 21.3904614,-157.9713384 21.3906133,-157.9767146 21.392707,-157.9785825 21.3934344),(-157.9596998 21.3922676,-157.9620701 21.3912806),(-157.9588428 21.3926072,-157.9596998 21.3922676),(-157.9531852 21.3931227,-157.9539437 21.3933065,-157.9544973 21.3933845,-157.9550477 21.3934264,-157.9557344 21.3934024,-157.9563953 21.3933385,-157.957039 21.3932106,-157.9577771 21.3930268,-157.958438 21.392787,-157.9588428 21.3926072),(-157.9487134 21.3914924,-157.9514257 21.3924993,-157.9526616 21.3929549,-157.9531852 21.3931227),(-157.94517 21.3902176,-157.9487134 21.3914924),(-157.9260741 21.3703845,-157.9255048 21.3715515,-157.9252902 21.372103,-157.9252044 21.3725267,-157.9251701 21.3731661,-157.9251958 21.3738774,-157.925316 21.3744449,-157.9254876 21.3749165,-157.9257623 21.3754999,-157.926037 21.3758676,-157.9268094 21.3768987,-157.9279277 21.3784371,-157.9288951 21.379776,-157.9311439 21.3827731,-157.9319679 21.3839,-157.9321653 21.3841797,-157.9325086 21.3845953,-157.9330922 21.3851787,-157.9335643 21.3855624,-157.934148 21.3859859,-157.9345342 21.3862097,-157.9349891 21.3864814,-157.9355985 21.3867452,-157.9363023 21.3870169,-157.9372121 21.3873366,-157.9388171 21.387944,-157.9406295 21.3885953,-157.94517 21.3902176),(-157.9262296 21.3700398,-157.9260741 21.3703845),(-157.9271289 21.3680868,-157.9267494 21.3689139,-157.9262296 21.3700398),(-157.92935 21.3633586,-157.9277266 21.3668181,-157.9271289 21.3680868),(-157.932281 21.3430995,-157.9326631 21.3438296,-157.9328262 21.3441814,-157.9330064 21.3446371,-157.9331952 21.3453166,-157.9332982 21.3459002,-157.9333645 21.3462826,-157.9334098 21.3470753,-157.9333583 21.3477468,-157.9332897 21.3482265,-157.9325453 21.3518856,-157.9317447 21.3558207,-157.93144 21.3572618,-157.9311954 21.3584186,-157.9310323 21.3591541,-157.9308006 21.3599374,-157.9306032 21.360513,-157.9303972 21.3610565,-157.92935 21.3633586),(-157.9321067 21.3427872,-157.932281 21.3430995),(-157.9301713 21.3407513,-157.930689 21.3411835,-157.931204 21.3416711,-157.9316794 21.3422123,-157.9321067 21.3427872),(-157.8880433 21.3354212,-157.8887053 21.3350056,-157.8892976 21.3347577,-157.889907 21.3345739,-157.8905078 21.3344539,-157.8908254 21.334414,-157.8912373 21.33439,-157.8918038 21.334382,-157.8927194 21.3344641,-157.8935977 21.3345339,-157.8944131 21.3345898,-157.8949281 21.3346458,-157.8957778 21.3346858,-157.8964473 21.3347337,-157.896988 21.3347897,-157.8977347 21.3348617,-157.8984986 21.3349256,-157.8991853 21.3350136,-157.9001122 21.3351495,-157.9023782 21.3355013,-157.9046012 21.335837,-157.9089052 21.3364906,-157.9210159 21.3382654,-157.9257881 21.338985,-157.9265949 21.3391289,-157.9274446 21.3393607,-157.9281312 21.3396085,-157.928835 21.3399283,-157.9294874 21.3402881,-157.9301713 21.3407513),(-157.884067 21.3378087,-157.8843262 21.337798,-157.8845222 21.3377673,-157.8847344 21.3377006,-157.8849917 21.3375962,-157.8852568 21.3374482,-157.8880433 21.3354212),(-157.8834549 21.337632,-157.8835766 21.3376898,-157.8836831 21.3377293,-157.8837852 21.337763,-157.8839272 21.3377946,-157.884067 21.3378087),(-157.8779177 21.3359254,-157.8782283 21.3360045,-157.8786184 21.3360972,-157.879148 21.3362233,-157.8798816 21.3363157,-157.8802768 21.3363487,-157.8811179 21.3364526,-157.8813425 21.3364889,-157.8817419 21.3366011,-157.8820432 21.3367175,-157.882462 21.3369177,-157.882755 21.3371103,-157.8831006 21.337404,-157.8834549 21.337632),(-157.8774238 21.3357688,-157.8779177 21.3359254),(-157.8699993 21.3303273,-157.8705006 21.3309361,-157.8708354 21.3312719,-157.8719649 21.3323197,-157.8729297 21.3332147,-157.873867 21.3339573,-157.8744463 21.3343201,-157.8750358 21.3346776,-157.8756997 21.3350405,-157.8763352 21.3353138,-157.8765871 21.3354181,-157.8769582 21.3356123,-157.8774238 21.3357688),(-157.8696388 21.3298636,-157.8699993 21.3303273),(-157.8676654 21.3269647,-157.8680077 21.3274554,-157.8696388 21.3298636),(-157.8672534 21.3263891,-157.8676654 21.3269647),(-157.8657613 21.3245853,-157.8658855 21.3246834,-157.8660063 21.3247874,-157.8661185 21.3248954,-157.8662269 21.325009,-157.8663387 21.325136,-157.8664482 21.3252711,-157.8668614 21.3258592,-157.8672534 21.3263891),(-157.8625422 21.3236073,-157.8627321 21.3236729,-157.8629153 21.3237268,-157.863111 21.323773,-157.863318 21.3238126,-157.8642807 21.3239695,-157.8644734 21.3240059,-157.8646852 21.324059,-157.8648726 21.3241223,-157.8650638 21.3241938,-157.8652474 21.3242762,-157.865433 21.3243737,-157.8656065 21.3244785,-157.8657613 21.3245853),(-157.8577497 21.318385,-157.8601337 21.3213251,-157.8610717 21.3225075,-157.8611888 21.3226469,-157.8613107 21.3227847,-157.8614371 21.3229076,-157.8615734 21.3230303,-157.8617231 21.3231496,-157.861866 21.3232527,-157.8620194 21.3233524,-157.862193 21.3234508,-157.8623649 21.3235323,-157.8625422 21.3236073),(-157.857535 21.3181186,-157.8577497 21.318385),(-157.8527098 21.3118395,-157.8527841 21.3119802,-157.8528635 21.3121266,-157.8529431 21.3122691,-157.8530349 21.3124226,-157.8531222 21.3125599,-157.8532171 21.3127056,-157.8533232 21.3128576,-157.8534206 21.3129927,-157.8535182 21.3131266,-157.8536302 21.3132727,-157.8552278 21.3152554,-157.8559235 21.3161187,-157.857535 21.3181186),(-157.8524584 21.3112802,-157.8525154 21.3114221,-157.8525766 21.3115624,-157.8526387 21.311695,-157.8527098 21.3118395),(-157.845235 21.304549,-157.8473693 21.3052736,-157.8476942 21.3053857,-157.8491443 21.3058863,-157.8493537 21.3059624,-157.8495536 21.306041,-157.849738 21.306128,-157.8499141 21.3062216,-157.8500919 21.3063316,-157.8502358 21.306437,-157.850372 21.3065471,-157.8504924 21.3066568,-157.8506296 21.3067891,-157.8507428 21.3069114,-157.850849 21.3070408,-157.8509506 21.3071733,-157.8510586 21.3073289,-157.8511557 21.3074802,-157.8512438 21.3076427,-157.8513095 21.3077835,-157.8513766 21.3079412,-157.8514345 21.3081034,-157.8521655 21.3104222,-157.852293 21.3108264,-157.8523457 21.3109812,-157.8524016 21.3111335,-157.8524584 21.3112802),(-157.8396763 21.3026507,-157.8398556 21.3026918,-157.840031 21.3027429,-157.8402064 21.3028011,-157.8423195 21.3035619,-157.8442855 21.3042316,-157.845235 21.304549),(-157.8366046 21.3022191,-157.8370683 21.3022818,-157.8391014 21.3025567,-157.839294 21.3025831,-157.8394858 21.3026148,-157.8396763 21.3026507),(-157.8214474 21.2947647,-157.8219341 21.2950513,-157.8231495 21.2957177,-157.8241704 21.2962774,-157.8243481 21.2963691,-157.8245412 21.2964598,-157.824718 21.2965341,-157.8248975 21.2966015,-157.8294163 21.2982471,-157.8296205 21.2983233,-157.8298046 21.2983959,-157.8299944 21.2984818,-157.830185 21.2985748,-157.8303701 21.2986773,-157.8305703 21.2987983,-157.8320661 21.2997696,-157.8347249 21.3015078,-157.8348971 21.3016169,-157.8350745 21.3017196,-157.8352586 21.301813,-157.8354404 21.3018959,-157.8356261 21.3019703,-157.835818 21.3020374,-157.8360031 21.302093,-157.8362049 21.3021455,-157.8364101 21.3021884,-157.8366046 21.3022191),(-157.8210282 21.2944178,-157.8212139 21.2945766,-157.8214474 21.2947647),(-157.8158031 21.2898165,-157.8176218 21.2909112,-157.8182984 21.2914191,-157.8190411 21.2921377,-157.8195581 21.2927893,-157.8201032 21.2934761,-157.8203826 21.2937817,-157.8206628 21.2940749,-157.8210282 21.2944178),(-157.8155037 21.2896406,-157.8158031 21.2898165),(-157.8142763 21.2889252,-157.8155037 21.2896406),(-157.8120803 21.287036,-157.8123453 21.287334,-157.8127132 21.2877382,-157.8129056 21.2879216,-157.8130647 21.2880665,-157.8132762 21.2882329,-157.8136278 21.288499,-157.8139399 21.288709,-157.8142763 21.2889252),(-157.8026247 21.2815767,-157.802923 21.2817301,-157.8036063 21.282084,-157.8100483 21.2853797,-157.8102541 21.2854856,-157.8104603 21.2856021,-157.8106545 21.2857223,-157.8108344 21.2858429,-157.8110107 21.2859701,-157.8111756 21.2861008,-157.8113239 21.2862273,-157.8114661 21.286356,-157.8117107 21.2866267,-157.8120803 21.287036),(-157.8023689 21.2814452,-157.8026247 21.2815767),(-157.7898518 21.2790207,-157.7905107 21.2791329,-157.7907155 21.2791607,-157.7909138 21.2791782,-157.7911077 21.2791895,-157.7913211 21.279189,-157.7915211 21.2791802,-157.7917238 21.2791595,-157.7919242 21.2791294,-157.7921181 21.2790892,-157.7923012 21.2790418,-157.7928694 21.278876,-157.793257 21.2787629,-157.793454 21.2787132,-157.7936521 21.2786687,-157.7938514 21.2786283,-157.7940472 21.2785974,-157.7942416 21.2785718,-157.7944329 21.2785522,-157.7946564 21.2785377,-157.79488 21.2785296,-157.7950901 21.2785275,-157.7953063 21.2785327,-157.7955116 21.2785435,-157.7957064 21.278559,-157.7958976 21.2785808,-157.7960954 21.2786092,-157.7962945 21.2786432,-157.7964912 21.2786832,-157.7966827 21.278728,-157.7968837 21.2787801,-157.7970828 21.2788356,-157.7972787 21.2788977,-157.7974937 21.2789766,-157.7976905 21.2790582,-157.7978878 21.2791467,-157.7980886 21.2792445,-157.7982826 21.2793437,-157.8023689 21.2814452),(-157.7835222 21.2783209,-157.7887428 21.278832,-157.7889936 21.2788595,-157.7892498 21.2789019,-157.7894749 21.2789586,-157.7898518 21.2790207),(-157.7805411 21.2780102,-157.7810196 21.27806,-157.7835222 21.2783209),(-157.7797623 21.2779215,-157.7805411 21.2780102))";

    @Test
    public void testWithoutIndex() {
        testWithIndex();
        db.command(new OCommandSQL("Drop INDEX Place.location")).execute();
        testQueryMultiLineString();
    }

    @Test
    public void testWithIndex() {
        db.command(new OCommandSQL((("insert into Place set name = 'TestInsert' , location = ST_GeomFromText('" + (LuceneSpatialMultiLineStringTest.WKT)) + "')"))).execute();
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("Place.location");
        Assert.assertEquals(2, index.getSize());
        testQueryMultiLineString();
    }
}
