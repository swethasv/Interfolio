package com.intf.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intf.DBConfig.INTFDBConfig;
import com.intf.model.ParamVO;
import com.intf.model.Template;

@Component
public class DBOperation {

	@Autowired
	INTFDBConfig dbConfig;

	private String INTERFOLIO_STG_TABLE_NAME = "WC_INTER_STG_1210";

	@SuppressWarnings("static-access")
	public ParamVO getParamValues() throws Exception {
		ParamVO paramVO = new ParamVO();
		List<ParamVO> paramList = new ArrayList<ParamVO>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			conn = dbConfig.createConnection();
			ps = conn.prepareStatement("select * from " + INTERFOLIO_STG_TABLE_NAME + " where CREATED_FLG is null");
			rs = ps.executeQuery();
			if (rs.next()) {
				ParamVO tempParamVO = null;
				do {
					tempParamVO = new ParamVO();
					tempParamVO.setCandidate_first_name(rs.getString("CAND_FIRST_NM"));
					tempParamVO.setCandidate_last_name(rs.getString("CAND_LAST_NM"));
					tempParamVO.setCandidate_email(rs.getString("CAND_EMAIL_ADDR"));
					// tempParamVO.setDue_at(rs.getDate("DUE_AT"));
					tempParamVO.setCwid(rs.getString("CWID"));
					tempParamVO.setTemplate_id(rs.getString("TEMPLATE_ID"));
					paramList.add(tempParamVO);

				} while (rs.next());
				paramVO.setParamList(paramList);
			}

			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConfig.closeConnection(conn, null, null);
		}
		return paramVO;
	}

	@SuppressWarnings("static-access")
	public void updateAuditFlg(String cwid, String templateID) {
		PreparedStatement ps = null;
		Connection conn = null;
		try {

			conn = dbConfig.createConnection();
			ps = conn.prepareStatement("update " + INTERFOLIO_STG_TABLE_NAME + " set created_flg= 'Y' where cwid="
					+ cwid + " and TEMPLATE_ID=" + templateID);
			ps.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConfig.closeConnection(conn, null, null);
		}

	}

	@SuppressWarnings("static-access")
	public List<Template> getTemplateId() {
		List<Template> templateList = new ArrayList<Template>();
		PreparedStatement ps = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = dbConfig.createConnection();
			ps = conn.prepareStatement("select distinct TEMPLATE_ID from WC_INTER_INSTR_DATA");
			rs = ps.executeQuery();
			if (rs.next()) {
				Template template = null;
				do {
					template = new Template();
					template.setTemplateId(rs.getString("TEMPLATE_ID"));

					templateList.add(template);

				} while (rs.next());
			}

			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConfig.closeConnection(conn, null, null);
		}

		return templateList;

	}
}
