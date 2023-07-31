package com.example.standard.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ngsecdev.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模拟 i8 前端参数，封装并请求推送数据到后端接口
 */
@Slf4j
@Service
public class I8Request implements InitializingBean {//继承InitializingBean ，解决构造函数中使用注入对象报空指针的问题

    static String ngtoken = "";
    @Autowired
    private CloseableHttpClient httpClient;
    @Autowired
    private RequestConfig requestConfig;

    @Value("${i8.url}")
    private String i8url;

    @Value("${i8.user}")
    private String i8user;

    @Value("${i8.database}")
    private String i8database;

    @Value("${i8.ocode}")
    private String ocode;

    @Value("${i8.dblink}")
    private String asr_dbconn;

    //缓冲时间 23小时  //1天
    private final static long EXPIRATIONTIME = 1000 * 60 * 60 * 23;
    public static Map<String, String> keySecretMap = new HashMap();
    public static Map<String, Long> keyTimeMap = new HashMap();

    /*
     * 是否过期
     */
    private boolean isInvalid(String timeKey) {
        //如果时间key为null或者map里没这个key就查数据库
        if (StringUtils.isEmpty(timeKey) || !keyTimeMap.containsKey(timeKey))
            return false;
        Long expiryTime = keyTimeMap.get(timeKey);
        //如果当前时间大于缓存过期时间就移除map里的数据key
        if (System.currentTimeMillis() > expiryTime.longValue()) {
            keySecretMap.remove(ocode);
            keyTimeMap.remove(timeKey);
            return false;
        }
        return true;
    }


    public I8Request() throws IOException {
    }

    private void console(String str) {
        System.out.println(str);
    }

