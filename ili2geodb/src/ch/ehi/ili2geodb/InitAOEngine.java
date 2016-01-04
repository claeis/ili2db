package ch.ehi.ili2geodb;

import java.io.IOException;
import java.net.UnknownHostException;

import ch.ehi.basics.logging.EhiLogger;

import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.esri.arcgis.system.esriLicenseStatus;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system.AoInitialize;

public class InitAOEngine implements ch.ehi.ili2db.base.Ili2dbLibraryInit {
	private static int refc=0;
	static AoInitialize aoInitializer = null;
	public void init()
	{
		refc++;
		if(refc==1){
			try {
				EngineInitializer.initializeEngine();
				aoInitializer = new AoInitialize();
				int lic=0;
				//lic=aoInitializer.initialize(esriLicenseProductCode.esriLicenseProductCodeEngine);
				//lic=aoInitializer.initialize(esriLicenseProductCode.esriLicenseProductCodeEngineGeoDB);
				lic=aoInitializer.initialize(esriLicenseProductCode.esriLicenseProductCodeEngineGeoDB);
				if(lic!=esriLicenseStatus.esriLicenseCheckedOut){
					lic=aoInitializer.initialize(esriLicenseProductCode.esriLicenseProductCodeArcView);
				}
				if(lic!=esriLicenseStatus.esriLicenseCheckedOut){
					lic=aoInitializer.initialize(esriLicenseProductCode.esriLicenseProductCodeArcEditor);
				}
				if(lic!=esriLicenseStatus.esriLicenseCheckedOut){
					throw new IllegalStateException("failed to checkout license");
				}
			} catch (UnknownHostException e) {
				throw new IllegalStateException(e);
			} catch (AutomationException e) {
				throw new IllegalStateException(e);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	public void end()
	{
		refc--;
		if(refc==0){
			try {
				aoInitializer.shutdown();
			} catch (AutomationException e) {
				throw new IllegalStateException(e);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
