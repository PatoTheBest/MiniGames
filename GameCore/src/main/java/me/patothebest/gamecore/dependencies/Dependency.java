package me.patothebest.gamecore.dependencies;

public enum Dependency {

    // -------------------------------------------- //
    // UNUSED
    // -------------------------------------------- //

    MYSQL_DRIVER("https://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.6/mysql-connector-java-5.1.6.jar", "5.1.6", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"),
    POSTGRESQL_DRIVER("https://repo1.maven.org/maven2/org/postgresql/postgresql/9.4.1212/postgresql-9.4.1212.jar", "9.4.1212", "org.postgresql.ds.PGSimpleDataSource"),
    H2_DRIVER("https://repo1.maven.org/maven2/com/h2database/h2/1.4.193/h2-1.4.193.jar", "1.4.193", "org.h2.Driver"),
    SQLITE_DRIVER("https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.15.1/sqlite-jdbc-3.15.1.jar", "3.15.1", "org.sqlite.JDBC"),
    MONGODB_DRIVER("https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/3.4.1/mongo-java-driver-3.4.1.jar", "3.4.1", "com.mongodb.Mongo"),

    // -------------------------------------------- //
    // GUICE
    // -------------------------------------------- //

    GUICE_ANNOTATIONS("http://central.maven.org/maven2/com/google/code/findbugs/annotations/3.0.0/annotations-3.0.0.jar", "3.0.0", null),
    GUICE_ASSISTENINJECT("http://central.maven.org/maven2/com/google/inject/extensions/guice-assistedinject/4.1.0/guice-assistedinject-4.1.0.jar", "4.1.0", null),
    GUICE_GRAPHER("http://central.maven.org/maven2/com/google/inject/extensions/guice-grapher/4.1.0/guice-grapher-4.1.0.jar", "4.1.0", null),

    // -------------------------------------------- //
    // DATABASE
    // -------------------------------------------- //

    //HIBERNATE_CORE("http://central.maven.org/maven2/org/hibernate/hibernate-core/5.2.8.Final/hibernate-core-5.2.8.Final.jar", "5.2.8.Final.jar", null),
    //HIBERNATE_ANNOTATIONS("http://central.maven.org/maven2/org/hibernate/common/hibernate-commons-annotations/5.0.1.Final/hibernate-commons-annotations-5.0.1.Final.jar", "5.0.1.Final", null),
    //JPA2("http://central.maven.org/maven2/org/hibernate/javax/persistence/hibernate-jpa-2.1-api/1.0.0.Final/hibernate-jpa-2.1-api-1.0.0.Final.jar", "1.0.0.Final", null),
    HIBERNATE("https://www.dropbox.com/s/1kh0oiq8d8cy9yh/hibernate-jpa2-relocated.jar?dl=1", "jpa2-relocated", null),
    JAVAASSIST("http://central.maven.org/maven2/org/javassist/javassist/3.20.0-GA/javassist-3.20.0-GA.jar", "3.20.0-GA", null),
    ANTLR("http://central.maven.org/maven2/antlr/antlr/2.7.7/antlr-2.7.7.jar", "2.7.7", null),
    CLASSMATE("http://central.maven.org/maven2/com/fasterxml/classmate/1.3.0/classmate-1.3.0.jar", "1.3.0", null),
    HIKARICP("https://repo1.maven.org/maven2/com/zaxxer/HikariCP/3.2.0/HikariCP-3.2.0.jar", "3.2.0", "com.zaxxer.hikari.HikariConfig"),
    SLF4J_SIMPLE("https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.9/slf4j-simple-1.7.9.jar", "1.7.9", "org.slf4j.impl.SimpleLoggerFactory"),
    SLF4J_API("https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.9/slf4j-api-1.7.9.jar", "1.7.9", "org.slf4j.helpers.BasicMarkerFactory"),
    JBOSS_LOGGING("http://central.maven.org/maven2/org/jboss/logging/jboss-logging/3.3.0.Final/jboss-logging-3.3.0.Final.jar", "3.3.0.Final", null),
    JBOSS_TRANSACTION_API("http://central.maven.org/maven2/org/jboss/spec/javax/transaction/jboss-transaction-api_1.2_spec/1.0.1.Final/jboss-transaction-api_1.2_spec-1.0.1.Final.jar", "1.0.1.Final", null),
    JBOSS_JANDEX("http://central.maven.org/maven2/org/jboss/jandex/2.0.3.Final/jandex-2.0.3.Final.jar", "2.0.3.Final", null),
    DOM4J("http://central.maven.org/maven2/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar", "1.6.1", null),

    // -------------------------------------------- //
    // NETWORK
    // -------------------------------------------- //

    GUAVA("http://central.maven.org/maven2/com/google/guava/guava/16.0.1/guava-16.0.1.jar", "16.0.1", null),
    GSON("http://central.maven.org/maven2/com/google/code/gson/gson/2.8.0/gson-2.8.0.jar", "2.8.0", null),
    NETTY("https://www.dropbox.com/s/35ef5ushyz5nhe3/netty-all-relocated.jar?dl=1", "all-relocated", null),
    JEDIS("http://central.maven.org/maven2/redis/clients/jedis/2.9.0/jedis-2.9.0.jar", "2.9.0.jar", "redis.clients.jedis.shaded.Jedis"),
    JSR305("http://central.maven.org/maven2/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar", "1.3.9", null),
    APACHE_COMMONS_POOL("http://central.maven.org/maven2/org/apache/commons/commons-pool2/2.4.2/commons-pool2-2.4.2.jar", "2.4.2", null),
    COMMONS_IO("https://repo1.maven.org/maven2/commons-io/commons-io/2.5/commons-io-2.5.jar", "2.5", null),

    // -------------------------------------------- //
    // OTHER
    // -------------------------------------------- //

    FANCIFUL("https://www.dropbox.com/s/evwg5t5hy60hb7e/fanciful-0.4.0-20160727.211139-1.jar?dl=1", "0.4.0", null),
    NETTY_SOCKETIO("https://www.dropbox.com/s/i4ue0sng7wn0kwb/netty-scoketio-relocated-1.7.12.jar?dl=1", "1.7.12", null),
    FASTUTIL("https://libraries.minecraft.net/it/unimi/dsi/fastutil/8.2.1/fastutil-8.2.1.jar", "8.2.1", null)
    ;

    private final String url;
    private final String version;
    private final String testClass;

    Dependency(String url, String version, String testClass) {
        this.url = url;
        this.version = version;
        this.testClass = testClass;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getTestClass() {
        return testClass;
    }
}