package it.mds.sdk.flusso.sism.residenziale.cont.parser.regole;

import it.mds.sdk.flusso.sism.residenziale.cont.parser.regole.conf.ConfigurazioneFlussoSismResContatti;
import it.mds.sdk.flusso.sism.residenziale.cont.tracciato.TracciatoSplitterImpl;
import it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ObjectFactory;
import it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ResidenzialeSemiresidenzialeContatto;
import it.mds.sdk.gestorefile.GestoreFile;
import it.mds.sdk.gestorefile.factory.GestoreFileFactory;
import it.mds.sdk.libreriaregole.dtos.CampiInputBean;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@MockitoSettings(strictness = Strictness.LENIENT)
public class TracciatoSplitterImplTest {

    @InjectMocks
    @Spy
    private TracciatoSplitterImpl tracciatoSplitter;
    private ConfigurazioneFlussoSismResContatti configurazione = Mockito.mock(ConfigurazioneFlussoSismResContatti.class);
    private ObjectFactory objectFactory = Mockito.mock(ObjectFactory.class);
    private ResidenzialeSemiresidenzialeContatto residenzialeSemiresidenzialeContatto = Mockito.mock(ResidenzialeSemiresidenzialeContatto.class);
    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento asl = Mockito.mock(ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.class);
    private ConfigurazioneFlussoSismResContatti.XmlOutput xmlOutput = Mockito.mock(ConfigurazioneFlussoSismResContatti.XmlOutput.class);
    private MockedStatic<GestoreFileFactory> gestore;
    private GestoreFile gestoreFile = Mockito.mock(GestoreFile.class);
    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento aziendaSanitariaRiferimento = Mockito.mock(ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.class);
    private List<ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento> aziendaSanitariaRiferimentoList = new ArrayList<>();
    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM dsm = Mockito.mock(ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.class);
    private List<ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM> listDsm = new ArrayList<>();
    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito assistito = Mockito.mock(ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito.class);
    private RecordDtoSismResidenzialeContatti r = new RecordDtoSismResidenzialeContatti();
    List<RecordDtoSismResidenzialeContatti> records = new ArrayList<>();

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        gestore = mockStatic(GestoreFileFactory.class);
        initMockedRecord(r);
        records.add(r);
    }

    @Test
    void dividiTracciatoTest() throws JAXBException, IOException, SAXException {

        when(tracciatoSplitter.getConfigurazione()).thenReturn(configurazione);
        when(objectFactory.createResidenzialeSemiresidenzialeContatto()).thenReturn(residenzialeSemiresidenzialeContatto);
        when(residenzialeSemiresidenzialeContatto.getAziendaSanitariaRiferimento()).thenReturn(List.of(asl));
        when(configurazione.getXmlOutput()).thenReturn(xmlOutput);
        when(xmlOutput.getPercorso()).thenReturn("percorso");
        gestore.when(() -> GestoreFileFactory.getGestoreFile("XML")).thenReturn(gestoreFile);
        doNothing().when(gestoreFile).scriviDto(any(), any(), any());

        Assertions.assertEquals(
                List.of(Path.of("percorsoSDK_RES_CNR_S1_100.xml")),
                this.tracciatoSplitter.dividiTracciato(records, "100")
        );

    }

    @Test
    void dividiTracciatoTestOk2() throws JAXBException, IOException, SAXException {
        when(tracciatoSplitter.getConfigurazione()).thenReturn(configurazione);
        when(objectFactory.createResidenzialeSemiresidenzialeContatto()).thenReturn(residenzialeSemiresidenzialeContatto);
        when(residenzialeSemiresidenzialeContatto.getAziendaSanitariaRiferimento()).thenReturn(List.of(asl));

        when(configurazione.getXmlOutput()).thenReturn(xmlOutput);
        when(xmlOutput.getPercorso()).thenReturn("percorso");
        gestore.when(() -> GestoreFileFactory.getGestoreFile("XML")).thenReturn(gestoreFile);
        doNothing().when(gestoreFile).scriviDto(any(), any(), any());

        doReturn(null).when(tracciatoSplitter).getCurrentAsl(any(), any());
        doReturn(null).when(tracciatoSplitter).getCurrentDsm(any(), any());

        Assertions.assertEquals(
                List.of(Path.of("percorsoSDK_RES_CNR_S1_100.xml")),
                this.tracciatoSplitter.dividiTracciato(records, "100")
        );

    }

    @Test
    void getCurrentDsmTest() {
        var list = List.of(dsm);
        when(asl.getDSM()).thenReturn(list);
        var c = tracciatoSplitter.getCurrentDsm(asl, r);
    }

    @Test
    void getCurrentAslTest() {
        var list = List.of(asl);

        when(residenzialeSemiresidenzialeContatto.getAziendaSanitariaRiferimento()).thenReturn(list);
        var c = tracciatoSplitter.getCurrentAsl(residenzialeSemiresidenzialeContatto, r);
    }

    @Test
    void creaPrestazioniSanitarieTest() {
        var list = List.of(asl);
        var c = tracciatoSplitter.creaSemiresidenzialeContatto(records, null);
    }

    @AfterEach
    void closeMocks() {
        gestore.close();
    }

    private void initMockedRecord(RecordDtoSismResidenzialeContatti r) {
        CampiInputBean campiInputBean = new CampiInputBean();
        campiInputBean.setPeriodoRiferimentoInput("Q1");
        campiInputBean.setAnnoRiferimentoInput("2022");
        r.setTipoOperazioneContatto("C");
        r.setAnnoRiferimento("2022");
        r.setCodiceRegione("080");
        r.setPeriodoRiferimento("S1");
        r.setCodiceDipartimentoSaluteMentale("cdsm");
        r.setCodiceAziendaSanitariaRiferimento("casr");
        r.setIdRecord("ic");
        r.setIdContatto(1L);
        r.setCodiceStruttura("codstr");
        records.add(r);
    }
}
