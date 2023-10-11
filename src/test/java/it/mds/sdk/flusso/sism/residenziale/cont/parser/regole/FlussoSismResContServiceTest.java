package it.mds.sdk.flusso.sism.residenziale.cont.parser.regole;

import it.mds.sdk.connettoremds.ConnettoreMds;
import it.mds.sdk.connettoremds.exception.ConnettoreMdsException;
import it.mds.sdk.connettoremds.gaf.webservices.bean.ArrayOfUploadEsito;
import it.mds.sdk.connettoremds.gaf.webservices.bean.ResponseUploadFile;
import it.mds.sdk.flusso.sism.residenziale.cont.parser.regole.conf.ConfigurazioneFlussoSismResContatti;
import it.mds.sdk.flusso.sism.residenziale.cont.service.FlussoSismResContattiService;
import it.mds.sdk.flusso.sism.residenziale.cont.tracciato.TracciatoSplitterImpl;
import it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ResidenzialeSemiresidenzialeContatto;
import it.mds.sdk.gestoreesiti.GestoreRunLog;
import it.mds.sdk.gestoreesiti.conf.Configurazione;
import it.mds.sdk.gestoreesiti.modelli.InfoRun;
import it.mds.sdk.gestoreesiti.modelli.ModalitaOperativa;
import it.mds.sdk.gestorefile.GestoreFile;
import it.mds.sdk.gestorefile.exception.XSDNonSupportedException;
import it.mds.sdk.gestorefile.factory.GestoreFileFactory;
import it.mds.sdk.libreriaregole.dtos.RecordDtoGenerico;
import it.mds.sdk.libreriaregole.gestorevalidazione.BloccoValidazione;
import it.mds.sdk.libreriaregole.regole.beans.Campo;
import it.mds.sdk.libreriaregole.regole.beans.Parametri;
import it.mds.sdk.libreriaregole.regole.beans.RegolaGenerica;
import it.mds.sdk.libreriaregole.regole.beans.RegoleFlusso;
import it.mds.sdk.libreriaregole.regole.catalogo.input.RegolaCheckCampiInput;
import it.mds.sdk.libreriaregole.validator.ValidationEngine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Slf4j
@MockitoSettings(strictness = Strictness.LENIENT)
class FlussoSismResContServiceTest {

    private final String OSP_ACN_CSV = "OSP_ACN.csv";
    private final int dimensioneBlocco = 250000;

    private final String idClient = "";
    private final String periodoRiferimento = "13";
    private final String annoRfierimento = "2022";
    private final String codiceRegione = "080";
    @Autowired
    ConfigurazioneFlussoSismResContatti configurazioneFlusso;
    ParserTracciatoImpl parser = Mockito.mock(ParserTracciatoImpl.class);
    @InjectMocks
    @Spy
    private FlussoSismResContattiService flusso;
    //  private GestoreRunLog gestoreRunLog;
    @Mock
    private RegoleFlusso regoleFlusso;
    @Spy
    private ConfigurazioneFlussoSismResContatti conf;
    ;
    private TracciatoSplitterImpl tracciatoSplitter = Mockito.mock(TracciatoSplitterImpl.class);
    private GestoreFile gestoreFileInt2 = Mockito.mock(GestoreFile.class);
    private GestoreFile gestoreFileInt = Mockito.mock(GestoreFile.class);
    private ConnettoreMds connettoreMds = Mockito.mock(ConnettoreMds.class);
    private GestoreRunLog grl = Mockito.mock(GestoreRunLog.class);
    //    @Mock
    private ValidationEngine validationEngine = Mockito.mock(ValidationEngine.class);
    private ResidenzialeSemiresidenzialeContatto residenzialeSemiresidenzialeContatto = Mockito.mock(ResidenzialeSemiresidenzialeContatto.class);
    private String idRun = "928";
    private BloccoValidazione bloccoValidazione;
    private File file;

    private Configurazione config = new Configurazione();

    private final String percorso = String.format("%s/ESITO_%s.json", config.getEsito().getPercorso(), idRun);
    private final String percorsoTemp = String.format("%s/ESITO_%s_TEMP.json", config.getEsito().getPercorso(), idRun);
    private RecordDtoSismResidenzialeContatti recordDtoSismResidenziale;
    private ParserTracciatoImpl parserTracciato = Mockito.mock(ParserTracciatoImpl.class);

    private InfoRun infoRun;
    private MockedStatic<GestoreFileFactory> mockedStatic;
    /*
      TODO:
       **************** ATTENZIONE ******************
       Prima di lanciare i test assicurarsi
       di aver settato correttamente i parametri
       nel file di properties.
       **********************************************
     */

