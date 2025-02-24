module fr.univtln.bruno.samples.network.server {
    requires java.base;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires java.logging;
    requires java.net.http;
    requires static lombok;

    opens fr.univtln.bruno.samples.network.server to ch.qos.logback.classic, ch.qos.logback.core;
    exports fr.univtln.bruno.samples.network.server;
}
