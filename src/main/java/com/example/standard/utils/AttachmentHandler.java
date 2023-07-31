package com.example.standard.utils;

import lombok.extern.slf4j.Slf4j;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 用来处理附件的类
 */
@Slf4j
@Service
public class AttachmentHandler {

    static int socketTimeout = 500000;// 请求超时时间
    static int connectTimeout = 500000;// 传输超时时间

    @Value("${i8.dblink}")
    private String dbConnect;

    /**
     * 使用post请求登录来提取文件byte数组
     * 所用参数是通过接口文档确定的
     *
     * @param postUrl          webservice地址
     * @param soapXml          发送的xml模板
     * @param asr_code         附件所属数据条目phid
     * @param asr_table        附件所属table名称
     * @param asr_attach_table 默认asr_info
     * @param asr_filename     附件文件名称
     * @return 文件二进制数据数组
     */
    public byte[] postGetFileByte(String postUrl, String soapXml, String asr_code,
                                  String asr_table, String asr_attach_table, String asr_filename) throws IOException {
        String retStr = "";
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);

        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);

        //设置请求参数
        soapXml = soapXml.replace("{asr_code}", asr_code);
        soapXml = soapXml.replace("{asr_table}", asr_table);
        soapXml = soapXml.replace("{asr_attach_table}", asr_attach_table);
        soapXml = soapXml.replace("{asr_filename}", asr_filename);
        soapXml = soapXml.replace("{dbConn}", dbConnect);//环境变量 设置数据库连接


        try {
            httpPost.setHeader("Content-Type", "application/xml");
            StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
            httpPost.setEntity(data);
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                // 提取文件数据
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
                //根据原来.net代码翻译而来
                int start = retStr.indexOf("<GetResult>");
                int end = retStr.indexOf("</GetResult>");
                //System.out.println(retStr.substring(start + 11,end));
                if (start == -1 || end == -1) {
                    System.out.println("请求WS文件没有返回文件" + "ws没有返回文件");
                    return null;
                } else {
                    String rawFile = retStr.substring(start + 11, end);
                    return Base64.getDecoder().decode(rawFile); //解码成8bit
                }
            } else {
                System.out.println("请求WS文件没有返回值" + "ws没有返回");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            // 释放资源
            try {
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public boolean postSaveFile(String postUrl, String soapXml) {
        String retStr = "";
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);

        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);

        try {
            httpPost.setHeader("Content-Type", "application/xml");
            StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
            httpPost.setEntity(data);
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                // 提取文件数据
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
                //根据原来.net代码翻译而来
                int start = retStr.indexOf("<SaveDataResult>");
                int end = retStr.indexOf("</SaveDataResult>");
                // System.out.println(retStr.substring(start + 11,end));
                // System.out.println(retStr);
                if (start == -1 || end == -1) {
                    System.out.println("请求WS上传失败" + "ws没有返回值");
                    return false;
                } else {
                    String result = retStr.substring(start + "<SaveDataResult>".length(), end);
                    //System.out.println(String.format("result: %s", result));
                    if (result.equals("1")) {
                        System.out.println("请求WS上传成功" + String.format("成功上传了文件，返回1"));
                        return true;
                    } else {
                        System.out.println("请求WS上传失败" + "ws返回上传失败");
                        return false;
                    }
                }
            } else {
                System.out.println("请求WS文件没有返回值" + "ws没有返回");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            // 释放资源
            try {
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取附件块
     *
     * @param postUrl
     * @param soapXml
     * @return
     */
    public String postGetBlockCount(String postUrl, String soapXml) {
        String retStr;
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);
        try {
            httpPost.setHeader("Content-Type", "application/xml");
            StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
            httpPost.setEntity(data);
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(10 * 60 * 60).build());
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                System.out.println("ws没有返回");
                return null;
            }
            // 提取文件数据
            retStr = EntityUtils.toString(httpEntity, "UTF-8");
            //根据原来.net代码翻译而来
            int start = retStr.indexOf("<GetFileBlockCountResult>");
            int end = retStr.indexOf("</GetFileBlockCountResult>");
            //System.out.println(retStr.substring(start + 11,end));
            if (start == -1 || end == -1) {
                System.out.println("ws没有返回文件");
                return null;
            } else {
                String rawFile = retStr.substring(start + 25, end);
                return rawFile; //解码成8bit
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            // 释放资源
            try {
                if (closeableHttpClient != null)
                    closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取附件流块
     *
     * @param postUrl
     * @param soapXml
     * @return
     */
    public byte[] postGetBlock(String postUrl, String soapXml) {
        String retStr;
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);
        try {
            httpPost.setHeader("Content-Type", "application/xml");
            StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
            httpPost.setEntity(data);
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(10 * 60 * 60).build());
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                System.out.println("ws没有返回");
                return null;
            }
            // 提取文件数据
            retStr = EntityUtils.toString(httpEntity, "UTF-8");
            //根据原来.net代码翻译而来
            int start = retStr.indexOf("<GetFileBlockResult>");
            int end = retStr.indexOf("</GetFileBlockResult>");
            if (start == -1 || end == -1) {
                System.out.println("ws没有返回文件");
            } else {
                String rawFile = retStr.substring(start + 20, end);
                return Base64.getDecoder().decode(rawFile); //解码成8bit
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            // 释放资源
            try {
                if (closeableHttpClient != null)
                    closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
