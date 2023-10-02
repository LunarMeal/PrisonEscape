package com.lunarmeal.prisonescape.Utils;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.lunarmeal.prisonescape.PrisonData;
import org.bukkit.Location;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static com.bekvon.bukkit.residence.api.ResidenceApi.getResidenceManager;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager(String databasePath) {
        setupDatabase(databasePath);
    }

    private void setupDatabase(String databasePath) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        try {
            // 创建表格
            PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS prisonData (prisonID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "prisonName TEXT NOT NULL," +
                            "resName TEXT NOT NULL," +
                            "prisonOwner TEXT NOT NULL," +
                            "prisonSpawn TEXT NOT NULL," +
                            "counter FLOAT NOT NULL," +
                            "rankingList TEXT NOT NULL,"+
                            "escapeTime INTEGER NOT NULL)");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertData(PrisonData prisonData) {
        int prisonID = -1;
        try {
            // 插入数据
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO prisonData (prisonName,resName,prisonOwner,prisonSpawn,counter,rankingList,escapeTime) VALUES (?,?,?,?,?,?,?)");
            statement.setString(1, prisonData.getPrisonName());
            statement.setString(2, prisonData.getResName());
            statement.setString(3, prisonData.getPrisonOwner());
            Location loc = prisonData.getPrisonSpawn();
            String strLoc = loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
            statement.setString(4, strLoc);
            statement.setFloat(5, prisonData.getCounter());
            statement.setString(6, prisonData.getRankingList().toString());
            statement.setInt(7, prisonData.getEscapeTime());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if(resultSet.next()){
                prisonID = (int)resultSet.getObject(1);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prisonID;
    }

    public void updateDataWithPname(PrisonData prisonData) {
        try {
            // 更新数据
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE prisonData SET resName = ?,prisonOwner = ?,prisonSpawn = ?,counter = ?,rankingList = ?,escapeTime = ? WHERE prisonName = ?");
            statement.setString(1, prisonData.getResName());
            statement.setString(2, prisonData.getPrisonOwner());
            Location loc = prisonData.getPrisonSpawn();
            statement.setString(3, LocationSerialize.LocToStr(loc));
            statement.setFloat(4, prisonData.getCounter());
            statement.setString(5, prisonData.getRankingList().toString());
            statement.setInt(6, prisonData.getEscapeTime());
            statement.setString(7, prisonData.getPrisonName());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDataWithRname(PrisonData prisonData) {
        try {
            // 更新数据
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE prisonData SET prisonName = ?,prisonOwner = ?,prisonSpawn = ?,counter = ?,rankingList = ?,escapeTime = ? WHERE resName = ?");
            statement.setString(1, prisonData.getPrisonName());
            statement.setString(2, prisonData.getPrisonOwner());
            Location loc = prisonData.getPrisonSpawn();
            statement.setString(3, LocationSerialize.LocToStr(loc));
            statement.setFloat(4, prisonData.getCounter());
            statement.setString(5, prisonData.getRankingList().toString());
            statement.setInt(6, prisonData.getEscapeTime());
            statement.setString(7, prisonData.getResName());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PrisonData retrieveData(String prisonName) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // 查询数据
            statement = connection.prepareStatement(
                    "SELECT * FROM prisonData WHERE prisonName = ?");
            statement.setString(1, prisonName);
            resultSet = statement.executeQuery();
            PrisonData prisonData = null;

            while (resultSet.next()) {
                String resName = resultSet.getString("resName");
                ClaimedResidence res = getResidenceManager().getByName(resName);
                if (res == null)
                    return null;
                String strLoc = resultSet.getString("prisonSpawn");
                prisonData = new PrisonData(resultSet.getString("prisonName"), resName,
                        LocationSerialize.StrToLoc(strLoc), res.getPermissions().getFlags());
                prisonData.setPrisonID(resultSet.getInt("prisonID"));
                prisonData.setCounter(resultSet.getInt("counter"));
                prisonData.setPrisonOwner(resultSet.getString("prisonOwner"));

                // 添加排行榜单
                String strings = resultSet.getString("rankingList");
                strings = strings.substring(1, strings.length() - 1);
                String[] strs = strings.split(",");
                for (String str : strs) {
                    String playerName = str.split("=")[0];
                    Integer timeScore = Integer.parseInt(str.split("=")[1]);;
                    prisonData.getRankingList().put(playerName, timeScore);
                }

                prisonData.setEscapeTime(resultSet.getInt("escapeTime"));
            }

            return prisonData;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public Map<String, PrisonData> retrieveAllData() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(
                    "SELECT * FROM prisonData");
            // 查询数据
            resultSet = statement.executeQuery();
            Map<String, PrisonData> prisonDataList = new HashMap<>();

            while (resultSet.next()) {
                String prisonName = resultSet.getString("prisonName");
                String resName = resultSet.getString("resName");
                ClaimedResidence res = getResidenceManager().getByName(resName);
                if (res == null)
                    return null;
                String strLoc = resultSet.getString("prisonSpawn");
                PrisonData prisonData = new PrisonData(prisonName, resName, LocationSerialize.StrToLoc(strLoc), res.getPermissions().getFlags());
                prisonData.setPrisonID(resultSet.getInt("prisonID"));
                prisonData.setPrisonOwner(resultSet.getString("prisonOwner"));
                prisonData.setCounter(resultSet.getFloat("counter"));
                //添加排行榜单
                String strings = resultSet.getString("rankingList");
                strings = strings.substring(1, strings.length() - 1);
                String[] strs = strings.split(",");
                for (String str : strs) {
                    String playerName = str.split("=")[0];
                    Integer timeScore = Integer.parseInt(str.split("=")[1]);
                    prisonData.getRankingList().put(playerName, timeScore);
                }
                prisonData.setEscapeTime(resultSet.getInt("escapeTime"));

                prisonDataList.put(prisonName, prisonData);
            }

            return prisonDataList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public void deleteData(String prisonName) {
        try {
            // 删除数据
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM prisonData WHERE prisonName = ?");
            statement.setString(1, prisonName);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
