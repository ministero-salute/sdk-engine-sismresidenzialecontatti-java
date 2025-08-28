# **1 Introduzione**

## ***1.1 Obiettivi del documento***


Il Ministero della Salute (MdS) metterà a disposizione degli Enti, da cui riceve dati, applicazioni SDK specifiche per flusso logico e tecnologie applicative (Java, PHP e C#) per verifica preventiva (in casa Ente) della qualità del dato prodotto.

![](img/img4.png)

Nel presente documento sono fornite la struttura e la sintassi dei tracciati previsti dalla soluzione SDK per avviare il proprio processo elaborativo, nonché i relativi schemi xsd di convalida e i controlli di merito sulla qualità, completezza e coerenza dei dati.

Gli obiettivi del documento sono:

- fornire una descrizione funzionale chiara e consistente dei tracciati di input a SDK;
- fornire le regole funzionali per la verifica di qualità, completezza e coerenza dei dati.

In generale, la soluzione SDK è costituita da 2 diversi moduli applicativi (Access Layer e Validation Engine) per abilitare

- l’interoperabilità con il contesto tecnologico dell’Ente in cui la soluzione sarà installata;
- la validazione del dato ed il suo successivo invio verso il MdS.

La figura che segue descrive la soluzione funzionale ed i relativi benefici attesi.

![](img/img2.png)

## ***1.2 Acronimi***

Nella tabella riportata di seguito sono elencati tutti gli acronimi e le definizioni adottati nel presente documento.


|**#**|**Acronimo / Riferimento**|**Definizione**|
| - | - | - |
|1|NSIS|Nuovo Sistema Informativo Sanitario|
|2|SDK|Software Development Kit|
|3|SISM|Sistema Informativo Salute Mentale|


# **2. Architettura SDK**

L'architettura degli SDK è disponibile al seguente link [`ARCHITECTURE.md`](https://github.com/ministero-salute/sdk-utilities-regole-properties/blob/main/ARCHITECTURE.md).

# **3. Funzionamento della soluzione SDK**

In questa sezione è descritta le specifica di funzionamento del flusso **CNR**  per l’alimentazione dello stesso.

## ***3.1 Input SDK***

In fase di caricamento del file verrano impostati i seguenti parametri che andranno in input al SDK in fase di processamento del file:


|**NOME PARAMETRO**|**DESCRIZIONE**|**LUNGHEZZA**|**DOMINIO VALORI**|
| :- | :- | :- | :- |
|ID CLIENT|Identificativo univoco della transazione che fa richiesta all'SDK|100|Non definito|
|NOME FILE INPUT|Nome del file per il quale si richiede il processamento lato SDK|256|Non definito|
|ANNO RIFERIMENTO|Stringa numerica rappresentante l’anno di riferimento per cui si intende inviare la fornitura|4|Anno (Es. 2022)|
|PERIODO RIFERIMENTO|Stringa alfanumerica rappresentante il periodo per il quale si intende inviare la fornitura. In fase di invio della fornitura verso Mds si dovrà concatenare al valore di questo campo il carattere I (i MAIUSCOLA) (Es. S1I)|2|S1, S2|
|TIPO TRASMISSIONE |Indica se la trasmissione dei dati verso MDS avverrà in modalità full (F) o record per record (R). Per questo flusso la valorizzazione del parametro sarà impostata di default a F|1|F/R|
|FINALITA’ ELABORAZIONE|Indica se i flussi in output prodotti dal SDK verranno inviati verso MDS (Produzione) oppure se rimarranno all’interno del SDK e il processamento vale solo come test del flusso (Test)|1|Produzione/Test|
|CODICE REGIONE|<p>Individua la Regione a cui afferisce la struttura. Il codice da utilizzare è quello a tre caratteri definito con DM 17 settembre 1986, pubblicato nella Gazzetta Ufficiale n.240 del 15 ottobre 1986, e successive modifiche, utilizzato anche nei modelli per le rilevazioni delle attività gestionali ed economiche delle Aziende unità sanitarie locali.</p><p></p>|3|Es. 010|

## ***3.2 Tracciato input a SDK***

Il flusso di input avrà formato **csv** posizionale e una naming convention libera a discrezione dell’utente che carica il flusso senza alcun vincolo di nomenclatura specifica (es: nome\_file.csv). Il separatore per il file csv sarà la combinazione di caratteri tra doppi apici: “~“.

All’interno della specifica del tracciato sono indicati i dettagli dei campi di business del tracciato di input atteso da SDK, il quale differisce per i diversi flussi dell’area SISM. All’interno di tale file è presente la colonna **Posizione nel file** la quale rappresenta l’ordinamento delle colonne del tracciato di input da caricare all’SDK.

Di seguito la tabella in cui è riportata la specifica del tracciato di input per il flusso in oggetto:

|**Nome campo**|**Posizione nel File**|**Key**|**Descrizione**|**Tipo** |**Obbligatorietà**|**Informazioni di Dominio**|**Lunghezza campo**|**XPATH Tracciato Output**|
| :-: | :- | :- | :-: | :-: | :-: | :-: | :-: | :-: |
|Anno di riferimento|1|KEY|Indica l’anno a cui si riferisce la rilevazione.|N|OBB|Formato AAAA|4|/ResidenzialeSemiresidenzialeContatto/AnnoRiferimento|
|Periodo di Riferimento |2|KEY|Indica il semestre a cui si riferisce la rilevazione.|AN|OBB|Valori Accettati<br>• S1<br>• S2|2|/ResidenzialeSemiresidenzialeContatto/PeriodoRiferimento|
|Codice Regione|3|KEY|Individua la Regione a cui afferisce la struttura. Il codice da utilizzare è quello a tre caratteri definito con DM 17 settembre 1986, pubblicato nella Gazzetta Ufficiale n.240 del 15 ottobre 1986, e successive modifiche, utilizzato anche nei modelli per le rilevazioni delle attività gestionali ed economiche delle Aziende unità sanitarie locali.|AN|OBB||3|/ResidenzialeSemiresidenzialeContatto/CodiceRegione|
|Codice Azienda Sanitaria di Riferimento |4|KEY|Id Identifica l’azienda sanitaria locale in cui e’ sito il Servizio. Il codice da utilizzare è quello a tre caratteri usato anche nei modelli per le rilevazioni delle attività gestionali ed economiche delle Aziende unità sanitarie locali (codici di cui al D.M. 05/12/2006 e successive modifiche).|AN|OBB|**Riferimento:** ·       codice ASL - MRA (Monitoraggio Rete Assistenza);|3|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/CodiceAziendaSanitariaRiferimento|
|Codice Dipartimento Salute Mentale|5|KEY|Identifica il dipartimento di Salute Mentale interessato alla rilevazione.|AN|OBB|** |3|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/CodiceDSM|
|Id Record|6|KEY|Codice identificativo unico del record |AN|OBB|Il valore deve essere generato come descritto nel par. 2.2.3.3.2 - Codice identificativo unico del record (ID\_REC) – modalità di alimentazione|88|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Id\_Rec|
|Struttura|7|KEY|Codice della Struttura di erogazione Univocità della Chiave, in unione ai campi Codice ASL, codice DSM, Codice Sanitario Individuale. Appartenenza alla regione ed all’ASL di riferimento.|AN|OBB| |8|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Struttura/CodiceStruttura|
|ID Contatto|8|KEY|Identificativo univoco del Contatto.|A|OBB| |14|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Struttura/Contatto/IDContatto|
|Data Apertura Scheda Paziente|9| |Data di apertura della scheda del paziente.|AN|OBB|Formato: AAAA-MM-GG|10|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Struttura/Contatto/DataAperturaSchedaPaziente|
|Diagnosi Apertura|10| |Individua la diagnosi che è indicata nella scheda del paziente quando ha inizio l'episodio di cura.|AN|OBB|I codici da utilizzare sono quelli previsti dalla Classificazione Internazionale delle<br>Malattie-modificazioni cliniche (versione italiana 2002 ICD-9 CM e<br>successive modifiche) appartenenti al Capitolo di patologie psichiatriche (codici 290.XX – 319.XX).<br>In caso di “Assenza di patologia psichiatrica”, si può utilizzare il seguente valore:<br>00000=“Assenza di patologia psichiatrica”.<br>Nel caso in cui la diagnosi di apertura non sia chiaramente definita, si può utilizzare il seguente valore:<br>99999=Diagnosi in attesa di definizione|5|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Struttura/Contatto/DiagnosiApertura|
|Precedenti Contatti|11| |Indica la presenza di precedenti contatti psichiatrici alla data di rilevazione.|AN|OBB|Valori Ammessi:<br>1= SI<br>2= NO<br>9=NON NOTO/NON RISULTA |1|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Struttura/Contatto/PrecedentiContatti|
|Inviante per primo Contatto|12| |Identifica il soggetto che richiede, dal punto di vista amministrativo, il primo contatto con il DSM o la struttura privata accreditata.|N|OBB|Valori Ammessi:<br>1=accesso diretto <br>2=medico di medicina generale<br>3= ospedale e altre strutture sanitarie e sociosanitarie <br>non psichiatriche<br>4= altri DSM e strutture psichiatriche private <br>5=servizi pubblici non sanitari <br>9=sconosciuto|1|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Struttura/Contatto/InviantePrimoContatto|
|Data Chiusura Scheda Paziente|13| |Indica la data di chiusura della scheda del paziente per la<br>conclusione dell'episodio di cura.<br>La “Data chiusura scheda paziente” deve essere obbligatoriamente specificata se è valorizzato almeno 1 dei seguenti campi:<br>• “Diagnosi di chiusura”;<br>• “Modalità conclusione”.<br>Indica la data di chiusura della scheda del paziente per la<br>conclusione dell'episodio di cura.<br>La “Data chiusura scheda paziente” deve essere obbligatoriamente specificata se è valorizzato almeno 1 dei seguenti campi:<br>• “Diagnosi di chiusura”;<br>• “Modalità conclusione”.|AN|NBB (Obbligatoria se “Diagnosi di chiusura” o “Modalità di conclusione” valorizzati)|Formato: AAAA-MM-GG|10|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Struttura/Contatto/DataChiusuraSchedaPaziente|
|Diagnosi Chiusura|14| |Individua la diagnosi che è indicata nella scheda del paziente al momento della conclusione dell'episodio di cura.<br>La “Diagnosi di chiusura” deve essere obbligatoriamente specificata se si verifica almeno 1 delle seguenti condizioni:<br>•  “Data di chiusura scheda paziente” valorizzata;<br>•  “Modalità di conclusione” valorizzata con valore > 1 .<br>In caso di “Modalità di conclusione”= 9-chiusura amministrativa, deve essere specificata “Diagnosi di chiusura” = “xxxxx”.|AN|NBB<br>(Obbligatoria se “Data di chiusura scheda paziente” valorizzata e “Modalità di conclusione” valorizzata con valore <br>> 1)|I codici da utilizzare sono quelli previsti dalla Classificazione Internazionale delle<br>Malattie-modificazioni cliniche (versione italiana 2002 ICD-9 CM e<br>successive modifiche) appartenenti al Capitolo di patologie psichiatriche (codici 290.XX – 319.XX).<br>In caso di “Assenza di patologia psichiatrica”, si può utilizzare il seguente valore:<br>00000=“Assenza di patologia psichiatrica”.<br>In caso di chiusura<br>amministrativa, si inserisce il valore “xxxxx”.|5|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Struttura/Contatto/DiagnosiChiusura|
|Modalità Conclusione|15| |Indica la modalità di conclusione dell'episodio di cura.<br>La “modalità di conclusione” deve essere obbligatoriamente specificata se è valorizzato almeno 1 dei seguenti campi:<br>• “Data di chiusura scheda paziente”;<br>• “Diagnosi di chiusura”.|AN|NBB<br>(Obbligatoria se “Data di chiusura scheda paziente” o “Diagnosi di chiusura” valorizzati)|Valori Ammessi:<br>1=assenza di indicazioni al trattamento psichiatrico  <br>2=conclusione    concordata del rapporto terapeutico<br>3=interruzione non concordata <br>4=suicidio<br>5=decesso <br>9=amministrativa|1|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Struttura/Contatto/ModalitaConclusione|
|Tipo operazione |16| |Campo tecnico utilizzato per distinguere la trasmissione di informazioni nuove, modificate o eventualmente annullate.|A|OBB|Valori Ammessi:<br>I=Inserimento<br>C=Cancellazione<br>V=Variazione<br>NM: Non Movimentato (la componente Contatto del record non viene inserita nel relativo xml a valle della validazione)|1|/ResidenzialeSemiresidenzialeContatto/AziendaSanitariaRiferimento/DSM/Assistito/Struttura/Contatto/TipoOperazione|

## ***3.3 Controlli di validazione del dato (business rules)***

Di seguito sono indicati i controlli da configurare sulla componente di Validation Engine e rispettivi error code associati riscontrabili sui dati di input per il flusso **CNR**.

Gli errori sono solo di tipo scarti (mancato invio del record).

Al verificarsi anche di un solo errore di scarto, tra quelli descritti, il record oggetto di controllo sarà inserito tra i record scartati.

Business Rule non implementabili lato SDK:

- Storiche (Business Rule che effettuano controlli su dati già acquisiti/consolidati che non facciano parte del dato anagrafico)
- Transazionali (Business Rule che effettuano controlli su record, i quali rappresentano transazioni, su cui andrebbe garantito l’ACID (Atomicità-Consistenza-Isolamento-Durabilità))
- Controllo d’integrità (cross flusso) (Business Rule che effettuano controlli sui record utilizzando informazioni estratte da record di altri flussi)

Di seguito le BR per il flusso in oggetto:

|**CAMPO**|**FLUSSO**|**CODICE ERRORE**|**ATTIVA/DISATTIVA**|**DESCRIZIONE ERRORE**|**DESCRIZIONE MDS**|**DESCRIZIONE ALGORITMO**|**TABELLA ANAGRAFICA**|**CAMPI DI COERENZA**|**SCARTI/ANOMALIE**|**TIPOLOGIA BR**|
| :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: |
|Anno Riferimento|CNR|3001|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Anno Riferimento|CNR|3000|Attiva|Datatype errato in un campo obbligatorio|Valore compreso tra 1000 e 2999|Non definito|Non definito|Non definito|Scarti|Basic|
|Anno Riferimento|CNR|3009|Attiva|Il valore del campo Anno Riferimento e' diverso dal valore Anno Riferimento GAF|Il valore del campo Anno Riferimento e' diverso dall’Anno di Riferimento specificato al momento dell’upload sul GAF|Il valore del campo Anno Riferimento del tracciato di input e' diverso dall’Anno di Riferimento passato come parametro all'SDK|Non Definito|Non definito|Scarti|Basic|
|Codice Regione|CNR|3011|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Codice Regione|CNR|3012|Attiva|Non appartenenza alla tabella di riferimento per un campo obbligatorio|Valori diversi da 010,020,030,041,042,050,060,070,080,090,100,110,120,130,140,150,160,170,180,190,200|Non definito|Non definito|Non definito|Scarti|Basic|
|Codice Regione|CNR|3305|Attiva|Il codice regione non coincide con il MITTENTE|Il parametro Codice Regione passato in input all'SDK non coincide con il campo Codice Regione|Non definito|Non definito|Non definito|Scarti|Basic|
|Periodo Riferimento|CNR|3021|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Periodo Riferimento|CNR|3022|Attiva|Non appartenenza alla tabella di riferimento per un campo obbligatorio|Il campo è valorizzato con valori diversi da S1, S2|Non definito|Non definito|Non definito|Scarti|Basic|
|Periodo Riferimento|CNR|3010|Attiva|Il valore del campo Periodo Riferimento e' diverso dal valore Periodo Riferimento GAF|Il valore del campo Periodo Riferimento e' diverso dal Periodo di Riferimento specificato al momento dell’upload sul GAF|Il valore del campo Periodo Riferimento del tracciato di input e' diverso dall’Anno di Riferimento passato come parametro all'SDK|Non Definito|Non definito|Scarti|Basic|
|Codice Azienda Sanitaria di Riferimento|CNR|3031|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Codice Azienda Sanitaria di Riferimento|CNR|3030|Attiva|Datatype errato in un campo obbligatorio|Il campo prevede 3 caratteri numerici.|Non definito|Non definito|Non definito|Scarti|Basic|
|Codice Azienda Sanitaria di Riferimento|CNR|3310|Attiva|Il codice della ASL di riferimento non esiste nella relativa anagrafica|Non Definito|Il valore del campo Codice Azienda Sanitaria di Riferimento del tracciato di input deve esistere all'interno della colonna COD\_ASL della query filtrata con le seguenti condizioni:<br>` `- num\_ann (QUERY) = Anno Riferimento  (TRACCIATO INPUT)<br>cod\_reg\_erg (QUERY) = Codice Regione (TRACCIATO INPUT)<br>cod\_asl (QUERY)= Codice Azienda Sanitaria di Riferimento (TRACCIATO INPUT).|ASL|Anno Riferimento; Codice Regione; Codice Azienda Sanitaria di Riferimento|Scarti|Anagrafica|
|Codice Dipartimento Salute Mentale|CNR|3041|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Codice Dipartimento Salute Mentale|CNR|3040|Attiva|Datatype errato in un campo obbligatorio|Il campo prevede 3 caratteri alfanumerici|Non definito|Non definito|Non definito|Scarti|Basic|
|Codice Dipartimento Salute Mentale|CNR|3315|Attiva|Il codice DSM non esiste nella relativa anagrafica|Non Definito|Il **Codice Dipartimento Salute Mentale** non esiste all'interno della colonna **COD\_DSM** della query  della query filtrata con le seguenti condizioni:<br>` `- num\_ann (QUERY) = Anno Riferimento  (TRACCIATO INPUT)<br>cod\_reg (QUERY) = Codice Regione (TRACCIATO INPUT)<br>` `cod\_asr\_rfr (QUERY)= Codice Azienda Sanitaria di Riferimento (TRACCIATO INPUT)|DSM|Anno Riferimento; Codice Regione; Codice Azienda Sanitaria di Riferimento|Scarti|Anagrafica|
|Id Record|CNR|3051|Attiva|mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Id Record|CNR|3050|Attiva|Lunghezza non conforme a quella attesa|La lunghezza del valore specificato non è conforme a quanto previsto (88 caratteri)|Non definito|Non definito|Non definito|Scarti|Basic|
|Tipo operazione Contatto|CNR|3181.2|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Tipo operazione Contatto|CNR|3182.2|Attiva|Tipo operazione non appartenente al dominio atteso (I,V,C)|Non Definito|Non definito|Non Definito|Non definito|Scarti|Basic|
|Struttura|CNR|3191|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Struttura|CNR|3190|Attiva|Datatype errato in un campo obbligatorio|Il campo deve avere lunghezza compresa tra 6 e 8 caratteri alfanumerici|Non definito|Non definito|Non definito|Scarti|Basic|
|Struttura|CNR|3330|Attiva|La Struttura non esiste nella relativa anagrafica|Non Definito|Il valore del campo  **Codice Struttura** del tracciato di input non esiste all'interno della colonna **COD\_IST** della query filtrata con le seguenti condizioni: <br>ANN\_rif (QUERY)= Anno Riferimento  (TRACCIATO INPUT), <br>COD\_REG (QUERY) = Codice Regione (TRACCIATO INPUT),<br>COD\_ASR\_RFR (QUERY) = Codice Azienda Sanitaria di Riferimento (TRACCIATO INPUT)<br>COD\_IST (QUERY) = Codice Struttura (TRACCIATO INPUT)<br><br><br>Nota: flg\_ist\_psi (c'è ma non serve filtrare su questo) |Struttura|` `Anno Riferimento;Codice Regione; Codice Azienda Sanitaria di Riferimento|Scarti|Anagrafica|
|Id Contatto|CNR|3211|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Id Contatto|CNR|3210|Attiva|Datatype errato in un campo obbligatorio|Il campo deve essere un numero massimo di 14 cifre.|Non definito|Non definito|Non definito|Scarti|Basic|
|Data Apertura scheda paziente|CNR|3221|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Data Apertura scheda paziente|CNR|3220|Attiva|Datatype errato in un campo obbligatorio|La data deve essere indicata nel formato AAAA-MM-GG|Non definito|Non definito|Non definito|Scarti|Basic|
|Data Apertura scheda paziente|CNR|3380|Attiva|La data di apertura scheda non puo' essere successica al periodo di riferimento|Non Definito|Non definito|Non Definito|periodo di riferimento|Scarti|Basic|
|Diagnosi di Apertura|CNR|3231|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Diagnosi di Apertura|CNR|3230|Attiva|Datatype errato in un campo obbligatorio|Il campo deve essere di massimo 5 caratteri.|Non definito|Non definito|Non definito|Scarti|Basic|
|Diagnosi di Apertura|CNR|3445|Attiva|Diagnosi di apertura non presente nell'anagrafica.|Diagnosi di apertura non presente nell'anagrafica ICD-09-CM- Capitolo di patologie psichiatriche (codici 290.XX – 319.XX)  e diversa da 00000 e 99999.|la diagnosi di apertura non esiste all'interno dell'unica colonna della query |ICD-09-CM- Capitolo di patologie psichiatriche|Non definito|Scarti|Basic|
|Precedenti Contatti|CNR|3241|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Precedenti Contatti|CNR|3242|Attiva|Non appartenenza alla tabella di riferimento per un campo obbligatorio|Valori diversi dai quelli Ammessi:1,2,9|Non definito|Non definito|Non definito|Scarti|Basic|
|Inviante per primo Contatto|CNR|3251|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|Mancata valorizzazione di un campo obbligatorio (non presente o uguale a blanks)|Non Definito|Non definito|Scarti|Basic|
|Inviante per primo Contatto|CNR|3252|Attiva|Non appartenenza alla tabella di riferimento per un campo obbligatorio|Valori diversi dai quelli Ammessi:1,2,3,4,5,9|Non definito|Non definito|Non definito|Scarti|Basic|
|Data Chiusura Scheda Paziente|CNR|3250|Attiva|Datatype errato in un campo facoltativo|La data deve essere indicata nel formato AAAA-MM-GG|Non definito|Non definito|Non definito|Scarti|Basic|
|Data Chiusura Scheda Paziente|CNR|3385|Attiva|La data di chiusura scheda non puo' essere precedente alla data di apertura scheda|Non Definito|Non definito|Non Definito|Data Apertura scheda paziente|Scarti|Basic|
|Data Chiusura Scheda Paziente|CNR|7395|Attiva|Data di chiusura scheda non compresa nel periodo di riferimento|Non Definito|**Data Chiusura Scheda Paziente** non compresa nel semestre di riferimento (periodo di riferimento). Es. con Periodo Riferimento = 'S1' e Anno riferimento = '2021' ci si aspetta che la Data Dimissione sia compresa nel primo primo semestre del 2021 |Non Definito|periodo di riferimento|Anomalia|Basic|
|Data Chiusura Scheda Paziente|CNR|3942|Attiva|Mancata valorizzazione di un campo ad obbligatorietà condizionata|La “data di chiusura scheda paziente” non è stata valorizzata ma risulta valorizzato almeno  uno dei seguenti campi: “Diagnosi di chiusura” o “Modalità conclusione”.|\*non valorizzato = null (elemento del linguaggio) oppure blanks|Non definito|Diagnosi di chiusura, Modalità conclusione|Scarti|Basic|
|Data Chiusura Scheda Paziente|CNR|3947|Attiva|Data chiusura scheda paziente non compresa nel Periodo Riferimento GAF|La “data di chiusura scheda paziente” non è compresa nel periodo di Periodo di Riferimento specificato al momento dell’upload sul GAF.|La **data di chiusura scheda paziente** non compresa nel semestre di riferimento (periodo di riferimento). Es. con Periodo Riferimento = 'S1' e Anno riferimento = '2021' ci si aspetta che ladata intervento  sia compresa nel primo primo semestre del 2021 | | | |Basic|
|Diagnosi di Chiusura|CNR|3260|Attiva|Datatype errato in un campo obbligatorio|Il campo deve essere di massimo 5 caratteri.|Non Definito|Non Definito|Non Definito|Scarti|Basic|
|Diagnosi di Chiusura|CNR|3261|Attiva|Mancata valorizzazione di un campo obbligatorio|Non Definito|il campo deve essere valorizzato e diverso da blanks|Non Definito|Non definito|Scarti|Basic|
|Diagnosi di Chiusura|CNR|3446|Attiva|Diagnosi di chiusura non presente nell'anagrafica.|Diagnosi di apertura non presente nell'anagrafica ICD-09-CM- Capitolo di patologie psichiatriche (codici 290.XX – 319.XX)  e diversa da 00000 e xxxxx.|la diagnosi di chiusura non esiste all'interno dell'unica colonna della query |ICD-09-CM- Capitolo di patologie psichiatriche|Non definito|Scarti|Basic|
|Diagnosi di Chiusura|CNR|3943|Attiva|Mancata valorizzazione di un campo ad obbligatorietà condizionata|La “Diagnosi di chiusura” non è stata valorizzata ma si presentano le seguenti condizioni: <br>· “data di chiusura scheda paziente” valorizzata e “Modalità conclusione” non valorizzata;<br>· “data di chiusura scheda paziente” valorizzata e “Modalità conclusione” valorizzata con valore <> 1;<br>· “data di chiusura scheda paziente” non valorizzata e “Modalità conclusione” valorizzata con valore <> 1;|\*non valorizzato = null (elemento del linguaggio) oppure blanks|Non definito|data di chiusura scheda paziente, Modalità conclusione|Scarti|Basic|
|Diagnosi di Chiusura|CNR|3944|Attiva|Valore incoerente con il campo “Modalità di conclusione”.|È stata specificata “diagnosi di chiusura” = ‘xxxxx’ per una “modalità di conclusione” <> 9 - Amministrativa|“modalità di conclusione = 9 - Amministrativa" rappresenta una chiusura amministrativa perché magari l'ente da una certa data in poi non eroga più servizi, pertanto il paziente potrebbe ritrovarsi presso una nuova struttura. Il valore xxxxx per diagnosi chiusura a questo punto non rappresenta una vera chiusura ma un "trasferimento"|Non definito|modalità di conclusione|Scarti|Basic|
|Modalità Conclusione|CNR|3262|Attiva|Non appartenenza alla tabella di riferimento per un campo facoltativo|Valore diverso da: 1,2,3,4,5,9|Non definito|Non definito|Non definito|Scarti|Basic|
|Modalità Conclusione|CNR|3945|Attiva|Mancata valorizzazione di un campo ad obbligatorietà condizionata|La “Modalità conclusione” non è stata valorizzata ma risulta valorizzato almeno  uno dei seguenti campi: “data di chiusura scheda paziente” o “diagnosi di chiusura”.|\*non valorizzato = null (elemento del linguaggio) oppure blanks|Non definito|data di chiusura scheda paziente,diagnosi di chiusura.|Scarti|Basic|
|Modalità Conclusione|CNR|3946|Attiva|Valore incoerente con il campo “Diagnosi di chiusura”.|È stata specificata “diagnosi di chiusura” <> ‘xxxxx’ per una “modalità di conclusione” = 9 - Amministrativa|“modalità di conclusione = 9 - Amministrativa" rappresenta una chiusura amministrativa perché magari l'ente da una certa data in poi non eroga più servizi, pertanto il paziente potrebbe ritrovarsi presso una nuova struttura. Il valore xxxxx per diagnosi chiusura a questo punto non rappresenta una vera chiusura ma un "trasferimento"|Non definito|Diagnosi di Chiusura|Scarti|Basic|

