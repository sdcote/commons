package coyote.commons.jdbc;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 */
public class JdbcTemplateTest {

  // Our in-memory database / datasource
  private static JdbcDataSource ds;




  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    ds = new JdbcDataSource();
    ds.setURL( "jdbc:h2:mem:test;MODE=Oracle;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE" );
    ds.setUser( "sa" );
    ds.setPassword( "" );
  }




  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {}




  /**
   * Test method for {@link coyote.commons.jdbc.JdbcTemplate#JdbcTemplate(javax.sql.DataSource)}.
   */
  @Test
  public void testJdbcTemplate() {
    JdbcTemplate template = new JdbcTemplate( ds );
    assertNotNull( template );
  }




  @SuppressWarnings("unchecked")
  public void anotherTest() {

    // This creates the thing which converts rows in the result set into objects
    DataMapper employeeDataMapper = new DataMapper<Employee>() {
      @Override
      public Employee map( ExtendedResultSet rs ) throws SQLException {
        Integer empNo = rs.getNullableInt( "emp_no" ); // Get an integer from the result set.
                                                       // It returns null when it is SQL NULL.
        Date birthDate = rs.getDate( "birth_date" );
        String firstName = rs.getString( "first_name" );
        String lastName = rs.getString( "last_name" );
        Gender gender = Gender.toGender( rs.getString( "gender" ) );
        Date hireDate = rs.getDate( "hire_date" );
        return new Employee( empNo, birthDate, firstName, lastName, gender, hireDate );
      }
    };

    JdbcTemplate template = new JdbcTemplate( ds );

    List<Employee> tenEmployees = template.query( "SELECT * FROM employees LIMIT ?, ?;", Arrays.<Object> asList( 0, 10 ), employeeDataMapper );

    //Employee bob = template.updateOne( "UPDATE employees SET firstName=? WHERE emp_no=?;", Arrays.<Object> asList( "Bob", 1001 ), employeeDataMapper );

  }




  public void yetAnotherTest() {

  }

}




// 
//
//
class Employee {

  public Employee( Integer empNo, Date birthDate, String firstName, String lastName, Gender gender, Date hireDate ) {
    // TODO Auto-generated constructor stub
  }
}




//
//
//
class Gender {
  String type = null;




  Gender( String type ) {
    this.type = type;
  }




  static Gender toGender( String text ) {
    return new Gender( "Unknown" );
  }
}
