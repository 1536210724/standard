package com.example.standard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

@SpringBootTest
class StandardApplicationTests {

    @Test
    void contextLoads() {

        String json = "{\"busids\":[\"4386580744454144\",\"4385995746541568\"],\"sqr\":\"xzd2\"}";

        JSONObject obj = JSON.parseObject(json);
        JSONArray busids = obj.getJSONArray("busids");
        for (Object busid : busids) {
            String phid = busid.toString();
            System.out.println(phid);
        }


    }

    String xx = "001012.02," +
            "001012.06," +
            "001012.03," +
            "001012.05," +
            "001012.09," +
            "001012.04," +
            "001012.01," +
            "001012.07," +
            "001012.08," +
            "001012.10," +
            "001012.11";

    String yy = "206.101," +
            "206.102," +
            "206.103," +
            "206.104," +
            "206.105," +
            "206.107," +
            "206.201," +
            "206.202," +
            "206.203," +
            "206.204," +
            "206.205";

    @Test
    void Test2(){
        //System.out.println(xx);
        //System.out.println(yy);
        String[] newOcode = yy.split(",");
        String[] oldOcode = xx.split(",");

        for (int i = 0;i< newOcode.length;i++) {
            System.out.println("update fg_orglist set ocode = '"+newOcode[i]+"' where ocode = '"+oldOcode[i]+"';");
            System.out.println("update fg_orgrelatitem set ocode = '"+newOcode[i]+"' where ocode = '"+oldOcode[i]+"';");
            System.out.println("update fg3_enterprise set compno ='"+newOcode[i]+"' where compno = '"+oldOcode[i]+"';");
        }
    }

}
