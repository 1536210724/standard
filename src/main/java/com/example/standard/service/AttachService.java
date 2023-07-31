package com.example.standard.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/7/12 11:15
 */
public interface AttachService {

    void downLoad(HttpServletResponse httpServletResponse, String asr_code, String asr_table, String asr_attach_table, String asr_filename) throws IOException;

    byte[] getFileWithByte(String asr_code, String asr_table, String asr_attach_table, String asr_filename) throws IOException;

    boolean UploadFile(String asr_code,String asr_table,String asr_attach_table,String asr_params,String asr_data);

}
