package framework;

import java.net.URL;

public class RunningFromJar {

	private static final String JAR = "jar";
//	private static final String RSRC = "rsrc";
	private static final String FILE = "file";

	public static boolean isRunningFromJar() {
		URL url = RunningFromJar.class.getResource("RunningFromJar.class");
		String protocol = url.getProtocol();

		if (protocol.equalsIgnoreCase(FILE)) {
			return false;
		} else if (protocol.equalsIgnoreCase(JAR)) {
//				|| protocol.equalsIgnoreCase(RSRC)) {
			return true;
		} else {
			return false;
		}
	}

}
