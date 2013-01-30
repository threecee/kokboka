package models;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import play.Logger;
import play.libs.Images;

import java.io.*;
import java.util.Calendar;


public final class Image {




    public static String get(Recipe recipe) {
        if (recipe.photoName != null) {
            return "http://trippelc.com/kokboka/" + recipe.photoName;
        } else {
            return "/public/images/transparent.gif";
        }
    }

    public static String getThumb(Recipe recipe) {
        if (recipe.photoName != null) {
            return "http://trippelc.com/kokboka/thumb_" + recipe.photoName;
        } else {
            return "/public/images/food-plate-icons.jpg";
        }
    }


    private static InputStream createThumb(File fullSize) throws IOException {
        File resizedFile = new File("" + Calendar.getInstance().getTimeInMillis());

        Images.resize(fullSize, resizedFile, 140, 140);

        return new FileInputStream(resizedFile);
    }


    private static File getFileFromInputStream(InputStream inputStream) throws IOException {

        // write the inputStream to a FileOutputStream
        File outFile = new File("" + Calendar.getInstance().getTimeInMillis());

        OutputStream out = new FileOutputStream(outFile);

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }

        inputStream.close();
        out.flush();
        out.close();

        return outFile;
    }

    public static void save(String localFileName, InputStream localFileStream) {

        String server = "ftp.trippelc.com";
        String username = "kokboka@trippelc.com";
        String password = "kokboka!";

        boolean
                error = false;


        int port = 21;


        String remote = localFileName;

        final FTPClient ftp;
        ftp = new FTPClient();
        // suppress login details
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

        try {
            int reply;
            if (port > 0) {
                ftp.connect(server, port);
            } else {
                ftp.connect(server);
            }
            Logger.info("Connected to " + server + " on " + (port > 0 ? port : ftp.getDefaultPort()));

            // After connection attempt, you should check the reply code to verify
            // success.
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                Logger.error("FTP server refused connection.");
            }
        } catch (IOException e) {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException f) {
                    // do nothing
                }
            }
            Logger.error("Could not connect to server.",e);
        }

        __main:
        try {
            if (!ftp.login(username, password)) {
                ftp.logout();
                error = true;
                break __main;
            }

            Logger.info("Remote system is " + ftp.getSystemType());

            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // Use passive mode as default because most of us are
            // behind firewalls these days.
            ftp.enterLocalPassiveMode();
            ftp.setUseEPSVwithIPv4(true);

            File inputFile = getFileFromInputStream(localFileStream);

            InputStream input;
            input = new FileInputStream(inputFile);

            ftp.storeFile("thumb_" + remote, createThumb(inputFile));
            ftp.storeFile(remote, input);

            input.close();


            ftp.noop(); // check that control connection is working OK

            ftp.logout();
        } catch (FTPConnectionClosedException e) {
            error = true;
            Logger.error("Server closed connection.", e);
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException f) {
                    // do nothing
                }
            }
        }


    } // end main

    private static CopyStreamListener createListener() {
        return new CopyStreamListener() {
            private long megsTotal = 0;

            public void bytesTransferred(CopyStreamEvent event) {
                bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
            }

            public void bytesTransferred(long totalBytesTransferred,
                                         int bytesTransferred, long streamSize) {
                long megs = totalBytesTransferred / 1000000;
                for (long l = megsTotal; l < megs; l++) {
                    System.err.print("#");
                }
                megsTotal = megs;
            }
        };
    }
}
