package coyote.commons.jdbc.datasource;

/**
 * A {@code LoadBalancingDataSource} balances the traffic coming from the upper
 * layer, redirecting it to a data source randomly or with other strategies 
 * such as round-robin.
 */
public interface LoadBalancingStrategy {

  int next();
}
