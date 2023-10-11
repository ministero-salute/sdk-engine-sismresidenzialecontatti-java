//
// Questo file � stato generato dall'Eclipse Implementation of JAXB, v3.0.0 
// Vedere https://eclipse-ee4j.github.io/jaxb-ri 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2022.05.31 alle 06:59:58 PM CEST 
//


package it.mds.sdk.flusso.sism.residenziale.cont.tracciato.bean.output.contatto;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per PeriodoRiferimento.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <pre>
 * &lt;simpleType name="PeriodoRiferimento"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="S1"/&gt;
 *     &lt;enumeration value="S2"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PeriodoRiferimento")
@XmlEnum
public enum PeriodoRiferimento {

    @XmlEnumValue("S1")
    S_1("S1"),
    @XmlEnumValue("S2")
    S_2("S2");
    private final String value;

    PeriodoRiferimento(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PeriodoRiferimento fromValue(String v) {
        for (PeriodoRiferimento c: PeriodoRiferimento.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
