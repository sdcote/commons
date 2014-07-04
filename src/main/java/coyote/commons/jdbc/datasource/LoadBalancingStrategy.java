package coyote.commons.jdbc.datasource;

public interface LoadBalancingStrategy {

  int next();
}
