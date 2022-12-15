package ca.corefacility.bioinformatics.irida.util;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
public class SEU {
	private @Value("${sqlserver.seu.cnstr}") String CN_STRING_SEU;
	private @Value("${sqlserver.stec.cnstr}") String CN_STRING_STEC;
	private static final Logger logger = LoggerFactory.getLogger(SEU.class);

	public Map<String, String> getData(String strainID) throws SQLException {
		if (isStrainID(strainID)) {
			Map<String, String> SEUmap = new HashMap<String, String>();
			try { Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); 
			} catch ( ClassNotFoundException ex ) {
				logger.warn("Attempt to load class failed.", ex);
			}
			try (Connection con = DriverManager.getConnection(CN_STRING_SEU); Statement stmt = con.createStatement();) {
				String SQL = "SELECT TOP 1 * FROM ForIRIDAView WHERE Ceppo = '" + strainID + "' ORDER BY idCampioneVTECFeci";
				ResultSet rs = stmt.executeQuery(SQL);
				if (rs.next()) {
					SEUmap.put("DataEsordio", rs.getString("DataEsordio"));
					SEUmap.put("Ospedale", rs.getString("Ospedale"));
					SEUmap.put("Regione", rs.getString("Regione"));
					SEUmap.put("Localita", rs.getString("Localita"));
				} else {
					SEUmap.put("DataEsordio", null);
					SEUmap.put("Ospedale", null);
					SEUmap.put("Regione", null);
					SEUmap.put("Localita", null);
				}
				return SEUmap;
			}
		}
		else { return null; }
	}

	public Map<String, String> getSTECData(String strainID) throws SQLException {
		if (isStrainID(strainID)) {
			Map<String, String> STECmap = new HashMap<String, String>();
			try { Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); 
			} catch ( ClassNotFoundException ex ) {
				logger.warn("Attempt to load class failed.", ex);
			}
			try (Connection con = DriverManager.getConnection(CN_STRING_STEC); Statement stmt = con.createStatement();) {
				String SQL = "SELECT TOP 1 * FROM ForIRIDAView WHERE ISS_ID = '" + strainID + "'";
				ResultSet rs = stmt.executeQuery(SQL);
				if (rs.next()) {
					STECmap.put("DateOfSampling", rs.getString("DateOfSampling"));
					STECmap.put("DateOfReceiptReferenceLab", rs.getString("DateOfReceiptReferenceLab"));
					STECmap.put("Sender", rs.getString("Sender"));
					STECmap.put("sampId", rs.getString("sampId"));
					STECmap.put("IsolationSource", rs.getString("IsolationSource"));
					STECmap.put("Regione", rs.getString("Regione"));
				} else {
					STECmap.put("DateOfSampling", null);
					STECmap.put("DateOfReceiptReferenceLab", null);
					STECmap.put("Sender", null);
					STECmap.put("sampId", null);
					STECmap.put("IsolationSource", null);
					STECmap.put("Regione", null);
				}
				return STECmap;
			}
			
		}
		else { return null; }
    }

	private Boolean isStrainID(String strainID) {
		return strainID.matches("[a-zA-Z0-9]*");
	}
}
