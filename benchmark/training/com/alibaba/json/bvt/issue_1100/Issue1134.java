package com.alibaba.json.bvt.issue_1100;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


/**
 * Created by wenshao on 09/04/2017.
 */
public class Issue1134 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1134.Model model = new Issue1134.Model();
        model.blockpos = new Issue1134.BlockPos();
        model.blockpos.x = 526;
        model.blockpos.y = 65;
        model.blockpos.z = 554;
        model.passCode = "010";
        String text = JSON.toJSONString(model);
        TestCase.assertEquals("{\"Dimension\":0,\"PassCode\":\"010\",\"BlockPos\":{\"x\":526,\"y\":65,\"z\":554}}", text);
    }

    public static class Model {
        @JSONField(ordinal = 1, name = "Dimension")
        private int dimension;

        @JSONField(ordinal = 2, name = "PassCode")
        private String passCode;

        @JSONField(ordinal = 3, name = "BlockPos")
        private Issue1134.BlockPos blockpos;

        public int getDimension() {
            return dimension;
        }

        public void setDimension(int dimension) {
            this.dimension = dimension;
        }

        public String getPassCode() {
            return passCode;
        }

        public void setPassCode(String passCode) {
            this.passCode = passCode;
        }

        public Issue1134.BlockPos getBlockpos() {
            return blockpos;
        }

        public void setBlockpos(Issue1134.BlockPos blockpos) {
            this.blockpos = blockpos;
        }
    }

    public static class BlockPos {
        public int x;

        public int y;

        public int z;
    }
}

