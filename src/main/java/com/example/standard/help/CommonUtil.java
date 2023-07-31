package com.example.standard.help;

import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 通用工具类，包括了签名工具、url拼装以及httpResponse的解析
 */
public final class CommonUtil {

    /**
     * 解析http请求的response
     * @param method
     * @return 请求结果
     * @throws IOException
     */
    public static String parserResponse(PostMethod method) throws IOException{
        StringBuffer contentBuffer = new StringBuffer();
        InputStream in = method.getResponseBodyAsStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, method.getResponseCharSet()));
        String inputLine = null;
        while((inputLine = reader.readLine()) != null)
        {
            contentBuffer.append(inputLine);
            contentBuffer.append("/n");
        }
        //去掉结尾的换行符
        contentBuffer.delete(contentBuffer.length() - 2, contentBuffer.length());
        in.close();
        return contentBuffer.toString();
    }

    /**
     * 将urlPath和请求参数同时作为签名因子进行签名
     * @param urlPath protocol/version/namespace/name/appKey
     * @param params api请求的各参数键值对
     * @param appSecretKey app签名密钥
     * @return
     */
    public static String signatureWithParamsAndUrlPath(String urlPath, Map<String, String> params, String appSecretKey){
        List<String> paramValueList = new ArrayList<String>();
        if(params != null){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramValueList.add(entry.getKey() + entry.getValue());
            }
        }
        final String[] datas = new String[1 + paramValueList.size()];
        datas[0] = urlPath;
        Collections.sort(paramValueList);
        for (int i = 0; i < paramValueList.size(); i++) {
            datas[i+1] = paramValueList.get(i);
        }
        byte[] signature = SecurityUtil.hmacSha1(datas, StringUtil.toBytes(appSecretKey));
        return StringUtil.encodeHexStr(signature);
    }

    /**
     *
     * 仅将请求参数作为签名因子进行签名
     * @param params api请求的各参数键值对
     * @param appSecretKey
     * @return
     */
    public static String signatureWithParamsOnly(Map<String, String> params, String appSecretKey){
        List<String> paramValueList = new ArrayList<String>();
        if(params != null){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramValueList.add(entry.getKey() + entry.getValue());
            }
        }
        Collections.sort(paramValueList);
        String[] datas = new String[paramValueList.size()];
        paramValueList.toArray(datas);
        byte[] signature = SecurityUtil.hmacSha1(datas, StringUtil.toBytes(appSecretKey));
        return StringUtil.encodeHexStr(signature);
    }

    /**
     * 生成api签名的urlPath，即protocol/version/namespace/name/appKey
     * @param apiNamespace
     * @param apiName
     * @param apiVersion
     * @param protocol
     * @param appKey
     * @return
     */
    public static String buildInvokeUrlPath(String apiNamespace, String apiName, int apiVersion, String protocol, String appKey) {
        String url = protocol + "/" + apiVersion + "/" + apiNamespace + "/" + apiName + "/" + appKey;
        return url;
    }

    /**
     * 获取完整的url
     * @param url 请求uri
     * @param params 请求参数
     * @return
     */
    public static String getWholeUrl(String url, Map<String, String> params){
        if(url == null){
            return null;
        }
        if(params == null){
            return url;
        }
        Set<Map.Entry<String, String>> set = params.entrySet();
        if(set.size() <= 0){
            return url;
        }
        url += "?";
        Iterator<Map.Entry<String, String>> it = set.iterator();
        if(it.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            String param = entry.getKey() + "=" + entry.getValue();
            url += param;
        }
        while(it.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            String param = entry.getKey() + "=" + entry.getValue();
            url += "&" + param;
        }
        return url;
    }

    public static void main(String[] args) {
        String url = "param/1/cn.alibaba.open/com.alibaba.caigou.BiddingInfoWriteService.createBidding/7611870";
        Map<String,String> params = new HashMap<>();
        params.put("access_token","2d800d83-0e05-4f95-b623-88e109883057");
        params.put("param","{\n" +
                "  \"subject\": \"李伟0418-51个标包测试001\",\n" +
                "  \"number\": \"CGRW-2022-002399\",\n" +
                "  \"biddingMethod\": \"open\",\n" +
                "  \"biddingAreaCode\": \"1098,1099,4476\",\n" +
                "  \"biddingAreaName\": \"北京-北京-北京市朝阳区\",\n" +
                "  \"biddingType\": \"enginerring\",\n" +
                "  \"purchaseBudget\": null,\n" +
                "  \"subBizType\": \"singlepurchase\",\n" +
                "  \"receiveBeginDate\": null,\n" +
                "  \"receiveEndDate\": null,\n" +
                "  \"receiveAddressCode\": \"3478,3479,4326,良渚街道这是一个测试\",\n" +
                "  \"receiveDisplayAddress\": \"浙江省-杭州市-余杭区-良渚街道这是一个测试\",\n" +
                "  \"temporaryAddressCode\": \"3478,3486,3487,鄞州区\",\n" +
                "  \"temporaryDisplayAddress\": \"浙江-宁波-宁波市-鄞州区\",\n" +
                "  \"useTempAsReceive\": null,\n" +
                "  \"biddingOrgName\": \"成本中心1111\",\n" +
                "  \"biddingOrgCode\": \"cbzx\",\n" +
                "  \"biddingPlanCode\": null,\n" +
                "  \"biddingPlanDesc\": null,\n" +
                "  \"sectionList\": \"[{\\\"purchaseItemWriteParamStr\\\":[{\\\"unit\\\":\\\"个\\\",\\\"category\\\":\\\"67,10336,127822004\\\",\\\"subject\\\":\\\"你好小z啊1244\\\",\\\"purchaseAmount\\\":\\\"10\\\"}],\\\"name\\\":\\\"标段一\\\",\\\"index\\\":\\\"1\\\"}]\",\n" +
                "  \"biddingAttachmentStr\": [\n" +
                "    1111771248301\n" +
                "  ],\n" +
                "  \"techAttachmentStr\": null,\n" +
                "  \"bizAttachmentStr\": null,\n" +
                "  \"evaluateAttachmentStr\": null,\n" +
                "  \"openBiddingMethod\": \"one\",\n" +
                "  \"gmtApplyStart\": \"2022-11-13 00:00:00\",\n" +
                "  \"gmtApplyExpire\": \"2022-11-14 00:00:00\",\n" +
                "  \"gmtQuotationStart\": \"2022-11-15 00:00:00\",\n" +
                "  \"gmtQuotationExpire\": \"2022-11-16 00:00:00\",\n" +
                "  \"gmtOpenStart\": \"2022-11-17 00:00:00\",\n" +
                "  \"gmtOpenEnd\": \"2022-11-18 00:00:00\",\n" +
                "  \"gmtEvaluateStart\": \"2022-11-19 00:00:00\",\n" +
                "  \"gmtEvaluateEnd\": \"2022-11-20 00:00:00\",\n" +
                "  \"includeTax\": null,\n" +
                "  \"quoteHasPostFee\": null,\n" +
                "  \"allowPartOffer\": null,\n" +
                "  \"supplierCanModifyQuantity\": null,\n" +
                "  \"tradeMode\": \"{\\\"alipay\\\":false,\\\"payperiod\\\":false,\\\"payperiodType\\\":\\\"notspecify\\\",\\\"payperiodDays\\\":\\\"60\\\",\\\"steppay\\\":true,\\\"steppayType\\\":\\\"stepTrade91BusinessBuy\\\",\\\"steppayDays\\\":\\\"180\\\",\\\"other\\\":false,\\\"otherDesc\\\":\\\"\\\"}\",\n" +
                "  \"payType\": \"{\\\"aliPay\\\":true,\\\"aliPayEnterprise\\\":false,\\\"bankTransfer\\\":false,\\\"yqf\\\":true,\\\"cmbInvoice\\\":false,\\\"offlinePay\\\":true,\\\"creditPeriod\\\":\\\"\\\"}\",\n" +
                "  \"invoiceType\": \"common\",\n" +
                "  \"registeredCapital\": null,\n" +
                "  \"needSignAgreement\": null,\n" +
                "  \"businessModels\": null,\n" +
                "  \"certificateIds\": null,\n" +
                "  \"biddingAnnounce\": \"<p>11111111111111111111111111</p>\",\n" +
                "  \"addedAttachmentStr\": null,\n" +
                "  \"internalAttachmentStr\": null,\n" +
                "  \"publishChannel\": \"1688portal\",\n" +
                "  \"entrySource\": \"outside\",\n" +
                "  \"publishChange\": \"false\",\n" +
                "  \"inviteSupplierMemberIds\": null,\n" +
                "  \"modify\": \"false\",\n" +
                "  \"protocolBeginDate\": null,\n" +
                "  \"protocolEndDate\": null,\n" +
                "  \"buyerUserId\": 2813610054,\n" +
                "  \"acceptConsortiumBids\": null,\n" +
                "  \"industryCategoryCode\": null,\n" +
                "  \"biddingContact\": \"张明\",\n" +
                "  \"biddingContactPhone\": \"13701001002\",\n" +
                "  \"biddingContactEmail\": null,\n" +
                "  \"guaranteeAmountStr\": null,\n" +
                "  \"guaranteeAccounts\": null,\n" +
                "  \"guarantee\": null\n" +
                "}");
        String appSecretKey = "vWtU6kKtAyt";
        String s = signatureWithParamsAndUrlPath(url, params, appSecretKey);
        System.out.println(s);
    }
}
