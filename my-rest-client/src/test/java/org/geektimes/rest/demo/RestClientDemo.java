package org.geektimes.rest.demo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestClientDemo {

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hello/world")      // WebTarget
                .request() // Invocation.Builder
                .get();                                     //  Response

        // Form form = new Form();
        // form.param("x", "foo");
        // form.param("y", "bar");
        //
        // Client client = ClientBuilder.newClient();
        // //Response response = client
        // //        .target("http://127.0.0.1:8080/hello/world")      // WebTarget
        // //        .request(MediaType.TEXT_PLAIN_TYPE) // Invocation.Builder
        // //        .post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));  //  Response
        // Response response = client
        //         .target("http://127.0.0.1:8080/hello/world")      // WebTarget
        //         .request(MediaType.APPLICATION_JSON_TYPE) // Invocation.Builder
        //         .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));  //  Response

        String content = response.readEntity(String.class);

        System.out.println(content);

    }
}
