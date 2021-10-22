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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class SpMergeStagedProductsTests extends AbstractDataDrivenTest {

    @BeforeAll
    public static void triggerMerge() throws SQLException, ClassNotFoundException {
        CallableStatement cs = getOrCreateConnection().prepareCall("{call merge_staged_products()}");
        cs.execute();
    }

    @Test
    public void closedStatusTest() throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getOrCreateConnection().prepareStatement("select count(*) from products where status=?");
        ps.setString(1, "CLOSED");
        ResultSet rs = ps.executeQuery();

        rs.next();

        assertEquals(EXPECTED_PRODUCTS_STAGING, rs.getInt(1), "Expected that the number of closed rows would be the same as the number of duplicate entries in staging."); //test the expected number of rows that have been "closed"
    }

    @Test
    public void closeDateTest() throws SQLException, ClassNotFoundException {
        PreparedStatement ps2 = getOrCreateConnection().prepareStatement("select close_date from products where status=? limit 1");
        ps2.setString(1, "CLOSED");
        ResultSet rs2 = ps2.executeQuery();

        rs2.next();

        Date date = rs2.getDate(1);

        assertNotNull(date, "Expected to have a close date when a new entry is created for the same dimension.");
    }

    @Test
    public void clearProductsStagingTest() throws SQLException, ClassNotFoundException {
        PreparedStatement ps3 = getOrCreateConnection().prepareStatement("select count(*) from products_staging");
        ResultSet rs3 = ps3.executeQuery();

        rs3.next();

        assertEquals(0, rs3.getInt(1), "Expected that the staging would be cleared after the data is merged.");
    }
}