    public String PostFormSync(String url, List<NameValuePair> formdata) throws Exception {
        String result = "";
        //先登录获取token
        String ngtoken = GetAccesstoken();
        if (StringUtils.isEmpty(ngtoken)) {
            return result;
        }
        String postUrl = i8url + url;
        HttpPost httpPost = new HttpPost(postUrl);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("authorization", ngtoken);
        /*
        if(!StringUtils.isEmpty(contentType)){
            httpPost.addHeader("Content-Type",contentType);
        }*/
        HttpEntity entity = new UrlEncodedFormEntity(formdata, "utf-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        HttpEntity httpEntity = null;
        console("业务请求：" + JSON.toJSONString(formdata));
        try {
            response = httpClient.execute(httpPost);
            int error = 200;
            // 获取响应状态
            int statusCode = response.getStatusLine().getStatusCode();
            error = statusCode;
            // 获取响应体
            httpEntity = response.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity, "utf-8");
                //console("返回："+result);
            }
            // 没有正常响应
            if (statusCode < HttpStatus.SC_OK || statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                throw new RuntimeException("statusCode:" + statusCode);
            }
        } catch (Exception e) {
            //console("业务请求报错："+e.getMessage());
            httpPost.abort();
            return result;
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    /**
     * 上传i8文件
     *
     * @param asr_code
     * @param asr_table
     * @param asr_attach_table
     * @param asr_params
     * @param asr_data
     * @return
     */
    public boolean UploadFile(String asr_code, String asr_table,
                              String asr_attach_table,
                              String asr_params, String asr_data) {
        boolean flag = false;
        try {
            String i8UploadUrl = i8url + "/filesrv/UploadFileService.asmx?wsdl";
            String paramXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <tem:SaveData>\n" +
                    "         <!--Optional:-->\n" +
                    "         <tem:asr_guid></tem:asr_guid>\n" +
                    "         <!--Optional:-->\n" +
                    "         <tem:asr_code>" + asr_code + "</tem:asr_code>\n" +
                    "         <!--Optional:-->\n" +
                    "         <tem:asr_table>" + asr_table + "</tem:asr_table>\n" +
                    "         <!--Optional:-->\n" +
                    "         <tem:asr_attach_table>" + asr_attach_table + "</tem:asr_attach_table>\n" +
                    "         <!--Optional:-->\n" +
                    "         <tem:asr_dbconn>" + asr_dbconn + "</tem:asr_dbconn>\n" +
                    "         <!--Optional:-->\n" +
                    "         <tem:asr_params>" + StringEscapeUtils.escapeXml10(asr_params) + "</tem:asr_params>\n" +
                    "         <!--Optional:-->\n" +
                    "         <tem:asr_data>" + asr_data + "</tem:asr_data>\n" +
                    "         <!--Optional:-->\n" +
                    "         <tem:approved></tem:approved>\n" +
                    "         <!--Optional:-->\n" +
                    "         <tem:containerid></tem:containerid>\n" +
                    "      </tem:SaveData>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

            String xmlValue = doPostSoap1_1(i8UploadUrl, paramXml);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(xmlValue)) {
                SAXReader reader = new SAXReader();
                Document document = reader.read(new ByteArrayInputStream(xmlValue.getBytes("UTF-8")));
                Node bodyNode = document.selectSingleNode("//s:Envelope//s:Body");
                if (bodyNode != null) {
                    Element elResponse = ((Element) bodyNode).element("SaveDataResponse");
                    if (elResponse != null) {
                        Element elResult = elResponse.element("SaveDataResult");
                        if (elResult != null) {
                            String resultJson = elResult.getText();
                            if (org.apache.commons.lang3.StringUtils.isNotBlank(resultJson)) {
                                if ("1".equals(resultJson)) {
                                    flag = true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return flag;
    }

    public String doPostSoap1_1(String url, String xml) {
        String xmlResult = "";

        try {
            HttpClientBuilder builder = HttpClientBuilder.create();
            CloseableHttpClient client = builder.build();
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom()
                    .setSocketTimeout(30000)
                    .setConnectTimeout(30000)
                    .build();
            httpPost.setConfig(config);

            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            StringEntity data = new StringEntity(xml, Charset.forName("UTF-8"));
            httpPost.setEntity(data);
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity result = response.getEntity();
            if (result != null) {
                xmlResult = EntityUtils.toString(result, "UTF-8");

            }
            client.close();
        } catch (Exception e) {

        }

        return xmlResult;
    }


    /**
     * 获取token接口
     *
     * @return
     * @throws Exception
     */
    public String GetAccesstoken() throws Exception {

        //校验时间戳是否超时
        if (isInvalid(keySecretMap.get(ocode))) {
            return keySecretMap.get(ocode);
        }
        String accesstoken = "";
        String result = "";
        // 获取响应体
        HttpEntity httpEntity = null;
        String url = i8url + "/api/KernelSession";//需要密码验证和员工账号对应的用KernelSessionNew
        CloseableHttpResponse response = null;
        String tokenUrl = url + "?ucode=NG" + i8database + "&ocode=" + ocode + "&loginid=" + i8user;
        HttpGet httpGet = new HttpGet(tokenUrl);
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpGet);
            // 获取响应状态
            int statusCode = response.getStatusLine().getStatusCode();
            // 获取响应体
            httpEntity = response.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
                JSONObject tokenJO = JSON.parseObject(result);
                if (tokenJO != null && tokenJO.getString("status").toLowerCase().equals("success")) {
                    accesstoken = tokenJO.getString("accesstoken");
                    keySecretMap.put(ocode, accesstoken);
                    keyTimeMap.put(accesstoken, System.currentTimeMillis() + EXPIRATIONTIME);
                    console(tokenUrl + "获取token:" + accesstoken);
                }
            }
            // 没有正常响应
            if (statusCode < HttpStatus.SC_OK || statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                throw new RuntimeException("statusCode : " + statusCode);
            }

        } catch (Exception e) {
            httpGet.abort();
            result = e.getMessage();
            console("获取token接口异常:" + result);
            return "";
        } finally {
            // 如果httpEntity没有被完全消耗，那么连接无法安全重复使用，将被关闭并丢弃
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                response.close();
            }
        }
        return accesstoken;
    }

    private String ResponseHandle(CloseableHttpResponse response) {
        String result = "";
        // 获取响应体
        HttpEntity httpEntity = null;
        int error = 200;
        try {
            // 获取响应状态
            int statusCode = response.getStatusLine().getStatusCode();
            error = statusCode;
            // 获取响应体
            httpEntity = response.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
            }
            // 没有正常响应
            if (statusCode < HttpStatus.SC_OK || statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                throw new RuntimeException("statusCode : " + statusCode);
            }
        } catch (Exception e) {
            result = "i8接口响应：" + error + "，异常原因：" + e.getMessage();
            log.error("HttpClientHelper reponseHandle error", e);
        } finally {
            // 如果httpEntity没有被完全消耗，那么连接无法安全重复使用，将被关闭并丢弃
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
