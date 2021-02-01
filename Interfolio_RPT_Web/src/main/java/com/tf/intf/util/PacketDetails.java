package com.tf.intf.util;

import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PacketDetails {

	@Autowired
	IntfUtils intfUtils;

	@Autowired
	HMAC_Encryption hmc_Encryption;

	private String product = "byc"; // Value should be either 'faculty180', or 'byc' (if you are using FS or RPT)
	private String request_verb = "GET";

	private String getRequestString() {
		return "/byc-tenure/" + hmc_Encryption.getTenantId() + "/packet_templates/";
	}

	private String gen_HMAC(String query_string) {
		String HMAC_request_string = getRequestString();
		if (product == HMAC_Encryption.product) {
			HMAC_request_string = HMAC_request_string + query_string;
		}

		String verbReq = request_verb + "\n\n\n" + intfUtils.timestampString() + "\n" + HMAC_request_string;
		try {
			Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret_key = new SecretKeySpec(hmc_Encryption.getPrivateKey().getBytes(), "HmacSHA1");
			sha1_HMAC.init(secret_key);
			String output_hash = org.apache.commons.codec.binary.Base64
					.encodeBase64String(sha1_HMAC.doFinal(verbReq.getBytes()));

			String authorization_header = "INTF " + hmc_Encryption.getPublicKey() + output_hash;

			return authorization_header;
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return null;
		}
	}

	private String getResults(String query_string) throws IOException {
		HMAC_Encryption hmac = new HMAC_Encryption();
		String full_request = hmc_Encryption.getHost() + getRequestString() + query_string;

		URL url = new URL(full_request);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Timestamp", intfUtils.timestampString());
		conn.setRequestProperty("Authorization", gen_HMAC(query_string));
		conn.setRequestProperty("INTF-DatabaseID", hmc_Encryption.getTenantId());

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();
		return sb.toString();
	}

	public static int getPacketID(String query_string) {
		int packet_id = 0;

		PacketDetails token = new PacketDetails();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(token.getResults(query_string));
			JSONArray jsonArray = (JSONArray) jsonObject.get("results");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
				packet_id = (int) jsonObject1.get("id");

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return packet_id;
	}

	public static int getUnitID(String query_string) {

		int unit_id = 0;
		PacketDetails token = new PacketDetails();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(token.getResults(query_string));
			// jsonObject = (JSONObject) jsonObject.get("packet_template");
			JSONObject jsonObject1 = (JSONObject) jsonObject.get("packet_template");
			unit_id = (int) jsonObject1.get("unit_id");

			/*
			 * JSONArray jsonArray = (JSONArray)jsonObject.get("results"); for (int i = 0; i
			 * < jsonArray.length(); i++) { JSONObject jsonObject1 = (JSONObject)
			 * jsonArray.get(i);
			 * 
			 * unit_id = (int) jsonObject1.get("unit_id"); }
			 */
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return unit_id;
	}

	public static void main(String[] args) {
		// String query_string = "?search_text=College%20of%20Humanities";

		System.out.println(getUnitID("176648"));

	}

}
