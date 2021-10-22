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

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String DEFAULT_JDBC_DRIVER_CLASS = "com.amazon.redshift.jdbc42.Driver";

    private static BoneCP dataSource;

    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        if (dataSource == null) {
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(getUrl());
            config.setUsername(getUser());
            config.setPassword(getPassword());
            config.setMinConnectionsPerPartition(2);
            config.setLazyInit(true);
            dataSource = new BoneCP(config);
        }

        return dataSource.getConnection();
//
//        Class.forName(getDriverClassName());
//
//        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }

    private static String getDriverClassName() {
        return DEFAULT_JDBC_DRIVER_CLASS;
    }

    private static String getUrl() {
        return System.getenv("TEST_JDBC_URL");
    }

    private static String getUser() {
        return System.getenv("TEST_JDBC_USER");
    }

    private static String getPassword() {
        return System.getenv("TEST_JDBC_PASSWORD");
    }
}
