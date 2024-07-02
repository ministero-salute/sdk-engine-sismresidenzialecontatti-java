/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.sism.residenziale.cont.parser.regole;

import it.mds.sdk.flusso.sism.residenziale.cont.parser.regole.conf.ConfigurazioneFlussoSismResContatti;
import it.mds.sdk.libreriaregole.dtos.RecordDtoGenerico;
import it.mds.sdk.libreriaregole.parser.ParserTracciato;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import it.mds.sdk.rest.exception.ParseCSVException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * La classe implementa l'interfaccia ParserTracciato e il suo metodo parseTracciato(File tracciato)
 */
@Slf4j
@Component("parserTracciatoSismResCont")
public class ParserTracciatoImpl implements ParserTracciato {

    /**
     * Il metodo converte un File.csv in una List<RecordDtoGenerico> usando come separatore "~"
     *
     * @param tracciato, File.csv di input
     * @return una lista di RecordDtoDir
     */
    private final ConfigurazioneFlussoSismResContatti conf = new ConfigurazioneFlussoSismResContatti();

    @Override
    public List<RecordDtoGenerico> parseTracciato(File tracciato) {
        try(FileReader fileReader = new FileReader(tracciato)) {
            char separatore = conf.getSeparatore().getSeparatore().charAt(0);
            List<RecordDtoGenerico> dirList = new CsvToBeanBuilder<RecordDtoGenerico>(fileReader)
                    .withType(RecordDtoSismResidenzialeContatti.class)
                    .withSeparator(separatore)
                    .withSkipLines(1)   //Salta la prima riga del file CSV
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build()
                    .parse();
            return dirList;

        } catch (FileNotFoundException e) {
            log.debug(e.getMessage(), e);
        } catch (IOException e) {
            log.debug(e.getMessage(), e);
        }catch (Exception e) {
            log.debug(e.getMessage(), e);
            throw new ParseCSVException(e.getMessage());
        }

        return Collections.emptyList();
    }

    public List<RecordDtoGenerico> parseTracciatoBlocco(File tracciato, int inizio, int fine) {
        StopWatch stopWatch = new StopWatch();
        log.info("Inizio lettura file {} da riga {} a riga {}", tracciato.getName(), inizio, fine);
        stopWatch.start();
        char separatore = conf.getSeparatore().getSeparatore().charAt(0);
        try (Reader reader = Files.newBufferedReader(tracciato.toPath())) {
            List<RecordDtoGenerico> res = new ArrayList<>();
            Iterator<RecordDtoSismResidenzialeContatti> it = new CsvToBeanBuilder<RecordDtoSismResidenzialeContatti>(reader)
                    .withType(RecordDtoSismResidenzialeContatti.class)
                    .withSeparator(separatore)
                    .withSkipLines(inizio + 1)   //Salta la prima riga del file CSV
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build()
                    .iterator();
            int count = inizio;
            int totaleRecordElaborati = 0;
            while (it.hasNext() && count < fine) {
                count++;
                RecordDtoGenerico recordGen = it.next();
                res.add(recordGen);
                totaleRecordElaborati++;
            }
            stopWatch.stop();
            log.info("Fine lettura file {} da riga {} a riga {} con {} record in {} ms", tracciato.getName(), inizio,
                    fine, totaleRecordElaborati, stopWatch.getLastTaskTimeMillis());

            return res;
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
            throw new ParseCSVException(ex.getMessage());
        }
    }
}
