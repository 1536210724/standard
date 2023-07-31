package com.example.standard.controller;

import com.example.standard.service.TenderService;
import com.ngsecdev.model.I8ReturnModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author lishixu
 * @date 2023/3/21
 * @apiNote
 */
@Api(tags ="投标")
@RequestMapping("/tenderApi")
@RestController
public class TenderController {

    @Autowired
    private TenderService tenderService;

    @ApiOperation(value = "招标公告", notes = "招标公告", produces = "application/json")
    @RequestMapping(value = "/pushTenderTest", method = RequestMethod.POST)
    public I8ReturnModel pushTenderTest(@RequestBody String json) throws IOException {
        I8ReturnModel i8ReturnModel = tenderService.pushTenderTest(json);
        return i8ReturnModel;
    }

    @ApiOperation(value = "附件", notes = "招标公告", produces = "application/json")
    @RequestMapping(value = "/pushFile", method = RequestMethod.GET)
    public String pushFile(String asr_code,String asr_name,String asr_table,String asr_fileTable) throws IOException {
        return tenderService.pushFile(asr_code,asr_name,asr_table,asr_fileTable);
    }

    @ApiOperation(value = "查询所有报名和投标的供应商", notes = "查询所有报名和投标的供应商", produces = "application/json")
    @RequestMapping(value = "/queryAppliedSupplier", method = RequestMethod.GET)
    public I8ReturnModel queryAppliedSupplier(String biddingId) throws IOException {
        I8ReturnModel i8ReturnModel = tenderService.queryAppliedSupplier(biddingId);
        return i8ReturnModel;
    }


    @ApiOperation(value = "供应商信息获取", notes = "供应商信息获取", produces = "application/json")
    @RequestMapping(value = "/getSupplier", method = RequestMethod.GET)
    public I8ReturnModel getSupplier(String memberId) throws IOException {
        I8ReturnModel i8ReturnModel = tenderService.getSupplier(memberId);
        return i8ReturnModel;
    }

    @ApiOperation(value = "供应商信息获取", notes = "供应商信息获取", produces = "application/json")
    @RequestMapping(value = "/saveTenderFile", method = RequestMethod.GET)
    public void saveTenderFile(String data,Long memberId) throws Exception {
        tenderService.saveTenderFile(data,memberId);
    }
    @ApiOperation(value = "投标报名", notes = "投标报名", produces = "application/json")
    @RequestMapping(value = "/tenderSignup", method = RequestMethod.GET)
    public void tenderSignup(){

    }

    @ApiOperation(value = "投标文件", notes = "投标文件", produces = "application/json")
    @RequestMapping(value = "/tenderFile", method = RequestMethod.GET)
    public void tenderFile(){

    }

    @ApiOperation(value = "投标文件明细", notes = "投标文件明细", produces = "application/json")
    @RequestMapping(value = "/tenderFileD", method = RequestMethod.GET)
    public void tenderFileD(){

    }

    @ApiOperation(value = "评标任务主表", notes = "评标任务主表", produces = "application/json")
    @RequestMapping(value = "/bidevaTask", method = RequestMethod.GET)
    public void bidevaTask(){

    }

    @ApiOperation(value = "评标任务明细", notes = "评标任务明细", produces = "application/json")
    @RequestMapping(value = "/bidevaD", method = RequestMethod.GET)
    public void bidevaD(){

    }

    @ApiOperation(value = "评标任务分数明细", notes = "评标任务分数明细", produces = "application/json")
    @RequestMapping(value = "/bidevaDD", method = RequestMethod.GET)
    public void bidevaDD(){

    }

    @ApiOperation(value = "定标书", notes = "定标书", produces = "application/json")
    @RequestMapping(value = "/pForm700003M", method = RequestMethod.GET)
    public void pForm700003M(){

    }

    @ApiOperation(value = "定标书明细", notes = "定标书明细", produces = "application/json")
    @RequestMapping(value = "/pForm700003D", method = RequestMethod.GET)
    public void pForm700003D(){

    }

    @ApiOperation(value = "中标通知", notes = "中标通知", produces = "application/json")
    @RequestMapping(value = "/awardnotice", method = RequestMethod.GET)
    public void awardnotice(){

    }

    @ApiOperation(value = "中标通知明细", notes = "中标通知明细", produces = "application/json")
    @RequestMapping(value = "/awardnoticeD", method = RequestMethod.GET)
    public void awardnoticeD(){

    }

    @ApiOperation(value = "查询所有报名和投标的供应商", notes = "查询所有报名和投标的供应商", produces = "application/json")
    @RequestMapping(value = "/getAppliedSupplier", method = RequestMethod.GET)
    public void getAppliedSupplier(){

    }


}
