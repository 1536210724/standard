package com.example.standard.service;

import com.ngsecdev.model.I8ReturnModel;

import java.io.IOException;

/**
 * @author lishixu
 * @date 2023/3/21
 * @apiNote
 */
public interface TenderService {

    I8ReturnModel pushTenderTest(String json) throws IOException;
    I8ReturnModel queryAppliedSupplier(String biddingId) throws IOException;
    I8ReturnModel saveTenderFile(String data, Long phid) throws Exception;
    I8ReturnModel getSupplier(String memberId) throws IOException;
    String pushFile(String asr_code,String asr_filename, String asr_table,String asr_fileTable) throws IOException;
}
