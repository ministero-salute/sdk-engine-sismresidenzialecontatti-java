/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.sism.residenziale.cont.tracciato;

import it.mds.sdk.flusso.sism.residenziale.cont.parser.regole.RecordDtoSismResidenzialeContatti;
import it.mds.sdk.flusso.sism.residenziale.cont.parser.regole.conf.ConfigurazioneFlussoSismResContatti;
import it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ObjectFactory;
import it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ResidenzialeSemiresidenzialeContatto;
import it.mds.sdk.gestorefile.GestoreFile;
import it.mds.sdk.gestorefile.factory.GestoreFileFactory;
import it.mds.sdk.libreriaregole.tracciato.TracciatoSplitter;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("tracciatoSplitterSismResCont")
public class TracciatoSplitterImpl implements TracciatoSplitter<RecordDtoSismResidenzialeContatti> {

    @Override
    public List<Path> dividiTracciato(Path tracciato) {
        return null;
    }

    @Override
    public List<Path> dividiTracciato(List<RecordDtoSismResidenzialeContatti> records, String idRun) {

        try {

            ConfigurazioneFlussoSismResContatti conf = getConfigurazione();
            String annoRif = records.get(0).getAnnoRiferimento();
            String codiceRegione = records.get(0).getCodiceRegione();

            //XML CONTATTO
            //imposto la regione/periodo/anno che è unica per il file? TODO
            it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ObjectFactory objContatto = getObjectFactory();
            ResidenzialeSemiresidenzialeContatto residenzialeSemiresidenzialeContatto = objContatto.createResidenzialeSemiresidenzialeContatto();
            residenzialeSemiresidenzialeContatto.setCodiceRegione(codiceRegione);
            residenzialeSemiresidenzialeContatto.setAnnoRiferimento(annoRif);
            residenzialeSemiresidenzialeContatto.setPeriodoRiferimento(it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.PeriodoRiferimento.fromValue(records.get(0).getPeriodoRiferimento()));


            for (RecordDtoSismResidenzialeContatti r : records) {
                if (!r.getTipoOperazioneContatto().equalsIgnoreCase("NM")) {
                    creaContattoXml(r, residenzialeSemiresidenzialeContatto, objContatto);
                }
            }

            GestoreFile gestoreFile = GestoreFileFactory.getGestoreFile("XML");


            //recupero il path del file xsd di contatto
            URL resourceContatto = this.getClass().getClassLoader().getResource("CNR.xsd");
            log.debug("URL dell'XSD per la validazione idrun {} : {}", idRun, resourceContatto);

            //scrivi XML CONTATTO
            String pathContatto = conf.getXmlOutput().getPercorso() + "SDK_RES_CNR_" + records.get(0).getPeriodoRiferimento() + "_" + idRun + ".xml";
            gestoreFile.scriviDto(residenzialeSemiresidenzialeContatto, pathContatto, resourceContatto);


            return List.of(Path.of(pathContatto));
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            log.error("[{}].dividiTracciato  - records[{}]  - idRun[{}] -" + e.getMessage(),
                    this.getClass().getName(),
                    records.stream().map(obj -> "" + obj.toString()).collect(Collectors.joining("|")),
                    idRun,
                    e
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossibile validare il csv in ingresso. message: " + e.getMessage());
        } catch (JAXBException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private void creaContattoXml(RecordDtoSismResidenzialeContatti r, ResidenzialeSemiresidenzialeContatto residenzialeSemiresidenzialeContatto,
                                 it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ObjectFactory objContatto) {


        //ASL RIF
        ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento currentAsl = getCurrentAsl(residenzialeSemiresidenzialeContatto, r);
        if (currentAsl == null) {
            currentAsl = creaAslContatto(r.getCodiceAziendaSanitariaRiferimento(), objContatto);
            residenzialeSemiresidenzialeContatto.getAziendaSanitariaRiferimento().add(currentAsl);

        }

        //DSM
        ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM currentDsm = getCurrentDsm(currentAsl, r);
        if (currentDsm == null) {
            currentDsm = creaDSMContatto(r.getCodiceDipartimentoSaluteMentale(), objContatto);
            currentAsl.getDSM().add(currentDsm);
        }

        //ASSISTITO
        ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito currentAssisitito = getCurrentAssistito(currentDsm, r);
        if (currentAssisitito == null) {
            currentAssisitito = creaAssistitoContatto(r, objContatto);
            currentDsm.getAssistito().add(currentAssisitito);
        }

        //STRUTTURA
        ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito.Struttura currentStruttura = getStruttura(currentAssisitito, r);
        if (currentStruttura == null) {
            currentStruttura = creaStrutturaContatto(r, objContatto);
            currentAssisitito.getStruttura().add(currentStruttura);
        }

        //CONTATTO

        ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito.Struttura.Contatto currentContatto = creaContatto(r, objContatto);
        currentStruttura.getContatto().add(currentContatto);

    }

    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento creaAslContatto(String codAsl,
                                                                                             it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ObjectFactory objContatto) {
        ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento asl = objContatto.createResidenzialeSemiresidenzialeContattoAziendaSanitariaRiferimento();
        asl.setCodiceAziendaSanitariaRiferimento(codAsl);
        return asl;
    }

    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM creaDSMContatto(String codDsm,
                                                                                                 it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ObjectFactory objContatto) {
        ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM dsm = objContatto.createResidenzialeSemiresidenzialeContattoAziendaSanitariaRiferimentoDSM();
        dsm.setCodiceDSM(codDsm);
        return dsm;
    }

    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito creaAssistitoContatto(RecordDtoSismResidenzialeContatti r,
                                                                                                                 it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ObjectFactory objContatto) {
        ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito assistito = objContatto.createResidenzialeSemiresidenzialeContattoAziendaSanitariaRiferimentoDSMAssistito();
        assistito.setIdRec(r.getIdRecord());
        return assistito;
    }

    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito.Struttura creaStrutturaContatto(RecordDtoSismResidenzialeContatti r,
                                                                                                                           it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ObjectFactory objContatto) {
        ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito.Struttura struttura = objContatto.createResidenzialeSemiresidenzialeContattoAziendaSanitariaRiferimentoDSMAssistitoStruttura();
        struttura.setCodiceStruttura(r.getCodiceStruttura());
        return struttura;
    }

    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito.Struttura.Contatto creaContatto(RecordDtoSismResidenzialeContatti r,
                                                                                                                           it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.ObjectFactory objContatto) {
        ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito.Struttura.Contatto contatto = objContatto.createResidenzialeSemiresidenzialeContattoAziendaSanitariaRiferimentoDSMAssistitoStrutturaContatto();
        contatto.setIDContatto(r.getIdContatto());
        contatto.setInviantePrimoContatto(r.getInviantePrimoContatto());
        XMLGregorianCalendar dataAp = null;
        XMLGregorianCalendar dataChiusura = null;
        try {
            dataAp = r.getDataAperturaSchedaPaziente() != null ? DatatypeFactory.newInstance().newXMLGregorianCalendar(r.getDataAperturaSchedaPaziente()) : null;
            dataChiusura = r.getDataChiusuraSchedaPaziente() != null ? DatatypeFactory.newInstance().newXMLGregorianCalendar(r.getDataChiusuraSchedaPaziente()) : null;
        } catch (DatatypeConfigurationException e) {
            log.error("Errore conversione XMLGregorianCalendar date", e);
        }
        contatto.setDataAperturaSchedaPaziente(dataAp);
        contatto.setTipoOperazione(it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.TipoOperazione.fromValue(r.getTipoOperazioneContatto()));
        contatto.setPrecedentiContatti(r.getPrecedentiContatti());
        contatto.setDataChiusuraSchedaPaziente(dataChiusura);
        contatto.setDiagnosiApertura(r.getDiagnosiApertura());
        contatto.setDiagnosiChiusura(r.getDiagnosiChiusura());
        contatto.setModalitaConclusione(r.getModalitàConclusione());
        return contatto;
    }

    public ResidenzialeSemiresidenzialeContatto creaSemiresidenzialeContatto(List<RecordDtoSismResidenzialeContatti> records, ResidenzialeSemiresidenzialeContatto residenzialeSemiresidenzialeContatto) {

        //Imposto gli attribute element

        String annoRif = records.get(0).getAnnoRiferimento();
        String codiceRegione = records.get(0).getCodiceRegione();

        if (residenzialeSemiresidenzialeContatto == null) {
            ObjectFactory objResSemiresContatto = getObjectFactory();
            residenzialeSemiresidenzialeContatto = objResSemiresContatto.createResidenzialeSemiresidenzialeContatto();
            residenzialeSemiresidenzialeContatto.setAnnoRiferimento(annoRif);
            residenzialeSemiresidenzialeContatto.setCodiceRegione(codiceRegione);
            residenzialeSemiresidenzialeContatto.setPeriodoRiferimento(it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto.PeriodoRiferimento.fromValue(records.get(0).getPeriodoRiferimento()));


            for (RecordDtoSismResidenzialeContatti r : records) {
                if (!r.getTipoOperazioneContatto().equalsIgnoreCase("NM")) {
                    creaContattoXml(r, residenzialeSemiresidenzialeContatto, objResSemiresContatto);
                }
            }

        }
        return residenzialeSemiresidenzialeContatto;
    }

    public ConfigurazioneFlussoSismResContatti getConfigurazione() {
        return new ConfigurazioneFlussoSismResContatti();
    }

    private ObjectFactory getObjectFactory() {
        return new ObjectFactory();
    }

    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito getCurrentAssistito(ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM currentDsm, RecordDtoSismResidenzialeContatti r) {
        return currentDsm.getAssistito()
                .stream()
                .filter(ass -> r.getIdRecord().equalsIgnoreCase(ass.getIdRec()))
                .findFirst()
                .orElse(null);
    }

    public ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM getCurrentDsm(ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento currentAsl, RecordDtoSismResidenzialeContatti r) {
        return currentAsl.getDSM()
                .stream()
                .filter(dsm -> r.getCodiceDipartimentoSaluteMentale().equalsIgnoreCase(dsm.getCodiceDSM()))
                .findFirst()
                .orElse(null);
    }

    public ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento getCurrentAsl(ResidenzialeSemiresidenzialeContatto residenzialeSemiresidenzialeContatto, RecordDtoSismResidenzialeContatti r) {
        return residenzialeSemiresidenzialeContatto.getAziendaSanitariaRiferimento()
                .stream()
                .filter(asl -> r.getCodiceAziendaSanitariaRiferimento().equalsIgnoreCase(asl.getCodiceAziendaSanitariaRiferimento()))
                .findFirst()
                .orElse(null);
    }

    private ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito.Struttura getStruttura(ResidenzialeSemiresidenzialeContatto.AziendaSanitariaRiferimento.DSM.Assistito currentAssisitito, RecordDtoSismResidenzialeContatti r) {
        return currentAssisitito.getStruttura()
                .stream()
                .filter(str -> r.getCodiceStruttura().equalsIgnoreCase(str.getCodiceStruttura()))
                .findFirst()
                .orElse(null);
    }
}
