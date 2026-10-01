package org.example.backendweride.platform.shared.infraestructure.persistence.jpa.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * A PhysicalNamingStrategy that converts all table and column names to snake_case and pluralizes table names.
 */
public class SnakeCasePhysicalNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    /**
     * Convert the identifier to snake case.
     *
     * @param identifier the original identifier (may be null)
     * @param jdbcEnvironment the JDBC environment (not used)
     * @return an Identifier with snake_case text, or null when input is null
     */
    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(this.toPlural(identifier));
    }

    /**
     * Convert the identifier to snake case.
     *
     * @param identifier the identifier to convert (may be null)
     * @return an Identifier whose text is converted to snake_case, or null if input is null
     */
    private Identifier toSnakeCase(final Identifier identifier) {
        if (identifier == null) return null;

        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        final String newName = identifier.getText()
                .replaceAll(regex, replacement)
                .toLowerCase();
        return Identifier.toIdentifier(newName);
    }

    // Simple pluralization fallback to avoid an external dependency.
    private Identifier toPlural(final Identifier identifier) {
        if (identifier == null) return null;
        String word = identifier.getText();
        String plural = simplePluralize(word);
        return Identifier.toIdentifier(plural);
    }

    private String simplePluralize(String word) {
        if (word == null || word.isEmpty()) return word;
        String lower = word.toLowerCase();
        // basic rules
        if (lower.endsWith("y") && lower.length() > 1 && !isVowel(lower.charAt(lower.length() - 2))) {
            return word.substring(0, word.length() - 1) + "ies";
        }
        if (lower.endsWith("s") || lower.endsWith("x") || lower.endsWith("z") || lower.endsWith("ch") || lower.endsWith("sh")) {
            return word + "es";
        }
        return word + "s";
    }

    private boolean isVowel(char c) {
        return "aeiou".indexOf(Character.toLowerCase(c)) >= 0;
    }

}
