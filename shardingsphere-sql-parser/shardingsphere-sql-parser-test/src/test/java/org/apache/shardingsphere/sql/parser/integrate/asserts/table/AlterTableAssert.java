/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.sql.parser.integrate.asserts.table;

import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.sql.parser.integrate.asserts.SQLStatementAssertMessage;
import org.apache.shardingsphere.sql.parser.integrate.jaxb.table.ExpectedAlterTable;
import org.apache.shardingsphere.sql.parser.integrate.jaxb.token.ExpectedColumnDefinition;
import org.apache.shardingsphere.sql.parser.integrate.jaxb.token.ExpectedColumnPosition;
import org.apache.shardingsphere.sql.parser.sql.segment.ddl.column.ColumnDefinitionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.ddl.column.position.ColumnAfterPositionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.ddl.column.position.ColumnPositionSegment;
import org.apache.shardingsphere.sql.parser.sql.statement.ddl.AlterTableStatement;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlterTableAssert {
    
    /**
     * Assert actual alter table statement is correct with expected alter table.
     * 
     * @param assertMessage assert message
     * @param actual actual alter table statement
     * @param expected expected alter table
     */
    public static void assertIs(final SQLStatementAssertMessage assertMessage, final AlterTableStatement actual, final ExpectedAlterTable expected) {
        assertThat(assertMessage.getText("Drop names assertion error: "), Joiner.on(",").join(actual.getDroppedColumnNames()), is(expected.getDropColumns()));
        assertAddColumns(assertMessage, actual, expected.getAddColumns());
        assertColumnPositions(assertMessage, actual.getChangedPositionColumns(), expected.getPositionChangedColumns());
    }
    
    private static void assertAddColumns(final SQLStatementAssertMessage assertMessage, final AlterTableStatement actual, final List<ExpectedColumnDefinition> expected) {
        assertThat(assertMessage.getText("Add column size error: "), actual.getAddedColumnDefinitions().size(), is(expected.size()));
        int count = 0;
        for (ColumnDefinitionSegment each : actual.getAddedColumnDefinitions()) {
            assertColumnDefinition(assertMessage, each, expected.get(count));
            count++;
        }
    }
    
    private static void assertColumnDefinition(final SQLStatementAssertMessage assertMessage, final ColumnDefinitionSegment actual, final ExpectedColumnDefinition expected) {
        assertThat(assertMessage.getText("Column name assertion error: "), actual.getColumnName(), is(expected.getName()));
        assertThat(assertMessage.getText("Column " + actual.getColumnName() + " type assertion error: "), actual.getDataType(), is(expected.getType()));
    }
    
    private static void assertColumnPositions(final SQLStatementAssertMessage assertMessage, final Collection<ColumnPositionSegment> actual, final List<ExpectedColumnPosition> expected) {
        if (null == expected) {
            return;
        }
        assertThat(assertMessage.getText("Alter column position size error: "), actual.size(), is(expected.size()));
        int count = 0;
        for (ColumnPositionSegment each : actual) {
            assertColumnPosition(assertMessage, each, expected.get(count));
            count++;
        }
    }
    
    private static void assertColumnPosition(final SQLStatementAssertMessage assertMessage, final ColumnPositionSegment actual, final ExpectedColumnPosition expected) {
        assertThat(assertMessage.getText("Alter column position name assertion error: "), actual.getColumnName(), is(expected.getColumnName()));
        assertThat(assertMessage.getText("Alter column [" + actual.getColumnName() + "]position startIndex assertion error: "), actual.getStartIndex(), is(expected.getStartIndex()));
        if (actual instanceof ColumnAfterPositionSegment) {
            assertThat(assertMessage.getText("Alter column [" + actual.getColumnName() + "]position afterColumnName assertion error: "), 
                    ((ColumnAfterPositionSegment) actual).getAfterColumnName(), is(expected.getAfterColumn()));
        }
    }
}
