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

import org.junit.jupiter.api.Test;

import java.sql.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpGenOrdersTests extends AbstractDataDrivenTest {

    @Test
    public void genOrdersTest() throws SQLException, ClassNotFoundException {
        clearTable("orders");

        String email = getRandomEmail();

        int numOfOrders = 50;
        CallableStatement cs = getOrCreateConnection().prepareCall("{call gen_orders(?, ?)}");
        cs.setString(1, email);
        cs.setInt(2, numOfOrders);
        cs.execute();

        PreparedStatement ps = getOrCreateConnection().prepareStatement("select count(*) from orders where email=?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        rs.next();

        int count = rs.getInt(1);

        assertEquals(numOfOrders, count, "The number of orders created should be the same as the parameter");
    }

    private String getRandomEmail() throws SQLException, ClassNotFoundException {
        Statement stmt = getOrCreateConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select email from users order by random() limit 1");
        rs.next();

        return rs.getString(1);
    }
}
