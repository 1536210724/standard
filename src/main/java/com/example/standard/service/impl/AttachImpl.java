package com.example.standard.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.standard.service.AttachService;
import com.example.standard.utils.AttachmentHandler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/7/12 11:16
 */
@Service
public class AttachImpl implements AttachService {

    @Value("${i8.url}")
    private String i8Url;
    @Value("${i8.dblink}")
    private String dbConnect;
    @Autowired
    private AttachmentHandler attachmentHandler;

    static int socketTimeout = 200000;// 请求超时时间
    static int connectTimeout = 200000;// 传输超时时间


    public void downLoad(HttpServletResponse httpServletResponse, String asr_code, String asr_table, String asr_attach_table, String asr_filename) throws IOException {
        System.out.println(String.format("来源table: %s, 文件名称: %s", asr_table, asr_filename));

        String getUrl = i8Url + "/filesrv/UploadFileService.asmx";
        String xmlTemplate = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <tem:GetFileBlockCount>\n" +
                "        <tem:asr_session_guid></tem:asr_session_guid>\n" +
                "        <tem:asr_code>" + asr_code + "</tem:asr_code>\n" +
                "        <tem:asr_table>" + asr_table + "</tem:asr_table>\n" +
                "        <tem:asr_attach_table>" + asr_attach_table + "</tem:asr_attach_table>\n" +
                "        <tem:asr_name>" + asr_filename + "</tem:asr_name>\n" +
                "        <tem:asr_dbconn>" + dbConnect + "</tem:asr_dbconn>\n" +
                "      </tem:GetFileBlockCount>\n" +
                "    </soapenv:Body>\n" +
                " </soapenv:Envelope>";
        String returnBody = attachmentHandler.postGetBlockCount(getUrl, xmlTemplate);
        if (StringUtils.isEmpty(returnBody)) {
            System.out.println(String.format("来源table: %s, 文件名称: %s 下载失败", asr_table, asr_filename));
        }

        JSONObject body = JSONObject.parseObject(returnBody);
        String success = body.getString("success");
        String asrsessionguid = body.getString("asrsessionguid");
        String asrfid = body.getString("asrfid");
        String count = body.getString("count");
        int counti = Integer.parseInt(count);
        byte[] decodedBytes = new byte[0];
        if ("1".equals(success)) {
            for (int i = 0; i < counti; i++) {
                String xmlBlock = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <tem:GetFileBlock>\n" +
                        "        <tem:asr_session_guid>" + asrsessionguid + "</tem:asr_session_guid>\n" +
                        "        <tem:asr_fid>" + asrfid + "</tem:asr_fid>\n" +
                        "        <tem:asr_seq>" + i + "</tem:asr_seq>\n" +
                        "        <tem:asr_dbconn>" + dbConnect + "</tem:asr_dbconn>\n" +
                        "      </tem:GetFileBlock>\n" +
                        "    </soapenv:Body>\n" +
                        " </soapenv:Envelope>";
                decodedBytes = addBytes(decodedBytes, attachmentHandler.postGetBlock(getUrl, xmlBlock));
            }
        }

        //var decodedBytes = attachmentHandler.post(getUrl, xmlTemplate);
        //设置响应体保证自动下载
        httpServletResponse.setHeader("content-type", "application/octet-stream");
        httpServletResponse.setContentType("application/octet-stream");
        // 下载文件能正常显示中文
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(asr_filename, "UTF-8"));
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        if (decodedBytes != null) {
            InputStream inputStream = new ByteArrayInputStream(decodedBytes);
            byte[] c = new byte[1024]; //缓冲
            int length;
            while ((length = inputStream.read(c)) > 0) { //将数据读入缓冲
                servletOutputStream.write(c, 0, length); //将缓冲写入返回
            }
        } else {
            System.out.println(String.format("来源table: %s, 文件名称: %s 不存在", asr_table, asr_filename));
        }

