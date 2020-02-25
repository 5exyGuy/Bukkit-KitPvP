package eu.spikedpvp.kitpvp.database;

import com.google.inject.Inject;
import eu.spikedpvp.kitpvp.KitPvP;
import eu.spikedpvp.kitpvp.utilities.Console;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;

public class SQL {

    public enum LogicGate {
        EQUALS("="),
        NOT_EQUAL("!=");

        private String s;

        LogicGate(String s) {
            this.s = s;
        }

        public String get() {
            return s;
        }
    }

    private KitPvP plugin;

    @Inject
    public SQL(KitPvP plugin) {
        this.plugin = plugin;
    }

    public boolean tableExists(final String tableName) {
        if (tableName.isEmpty()) {
            return false;
        }

        try {
            final Connection connection = plugin.getMySQL().getConnection();
            if (connection == null) {
                return false;
            }

            final DatabaseMetaData metadata = connection.getMetaData();
            if (metadata == null) {
                return false;
            }

            final ResultSet rs = metadata.getTables(null, null, tableName, null);
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            Console.sendError(e.getMessage());
        }
        return false;
    }

    public boolean insertData(final JSONObject columns, final String tableName) {
        StringBuilder tableColumns = new StringBuilder();
        StringBuilder tableValues = new StringBuilder();

        for (Object key : columns.keySet()) {
            Object value = columns.get(key);
            tableColumns.append(key).append(",");
            tableValues.append("'").append(value).append("',");
        }

        if ((tableColumns.length() == 0) || (tableValues.length() == 0) || tableName.isEmpty()) {
            return false;
        }

        tableColumns = new StringBuilder(tableColumns.substring(0, tableColumns.length() - 1));
        tableValues = new StringBuilder(tableValues.substring(0, tableValues.length() - 1));

        Console.sendInfo("INSERT INTO " + tableName + " (" + tableColumns + ") VALUES (" + tableValues + ");");

        return plugin.getMySQL().update("INSERT INTO " + tableName + " (" + tableColumns + ") VALUES (" + tableValues + ");");
    }

    public boolean deleteData(final JSONObject conditions, final LogicGate logicGate, final String tableName) {
        StringBuilder tableConditions = new StringBuilder();

        for (Object key : conditions.keySet()) {
            Object value = conditions.get(key);

            if (value instanceof String) {
                tableConditions.append(key).append(logicGate.get()).append("'").append(value).append("' AND ");
            } else {
                tableConditions.append(key).append(logicGate.get()).append(value).append(" AND ");
            }
        }

        if (conditions.isEmpty() || tableName.isEmpty()) {
            return false;
        }

        tableConditions = new StringBuilder(tableConditions.substring(0, tableConditions.length() - 5));

        return plugin.getMySQL().update("DELETE FROM " + tableName + " WHERE " + tableConditions  + ";");
    }

    public boolean exists(final JSONObject conditions, final String tableName) {
        StringBuilder tableConditions = new StringBuilder();

        for (Object key : conditions.keySet()) {
            Object value = conditions.get(key);

            if (value instanceof String) {
                tableConditions.append(key).append("='").append(value).append("' AND ");
            } else {
                tableConditions.append(key).append("=").append(value).append(" AND ");
            }

        }

        if ((tableConditions.length() == 0) || tableName.isEmpty()) {
            return false;
        }

        tableConditions = new StringBuilder(tableConditions.substring(0, tableConditions.length() - 5));

        try {
            final ResultSet rs = plugin.getMySQL().query("SELECT * FROM " + tableName + " WHERE " + tableConditions + ";");
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            Console.sendError(e.getMessage());
        }

        return false;
    }

    public boolean deleteTable(final String tableName) {
        return plugin.getMySQL().update("DROP TABLE " + tableName + ";");
    }

    public boolean truncateTable(final String tableName) {
        return plugin.getMySQL().update("TRUNCATE TABLE " + tableName + ";");
    }

    public boolean createTable(final String tableName, JSONArray columns) {
        StringBuilder tableColumns = new StringBuilder();

        for (Object column : columns.toArray()) {
            tableColumns.append(column).append(",");
        }

        if ((tableColumns.length() == 0) || tableName.isEmpty()) {
            return false;
        }

        tableColumns = new StringBuilder(tableColumns.substring(0, tableColumns.length() - 1));

        return !tableExists(tableName) && plugin.getMySQL().update("CREATE TABLE " + tableName + " (" + tableColumns + ");");
    }

    public boolean upsert(final JSONObject columns, final JSONObject conditions, final LogicGate logicGate, final String tableName) {
        try {
            final ResultSet rs = get(conditions, logicGate, tableName);
            if (rs.next()) {
                set(columns, conditions, logicGate, tableName);
            } else {
                insertData(columns, tableName);
            }
        } catch (Exception e) {
            Console.sendError(e.getMessage());
        }
        return false;
    }

