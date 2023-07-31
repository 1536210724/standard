package com.example.standard.help;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ngsecdev.model.I8ReturnModel;
import com.ngsecdev.utils.I8ResultUtils;
import com.ngsecdev.utils.StringUtils;
import okhttp3.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * api调用的服务类
 */
public class ApiCallService {

    /**
     * 调用api测试
     * @param urlHead 请求的url到openapi的部分，如http://gw.open.1688.com/openapi/
     * @param urlPath protocol/version/namespace/name/appKey
     * @param appSecretKey 测试的app密钥，如果为空表示不需要签名
     * @param params api请求参数map。如果api需要用户授权访问，那么必须完成授权流程，params中必须包含access_token参数
     * @return json格式的调用结果
     */
    public static I8ReturnModel callApiTest(String urlHead, String urlPath, String appSecretKey, Map<String, String> params){
        final HttpClient httpClient = new HttpClient();
        final PostMethod method = new PostMethod(urlHead + urlPath);
        method.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");

        if(params != null){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                method.setParameter(entry.getKey(), entry.getValue());
            }
        }
        if(appSecretKey != null){
            method.setParameter("_aop_signature", CommonUtil.signatureWithParamsAndUrlPath(urlPath, params, appSecretKey));
        }
        String response = "";
        try{
            int status = httpClient.executeMethod(method);
            if(status >= 300 || status < 200){
                throw new RuntimeException("invoke api failed, urlPath:" + urlPath
                        + " status:" + status + " response:" + method.getResponseBodyAsString());
            }
            response = CommonUtil.parserResponse(method);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }finally{
            method.releaseConnection();
        }
        return isTrue(response);
    }
    public static I8ReturnModel callApi(String urlHead, String urlPath, String appSecretKey, Map<String, String> params,String memberId){
        final HttpClient httpClient = new HttpClient();
        final PostMethod method = new PostMethod(urlHead + urlPath);
        method.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");

        if(params != null){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                method.setParameter(entry.getKey(), entry.getValue());
            }
        }
        if(appSecretKey != null){
            method.setParameter("_aop_signature", CommonUtil.signatureWithParamsAndUrlPath(urlPath, params, appSecretKey));
        }
        String response = "";
        try{
            int status = httpClient.executeMethod(method);
            if(status >= 300 || status < 200){
                throw new RuntimeException("invoke api failed, urlPath:" + urlPath
                        + " status:" + status + " response:" + method.getResponseBodyAsString());
            }
            response = CommonUtil.parserResponse(method);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }finally{
            method.releaseConnection();
        }
        if ("param2/1/cn.alibaba.open/caigou.api.supplier.getSupplier".equals(urlPath)) {
            saveSupplier(response, memberId);
        }
        return isTrue(response);
    }

    public static I8ReturnModel isTrue(String data){
        System.err.println("调用1688接口返回："+data);
        JSONObject obj = JSON.parseObject(data);
        String success = obj.getString("success");
        if (StringUtils.isEmpty(success)) {
            return I8ResultUtils.error("接口请求失败");
        }

        if ("true".equals(success)) {

            String resData = obj.getString("data");
            if (!StringUtils.isEmpty(resData)) {
                return I8ResultUtils.success(resData);
            }

            String result = obj.getString("result");
            if (!StringUtils.isEmpty(result)) {
                JSONObject robj = JSON.parseObject(result);
                resData = robj.getString("data");
                return I8ResultUtils.success(resData);
            }

            String dataResult = obj.getString("dataResult");
            if (!StringUtils.isEmpty(dataResult)) {
                resData = dataResult;
                return I8ResultUtils.success(resData);
            }

            String id = obj.getString("id");
            if (!StringUtils.isEmpty(id)) {
                return I8ResultUtils.success(id);
            }
            return I8ResultUtils.success(resData);
        }else{
            String message = obj.getString("message");
            return I8ResultUtils.error("接口请求失败："+message);
        }
    }

    public static I8ReturnModel saveSupplier(String data,String memberId){
        JSONObject obj = JSONObject.parseObject(data);
        String supplier = obj.getString("supplier");

        if (StringUtils.isEmpty(supplier)) {
            return I8ResultUtils.error("接口请求失败");
        }

        return I8ResultUtils.success("");
    }


    public static void main(String[] args) {
        String urlHead = "https://gw.open.1688.com/openapi/";
        String urlPath = "param2/1/cn.alibaba.open/com.alibaba.caigou.BiddingInfoWriteService.createBidding/7611870";
//        String urlPath = "param/1/cn.alibaba.open/cn.alibaba.open:formalbidding.appliedSupplier.getInfo-1/7611870";
        Map<String,String> params = new HashMap<>();
        params.put("access_token","2d800d83-0e05-4f95-b623-88e109883057");
        params.put("param","{\n" +
                "    \"number\":\"XTJT/JSCLZB/2023/NO.0003\",\n" +
                "    \"subject\":\"伊利测试项目-1688数据互通测试\",\n" +
                "    \"biddingMethod\":\"open\",\n" +
                "    \"biddingType\":\"material\",\n" +
                "    \"I8_phid_tenderproject\":\"伊利测试项目-1688数据互通测试\",\n" +
                "    \"biddingOrgName\":\"兴泰建设集团有限公司\",\n" +
                "    \"I8_tender_bond\":0,\n" +
                "    \"gmtApplyStart\":1687213013000,\n" +
                "    \"gmtApplyExpire\":1687385818000,\n" +
                "    \"gmtOpenStart\":1687558622000,\n" +
                "    \"I8_downfile_start_dt\":1687213026000,\n" +
                "    \"I8_downfile_end_dt\":1687295835000,\n" +
                "    \"gmtQuotationExpire\":1687555042000,\n" +
                "    \"I8_phid_pc\":\"伊利测试项目-张权威\",\n" +
                "    \"I8_cons_site\":\"呼市\",\n" +
                "    \"I8_proj_summary\":\"111111111\",\n" +
                "    \"I8_phid_kb_type\":\"1688平台\",\n" +
                "    \"biddingContact\":\"李飞  电话:18647732696     张鹏  电话:1425367894     \",\n" +
                "    \"I8_need_signup\":\"1\",\n" +
                "    \"I8_tender_con_price\":0,\n" +
                "    \"I8_phid_quote_type\":\"固定综合单价\",\n" +
                "    \"I8_phid_money_source\":\"企业自筹\",\n" +
                "    \"I8_js_type\":\"救死扶伤\",\n" +
                "    \"I8_tender_file_explain\":\"投标单位必须将电子投标文件和excel清单（用扫描仪或手机版全能扫描王进行扫描）逐页签字并加盖经工商部门备案的公章扫描成PDF版本一 份，请勿设置密码，发送到1688平台，添加钉钉好友。\",\n" +
                "    \"I8_phid_contract_type\":\"材料采购\",\n" +
                "    \"invoiceType\":\"增值税\",\n" +
                "    \"I8_tax_rate\":\"13.00%\",\n" +
                "    \"I8_tec_standard_claim\":\"东方故事\",\n" +
                "    \"I8_prc_include_content\":\"在v最新版v\",\n" +
                "    \"I8_plan_continue_dt\":1687622400000,\n" +
                "    \"I8_plan_end_dt\":1688054400000,\n" +
                "    \"I8_phid_contract_pay\":\"无预付款,中,入职,体制改革,合同到期,正式录用审批中,技术改造工程,正式录用已审批,费用单,中,不满意,市场营销活动计划,电话关怀,服务任务,货到现场验收合格办理挂账手续待增值税发票认证后，支付当月结算货款的97%，剩余货款根据项目施工进度及回款分批支付,管理,一级,计价方法,项目评审工件,培训情况,成人高考,销售,外部运输合同,丢失,一次付清,内部员工,提价单,政府部门,汇票,其它,高,培训活动计划,护照,本科\",\n" +
                "    \"I8_tender_quali_claim\":\"1）投标人必须是经工商、税务登记注册，并符合投标项目经营范围，能独立承担民事责任的法人组织；2）投标人需提供所投产品制造厂商对本项目的专项授权书；3）投标人需提供产品国家级检测报告；\",\n" +
                "    \"I8_ach_standard_claim\":\"现成发过的\",\n" +
                "    \"I8_user_jsjzy\":\"财务办理完结算挂账手续之日起24个月截止\",\n" +
                "    \"I8_phid_warrant_gold\":\"合同最终结算金额的3%扣除\",\n" +
                "    \"I8_user_pbff\":\"综合评分法\",\n" +
                "    \"I8_user_zgscfs\":\"资格预审\",\n" +
                "    \"I8_user_ysfs\":\"汽车运输\",\n" +
                "    \"biddingAreaCode\":\"2434,2469,2473\",\n" +
                "    \"biddingAreaName\":\"内蒙古-鄂尔多斯-伊金霍洛旗\",\n" +
                "    \"subBizType\":\"singlepurchase\",\n" +
                "    \"receiveAddressCode\":\"\",\n" +
                "    \"receiveDisplayAddress\":\"\",\n" +
                "    \"temporaryAddressCode\":\"2434,2469,2473-CBD\",\n" +
                "    \"temporaryDisplayAddress\":\"内蒙古-鄂尔多斯-伊金霍洛旗-CBD\",\n" +
                "    \"biddingAttachmentStr\":[\n" +
                "        null\n" +
                "    ],\n" +
                "    \"openBiddingMethod\":\"one\",\n" +
                "    \"gmtQuotationStart\":\"\",\n" +
                "    \"gmtOpenEnd\":\"\",\n" +
                "    \"gmtEvaluateStart\":\"\",\n" +
                "    \"gmtEvaluateEnd\":\"\",\n" +
                "    \"tradeMode\":\"{\\\"alipay\\\":false,\\\"payperiod\\\":true,\\\"payperiodType\\\":\\\" specify\\\",\\\"payperiodDays\\\":\\\"6\\\",\\\"steppay\\\":false,\\\"steppayType\\\":\\\"\\\",\\\"steppayDays\\\":\\\"\\\",\\\"other\\\":false,\\\"otherDesc\\\":\\\"\\\"}\",\n" +
                "    \"asbiddingAnnounce\":\"\",\n" +
                "    \"publishChannel\":\"1688portal\",\n" +
                "    \"entrySource\":\"outside\",\n" +
                "    \"publishChange\":\"false\",\n" +
                "    \"modify\":\"false\",\n" +
                "    \"buyerUserId\":\"53244589\",\n" +
                "    \"useTempAsReceive\":\"true\",\n" +
                "    \"sectionList\":[\n" +
                "        {\n" +
                "            \"purchaseItemWriteParamStr\":[\n" +
                "                {\n" +
                "                    \"I8_list_code\":\"030101003002\",\n" +
                "                    \"subject\":\"低温热水地板辐射供暖系统\",\n" +
                "                    \"productFeature\":\"管道安装、配件安装（阀门）、分集水器安装、钢丝网、铝箔保护层铺设、苯板条安装、系统冲洗、消毒，水压试验，调试运行、管道及设备标识等。\",\n" +
                "                    \"unit\":\"平方米\",\n" +
                "                    \"purchaseAmount\":10,\n" +
                "                    \"I8_price_content\":\"人工、机具机械、辅材、安全文明费、管理费、利润和税金等\",\n" +
                "                    \"I8_measure_yz\":\"依据图纸按地暖面积据实计量\",\n" +
                "                    \"basePrice\":0,\n" +
                "                    \"I8_ctl_tax_amt\":0,\n" +
                "                    \"I8_ctl_tax_rate\":\"13.00%\",\n" +
                "                    \"name\":\"one\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"I8_list_code\":\"030101003001\",\n" +
                "                    \"subject\":\"散热器采暖系统\",\n" +
                "                    \"productFeature\":\"预制加工、管道安装、配件安装（阀门）、散热器安装、采暖设备安装、水压试验、系统冲洗、消毒、调试运行、管道及设备标识等。\",\n" +
                "                    \"unit\":\"平方米\",\n" +
                "                    \"purchaseAmount\":10,\n" +
                "                    \"I8_price_content\":\"人工、机具机械、辅材、安全文明费、管理费、利润和税金等\",\n" +
                "                    \"I8_measure_yz\":\"依据图纸按建筑面积据实计量，扣除（玻璃雨棚、钢结构车库坡道、外墙保温、伸缩缝、变形缝、沉降缝、无安装内容的通道及核心筒、室外建筑楼梯、采光井、看台等面积）\",\n" +
                "                    \"basePrice\":0,\n" +
                "                    \"I8_ctl_tax_amt\":0,\n" +
                "                    \"I8_ctl_tax_rate\":\"13.00%\",\n" +
                "                    \"name\":\"one\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"name\":\"标段一\",\n" +
                "            \"index\":\"1\"\n" +
                "        }\n" +
                "    ]\n" +
                "}");
//        params.put("param","123");
        String appSecretKey = "vWtU6kKtAyt";
        try{
//            System.out.println(params);
            I8ReturnModel i8ReturnModel = callApiTest(urlHead, urlPath, appSecretKey, params);
            System.out.println(i8ReturnModel);
        }catch (Exception e){
            System.out.println(e);
        }

    }

    public static void pushFile(String urlHead, String urlPath,Map<String,String> params,String appSecretKey, byte[] fileBytes, String accesstoken) throws IOException {
        String result = "";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("name",params.get("name"))
                .build();
        Request request = new Request.Builder()
                .url(urlHead+urlPath)
                .method("POST", body)
                .addHeader("access_token", accesstoken)
                .addHeader("_aop_signature", CommonUtil.signatureWithParamsAndUrlPath(urlPath, params, appSecretKey))
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            result = response.body().string();
        }
    }
}
