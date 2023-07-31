package com.example.standard;

import cn.alibaba.open.param.*;
import com.alibaba.ocean.rawsdk.ApiExecutor;
import com.ngsecdev.utils.I8ResultUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author lishixu
 * @date 2023/7/27
 * @apiNote
 */
@SpringBootTest
public class LuQiaoWith1688Test {


    private String AppKey = "3429134";

    private String AppSecret = "J7O15TGmjOQ";

    private String access_token = "e7930e7f-ec3f-4a1a-bce4-74cde440b3ef";

    @Test
    public void xx(){
        //--报价单 获取1688询价单id
        Long  getBiddingId = 568887700294L;

        //--报价单明细 获取1688报价单id,中标理由,supplierMemberId供应商id
        String candidateInfoStr = "[{\"quoteBiddingId\":1412537090294,\"comment\":\"中标了1\",\"supplierMemberId\":\"b2b-2248544159\",\"attachmentStr\":{}}]";

        //
        String lossCandidateInfoStr = null;


        ApiExecutor apiExecutor = new ApiExecutor(AppKey, AppSecret);
        CaigouApiBuyOfferBuyOfferAwardBiddingParam param = new CaigouApiBuyOfferBuyOfferAwardBiddingParam();
        ComAlibabaCaigouCoopapiBuyofferParamBuyOfferAwardBiddingParam p = new ComAlibabaCaigouCoopapiBuyofferParamBuyOfferAwardBiddingParam();

        p.setBiddingId(getBiddingId);//询价单id
        p.setCandidateInfoStr(candidateInfoStr);//中标候选人JSON格式
        p.setLossCandidateInfoStr(lossCandidateInfoStr);//未中标候选人JSON格式
        param.setParam(p);
        CaigouApiBuyOfferBuyOfferAwardBiddingResult result = apiExecutor.execute(param,access_token).getResult();

        if (result.getSuccess()) {
            Long awardBiddingId = result.getData().getAwardBiddingId();
            System.out.println(awardBiddingId);
            //95754080294
        }else{
            String message = result.getMessage();
            System.err.println(message);
        }
    }


    @Test
    public void xxx(){

        Long purchaseId = 568887700294L;

        Long awardBiddingId = 95754080294L;

        String notiContent = "嘿嘿 恭喜中标了";

        String attachmentStr = null;

        Long userId = 2216132192351L;

        ApiExecutor apiExecutor = new ApiExecutor(AppKey, AppSecret);

        CaigouApiBuyOfferPostBuyOfferNoticeParam param = new CaigouApiBuyOfferPostBuyOfferNoticeParam();
        ComAlibabaCaigouCoopapiBuyofferParamNoticePostParam p = new ComAlibabaCaigouCoopapiBuyofferParamNoticePostParam();
        p.setPurchaseId(purchaseId);
        p.setAwardBiddingId(awardBiddingId);
        p.setNoticeContent(notiContent);
        p.setAttachmentStr(attachmentStr);
        p.setUserId(userId);
        param.setParam(p);

        CaigouApiBuyOfferPostBuyOfferNoticeResult result = apiExecutor.execute(param,access_token).getResult();

        if (result.getSuccess()) {
            System.out.println("chenggong");
        }else{
            System.err.println(result.getMessage());
        }
    }

    @Test
    public void test(){
        String a = "-123";
        System.out.println(new BigDecimal(a.toString()));
    }
}
