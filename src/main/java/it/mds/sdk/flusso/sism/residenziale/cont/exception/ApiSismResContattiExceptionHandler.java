/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.sism.residenziale.cont.exception;

import it.mds.sdk.rest.exception.ApiErrorResponse;
import it.mds.sdk.rest.exception.ParseCSVException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiSismResContattiExceptionHandler {

    @ExceptionHandler(ParseCSVException.class)
    public ResponseEntity<ApiErrorResponse> handleApiParseCSVException(
            ParseCSVException e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Verifica che le date abbiano il formato AAAA-MM-GG,");
        stringBuilder.append("che i numeri decimali abbiano il formato 000.00 e non 000,00 e che l'ordine dei campi sia corretto.");
        ApiErrorResponse response = ApiErrorResponse.builder().withError("Errore nel parse del CSV")
                .withMessage("File CSV di input errato.")
                .withDescrizione(String.valueOf(stringBuilder))
                .withRiga(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
