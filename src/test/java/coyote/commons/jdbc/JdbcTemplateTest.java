package coyote.commons.jdbc;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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

    SimpleDateFormat dateformat = new SimpleDateFormat( "yyyy/MM/dd" );

    // use regular JDBC to populate a small database as an illustration of the
    // efficiencies gained with the JDBC helper classes being tested

    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pstmt;

    try {
      conn = ds.getConnection( "sa", "" );
      conn.setAutoCommit( true );

      stmt = conn.createStatement();

      String sql = "CREATE TABLE EMPLOYEE (emp_no INTEGER not NULL, birth_date DATE, first_name VARCHAR(32), last_name VARCHAR(32), gender VARCHAR(32), hire_date DATE, PRIMARY KEY ( emp_no ))";

      stmt.executeUpdate( sql );

      String query = "insert into EMPLOYEE(emp_no, birth_date, first_name,last_name,gender,hire_date) values(?, ?, ?, ?, ?, ?)";
      pstmt = conn.prepareStatement( query );

      // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
      // Create employees in the test database
      pstmt.setInt( 1, 1001 );
      pstmt.setDate( 2, new Date( dateformat.parse( "1974/06/22" ).getTime() ) );
      pstmt.setString( 3, "Robert" );
      pstmt.setString( 4, "White" );
      pstmt.setString( 5, "Male" );
      pstmt.setDate( 6, new Date( dateformat.parse( "2004/01/04" ).getTime() ) );
      pstmt.executeUpdate();

      pstmt.setInt( 1, 1002 );
      pstmt.setDate( 2, new Date( dateformat.parse( "1974/10/06" ).getTime() ) );
      pstmt.setString( 3, "Alice" );
      pstmt.setString( 4, "McKensy" );
      pstmt.setString( 5, "Female" );
      pstmt.setDate( 6, new Date( dateformat.parse( "2003/06/12" ).getTime() ) );
      pstmt.executeUpdate();

      // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /

    } catch ( SQLException se ) {
      se.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    finally {
      //finally block used to close resources
      try {
        if ( stmt != null )
          conn.close();
      } catch ( SQLException se ) {}// do nothing
      try {
        if ( conn != null )
          conn.close();
      } catch ( SQLException se ) {}// do nothing
    }

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




  @Test
  public void queryTests() {

    ResultMapper<Employee> employeeDataMapper = new ResultMapper<Employee>() {
      @Override
      public Employee map( ExtendedResultSet rs ) throws SQLException {

        //System.out.println( "Pos:" + rs.getRow() );

        // Get an integer from the result set.
        Integer empNo = rs.getNullableInt( "emp_no" );
        Date birthDate = rs.getDate( "birth_date" );
        String firstName = rs.getString( "first_name" );
        String lastName = rs.getString( "last_name" );
        Gender gender = Gender.toGender( rs.getString( "gender" ) );
        Date hireDate = rs.getDate( "hire_date" );
        return new Employee( empNo, birthDate, firstName, lastName, gender, hireDate );
      }
    };

    JdbcTemplate template = new JdbcTemplate( ds );

    List<Employee> emps = template.query( "SELECT * FROM EMPLOYEE LIMIT ?, ?;", Arrays.<Object> asList( 0, 5 ), employeeDataMapper );
    // System.out.println( "Found " + emps.size() + " records" );
    assertTrue( emps.size() == 2 );

    emps = template.query( "SELECT * FROM EMPLOYEE WHERE emp_no= ?;", Arrays.<Object> asList( 1001 ), employeeDataMapper );
    // System.out.println( "Found " + emps.size() + " records" );
    // System.out.println( emps.get( 0 ) );
    assertTrue( emps.size() == 1 );

  }




  @Test
  public void updateTest() {
    JdbcTemplate template = new JdbcTemplate( ds );
    int count = template.update( "UPDATE EMPLOYEE SET first_name=? WHERE emp_no=?;", Arrays.<Object> asList( "Bob", 1001 ) );
    assertTrue( count == 1 );
  }

}




// 
//
//
class Employee {
  Integer empNo;
  Date birthDate;
  String firstName;
  String lastName;
  Gender gender;
  Date hireDate;




  public Employee( Integer empNo, Date birthDate, String firstName, String lastName, Gender gender, Date hireDate ) {
    this.empNo = empNo;
    this.birthDate = birthDate;
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
    this.hireDate = hireDate;
  }




  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer( "Employee:" );
    buffer.append( empNo );
    buffer.append( " Name:" );
    buffer.append( firstName );
    buffer.append( " " );
    buffer.append( lastName );
    buffer.append( " Hired:" );
    buffer.append( hireDate );
    return buffer.toString();
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
    // convert M to Male, F to Female, etc.
    return new Gender( "Unknown" );
  }
}
