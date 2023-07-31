package com.example.standard.service.impl;

import cn.alibaba.open.param.CaigouApiAttachmentUploadParam;
import cn.alibaba.open.param.CaigouApiAttachmentUploadResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.ocean.rawsdk.ApiExecutor;
import com.example.standard.help.ApiCallService;
import com.example.standard.service.AttachService;
import com.example.standard.service.TenderService;
import com.example.standard.utils.I8Request;
import com.example.standard.utils.TimeUtils;
import com.ngsecdev.model.I8ReturnModel;
import com.ngsecdev.utils.I8ResultUtils;
import com.ngsecdev.utils.StringUtils;
import javafx.css.Styleable;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lishixu
 * @date 2023/3/21
 * @apiNote
 */
@Service
public class TenderImpl implements TenderService {

    @Value("${1688.url}")
    private String urlHead;

    @Value("${1688.AppKey}")
    private String AppKey;

    @Value("${1688.AppSecret}")
    private String AppSecret;

    @Value("${1688.access_token}")
    private String access_token;

    @Autowired
    private I8Request i8Request;

    @Autowired
    private AttachService attachService;


    @Override
    public I8ReturnModel pushTenderTest(String json) throws IOException {
        String urlPath = "param2/1/cn.alibaba.open/com.alibaba.caigou.BiddingInfoWriteService.createBidding/"+AppKey;
        Map<String,String> params = new HashMap<>();
        params.put("access_token",access_token);
        params.put("param",json);
        System.out.println(params);
        return ApiCallService.callApiTest(urlHead, urlPath, AppSecret, params);
    }

    @Override
    public String pushFile(String asr_code, String asr_filename, String asr_table, String asr_fileTable) throws IOException {
        byte[] fileWithByte = attachService.getFileWithByte(asr_code, asr_table, asr_fileTable, asr_filename);
        String urlPath = "param2/1/cn.alibaba.open/caigou.api.attachment.upload/"+AppKey;
        Map<String,String> params = new HashMap<>();
        params.put("access_token",access_token);
        params.put("name",asr_filename);
        params.put("fileBytes",Arrays.toString(fileWithByte));
        ApiExecutor apiExecutor = new ApiExecutor(AppKey, AppSecret);
        CaigouApiAttachmentUploadParam param = new CaigouApiAttachmentUploadParam();
        param.setName(asr_filename);
        param.setFileBytes(fileWithByte);
        CaigouApiAttachmentUploadResult result = apiExecutor.execute(param, access_token).getResult();
        //I8ReturnModel i8ReturnModel = ApiCallService.callApiTest(urlHead, urlPath, AppSecret, params);
        if (result.getSuccess())
        {
            Long id = result.getId();
            return String.valueOf(id);
        }
        return null;
    }

    public I8ReturnModel queryAppliedSupplier(String biddingId){
        String urlPath = "param2/1/cn.alibaba.open/formalbidding.appliedSupplier.getInfo/"+AppKey;
        Map<String,String> params = new HashMap<>();
        params.put("access_token",access_token);
        Map<String,Object> zbtz = new HashMap<>();
        zbtz.put("biddingId",biddingId);
        params.put("param",JSONObject.toJSONString(zbtz));
        System.out.println(params);
        I8ReturnModel i8ReturnModel = ApiCallService.callApiTest(urlHead, urlPath, AppSecret, params);
        if (i8ReturnModel.getIsOk()) {
            // TODO 获取数据
            String data = i8ReturnModel.getMessage().toString();
            JSONArray arr = JSONArray.parseArray(data);
            for(Iterator iterator = arr.iterator();iterator.hasNext();){
                JSONObject obj = (JSONObject) iterator.next();
                String supplierMemberId = obj.getString("supplierMemberId");//供应商memberid
                String isQuote = obj.getString("isQuote");//是否报价 true：已报价，false：未报价
                String applyId = obj.getString("applyId");//报名单id
                String supplierName = obj.getString("supplierName");//供应商名称
                String status = obj.getString("status");//报名状态 pass：审核通过，refuse：审核不通过，pending：审核中

                //查询投标报名
                queryApplyBiddingDetail(supplierMemberId,applyId,biddingId);
                //查询投标报名详情
                supplierBiddingDetail(supplierMemberId,applyId,biddingId);
            }
        }

        return i8ReturnModel;
    }

