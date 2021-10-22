/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.rsdevops.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.*;

public abstract class AbstractDataDrivenTest {
    private static Connection conn = null;

    protected static int EXPECTED_USERS = 1000;
    protected static int EXPECTED_PRODUCTS = 500;
    protected static int EXPECTED_PRODUCTS_STAGING = 100;

    @BeforeAll
    public static void setup() throws SQLException, ClassNotFoundException {
        processTestDataset("users", "email,first_name,last_name", EXPECTED_USERS, "users.csv");
        processTestDataset("products", "product_name,price", EXPECTED_PRODUCTS, "products.csv");
        processTestDataset("products_staging", "product_name,price", EXPECTED_PRODUCTS_STAGING, "products_staging.csv");
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        conn.close();
    }

    private static void processTestDataset(String table, String columnsList, int expectedCount, String dataFile) throws SQLException, ClassNotFoundException {
        if (!isTestDataValid(table, expectedCount))
            reloadData(table, columnsList, dataFile);
    }

    private static boolean isTestDataValid(String table, int expectedCount) throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getOrCreateConnection().prepareStatement("select count(*) from "+table);
        ResultSet rs = ps.executeQuery();
        rs.next();

        int count = rs.getInt(1);

        return count > 0 && count == expectedCount;
    }

    protected static void clearTable(String table) throws SQLException, ClassNotFoundException {
        Statement stmt = getOrCreateConnection().createStatement();
        stmt.execute("truncate "+table);
        stmt.close();
    }

    private static void reloadData(String table, String columnsList, String dataFile) throws SQLException, ClassNotFoundException {
        clearTable(table);
        Statement stmt = getOrCreateConnection().createStatement();
        stmt.execute("copy "+table+"("+columnsList+") from '"+getDataLocation(dataFile)+"' csv iam_role '"+getDataLoadIAMRole()+"' ignoreheader 1");
        stmt.close();
    }

    private static String getDataLocation(String dataFile) {
        return "s3://"+System.getenv("TEST_DATA_S3_BUCKET")+"/"+dataFile;
    }

    private static String getDataLoadIAMRole() {
        return System.getenv("TEST_REDSHIFT_IAM_ROLE");
    }

    protected static Connection getOrCreateConnection() throws SQLException, ClassNotFoundException {
        if (conn == null || conn.isClosed()) {
            conn = ConnectionManager.createConnection();
        }

        return conn;
    }
}
