package net.mrgeotech.storage;

import java.sql.*;
import java.util.List;
import java.util.Locale;

public class StorageManager {

    public static String DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD;

    public static String getDownloadUrl(String name) {
        return getDownloadUrl(name, "latest");
    }

    public static String getDownloadUrl(String name, String version) {
        if (version == null) version = "latest";

        // Getting data from database
        try (PreparedStatement statement = DriverManager.getConnection("jdbc:mysql://u1_cw2jeaNyDk:Db2!qz3=UJBGZEgg7RVek!i+@192.168.254.99:3306/s1_PackNPlug")
                .prepareStatement("SELECT DownloadUrl FROM ? WHERE Version=?;")) {
            statement.setString(1, name.toLowerCase(Locale.ROOT));
            statement.setString(2, version);
            ResultSet resultSet = statement.executeQuery();

            // Checking that it actually exists
            if (resultSet == null || !resultSet.next())
                return "null";

            // Returning wanted value
            return resultSet.getString("DownloadUrl");
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public static String getInfo(String name) {
        return getInfo(name, "latest");
    }

    public static String getInfo(String name, String version) {
        if (version == null) version = "latest";

        // Getting data from database
        try (PreparedStatement statement = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)
                .prepareStatement("SELECT * FROM ? WHERE Version=?;")) {
            statement.setString(1, name.toLowerCase(Locale.ROOT));
            statement.setString(2, version);
            ResultSet resultSet = statement.executeQuery();

            // Checking that it actually exists
            if (resultSet == null || !resultSet.next())
                return "null";

            String fundingUrl = resultSet.getString("FundingUrl");
            if (fundingUrl == null) fundingUrl = "null";

            // Returning wanted value
            return "{" + resultSet.getString("Version") + ";" + resultSet.getString("DownloadUrl") + ";" + resultSet.getString("SupportedVersions") + ";" + resultSet.getString("Authors") + ";" + fundingUrl + "}";
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public static String getFunding(String name) {
        return getFunding(name, "latest");
    }

    public static String getFunding(String name, String version) {
        if (version == null) version = "latest";

        // Getting data from database
        try (PreparedStatement statement = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)
                .prepareStatement("SELECT FundingUrl FROM ? WHERE Version=?;")) {
            statement.setString(1, name.toLowerCase(Locale.ROOT));
            statement.setString(2, version);
            ResultSet resultSet = statement.executeQuery();

            // Checking that it actually exists
            if (resultSet == null || !resultSet.next())
                return "null";

            String fundingUrl = resultSet.getString("FundingUrl");
            if (fundingUrl == null) fundingUrl = "null";

            // Returning wanted value
            return fundingUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

}
