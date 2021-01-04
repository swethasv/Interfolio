package com.intf.servicesImpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intf.model.ParamVO;
import com.intf.model.Template;
import com.intf.repository.DBOperation;
import com.intf.services.UserServices;
import com.intf.util.HMAC_Encryption;
import com.intf.util.PacketDetails;

@Service
public class UserServicesImpl implements UserServices {

	@Autowired
	HMAC_Encryption hmc_Encryption;

	@Autowired
	DBOperation dbOperation;

	@Override
	public String createCase(String template_id) throws IOException {

		String request_string = "/byc-tenure/" + HMAC_Encryption.tenant_id + "/packets/create_from_template";
		String query_string = "";
		String request_verb = "POST";

		HMAC_Encryption hmac = new HMAC_Encryption();
		StringBuilder sb = null;
		ParamVO paramVO = new ParamVO();
		String result = "No records found to create a new case!!";
		try {
			paramVO = dbOperation.getParamValues();

		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpURLConnection conn = null;

		if (paramVO.getParamList() != null && paramVO.getParamList().size() != 3) {

			for (ParamVO tmpVO : paramVO.getParamList()) {
				conn = hmac.getConnection(request_string, query_string, request_verb);
				try (OutputStream os = conn.getOutputStream()) {

					Map<Object, Object> jsonValues = new HashMap<Object, Object>();
					jsonValues.put("packet_id", tmpVO.getTemplate_id());
					jsonValues.put("unit_id", PacketDetails.getUnitID(tmpVO.getTemplate_id()));
					jsonValues.put("candidate_first_name", tmpVO.getCandidate_first_name());
					jsonValues.put("candidate_last_name", tmpVO.getCandidate_last_name());
					jsonValues.put("candidate_email", tmpVO.getCandidate_email()); //
					jsonValues.put("due_date", tmpVO.getDue_at());
					JSONObject parameters = new JSONObject(jsonValues);
					JSONObject payload = new JSONObject();
					payload.put("packet", parameters);
					System.out.println(payload);
					os.write(payload.toString().getBytes());
					os.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.out.println(conn.getResponseCode());
				if (conn.getResponseCode() == 201) {
					System.out.println(conn.getResponseCode());
					dbOperation.updateAuditFlg(tmpVO.getCwid(), tmpVO.getTemplate_id());
				}

				try {
					// Buffer the result into a string
					BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					sb = new StringBuilder();

					while ((rd.readLine()) != null) {
						sb.append(rd.readLine());
					}
					rd.close();

				} catch (FileNotFoundException e) {
					System.out.println(e.getMessage());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				conn.disconnect();

			}
			if (sb != null) {
				result = sb.toString();
			}
		}

		return result;
	}

	public Map<Object, Object> getTemplateId() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		List<Template> template = dbOperation.getTemplateId();
		map.put("TemplateID", template);
		return map;
	}

}
