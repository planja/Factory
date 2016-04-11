package factory.db.manager;
//import models.Airports;
//import models.Airport;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Created by Baka on 27.05.2015.
 */
public class DatabaseManager {

    private String url;

    private String driver;
    private String username;
    private String password;

    public DatabaseManager() {

        try {

            Properties properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            this.url = properties.getProperty("jdbc.url");
            this.driver = properties.getProperty("jdbc.driver");
            this.username = properties.getProperty("jdbc.username");
            this.password = properties.getProperty("jdbc.password");
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] argc) throws SQLException {

        DatabaseManager dm = new DatabaseManager();

        System.out.println("" + dm.isUSA("SFO"));

    }

    public boolean isUSA(String code) throws SQLException {

        Connection conn = DriverManager.getConnection(url, username, password);

        try {

            PreparedStatement p = conn.prepareStatement("select country from airports where code_iata like '%" + code + "%'");

            try {

                ResultSet resultSet = p.executeQuery();

                try {

                    if (resultSet.next()) {

                        String country = resultSet.getString("country");

                        if (country.contains("United States")) {

                            return true;
                        }
                    }

                } finally {
                    resultSet.close();
                }
            } finally {
                p.close();
            }

        } finally {
            conn.close();
        }

        return false;
    }

    public String getCodeByAirport(String airport) throws SQLException {

//        if(true){
//
//            return "";
//        }

        Connection conn = DriverManager.getConnection(url, username, password);

        try {

            PreparedStatement p = conn.prepareStatement("select * from airports where airport like '%" + airport + "%'");

            try {

                ResultSet resultSet = p.executeQuery();

                try {

                    if (resultSet.next()) {

                        return resultSet.getString("code_iata");
                    }

                } finally {
                    resultSet.close();
                }
            } finally {
                p.close();
            }

        } finally {
            conn.close();
        }

        return null;
    }

    public String getAirportByCode(String code) throws SQLException {

//        if(true){
//
//            return "";
//        }

        Connection conn = DriverManager.getConnection(url, username, password);

        try {

            PreparedStatement p = conn.prepareStatement("select * from airports where code_iata = ?");
            p.setString(1, code);

            try {

                ResultSet resultSet = p.executeQuery();

                try {

                    if (resultSet.next()) {

                        return resultSet.getString("airport");
                    }

                } finally {
                    resultSet.close();
                }
            } finally {
                p.close();
            }

        } finally {
            conn.close();
        }

        return null;
    }

    public String getCityByCode(String code) throws SQLException {

//         if(true){
//
//            return "";
//        }

        Connection conn = DriverManager.getConnection(url, username, password);

        try {

            PreparedStatement p = conn.prepareStatement("select * from airports where code_iata = ?");
            p.setString(1, code);

            try {

                ResultSet resultSet = p.executeQuery();

                try {

                    if (resultSet.next()) {

                        return resultSet.getString("city");
                    }

                } finally {
                    resultSet.close();
                }
            } finally {
                p.close();
            }

        } finally {
            conn.close();
        }

        return null;
    }

    public String getCodeByCity(String city) throws SQLException {

//        if(true){
//
//            return "";
//        }


        Connection conn = DriverManager.getConnection(url, username, password);

        try {

            PreparedStatement p = conn.prepareStatement("select * from airports where city like '%" + city + "%' and airport like '%Intl%'");

            try {

                ResultSet resultSet = p.executeQuery();

                try {

                    if (resultSet.next()) {

                        return resultSet.getString("code_iata");
                    }

                } finally {
                    resultSet.close();
                }
            } finally {
                p.close();
            }

        } finally {
            conn.close();
        }

        return null;
    }

    public int getTZByCode(String code) throws SQLException {

        if (code == null) {

            return 0;
        }

        Connection conn = DriverManager.getConnection(url, username, password);

        String tz = null;

        try {

            PreparedStatement p = conn.prepareStatement("select timezone from airports where code_iata = '" + code + "';");

            try {

                ResultSet resultSet = p.executeQuery();

                try {

                    if (resultSet.next()) {

                        tz = resultSet.getString("timezone");
                    }

                    if (tz == null) {

                        return 0;
                    }

                } finally {
                    resultSet.close();
                }
            } finally {
                p.close();
            }

        } finally {
            conn.close();
        }

        boolean positive = true;

        if (tz.contains("-")) {

            positive = false;
        }

        tz = tz.replaceAll("-", "").replaceAll("\\+", "");

        String[] tArray = tz.split(":");

        int hours = Integer.parseInt(tArray[0]) * 60 * 60;
        int minutes = Integer.parseInt(tArray[1]) * 60 * 60;

        return positive ? hours + minutes : -hours + minutes;
    }
}