## ***3.4 Accesso alle anagrafiche***

I controlli applicativi saranno implementati a partire dall’acquisizione dei seguenti dati anagrafici disponibili in ambito MdS e recuperati con servizi ad hoc (Service Layer mediante PDI):

- DSM
- ASL
- Struttura
- Codice Alpha2
- ICD-09-CM
- Capitolo di patologie psichiatriche

All’interno del file **censimento\_anagrafiche** sono presenti per ogni anagrafica il dettaglio implementativo (Query SQL) e la tabella fisica da cui alimentare l’anagrafica.

Il dato anagrafico sarà presente sottoforma di tabella composta da tre colonne:

- Valore (in cui è riportato il dato, nel caso di più valori, sarà usato il carattere # come separatore)

- Data inizio validità (rappresenta la data di inizio validità del campo Valore)
 - Formato: AAAA-MM-DD
 - Notazione inizio validità permanente: **1900-01-01**


- Data Fine Validità (rappresenta la data di fine validità del campo Valore)
  - Formato: AAAA-MM-DD
  - Notazione fine validità permanente: **9999-12-31**

Affinchè le Business Rule che usano il dato anagrafico per effettuare controlli siano correttamente funzionanti, occorre sempre controllare che la data di competenza del record su cui si effettua il controllo (la quale varia in base alla componente), sia compresa tra le data di validità.  

Di seguito viene mostrato un caso limite di anagrafica in cui sono presenti delle sovrapposizioni temporali e contraddizioni di validità permanente/specifico range:


|ID|VALUE|VALID\_FROM|VALID\_TO|
| - | - | - | - |
|1|VALORE 1|1900-01-01|9999-12-31|
|2|VALORE 1|2015-01-01|2015-12-31|
|3|VALORE 1|2018-01-01|2023-12-31|
|4|VALORE 1|2022-01-01|2024-12-31|


Diremo che il dato presente sul tracciato di input è valido se e solo se:

∃ VALUE\_R = VALUE\_A “tale che” VALID\_FROM <= **DATA\_COMPETENZA** <= VALID\_TO

(Esiste almeno un valore compreso tra le date di validità)

Dove:

- VALUE\_R rappresenta i campi del tracciato di input coinvolti nei controlli della specifica BR

- VALUE\_A rappresenta i campi dell’anagrafica coinvolti nei controlli della specifica BR

- VALID\_FROM/VALID\_TO rappresentano le colonne dell’anagrafica

- DATA\_COMPETENZA data da utilizzare per il filtraggio del dato anagrafico

## Istruzioni per l'installazione

Per l'installazione e l'avvio dell'engine seguire la documentazione tecnica dettagliata disponibile all'url [`INSTALL.md`](https://github.com/ministero-salute/sdk-utilities-regole-properties/blob/main/INSTALL.md).


## mantainer:
 Accenture SpA until January 2026

