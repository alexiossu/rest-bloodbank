/**
 * File: OrderSystemTestSuite.java
 * Course materials (20F) CST 8277
 * (Original Author) Mike Norman
 *
 * @date 2020 10
 *
 * (Modified) @author Zavar Siddiqui
 */
package bloodbank;

import static bloodbank.utility.MyConstants.APPLICATION_API_VERSION;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PREFIX;
import static bloodbank.utility.MyConstants.PERSON_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.ADDRESS_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.BLOOD_DONATION_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import bloodbank.entity.Address;
import bloodbank.entity.Person;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDAddress {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String APPLICATION_CONTEXT_ROOT = "REST-BloodBank";
    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;
    
	private static final String STREET_NUMBER = "1";
	private static final String STREET = "Test";
	private static final String CITY = "Ottawa";
	private static final String PROVINCE = "ON";
	private static final String COUNTRY = "CA";
	private static final String ZIPCODE = "A1B2C3";

    // test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER_PREFIX, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
    }
    
    @Test
    public void test01_all_addresses_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Address> adds = response.readEntity(new GenericType<List<Address>>(){});
        assertThat(adds, is(not(empty())));
        assertThat(adds, hasSize(1));
    }
    
    @Test
    public void test01_all_addresses_norole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
//            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(401));
    }
    
    @Test
    public void test02_all_addresses_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
//            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(401));
    }
    
    @Test
    public void test03_add_addresses_adminrole() throws JsonMappingException, JsonProcessingException {
    	Address address = new Address();
    	address.setAddress( STREET_NUMBER, STREET, CITY, PROVINCE, COUNTRY, ZIPCODE);
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .post(Entity.json(address));
        assertThat(response.getStatus(), is(200));
        
        //get the addresses to confirm it was added
        Response responseGet = webTarget
                //.register(userAuth)
                .register(adminAuth)
                .path(ADDRESS_RESOURCE_NAME)
                .request()
                .get();
        List<Address> adds = responseGet.readEntity(new GenericType<List<Address>>(){});
        assertThat(adds, is(not(empty())));
        assertThat(adds, hasSize(2));
    }
    
    @Test
    public void test04_add_addresses_norole() throws JsonMappingException, JsonProcessingException {
    	Address address = new Address();
    	address.setAddress( STREET_NUMBER, STREET, CITY, PROVINCE, COUNTRY, ZIPCODE);
        Response response = webTarget
            //.register(userAuth)
//            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .post(Entity.json(address));
        assertThat(response.getStatus(), is(401));
    }
    
    @Test
    public void test05_post_addresses_userrole() throws JsonMappingException, JsonProcessingException {
    	Address address = new Address();
    	address.setAddress( STREET_NUMBER, STREET, CITY, PROVINCE, COUNTRY, ZIPCODE);
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .post(Entity.json(address));
        assertThat(response.getStatus(), is(401));
    }
    
    @Test
    public void test06_put_addresses_adminrole() throws JsonMappingException, JsonProcessingException {
    	Address address = new Address();
    	address.setAddress( STREET_NUMBER, STREET, CITY, PROVINCE, COUNTRY, ZIPCODE);
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .put(Entity.json(address));
        assertThat(response.getStatus(), is(200));
        Address add = response.readEntity(Address.class);
        assertThat(add, is(address));
    }
    
    @Test
    public void test07_put_addresses_norole() throws JsonMappingException, JsonProcessingException {
    	Address address = new Address();
    	address.setAddress( STREET_NUMBER, STREET, CITY, PROVINCE, COUNTRY, ZIPCODE);
        Response response = webTarget
            //.register(userAuth)
//            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .put(Entity.json(address));
        assertThat(response.getStatus(), is(401));
    }
    
    @Test
    public void test08_put_addresses_userrole() throws JsonMappingException, JsonProcessingException {
    	Address address = new Address();
    	address.setAddress( STREET_NUMBER, STREET, CITY, PROVINCE, COUNTRY, ZIPCODE);
        Response response = webTarget
            .register(userAuth)
//            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .put(Entity.json(address));
        assertThat(response.getStatus(), is(401));
    }
    
    @Test
    public void test09_delete_addresses_norole() throws JsonMappingException, JsonProcessingException {
    	Address address = new Address();
    	address.setAddress( STREET_NUMBER, STREET, CITY, PROVINCE, COUNTRY, ZIPCODE);
        Response response = webTarget
            //.register(userAuth)
//            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .delete();
        assertThat(response.getStatus(), is(401));
    }
    
    @Test
    public void test10_delete_addresses_userrole() throws JsonMappingException, JsonProcessingException {
    	Address address = new Address();
    	address.setAddress( STREET_NUMBER, STREET, CITY, PROVINCE, COUNTRY, ZIPCODE);
        Response response = webTarget
            .register(userAuth)
//            .register(adminAuth)
            .path(ADDRESS_RESOURCE_NAME)
            .request()
            .delete();
        assertThat(response.getStatus(), is(401));
    }
    
}