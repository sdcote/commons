package coyote.commons;

import java.util.Map;


/**
 * Class with static assertions for data validations/
 */
public final class Assert {

  /**
   * Private Constructor so no instances of this class
   */
  private Assert() {}




  public static void argumentIsNotNull( final Object arg, final String msg ) throws IllegalArgumentException {
    if ( arg == null ) {
      throw new IllegalArgumentException( msg );
    }
  }




  public static void argumentIsPositive( final int number, final String msg ) {
    if ( number <= 0 ) {
      throw new IllegalArgumentException( msg );
    }
  }




  public static void argumentIsValid( final boolean validExpr, final String msg ) {
    if ( !validExpr ) {
      throw new IllegalArgumentException( msg );
    }
  }




  public static <K, V> void mapIsNotNullOrEmpty( final Map<K, V> map, final String msg ) {
    if ( ( map == null ) || map.isEmpty() ) {
      throw new IllegalArgumentException( msg );
    }
  }

}
