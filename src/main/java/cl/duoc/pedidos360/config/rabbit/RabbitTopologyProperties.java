package cl.duoc.pedidos360.config.rabbit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pedidos360.rabbitmq")
public class RabbitTopologyProperties {

    private String exchange;
    private String queue;
    private String routingKey;
    private String deadLetterExchange;
    private String deadLetterQueue;
    private String deadLetterRoutingKey;
    private final Retry retry = new Retry();

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public String getQueue() { return queue; }
    public void setQueue(String queue) { this.queue = queue; }
    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
    public String getDeadLetterExchange() { return deadLetterExchange; }
    public void setDeadLetterExchange(String deadLetterExchange) { this.deadLetterExchange = deadLetterExchange; }
    public String getDeadLetterQueue() { return deadLetterQueue; }
    public void setDeadLetterQueue(String deadLetterQueue) { this.deadLetterQueue = deadLetterQueue; }
    public String getDeadLetterRoutingKey() { return deadLetterRoutingKey; }
    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) { this.deadLetterRoutingKey = deadLetterRoutingKey; }
    public Retry getRetry() { return retry; }

    public static class Retry {
        private int maxAttempts;
        private long initialInterval;
        private double multiplier;
        private long maxInterval;

        public int getMaxAttempts() { return maxAttempts; }
        public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
        public long getInitialInterval() { return initialInterval; }
        public void setInitialInterval(long initialInterval) { this.initialInterval = initialInterval; }
        public double getMultiplier() { return multiplier; }
        public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
        public long getMaxInterval() { return maxInterval; }
        public void setMaxInterval(long maxInterval) { this.maxInterval = maxInterval; }
    }
}