        servletOutputStream.flush();
        servletOutputStream.close();
    }

    public byte[] getFileWithByte(String asr_code, String asr_table, String asr_attach_table, String asr_filename){
        String getUrl = i8Url + "/filesrv/UploadFileService.asmx";
        String xmlTemplate = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <tem:GetFileBlockCount>\n" +
                "        <tem:asr_session_guid></tem:asr_session_guid>\n" +
                "        <tem:asr_code>" + asr_code + "</tem:asr_code>\n" +
                "        <tem:asr_table>" + asr_table + "</tem:asr_table>\n" +
                "        <tem:asr_attach_table>" + asr_attach_table + "</tem:asr_attach_table>\n" +
                "        <tem:asr_name>" + asr_filename + "</tem:asr_name>\n" +
                "        <tem:asr_dbconn>" + dbConnect + "</tem:asr_dbconn>\n" +
                "      </tem:GetFileBlockCount>\n" +
                "    </soapenv:Body>\n" +
                " </soapenv:Envelope>";
        String returnBody = attachmentHandler.postGetBlockCount(getUrl, xmlTemplate);
        if (StringUtils.isEmpty(returnBody)) {
            System.out.println(String.format("来源table: %s, 文件名称: %s 下载失败", asr_table, asr_filename));
        }

        JSONObject body = JSONObject.parseObject(returnBody);
        String success = body.getString("success");
        String asrsessionguid = body.getString("asrsessionguid");
        String asrfid = body.getString("asrfid");
        String count = body.getString("count");
        int counti = Integer.parseInt(count);
        byte[] decodedBytes = new byte[0];
        if ("1".equals(success)) {
            for (int i = 0; i < counti; i++) {
                String xmlBlock = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <tem:GetFileBlock>\n" +
                        "        <tem:asr_session_guid>" + asrsessionguid + "</tem:asr_session_guid>\n" +
                        "        <tem:asr_fid>" + asrfid + "</tem:asr_fid>\n" +
                        "        <tem:asr_seq>" + i + "</tem:asr_seq>\n" +
                        "        <tem:asr_dbconn>" + dbConnect + "</tem:asr_dbconn>\n" +
                        "      </tem:GetFileBlock>\n" +
                        "    </soapenv:Body>\n" +
                        " </soapenv:Envelope>";
                decodedBytes = addBytes(decodedBytes, attachmentHandler.postGetBlock(getUrl, xmlBlock));
            }
        }
        return decodedBytes;
    }

    /**
     * 上传i8文件
     * @param asr_code
     * @param asr_table
     * @param asr_attach_table
     * @param asr_params
     * @param asr_data
     * @return
     */
    public boolean UploadFile(String asr_code,String asr_table,String asr_attach_table,String asr_params,String asr_data){
        String i8UploadUrl = i8Url + "/filesrv/UploadFileService.asmx?wsdl";
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
                "         <tem:asr_dbconn>" + dbConnect + "</tem:asr_dbconn>\n" +
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

        return postSaveFileEx(i8UploadUrl, paramXml);
    }

    public boolean postSaveFileEx(String url, String xml) {
        String xmlResult = "";
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(30000)
                .setConnectTimeout(30000)
                .build();
        httpPost.setConfig(config);
        httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
        try {

            StringEntity data = new StringEntity(xml, Charset.forName("UTF-8"));
            httpPost.setEntity(data);
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                // 提取文件数据
                xmlResult = EntityUtils.toString(httpEntity, "UTF-8");
                //根据原来.net代码翻译而来
                int start = xmlResult.indexOf("<SaveDataResult>");
                int end = xmlResult.indexOf("</SaveDataResult>");
                // System.out.println(retStr.substring(start + 11,end));
                // System.out.println(retStr);
                if (start == -1 || end == -1) {
                    System.out.println("请求WS上传失败, ws没有返回值");
                    return false;
                } else {
                    String result = xmlResult.substring(start + "<SaveDataResult>".length(), end);
                    //System.out.println(String.format("result: %s", result));
                    if (result.equals("1")) {
                        System.out.println("请求WS上传成功");
                        return true;
                    } else {
                        System.out.println("请求WS上传失败,ws返回上传失败");
                        return false;
                    }
                }
            } else {
                System.out.println("请求WS文件没有返回值, ws没有返回");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WS上传文件时发生错误"+e);
        } finally {
            // 释放资源
            try {
                if (client != null)
                    client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

}
