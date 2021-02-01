package com.tf.intf.DAOImpl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tf.intf.DAO.DAO;
import com.tf.intf.model.InputSourceVO;
import com.tf.intf.model.ParamVO;
import com.tf.intf.model.TemplateVO;
import com.tf.intf.util.IntfUtils;

@Repository
public class DAOImpl implements DAO {

	@Autowired
	IntfUtils intfUtils;

	@Autowired
	JdbcTemplate jdbcTemplate;

	private String INTERFOLIO_STG_TABLE_NAME = "WC_INTER_STG_1210";
	private String INTERFOLIO_FILE_TABLE_NAME = "WC_INTER_INSTR_DATA_1227";

	private final String GET_SOQ_DATA = "select * from " + INTERFOLIO_FILE_TABLE_NAME + " WHERE FILE_UPLOAD_FLAG = ?";
	private final String GET_SOQ_DATA_TO_FILE_UPLOAD = "select * from " + INTERFOLIO_FILE_TABLE_NAME
			+ " WHERE FILE_UPLOAD_FLAG = ? AND FILE_TYPE = ?";
	private final String GET_CASE_CREATE_DATA = "select * from " + INTERFOLIO_STG_TABLE_NAME
			+ " where TEMPLATE_ID=? AND CREATED_FLG = ?";
	private final String UPDATE_SOQ_AUDIT_FLAG = "update " + INTERFOLIO_FILE_TABLE_NAME
			+ " set FILE_UPLOAD_FLAG=?, PROCESS_DATE =? where FILE_NAME=? AND TEMPLATE_ID = ?";
	private final String GET_TEMPLATE = "select TEMPLATE_ID, CAND_NAME, CREATED_FLG from " + INTERFOLIO_STG_TABLE_NAME;
	private final String UPDATE_ERROR_MSG = "update " + INTERFOLIO_FILE_TABLE_NAME
			+ " set FILE_UPLOAD_FLAG=?,errorMsg=?, PROCESS_DATE =? where FILE_NAME=? AND TEMPLATE_ID=?";
	private final String UPDATE_CASE_CREATE_AUDIT_FLAG = "update " + INTERFOLIO_STG_TABLE_NAME
			+ " set created_flg= ? where cwid = ? and TEMPLATE_ID = ?";
	private final String UPDATE_SOQ_AUDIT_FLAG_BATCH = "update " + INTERFOLIO_FILE_TABLE_NAME
			+ " set FILE_UPLOAD_FLAG=?, ERRORMSG=?, PROCESS_DATE =? where FILE_NAME=? AND TEMPLATE_ID = ?";
	private final String CREATE_NEW_RECORDS = "insert into WC_TEST_SOURCE_INPUT (CWID, TEMPLATE_ID, REVIEW_TERM, TENURE, CREATE_DATE) values(?, ?, ?, ?, ?)";
	private final String DELETE_RECORDS = "DELETE from WC_TEST_SOURCE_INPUT";
	
	
	public List<ParamVO> getSOQFilesFromDataBase(String fILE_TO_UPLOAD) {
		List<ParamVO> result = new ArrayList<ParamVO>();
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(GET_SOQ_DATA, new Object[] { fILE_TO_UPLOAD });
			for (Map<String, Object> row : rows) {
				ParamVO param = new ParamVO();
				param.setCandidate_first_name(row.get("CAND_FIRST_NM") + " " + row.get("CAND_LAST_NM"));
				param.setTemplate_id((String) row.get("TEMPLATE_ID"));
				param.setFile_name((String) row.get("FILE_NAME"));
				result.add(param);
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<ParamVO> getSOQFileDetails(String fILE_TO_UPLOAD, String sOQ_STAT_FILE_TYPE) {
		List<ParamVO> result = new ArrayList<ParamVO>();
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(GET_SOQ_DATA_TO_FILE_UPLOAD,
					new Object[] { fILE_TO_UPLOAD, sOQ_STAT_FILE_TYPE });
			for (Map<String, Object> row : rows) {
				ParamVO param = new ParamVO();
				param.setCandidate_first_name(row.get("CAND_FIRST_NM") + " " + row.get("CAND_LAST_NM"));
				param.setTemplate_id((String) row.get("TEMPLATE_ID"));
				param.setFile_name((String) row.get("FILE_NAME"));
				result.add(param);
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<ParamVO> getCaseCreateData(String template_id, String fILE_TO_UPLOAD) {
		List<ParamVO> result = new ArrayList<ParamVO>();
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(GET_CASE_CREATE_DATA,
					new Object[] { template_id, fILE_TO_UPLOAD });
			for (Map<String, Object> row : rows) {
				ParamVO param = new ParamVO();
				param.setCandidate_first_name((String) row.get("CAND_FIRST_NM"));
				param.setCandidate_last_name((String) row.get("CAND_LAST_NM"));
				param.setTemplate_id((String) row.get("TEMPLATE_ID"));
				param.setCandidate_email((String) row.get("CAND_EMAIL_ADDR"));
				param.setCwid((String) row.get("CWID"));
				result.add(param);
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void updateSOQAuditFlg(ParamVO paramVO, String fILE_IN_RUNNING_STATE) {
		try {
			java.sql.Date date = intfUtils.getSysdate();
			jdbcTemplate.update(UPDATE_SOQ_AUDIT_FLAG,
					new Object[] { fILE_IN_RUNNING_STATE, date, paramVO.getFile_name(), paramVO.getTemplate_id() });
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<TemplateVO> getTemplateId() {
		List<TemplateVO> result = new ArrayList<TemplateVO>();
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(GET_TEMPLATE);
			for (Map<String, Object> row : rows) {
				TemplateVO template = new TemplateVO();
				template.setTemplateId((String) row.get("TEMPLATE_ID"));
				template.setCand_name((String) row.get("CAND_NAME"));
				template.setStatus((String) row.get("CREATED_FLG"));
				result.add(template);
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void updateErrorMsg(ParamVO paramVO, String fILE_FAILED_STATE) {
		try {
			java.sql.Date date = intfUtils.getSysdate();
			jdbcTemplate.update(UPDATE_ERROR_MSG, new Object[] { fILE_FAILED_STATE, paramVO.getErrorMsg(), date,
					paramVO.getFile_name(), paramVO.getTemplate_id() });
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateCaseCreateAuditFlag(ParamVO paramVO, String cASE_CREATE_FLAG) {
		try {
			jdbcTemplate.update(UPDATE_CASE_CREATE_AUDIT_FLAG,
					new Object[] { cASE_CREATE_FLAG, paramVO.getCwid(), paramVO.getTemplate_id() });
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	public int[] batchUpdateSOQAuditFlag(List<ParamVO> param) {
		return this.jdbcTemplate.batchUpdate(UPDATE_SOQ_AUDIT_FLAG_BATCH, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, param.get(i).getAuditFlag());
				ps.setString(2, param.get(i).getErrorMsg());
				ps.setDate(3, param.get(i).getProcessDate());
				ps.setString(4, param.get(i).getFile_name());
				ps.setString(5, param.get(i).getTemplate_id());
			}

			public int getBatchSize() {
				return param.size();
			}
		});
	}

	public int[] createDataFromInputSource(List<InputSourceVO> listInputSourceVO) {
		return this.jdbcTemplate.batchUpdate(CREATE_NEW_RECORDS, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, listInputSourceVO.get(i).getCwid());
				ps.setString(2, listInputSourceVO.get(i).getTemplate_id());
				ps.setString(3, listInputSourceVO.get(i).getReview_term());
				ps.setInt(4, listInputSourceVO.get(i).getTenure());
				ps.setDate(5, listInputSourceVO.get(i).getCreate_date());
			}

			public int getBatchSize() {
				return listInputSourceVO.size();
			}

		});
	}

	@Override
	public int deleteRecords() {
		int rowsDeleted = jdbcTemplate.update(DELETE_RECORDS);
		return rowsDeleted;
	}
}