    /**
     * 查询招标单报名详情
     * 1.根据供应商报名中供应商id、报名表ID、招标单id获取某个供应商具体报名信息，保存在报名详情
     * https://open.1688.com/api/apidocdetail.htm?id=cn.alibaba.open:com.alibaba.caigou.BiddingInfoReadService.queryApplyBiddingDetail-1
     * */
    public I8ReturnModel queryApplyBiddingDetail(String supplierMemberId,String applyId,String purchaseId){
        String urlPath = "param2/1/cn.alibaba.open/com.alibaba.caigou.BiddingInfoReadService.queryApplyBiddingDetail/"+AppKey;
        Map<String,String> params = new HashMap<>();
        params.put("access_token",access_token);
        Map<String,Object> zbtz = new HashMap<>();
        zbtz.put("supplierMemberId",supplierMemberId);//供应商memberId（供应商报名消息data中的supplierMemberId）
        zbtz.put("applyId",applyId);//报表单id（供应商报名消息data中的id）
        zbtz.put("purchaseId",purchaseId);//招标单id（供应商报名消息data中的bizId）
        params.put("param",JSONObject.toJSONString(zbtz));
        System.out.println(params);
        I8ReturnModel i8ReturnModel = ApiCallService.callApiTest(urlHead, urlPath, AppSecret, params);
        return i8ReturnModel;
    }

    /**
     * 查询招标单报名详情
     * 3.根据供应商报名中供应商id、报名表ID、招标单id获取某个供应商具体报名信息，保存在报名文件
     * https://open.1688.com/api/apidocdetail.htm?id=cn.alibaba.open:com.alibaba.caigou.BiddingInfoReadService.supplierBiddingDetail-1
     * */
    public I8ReturnModel supplierBiddingDetail(String supplierMemberId,String applyId,String purchaseId){
        String urlPath = "param2/1/cn.alibaba.open/com.alibaba.caigou.BiddingInfoReadService.supplierBiddingDetail/"+AppKey;
        Map<String,String> params = new HashMap<>();
        params.put("access_token",access_token);
        Map<String,Object> zbtz = new HashMap<>();
        zbtz.put("supplierMemberId",supplierMemberId);//供应商memberId（供应商报名消息data中的supplierMemberId）
        zbtz.put("applyId",applyId);//报表单id（供应商报名消息data中的id）
        zbtz.put("purchaseId",purchaseId);//招标单id（供应商报名消息data中的bizId）
        params.put("param",JSONObject.toJSONString(zbtz));
        System.out.println(params);
        I8ReturnModel i8ReturnModel = ApiCallService.callApiTest(urlHead, urlPath, AppSecret, params);
        return i8ReturnModel;
    }

    /**
     * 查询招标单所有专家评分和意见
     * 1.根据招标单id获取，并保存在评标任务管理中
     * */
    public void expertEvaluationScore(String biddingId){
        String urlPath = "param2/1/cn.alibaba.open/formalbidding.expertEvaluationScore.getInfo/"+AppKey;
        Map<String,String> params = new HashMap<>();
        params.put("access_token",access_token);
        Map<String,Object> zbtz = new HashMap<>();
        zbtz.put("biddingId",biddingId);
        params.put("params",JSONObject.toJSONString(zbtz));
        I8ReturnModel i8ReturnModel = ApiCallService.callApiTest(urlHead, urlPath, AppSecret, params);
        String data = i8ReturnModel.getData().toString();
    }

