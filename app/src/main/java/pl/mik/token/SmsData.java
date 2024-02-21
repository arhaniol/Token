package pl.mik.token;


import java.io.*;

public class SmsData {
    public static final String MESSAGE = "Message:";
    public static final String SMS_TO = "Sms to:";
    public static final String SMS_FROM = "Sms from:";
    private static final String INSTANT_TOKEN = "Instant Token:";

    private String smsTo;
    private String smsFrom;
    private String message;
    private boolean instantToken;
    private File filePath;
    private static Logger l;
    private static SmsData smsData;

    private SmsData() {
        String className = SmsData.class.getName();
        l = new Logger(className);
    }

    public static SmsData getInstance() {
        if (smsData == null) {
            smsData = new SmsData();
        }
        return smsData;
    }

    public void saveData(String to, String from, String message, boolean instantToken) {
        l.i("Saving data in:  " + filePath.getAbsolutePath());
        StringBuilder stringBuilder = new StringBuilder();
        FileOutputStream fileOutputStream = null;

        stringBuilder.append(SMS_TO);
        stringBuilder.append(to);
        stringBuilder.append("\n");
        stringBuilder.append(MESSAGE);
        stringBuilder.append(message);
        stringBuilder.append("\n");
        stringBuilder.append(SMS_FROM);
        stringBuilder.append(from);
        stringBuilder.append("\n");
        stringBuilder.append(INSTANT_TOKEN);
        stringBuilder.append(instantToken);
        stringBuilder.append("\n");

        l.i("stringBuilder: " + stringBuilder);
        try {
            fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(stringBuilder.toString().getBytes());
            if (stringBuilder.length() > 0) {
                l.i("File saved");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            l.i("File not saved");
        } catch (IOException e) {
            e.printStackTrace();
            l.i("File not saved");
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean readData() {
        l.i("Reading form file: " + filePath.getAbsolutePath());
        FileInputStream fileInputStream = null;
        try {
            if (filePath.isFile()) {
                fileInputStream = new FileInputStream(filePath);
                InputStreamReader streamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                if (stringBuilder.length() > 0) {
                    l.i("Reading complete! ");
                    return parseBuffer(stringBuilder);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            l.i("Reading error! ");
        } catch (IOException e) {
            e.printStackTrace();
            l.i("Reading error! ");
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    protected boolean parseBuffer(StringBuilder text) {
        l.i("Parsing");
        if (text == null || text.length() == 0) {
            l.i("Parsing input text is empty");
            return false;
        }
        int pos1 = text.indexOf(SMS_TO) + SMS_TO.length();
        int pos2 = text.indexOf(MESSAGE, pos1);
        if ((pos2 - pos1) > 0) {
            smsTo = text.substring(pos1, pos2);
            l.i("smsTo: " + smsTo);
        }
        pos1 = pos2 + MESSAGE.length();
        pos2 = text.indexOf(SMS_FROM, pos1);
        if ((pos2 - pos1) > 0) {
            message = text.substring(pos1, pos2);
            l.i("Message: " + message);
        }
        pos1 = pos2 + SMS_FROM.length();
        pos2 = text.indexOf(INSTANT_TOKEN, pos1);
        if ((pos2 - pos1) > 0) {
            smsFrom = text.substring(pos1, pos2);
            l.i("smsFrom: " + smsFrom);
        }
        pos1 = pos2 + INSTANT_TOKEN.length();
        if (pos1 > pos2) {
            instantToken = Boolean.parseBoolean(text.substring(pos1));
            l.i("instantToken: " + instantToken);
        }
        l.i("Parsing completed");
        if (smsTo != null && smsFrom != null && message != null) {
            return !smsFrom.isEmpty() && !smsTo.isEmpty() && !message.isEmpty();
        }
        return false;
    }

    public String getSmsTo() {
        return smsTo;
    }

    public String getSmsFrom() {
        return smsFrom;
    }

    public String getMessage() {
        return message;
    }

    public boolean dataExist() {
        if (filePath != null && filePath.isFile()) {
            l.i("Path exist");
            return true;
        } else {
            l.i("Path not exist");
            return false;
        }
    }

    public boolean isInstantToken() {
        return instantToken;
    }

    public void setInstantToken(boolean instantToken) {
        this.instantToken = instantToken;
    }

    public void setFilePath(File filePath) {
        l.i("Path set to: " + filePath.getAbsolutePath());
        this.filePath = filePath;
    }
}
