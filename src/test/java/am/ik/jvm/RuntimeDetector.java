package am.ik.jvm;

public final class RuntimeDetector {

	public static boolean isNativeImage() {
		return "runtime".equals(System.getProperty("org.graalvm.nativeimage.imagecode"));
	}

}