    @Override
    public I8ReturnModel saveTenderFile(String data, Long phid) throws Exception {
        JSONObject dataObj = JSON.parseObject(data);

        // 查询表头
        // PMS/PCM/CntTenderBulletin/GetCntTenderBulletinInfo
        // id: 806221129472002
        // tabtype: cnttenderbulletin
        // ng3_logid: 271200106000001
        List<NameValuePair> urlParameters = new ArrayList<>();

        urlParameters.add(new BasicNameValuePair("id", phid.toString()));
        urlParameters.add(new BasicNameValuePair("tabtype", "cnttenderbulletin"));
        urlParameters.add(new BasicNameValuePair("ng3_logid", ""));
        String formSync = i8Request.PostFormSync("/PMS/PCM/CntTenderBulletin/GetCntTenderBulletinInfo", urlParameters);
        System.out.println(formSync);

        JSONObject object = JSON.parseObject(formSync);
        String status = object.getString("Status");
        if (!"success".equals(status)) {
            return I8ResultUtils.error(object.getString("Msg"));
        }
        urlParameters.clear();
        String header = object.getString("Data");
        JSONObject headerObj = JSON.parseObject(header);


        // 查询表体
        // PMS/PCM/CntTenderBulletin/GetCntTenderBulletinInfo
        // id: 806221129472002
        // tabtype: cnttenderbulletind
        // ng3_logid: 271200106000001
        urlParameters.add(new BasicNameValuePair("id", phid.toString()));
        urlParameters.add(new BasicNameValuePair("tabtype", "cnttenderbulletind"));
        urlParameters.add(new BasicNameValuePair("ng3_logid", ""));
        formSync = i8Request.PostFormSync("/PMS/PCM/CntTenderBulletin/GetCntTenderBulletinInfo", urlParameters);
        System.out.println(formSync);
        urlParameters.clear();

        object = JSON.parseObject(formSync);
        String Record = object.getString("Record");
        if (StringUtils.isEmpty(Record)) {
            return I8ResultUtils.error("获取明细数据失败");
        }

        String body = Record;

        // 保存投标文件
        // PMS/PCM/CntTenderFile/Save
        // isContinue = false
        // attchmentGuid = 0

        //封装头部数据
        HashMap<String, Object> mapInfo = new HashMap<String, Object>();
        mapInfo.put("BillTitle", headerObj.getString("BulletinTitle"));
        mapInfo.put("SubmitDt", TimeUtils.getDateStr(dataObj.getString("quotationExpire")));
        mapInfo.put("PhidTenderproject", headerObj.getString("PhidTenderProject"));

        // TODO 查询 supplierMemberId 对应的 供应商
        mapInfo.put("PhidTendercompany", "306200221000001");

        // TODO 价格根据明细合计出金额
        mapInfo.put("TenderPrice", "");

        mapInfo.put("PhidPc", headerObj.getString("PhidPc"));
        mapInfo.put("Extendstr1", dataObj.getString("contact"));
        mapInfo.put("Phone", dataObj.getString("mobile"));
        mapInfo.put("PhidTenderType", headerObj.getString("PhidTenderType"));
        mapInfo.put("Remarks", "");
        mapInfo.put("PhidOcode", headerObj.getString("PhidOcode"));


        //封装明细数据
        JSONArray bodyArr = JSON.parseArray(body);

        String productList = dataObj.getString("productList");
        JSONObject product = (JSONObject) JSONArray.parseArray(productList).get(0);
        String itemList = product.getString("itemList");
        JSONArray itemArr = JSON.parseArray(itemList);

        for (Iterator iterator = itemArr.iterator();iterator.hasNext();){

            JSONObject itemObj = (JSONObject) iterator.next();

            // TODO 当前字段没有，需要新加
            String itemno = itemObj.getString("itemno");

            //过滤出编码相同的数据
            JSONObject bodyObj = (JSONObject) bodyArr.stream().filter(t -> ((JSONObject) t)
                            .getString("ItemDataNo").equals(itemno))
                    .collect(Collectors.toList());

            // TODO 封装明细数据

        }



        return I8ResultUtils.success();
    }

    @Override
    public I8ReturnModel getSupplier(String memberId){
        String urlPath = "param2/1/cn.alibaba.open/caigou.api.supplier.getSupplier/"+AppKey;
        Map<String,String> params = new HashMap<>();
        params.put("access_token",access_token);
        params.put("memberId",memberId);
        System.out.println(params);
        I8ReturnModel i8ReturnModel = ApiCallService.callApiTest(urlHead, urlPath, AppSecret, params);
        return i8ReturnModel;
    }


}
