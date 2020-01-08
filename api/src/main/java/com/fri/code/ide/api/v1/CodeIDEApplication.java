package com.fri.code.ide.api.v1;

import com.kumuluz.ee.discovery.annotations.RegisterService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/v1")
@OpenAPIDefinition(info = @Info(title = "IDEApi", version = "v1.0.0"), servers = @Server(url = "http://34.67.123.142/script/v1"), security = @SecurityRequirement(name = "openid-connect"))
@RegisterService
public class CodeIDEApplication extends Application {
}
