import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServeRestTest {
    private String nomeProduto = "Base de Computador";
    private int preco = 150;
    private String descricao = "Base para refrigeração de notebook";
    private int quantidade = 50;
    private static String token;
    private static String idProduto;

    @Test
    @Order(1)
    public void testDadoEmailSenhaValidosQuandoFacoLoginEntaoValidoStatusCodeSucesso(){
        baseURI = "https://serverest.dev";

        token = given()
                    .body("{\n" +
                            "    \"email\": \"fulano@qa.com\",\n" +
                            "    \"password\": \"teste\"\n" +
                            "}")
                    .contentType(ContentType.JSON)
                .when()
                        .post("/login")
                .then()
                        .statusCode(200)
                        .body("message", equalTo("Login realizado com sucesso"))
                        .extract()
                            .path("authorization");
    }

    @Test
    @Order(2)
    public void testDadoTokenValidoQuandoCadastroProdutoEntaoValidoStatusCodeSucesso(){
        idProduto = given()
                .body("{\n" +
                        "    \"nome\": \"Base de Computador\",\n" +
                        "    \"preco\": 1200,\n" +
                        "    \"descricao\": \"Base para refrigeração de notebook\",\n" +
                        "    \"quantidade\": 50\n" +
                        "}")
                .contentType(ContentType.JSON)
                .header("Authorization", token)
            .when()
                .post("/produtos")
            .then()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .extract()
                    .path("_id");
    }

    @Test
    @Order(3)
    public void testDadoIdProdutoQuandoConsultoProdutoEntaoValidoStatusCodeSucessoSchema(){
        given()
                .pathParam("id", idProduto)
            .when()
                .get("/produtos/{id}")
            .then()
                .statusCode(200)
                .body("nome", equalTo("Base de Computador"))
                .body("preco", equalTo(1200))
                .body("descricao", equalTo("Base para refrigeração de notebook"))
                .body("quantidade", equalTo(50))
                .body("_id", equalTo(idProduto));
//                .assertThat()
//                    .body(matchesJsonSchemaInClasspath("products-schema.json"));
    }

    @Test
    @Order(4)
    public void testDadoTokenValidoEIdProdutoQuandoEditoProdutoEntaoValidoStatusCodeSucesso(){
        given()
                .pathParam("id", idProduto)
                .body("{\n" +
                        "    \"nome\": \"Base de Notebook\",\n" +
                        "    \"preco\": 120,\n" +
                        "    \"descricao\": \"Base para refrigeração de notebook\",\n" +
                        "    \"quantidade\": 50\n" +
                        "}")
                .contentType(ContentType.JSON)
                .header("Authorization", token)
            .when()
                .put("/produtos/{id}")
            .then()
                .statusCode(200)
                .body("message", equalTo("Registro alterado com sucesso"));
    }

    @Test
    @Order(5)
    public void testDadoTokenValidoEIdProdutoQuandoDeletoProdutoEntaoValidoStatusCOdeSucesso(){
        given()
                .pathParam("id", idProduto)
                .header("Authorization", token)
            .when()
                .delete("/produtos/{id}")
            .then()
                .statusCode(200)
                .body("message", equalTo("Registro excluído com sucesso"));
    }

}