    @BeforeEach
    void init() {
        log.warn("\n*** ATTENZIONE ***\n" +
                "ATTENZIONE: I test runnati necessitano di proprietà settate nel file di properties.\n " +
                "Non settare le proprietà potrebbe causare il fallimento di alcuni test.\n" +
                "******************\n"
        );
        MockitoAnnotations.openMocks(this);
        // Create Parametri

        Properties prop = loadPropertiesFromFile("config-flusso-dir-test.properties");
        this.idRun = prop.getProperty("test.idrun");

        Map<String, String> parametriMap = new HashMap<>();
        parametriMap.put("parametroInput", "codiceRegioneInput");
        Parametri parametri = new Parametri();
        parametri.setParametriMap(parametriMap);
        // Create RegolaGenerica
        RegolaGenerica regolaGenerica = new RegolaCheckCampiInput();
        regolaGenerica.setCodErrore("B01");
        regolaGenerica.setDesErrore("Non appartenenza al dominio di riferimento");
        regolaGenerica.setParametri(parametri);
        // Create Campo
        Campo campo = new Campo(List.of(regolaGenerica), "codiceRegione");
        // Creazione RegoleFlusso
        regoleFlusso = new RegoleFlusso(List.of(campo));
        //    gestoreRunLog = new GestoreRunLog(new GestoreFileCSVImpl(), Progressivo.creaProgressivo(Progressivo.Fonte.FILE));

        recordDtoSismResidenziale = new RecordDtoSismResidenzialeContatti();

        bloccoValidazione = new BloccoValidazione();
        bloccoValidazione.setNumeroRecord(2);
        bloccoValidazione.setScartati(1);

        file = new File(conf.getFlusso().getPercorso() + "OSP_ACN.csv");

        infoRun = new InfoRun(
                null, null, null, null, null,
                null, null, null, null, null, null,
                1, 1, 1, null, null, null, null,
                null, null, null, null, null, null, null, "nomeFile",
                null, null, null
        );
        mockedStatic = mockStatic(GestoreFileFactory.class);
    }

    @Test
    void validazioneBlocchiTestModalitaT_KO() {
        File file = Mockito.mock(File.class);

        GestoreFile gestoreFile = Mockito.mock(GestoreFile.class);
        when(spy(conf.getFlusso())).thenReturn(configurazioneFlusso.getFlusso());
        when(conf.getXmlOutput()).thenReturn(configurazioneFlusso.getXmlOutput());
        List<RecordDtoGenerico> list = List.of(recordDtoSismResidenziale);

        doReturn(file).when(flusso).getfileFromPath(any());

        given(tracciatoSplitter.creaSemiresidenzialeContatto(any(), any())).willReturn(residenzialeSemiresidenzialeContatto);
        when(flusso.getParserTracciatoImpl()).thenReturn(parserTracciato);
        given(parserTracciato.parseTracciatoBlocco(any(), anyInt(), anyInt())).willReturn(list);
        //doThrow(new XSDNonSupportedException()).when(gestoreFile).scriviDtoFragment(eq(null), any(), any());
        mockedStatic.when(() -> GestoreFileFactory.getGestoreFile("XML")).thenReturn(gestoreFile);

        willThrow(new XSDNonSupportedException()).given(gestoreFile).scriviDtoFragment(any(), any(), any());

        given(validationEngine.startValidaFlussoBlocco(list, regoleFlusso, null, grl, 0)).willReturn(bloccoValidazione);
        given(validationEngine.formatJsonEsiti(percorso, percorsoTemp)).willReturn(true);
        given(validationEngine.formatXmlOutput(any(), any(), any())).willReturn(false);

        given(grl.getRun(any())).willReturn(infoRun);
        given(grl.cambiaStatoRun(any(), any())).willReturn(infoRun);
        given(grl.updateRun(any())).willReturn(infoRun);

        bloccoValidazione.setRecordList(List.of(recordDtoSismResidenziale));

        Assertions.assertThrows(RuntimeException.class,
                () -> this.flusso.validazioneBlocchi(
                        dimensioneBlocco,
                        OSP_ACN_CSV,
                        regoleFlusso,
                        idRun,
                        idClient,
                        ModalitaOperativa.T,
                        periodoRiferimento,
                        annoRfierimento,
                        codiceRegione,
                        grl
                ));
    }

