package apiproject.tests;

import ch.qos.logback.core.joran.conditional.Condition;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import static io.restassured.RestAssured.given;

public class SimpleSoapApiTests {
    private static final Logger logger = LoggerFactory.getLogger(SimpleSoapApiTests.class);
    private static RequestSpecification requestSpecification;

    /*
    @BeforeAll
    static void setup() {
        requestSpecification =new RequestSpecBuilder()
                .setBaseUri("https://soap.ga-test.csssr.com/ws/")
                .log(LogDetail.ALL)
                .build();
    }

    @Test
    void simpleSOAPTest() {
        String body = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sch=\"http://csssr.com/schemas\">\n" +
                "  <soapenv:Header/>\n" +
                "  <soapenv:Body>\n" +
                "    <sch:GetCompanyRequest>\n" +
                "      <sch:CompanyId>5e942ca330148f0001cd8806</sch:CompanyId>\n" +
                "    </sch:GetCompanyRequest>\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        Response response = given(requestSpecification)
                .contentType("text/xml")
                .body(body)
                .post();

        logger.info("Response status code: {}", response.statusCode());
        logger.info("Response body: {}", response.getBody().prettyPrint());

        String xml = response.andReturn().asString();
        XmlPath xmlPath = new XmlPath(xml);

        String actualName = xmlPath.get("Envelope.Body.GetCompanyResponse.Company.Name");
        String actualId = xmlPath.get("Envelope.Body.GetCompanyResponse.Company.Id");

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Test", actualName);
        Assertions.assertEquals("5e942ca330148f0001cd8806", actualId);
    }

     */

    @BeforeAll
    static void setup() throws FileNotFoundException {
        // Создаём файл для логов
        File logFile = new File("logs/app.log");
        PrintStream logStream = new PrintStream(logFile);

        requestSpecification =new RequestSpecBuilder()
                .setBaseUri("https://apps.learnwebservices.com/services/hello?WSDL")
                .log(LogDetail.ALL)
                .addFilter(new RequestLoggingFilter(logStream))  // Лог запросов
                .addFilter(new ResponseLoggingFilter(logStream)) // Лог ответов
                .build();

        logger.info("API тесты запущены. Логи сохраняются в logs/app.log");
    }

    @Test
    void simpleSOAPTest() {
        String name = "Valiantsin";

        String body = String.format("""
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
                  <soapenv:Header/>
                  <soapenv:Body>
                    <HelloRequest xmlns="http://learnwebservices.com/services/hello">
                      <Name>%s</Name>
                    </HelloRequest>
                  </soapenv:Body>
                </soapenv:Envelope>""", name);
        Response response = given(requestSpecification)
                .contentType("text/xml")
                .body(body)
                .post();

        logger.info("Response status code: {}", response.statusCode());
        logger.info("Response body: {}", response.getBody().prettyPrint());

        String xml = response.andReturn().asString();
        logger.info("XML Response to String: {}", xml);
        XmlPath xmlPath = new XmlPath(xml);

        String actualMessage = xmlPath.get("Envelope.Body.HelloResponse.Message");
        Assertions.assertTrue(actualMessage.contains(name));
    }
}
