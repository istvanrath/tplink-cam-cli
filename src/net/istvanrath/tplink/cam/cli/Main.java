package net.istvanrath.tplink.cam.cli;

/**
 * Entry point for the TP-Link Camera CLI application.
 * 
 * @author istvanrath
 *
 */
public class Main {

	public static void main(String[] args) {

		String camhost = args[0];
		String username = args[1];
		String password = args[2];
		String mode = args[3];
		String parameter = args[4];
	
		try {
			TPLinkCameraAPI api = new TPLinkCameraAPI(false, camhost, username, password);
			api.doLogin();
			switch (mode) {
				default:
				case "led": api.setLed(parameter.equalsIgnoreCase("on")?true:false); break;
			}
			api.doLogout();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
