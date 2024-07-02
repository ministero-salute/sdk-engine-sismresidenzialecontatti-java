/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.sism.residenziale.cont.parser.regole;

import it.mds.sdk.libreriaregole.dtos.RecordDtoGenerico;
import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordDtoSismResidenzialeContatti extends RecordDtoGenerico {

    //ANN_RIF~COD_PER~COD_REG~COD_ASR_RFR~COD_DSM~ID_REC_KEY~COD_STR~ID_CNT~DAT_APR_SCD_PAZ~DGN_APR~PRE_CNT~INV_PRM_CNT~DAT_CHS_SCD_PAZ~DGN_CHS~MDL_CNL~TIP_TRS

    @CsvBindByPosition(position = 0)
    private String annoRiferimento; //ANN_RIF

    @CsvBindByPosition(position = 1)
    private String periodoRiferimento; //COD_PER

    @CsvBindByPosition(position = 2)
    private String codiceRegione; //COD_REG

    @CsvBindByPosition(position = 3)
    private String codiceAziendaSanitariaRiferimento; //COD_ASR_RFR

    @CsvBindByPosition(position = 4)
    private String codiceDipartimentoSaluteMentale;//COD_DSM

    @CsvBindByPosition(position = 5)
    private String idRecord;//ID_REC_KEYX

    @CsvBindByPosition(position = 6)
    private String codiceStruttura;//COD_STR

    @CsvBindByPosition(position = 7)
    private Long idContatto; //ID_CNT

    @CsvBindByPosition(position = 8)
    private String dataAperturaSchedaPaziente; //DAT_APR_SCD_PAZ

    @CsvBindByPosition(position = 9)
    private String diagnosiApertura; //DGN_APR

    @CsvBindByPosition(position = 10)
    private String precedentiContatti; //PRE_CNT

    @CsvBindByPosition(position = 11)
    private String inviantePrimoContatto; //INV_PRM_CNT

    @CsvBindByPosition(position = 12)
    private String dataChiusuraSchedaPaziente; //DAT_CHS_SCD_PAZ

    @CsvBindByPosition(position = 13)
    private String diagnosiChiusura;//DGN_CHS

    @CsvBindByPosition(position = 14)
    private String modalitàConclusione; //MDL_CNL

    @CsvBindByPosition(position = 15)
    private String tipoOperazioneContatto; //TYP_OPR_CNT

}
