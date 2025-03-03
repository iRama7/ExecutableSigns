package me.rama;

import org.bukkit.block.Block;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final ExecutableSigns main;
    private Connection connection;

    public Database(ExecutableSigns main, String path) throws SQLException {
        this.main = main;

        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS signs
                    (location TEXT PRIMARY KEY,
                     command TEXT NOT NULL,
                     executor TEXT NOT NULL)
                    """);
        }

    }

    public void addSign(Sign sign) throws SQLException {

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO signs (location, command, executor) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, sign.getLocationString());
            preparedStatement.setString(2, sign.getCommand());
            preparedStatement.setString(3, sign.getExecutor());
            preparedStatement.executeUpdate();
        }

    }

    public void removeSign(Sign sign) throws SQLException {
        String location = sign.getLocationString();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM signs WHERE location = ?");
        statement.setString(1, location);
        statement.executeUpdate();
        statement.close();
    }

    public List<Sign> getSigns() throws SQLException {

        List<Sign> signs = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT * FROM signs");

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
                String location_string = resultSet.getString("location");
                Block block = main.serializeLocation(location_string);
                String command = resultSet.getString("command");
                String executor = resultSet.getString("executor");

                Sign sign = new Sign(command, executor, block, main);
                signs.add(sign);

        }

        return signs;

    }

}