    public boolean set(final JSONObject columns, final JSONObject conditions, final LogicGate logicGate, final String tableName) {
        StringBuilder tableColumns = new StringBuilder();
        StringBuilder tableConditions = new StringBuilder();

        for (Object key : columns.keySet()) {
            Object value = columns.get(key);

            if (value instanceof String) {
                tableColumns.append(key).append("='").append(value).append("',");
            } else {
                tableColumns.append(key).append("=").append(value).append(",");
            }
        }

        for (Object key : conditions.keySet()) {
            Object value = conditions.get(key);

            if (value instanceof String) {
                tableConditions.append(key).append(logicGate.get()).append("'").append(value).append("' AND ");
            } else {
                tableConditions.append(key).append(logicGate.get()).append(value).append(" AND ");
            }
        }

        if ((tableConditions.length() == 0) || (tableColumns.length() == 0) || tableName.isEmpty()) {
            return false;
        }

        tableColumns = new StringBuilder(tableColumns.substring(0, tableColumns.length() - 1));
        tableConditions = new StringBuilder(tableConditions.substring(0, tableConditions.length() - 5));

        return plugin.getMySQL().update("UPDATE " + tableName + " SET " + tableColumns + " WHERE " + tableConditions + ";");
    }

    public Object get(final String columnName, final JSONObject conditions, final LogicGate logicGate, final String tableName) {
        StringBuilder tableConditions = new StringBuilder();

        for (Object key : conditions.keySet()) {
            Object value = conditions.get(key);

            if (value instanceof String) {
                tableConditions.append(key).append(logicGate.get()).append("'").append(value).append("' AND ");
            } else {
                tableConditions.append(key).append(logicGate.get()).append(value).append(" AND ");
            }
        }

        if ((tableConditions.length() == 0) || columnName.isEmpty() || tableName.isEmpty()) {
            return false;
        }

        tableConditions = new StringBuilder(tableConditions.substring(0, tableConditions.length() - 5));

        try {
            final ResultSet rs = plugin.getMySQL().query("SELECT * FROM " + tableName + " WHERE " + tableConditions + ";");
            if (rs.next()) {
                return rs.getObject(columnName);
            }
        } catch (Exception e) {
            Console.sendError(e.getMessage());
        }
        return null;
    }

    public ResultSet get(final JSONObject conditions, final LogicGate logicGate, final String tableName) {
        StringBuilder tableConditions = new StringBuilder();

        for (Object key : conditions.keySet()) {
            Object value = conditions.get(key);

            if (value instanceof String) {
                tableConditions.append(key).append(logicGate.get()).append("'").append(value).append("' AND ");
            } else {
                tableConditions.append(key).append(logicGate.get()).append(value).append(" AND ");
            }
        }

        if ((tableConditions.length() == 0) || tableName.isEmpty()) {
            return null;
        }

        tableConditions = new StringBuilder(tableConditions.substring(0, tableConditions.length() - 5));

        return plugin.getMySQL().query("SELECT * FROM " + tableName + " WHERE " + tableConditions + ";");
    }

    public ArrayList<Object> getList(final String columnName, final JSONObject conditions, final LogicGate logicGate, final String tableName) {
        final ArrayList<Object> array = new ArrayList<>();
        StringBuilder tableConditions = new StringBuilder();

        for (Object key : conditions.keySet()) {
            Object value = conditions.get(key);

            if (value instanceof String) {
                tableConditions.append(key).append(logicGate.get()).append("'").append(value).append("' AND ");
            } else {
                tableConditions.append(key).append(logicGate.get()).append(value).append(" AND ");
            }
        }

        if ((tableConditions.length() == 0) || columnName.isEmpty() || tableName.isEmpty()) {
            return null;
        }

        tableConditions = new StringBuilder(tableConditions.substring(0, tableConditions.length() - 5));

        try {
            final ResultSet rs = plugin.getMySQL().query("SELECT * FROM " + tableName + " WHERE " + tableConditions + ";");
            while (rs.next()) {
                array.add(rs.getObject(columnName));
            }
        } catch (Exception e) {
            Console.sendError(e.getMessage());
        }
        return array;
    }

    public ArrayList<ResultSet> getList(final JSONObject conditions, final LogicGate logicGate, final String tableName) {
        final ArrayList<ResultSet> array = new ArrayList<>();
        StringBuilder tableConditions = new StringBuilder();

        for (Object key : conditions.keySet()) {
            Object value = conditions.get(key);

            if (value instanceof String) {
                tableConditions.append(key).append(logicGate.get()).append("'").append(value).append("' AND ");
            } else {
                tableConditions.append(key).append(logicGate.get()).append(value).append(" AND ");
            }
        }

        if ((tableConditions.length() == 0) || tableName.isEmpty()) {
            return null;
        }

        tableConditions = new StringBuilder(tableConditions.substring(0, tableConditions.length() - 5));

        try {
            final ResultSet rs = plugin.getMySQL().query("SELECT * FROM " + tableName + " WHERE " + tableConditions + ";");
            while (rs.next()) {
                array.add(rs);
            }
        } catch (Exception e) {
            Console.sendError(e.getMessage());
        }
        return array;
    }

    public int countRows(final String tableName) {
        int i = 0;

        if (tableName.isEmpty()) {
            return i;
        }

        final ResultSet rs = plugin.getMySQL().query("SELECT * FROM " + tableName + ";");

        try {
            while (rs.next()) {
                ++i;
            }
        } catch (Exception e) {
            Console.sendError(e.getMessage());
        }

        return i;
    }

}
