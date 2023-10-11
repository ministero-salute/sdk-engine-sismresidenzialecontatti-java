package it.mds.sdk.flusso.sism.residenziale.cont;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"it.mds.sdk.flusso.sism.residenziale.cont.controller", "it.mds.sdk.flusso.sism.residenziale.cont","it.mds.sdk.rest.persistence.entity",
        "it.mds.sdk.libreriaregole.validator",
        "it.mds.sdk.flusso.sism.residenziale.cont.service", "it.mds.sdk.flusso.sism.residenziale.cont.tracciato",
        "it.mds.sdk.gestoreesiti", "it.mds.sdk.flusso.sism.residenziale.cont.parser.regole","it.mds.sdk.flusso.sism.residenziale.cont.parser.regole.conf",
        "it.mds.sdk.connettoremds"})

@OpenAPIDefinition(info=@Info(title = "SDK Ministero Della Salute - Flusso CNR", version = "0.0.5-SNAPSHOT", description = "Flusso Sism Residenziale Contatti"))
public class FlussoSismResidenzialeContatti {
    public static void main(String[] args) {
        SpringApplication.run(FlussoSismResidenzialeContatti.class, args);
    }

}
