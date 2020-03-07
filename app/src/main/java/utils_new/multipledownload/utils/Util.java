package utils_new.multipledownload.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util{
    public static final String DOWNLOAD_PART = "DOWNLOAD_PART-";

    private Util() {
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean hasStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static String getCachePath(Context context) {
        File externalCacheDir = context.getExternalCacheDir();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (externalCacheDir != null) {
                return externalCacheDir.getAbsolutePath();
            } else {
                if (hasStoragePermission(context)) {
                    File cacheFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/cache/");
                    if (!cacheFile.exists()) {
                        cacheFile.mkdirs();
                    }
                    return cacheFile.getAbsolutePath();
                } else {
                    return context.getCacheDir().getAbsolutePath();
                }
            }
        } else {
            return context.getCacheDir().getAbsolutePath();
        }
    }

    public static File getTempDir(String filePath) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        return new File(parentFile, "." + file.getName() + ".temp" + File.separatorChar);
    }

    public static long getUsableSpace(File file) {
        if (file == null) return 0L;
        return getUsableSpaceBeforeO(file);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static long getUsableSpaceAfterO(Context context, File file) {
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            UUID uuid = sm.getUuidForPath(file);
            sm.getAllocatableBytes(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    private static long getUsableSpaceBeforeO(File file) {
        if (file.isDirectory()) {
            return file.getUsableSpace();
        } else {
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                //Create parent directory if not exists.
                parentFile.mkdirs();
                return parentFile.getUsableSpace();
            } else {
                return 0L;
            }
        }
    }

    public static long parseContentLength(String contentLength) {
        if (contentLength == null) return -1;

        try {
            return Long.parseLong(contentLength);
        } catch (NumberFormatException ignored) {
        }

        return -1;
    }

    public static String guessFileName(
            String url,
            String contentDisposition,
            String mimeType) {
        String filename = null;
        String extension = null;

        if (contentDisposition != null) {
            filename = parseContentDisposition(contentDisposition);
            if (filename != null) {
                int index = filename.lastIndexOf('/') + 1;
                if (index > 0) {
                    filename = filename.substring(index);
                }
            }
        }

        if (filename == null) {
            String decodedUrl = Uri.decode(url);
            if (decodedUrl != null) {
                int queryIndex = decodedUrl.indexOf('?');
                // If there is a query string strip it, same as desktop browsers
                if (queryIndex > 0) {
                    decodedUrl = decodedUrl.substring(0, queryIndex);
                }
                if (!decodedUrl.endsWith("/")) {
                    int index = decodedUrl.lastIndexOf('/') + 1;
                    if (index > 0) {
                        filename = decodedUrl.substring(index);
                    }
                }
            }
        }

        // Finally, if couldn't get filename from URI, get a generic filename
        if (filename == null) {
            filename = MD5Util.getMD5ByStr(url);
        }

        // Split filename between base and extension
        // Add an extension if filename does not have one
        int dotIndex = filename.indexOf('.');
        if (dotIndex >= 0) {
            if (mimeType != null) {
                // Compare the last segment of the extension against the mime type.
                // If there's a mismatch, discard the entire extension.
                int lastDotIndex = filename.lastIndexOf('.');
                String typeFromExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        filename.substring(lastDotIndex + 1));
                if (typeFromExt != null && !typeFromExt.equalsIgnoreCase(mimeType)) {
                    extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                    if (extension != null) {
                        extension = "." + extension;
                    }
                }
            }
        }
        if (extension == null) {
            if (mimeType != null) {
                int index = mimeType.indexOf(";");
                if (index >= 0) {
                    mimeType = mimeType.substring(0, index);
                }
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                if (extension != null) {
                    extension = "." + extension;
                }
            }
            if (extension == null) {
                if (mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("text/")) {
                    if (mimeType.equalsIgnoreCase("text/html")) {
                        extension = ".html";
                    } else {
                        extension = ".txt";
                    }
                }
            }
        }

        if (dotIndex >= 0) {
            if (extension == null) {
                extension = filename.substring(dotIndex);
            }
            filename = filename.substring(0, dotIndex);
        }
        if (extension != null) {
            filename = filename + extension;
        }
        return filename;
    }

    static String parseContentDisposition(String contentDisposition) {
        try {
            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
            if (m.find()) {
                return m.group(2);
            }
        } catch (IllegalStateException ex) {
            // This function is defined as returning null when it can't parse the header
        }
        return null;
    }

    /**
     * Regex used to parse content-disposition headers
     */
    private static final Pattern CONTENT_DISPOSITION_PATTERN =
            Pattern.compile("attachment;\\s*filename\\s*=\\s*(\"?)([^\"]*)\\1\\s*$",
                    Pattern.CASE_INSENSITIVE);
}