    @Test
    void validazioneBlocchiTestModalitaT_KO2() {

        GestoreFile gestoreFile = Mockito.mock(GestoreFile.class);
        when(spy(conf.getFlusso())).thenReturn(configurazioneFlusso.getFlusso());
        when(conf.getXmlOutput()).thenReturn(configurazioneFlusso.getXmlOutput());
        List<RecordDtoGenerico> list = List.of(recordDtoSismResidenziale);

        given(tracciatoSplitter.creaSemiresidenzialeContatto(any(), any())).willReturn(residenzialeSemiresidenzialeContatto);
        when(flusso.getParserTracciatoImpl()).thenReturn(parserTracciato);
        given(parserTracciato.parseTracciatoBlocco(any(), anyInt(), anyInt())).willReturn(list);
        //doThrow(new XSDNonSupportedException()).when(gestoreFile).scriviDtoFragment(eq(null), any(), any());

        mockedStatic.when(() -> GestoreFileFactory.getGestoreFile("XML")).thenReturn(gestoreFile);
        doNothing().when(gestoreFile).scriviDtoFragment(any(), any(), any());

        given(validationEngine.startValidaFlussoBlocco(list, regoleFlusso, null, grl, 0)).willReturn(bloccoValidazione);
        given(validationEngine.formatJsonEsiti(anyString(), anyString())).willReturn(true);
        given(validationEngine.formatXmlOutput(any(), any(), any())).willReturn(false);

        given(grl.getRun(any())).willReturn(infoRun);
        given(grl.cambiaStatoRun(any(), any())).willReturn(infoRun);
        given(grl.updateRun(any())).willReturn(infoRun);

        bloccoValidazione.setRecordList(List.of(recordDtoSismResidenziale));

        this.flusso.validazioneBlocchi(
                dimensioneBlocco,
                OSP_ACN_CSV,
                regoleFlusso,
                idRun,
                idClient,
                ModalitaOperativa.T,
                periodoRiferimento,
                annoRfierimento,
                codiceRegione,
                grl
        );
    }

    @Test
    void validazioneBlocchiTestModalitaT_OK() {

        GestoreFile gestoreFile = Mockito.mock(GestoreFile.class);

        when(spy(conf.getFlusso())).thenReturn(configurazioneFlusso.getFlusso());
        when(conf.getXmlOutput()).thenReturn(configurazioneFlusso.getXmlOutput());
        List<RecordDtoGenerico> list = List.of(recordDtoSismResidenziale);
        given(parser.parseTracciatoBlocco(any(), anyInt(), anyInt())).willReturn(list);

        bloccoValidazione.setRecordList(Collections.emptyList());

        given(parserTracciato.parseTracciatoBlocco(any(), anyInt(), anyInt())).willReturn(list);

        given(validationEngine.startValidaFlussoBlocco(list, regoleFlusso, null, grl, 0)).willReturn(bloccoValidazione);
        given(validationEngine.formatJsonEsiti(anyString(), anyString())).willReturn(true);
        given(validationEngine.formatXmlOutput(any(), any(), any())).willReturn(false);
        given(validationEngine.validateXML(eq(null), anyString())).willReturn(true);

        mockedStatic.when(() -> GestoreFileFactory.getGestoreFile("XML")).thenReturn(gestoreFile);
        doNothing().when(gestoreFile).scriviDtoFragment(any(), any(), any());

        given(grl.getRun(any())).willReturn(infoRun);
        given(grl.cambiaStatoRun(any(), any())).willReturn(infoRun);
        given(grl.updateRun(any())).willReturn(infoRun);

        bloccoValidazione.setRecordList(List.of(recordDtoSismResidenziale));

        when(flusso.getParserTracciatoImpl()).thenReturn(parserTracciato);

        this.flusso.validazioneBlocchi(
                dimensioneBlocco,
                OSP_ACN_CSV,
                regoleFlusso,
                idRun,
                idClient,
                ModalitaOperativa.T,
                periodoRiferimento,
                annoRfierimento,
                codiceRegione,
                grl
        );
    }

    @Test
    void validazioneBlocchiTestModalitaP_OK() {

        when(spy(conf.getFlusso())).thenReturn(configurazioneFlusso.getFlusso());
        when(conf.getXmlOutput()).thenReturn(configurazioneFlusso.getXmlOutput());
        List<RecordDtoGenerico> list = List.of(recordDtoSismResidenziale);
        given(parser.parseTracciatoBlocco(any(), anyInt(), anyInt())).willReturn(list);

        bloccoValidazione.setRecordList(List.of(recordDtoSismResidenziale));

        given(validationEngine.startValidaFlussoBlocco(anyList(), any(), anyString(), any(), anyInt())).willReturn(bloccoValidazione);
        given(validationEngine.formatJsonEsiti(any(), any())).willReturn(true);
        given(validationEngine.puliziaFileSism(any(), any(), any())).willReturn("mocked_filename");
        given(validationEngine.validateXML(eq(null), anyString())).willReturn(true);

        willDoNothing().given(flusso).inviaTracciatoMds(any(), any(), any(), any(), any());

        given(tracciatoSplitter.creaSemiresidenzialeContatto(any(), any())).willReturn(residenzialeSemiresidenzialeContatto);
        mockedStatic.when(() -> GestoreFileFactory.getGestoreFile("XML")).thenReturn(gestoreFileInt2);

//        willDoNothing().given(gestoreFileInt2).scriviDtoFragment(eq(dataroot), any(), any());

        given(grl.getRun(any())).willReturn(infoRun);
        given(grl.cambiaStatoRun(any(), any())).willReturn(infoRun);
        given(grl.updateRun(any())).willReturn(infoRun);

        this.flusso.validazioneBlocchi(
                dimensioneBlocco,
                OSP_ACN_CSV,
                regoleFlusso,
                idRun,
                idClient,
                ModalitaOperativa.P,
                periodoRiferimento,
                annoRfierimento,
                codiceRegione,
                grl
        );
    }

