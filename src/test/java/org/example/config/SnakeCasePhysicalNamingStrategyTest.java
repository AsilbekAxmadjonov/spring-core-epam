package org.example.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SnakeCasePhysicalNamingStrategyTest {

    private SnakeCasePhysicalNamingStrategy strategy;
    private JdbcEnvironment jdbcEnvironment;

    @BeforeEach
    void setUp() {
        strategy = new SnakeCasePhysicalNamingStrategy();
        jdbcEnvironment = mock(JdbcEnvironment.class);
    }

    @Test
    void testToPhysicalTableName() {
        Identifier id = Identifier.toIdentifier("MyTableName");
        Identifier result = strategy.toPhysicalTableName(id, jdbcEnvironment);
        assertEquals("my_table_name", result.getText());
    }

    @Test
    void testToPhysicalColumnName() {
        Identifier id = Identifier.toIdentifier("myColumnName");
        Identifier result = strategy.toPhysicalColumnName(id, jdbcEnvironment);
        assertEquals("my_column_name", result.getText());
    }

    @Test
    void testNullIdentifierReturnsNull() {
        assertNull(strategy.toPhysicalTableName(null, jdbcEnvironment));
        assertNull(strategy.toPhysicalColumnName(null, jdbcEnvironment));
    }

    @Test
    void testQuotedIdentifierPreserved() {
        Identifier id = new Identifier("MyColumn", true);
        Identifier result = strategy.toPhysicalColumnName(id, jdbcEnvironment);
        assertEquals("my_column", result.getText());
        assertTrue(result.isQuoted());
    }
}
