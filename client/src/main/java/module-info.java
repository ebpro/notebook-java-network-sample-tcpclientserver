module fr.univtln.bruno.samples.network.client {
    requires java.base;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires java.logging;
    requires java.net.http;
    requires static lombok;

    exports fr.univtln.bruno.samples.network.client;
    opens fr.univtln.bruno.samples.network.client to ch.qos.logback.classic, ch.qos.logback.core;
}