    @Test
    void inviaTracciatoMdsTest_KOMinistero() throws ConnettoreMdsException {
        String nomeFileXml = conf.getXmlOutput().getPercorso() + "SDK_DIR_IF3_" + periodoRiferimento + "_" + idRun +
                ".xml";
        given(grl.getRun(any())).willReturn(infoRun);
        ResponseUploadFile responseUploadFile = new ResponseUploadFile();
        responseUploadFile.setErrorCode("x");
        given(connettoreMds.invioTracciati(any(), any(), any(), any(), any())).willReturn(responseUploadFile);
        given(grl.cambiaStatoRun(any(), any())).willReturn(infoRun);
        flusso.inviaTracciatoMds(
                idRun,
                nomeFileXml,
                grl,
                periodoRiferimento,
                annoRfierimento
        );
    }

    @Test
    void inviaTracciatoMdsTest_ErrorCodeNull() throws ConnettoreMdsException {
        String nomeFileXml = conf.getXmlOutput().getPercorso() + "SDK_DIR_IF3_" + periodoRiferimento + "_" + idRun +
                ".xml";
        given(grl.getRun(any())).willReturn(infoRun);
        ResponseUploadFile responseUploadFile = new ResponseUploadFile();
        responseUploadFile.setErrorCode(null);
        given(connettoreMds.invioTracciati(any(), any(), any(), any(), any())).willReturn(responseUploadFile);
        given(grl.cambiaStatoRun(any(), any())).willReturn(infoRun);
        flusso.inviaTracciatoMds(
                idRun,
                nomeFileXml,
                grl,
                periodoRiferimento,
                annoRfierimento
        );
    }

    @Test
    void inviaTracciatoMdsTest_ListaEsitiNotNull() throws ConnettoreMdsException {
        String nomeFileXml = conf.getXmlOutput().getPercorso() + "SDK_DIR_IF3_" + periodoRiferimento + "_" + idRun +
                ".xml";
        given(grl.getRun(any())).willReturn(infoRun);
        ResponseUploadFile responseUploadFile = new ResponseUploadFile();
        ArrayOfUploadEsito arr = Mockito.mock(ArrayOfUploadEsito.class);

        responseUploadFile.setListaEsitiUpload(arr);
        responseUploadFile.setErrorCode(null);
        given(connettoreMds.invioTracciati(any(), any(), any(), any(), any())).willReturn(responseUploadFile);
        given(grl.cambiaStatoRun(any(), any())).willReturn(infoRun);
        flusso.inviaTracciatoMds(
                idRun,
                nomeFileXml,
                grl,
                periodoRiferimento,
                annoRfierimento
        );
    }

    @Test
    void inviaTracciatoMdsTestOk() throws ConnettoreMdsException, IOException {
        Path path = Mockito.mock(Path.class);
        String nomeFileXml = conf.getXmlOutput().getPercorso() + "SDK_DIR_IF3_" + periodoRiferimento + "_" + idRun +
                ".xml";
        given(grl.getRun(any())).willReturn(infoRun);
        ResponseUploadFile responseUploadFile = new ResponseUploadFile();
        ArrayOfUploadEsito arr = Mockito.mock(ArrayOfUploadEsito.class);

        responseUploadFile.setListaEsitiUpload(arr);
        responseUploadFile.setErrorCode("");
        given(connettoreMds.invioTracciati(any(), any(), any(), any(), any())).willReturn(responseUploadFile);
        given(grl.cambiaStatoRun(any(), any())).willReturn(infoRun);

        doNothing().when(flusso).fileCopy(any(), any());

        flusso.inviaTracciatoMds(
                idRun,
                nomeFileXml,
                grl,
                periodoRiferimento,
                annoRfierimento
        );

    }

    private Properties loadPropertiesFromFile(String fileName) {
        Properties prop = new Properties();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream(fileName);
            prop.load(stream);
            stream.close();
        } catch (Exception e) {
            String msg = String.format("Failed to load file '%s' - %s - %s", fileName, e.getClass().getName(),
                    e.getMessage());
            System.out.println(msg);
        }
        return prop;
    }
    @AfterEach
    void closeStaticMocks(){
        mockedStatic.close();
    }
}
