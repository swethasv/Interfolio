package com.tf.intf.DAO;

import java.sql.SQLException;
import java.util.List;

import com.tf.intf.model.InputSourceVO;
import com.tf.intf.model.ParamVO;
import com.tf.intf.model.TemplateVO;

public interface DAO {

	List<ParamVO> getSOQFilesFromDataBase(String fILE_TO_BE_UPLOADED);

	List<ParamVO> getSOQFileDetails(String fILE_TO_UPLOAD, String sOQ_STAT_FILE_TYPE);

	List<ParamVO> getCaseCreateData(String fILE_TO_UPLOAD);

	void updateSOQAuditFlg(ParamVO paramVO, String fILE_IN_RUNNING_STATE);

	List<TemplateVO> getTemplateId();
	
	void updateErrorMsg(ParamVO paramVO, String fILE_FAILED_STATE);

	void updateCaseCreateAuditFlag(ParamVO tmpVO, String cASE_CREATE_FLAG);
	
	public int[] batchUpdateSOQAuditFlag(List<ParamVO> param);

	public int[] createDataFromInputSource(List<InputSourceVO> listInputSourceVO);

	public int deleteRecords() throws SQLException;

	List<TemplateVO> getAllSOQData();

	List<TemplateVO> getSOQData(String sOQ_FILE_TYPE);

}
