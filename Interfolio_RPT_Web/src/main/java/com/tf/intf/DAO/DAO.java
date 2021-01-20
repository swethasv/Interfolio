package com.tf.intf.DAO;

import java.util.List;

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

}
