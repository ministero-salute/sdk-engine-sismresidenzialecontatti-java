/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.sism.residenziale.cont.parser.regole.conf;

import it.mds.sdk.connettore.anagrafiche.conf.Configurazione;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Properties;




@Slf4j
@Getter
@Component("configurazioneFlussoSismResCont")
public class ConfigurazioneFlussoSismResContatti {

    Rules rules;
    XmlOutput xmlOutput;
    Flusso flusso;
    SogliaInvio sogliaInvio;
    private Sent sent;
    private NomeFlusso nomeFLusso;

    private DimensioneBlocco dimensioneBlocco;
    private Separatore separatore;

    private static final String FILE_CONF = "config-flusso-sism-res-cont.properties";

    public ConfigurazioneFlussoSismResContatti() {
        this(leggiConfigurazioneEsterna());
    }

    public ConfigurazioneFlussoSismResContatti(final Properties conf) {

        this.rules = ConfigurazioneFlussoSismResContatti.Rules.builder()
                .withPercorso(conf.getProperty("regole.percorso", ""))
                .build();
        this.xmlOutput = ConfigurazioneFlussoSismResContatti.XmlOutput.builder()
                .withPercorso(conf.getProperty("xmloutput.percorso", ""))
                .build();
        this.flusso = ConfigurazioneFlussoSismResContatti.Flusso.builder()
                .withPercorso(conf.getProperty("flusso.percorso", ""))
                .build();
        this.sogliaInvio = ConfigurazioneFlussoSismResContatti.SogliaInvio.builder()
                .withSoglia(conf.getProperty("soglia.invio.mds", ""))
                .build();
        this.sent = ConfigurazioneFlussoSismResContatti.Sent.builder()
                .withPercorsoSent(conf.getProperty("sent.percorso", ""))
                .build();
        this.nomeFLusso = ConfigurazioneFlussoSismResContatti.NomeFlusso.builder()
                .withNomeFlusso(conf.getProperty("nome.flusso", ""))
                .build();
        this.dimensioneBlocco = ConfigurazioneFlussoSismResContatti.DimensioneBlocco.builder()
                .withDimensioneBlocco(conf.getProperty("dimensione.blocco", "1000"))
                .build();
        this.separatore = ConfigurazioneFlussoSismResContatti.Separatore.builder()
                .withSeparatore(conf.getProperty("separatore", "~"))
                .build();
    }

    @Value
    @Builder(setterPrefix = "with")
    public static class Rules {
        String percorso;
    }

    @Value
    @Builder(setterPrefix = "with")
    public static class XmlOutput {
        String percorso;
    }

    @Value
    @Builder(setterPrefix = "with")
    public static class Flusso {
        String percorso;
    }

    @Value
    @Builder(setterPrefix = "with")
    public static class SogliaInvio {
        String soglia;
    }
    @Value
    @Builder(setterPrefix = "with")
    public static class Sent {
        String percorsoSent;
    }
    @Value
    @Builder(setterPrefix = "with")
    public static class NomeFlusso {
        String nomeFlusso;
    }
    @Value
    @Builder(setterPrefix = "with")
    public static class DimensioneBlocco {
        String dimensioneBlocco;
    }
    @Value
    @Builder(setterPrefix = "with")
    public static class Separatore {
        String separatore;
    }

    private static Properties leggiConfigurazione(final String nomeFile) {
        final Properties prop = new Properties();
        try (final InputStream is = ConfigurazioneFlussoSismResContatti.class.getClassLoader().getResourceAsStream(nomeFile)) {
            prop.load(is);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        return prop;
    }

    private static Properties leggiConfigurazioneEsterna() {
        log.debug("{}.leggiConfigurazioneEsterna - BEGIN", it.mds.sdk.connettore.anagrafiche.conf.Configurazione.class.getName());
        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(Paths.get("/sdk/properties", FILE_CONF).toFile()),
                StandardCharsets.UTF_8)) {
            properties.load(in);
        } catch (IOException e) {
            log.warn("{}.leggiConfigurazioneEsterna [{}]: loading internal config", Configurazione.class.getName(), e.getMessage());
            return leggiConfigurazione(FILE_CONF);
        }
        return properties;
    }
}
