/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 *
 * @author Mr. Poyo
 */
@OpenAPIDefinition(info = @Info(
        contact = @Contact(
                name = "Kinweb",
                email = "kinweb@gmail.com"
        ),
        description = "OpenAPI documentation for kinomania website REST API",
        title = "Kinomania REST API documentation",
        version = "1.0",
        license = @License(
                name = "MIT license",
                url = "https://www.mit.edu/~amini/LICENSE.md"
        )
),
        servers = {
            @Server(
                    description = "Local ENV",
                    url = "http://localhost:8080"
            )
        }
)
@SecurityScheme(
        name = "jwtAuth",
        description = "Authentication by JWT token",
        type = SecuritySchemeType.APIKEY,       
        in = SecuritySchemeIn.COOKIE,
        paramName = "kinweb"
)
public class OpenAPIConfiguration {

}
