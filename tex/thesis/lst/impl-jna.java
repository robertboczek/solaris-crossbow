public interface LinkHandle extends Library {
  public int set_ip_address(String link, String address);
}

public class JNALinkHelper implements LinkHelper {

  protected LinkHandle handle = null;

  public static final String LIB_NAME
    = "libjims-crossbow-native-lib-link-3.0.0.so";

  /**
   * Creates the helper object and initializes underlying handler.
   *
   * @param libraryPath Path to native library
   */
  public JNALinkHelper(String libraryPath) {
    String filePath = libraryPath + File.separator + LIB_NAME;
    handle = (LinkHandle) Native.loadLibrary(filePath,
                                             LinkHandle.class);
    handle.init();
  }
  
  public int setIpAddress(String link, String ipAddress)
    throws LinkException, ValidationException {
    return handle.set_ip_address(link, ipAddress);
  }
}
